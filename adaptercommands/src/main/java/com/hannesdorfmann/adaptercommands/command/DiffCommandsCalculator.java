/*
 * Copyright (c) 2016 Hannes Dorfmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hannesdorfmann.adaptercommands.ItemChangedDetector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible to calculate the difference between two lists and returns a list of
 * {@link AdapterCommand} that can be executed to enable RecyclerView animations.
 *
 * <p>
 * <b>This class is not thread safe!</b> If you need a thread safe instance use {@link
 * ThreadSafeDiffCommandsCalculator}
 * </p>
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class DiffCommandsCalculator<T> {

  private final boolean itemRangeInsertedOnFirstDiff;
  private List<T> oldList;
  private final ItemChangedDetector<T> detector;

  /**
   * Default constructor. Uses {@link EntireDataSetChangedCommand} as resulting command on first
   * time {@link #diff(List)}. This can be changed by using {@link #DiffCommandsCalculator(boolean)}
   * constructor
   *
   * @see #DiffCommandsCalculator(boolean)
   * @see #DiffCommandsCalculator(boolean, ItemChangedDetector)
   */
  public DiffCommandsCalculator() {
    this(false, null);
  }

  /**
   * This constructor allows you to specify the resulting command on first time {@link
   * #diff(List)}.
   *
   * @param itemRangeInsertedOnFirstDiff if <b>true</b> {@link ItemRangeInsertedCommand} will be
   * used which cause a RecyclerView item animations. Use <b>false</b> if {@link
   * EntireDataSetChangedCommand} should be used (no RecyclerView item animations).
   */
  public DiffCommandsCalculator(boolean itemRangeInsertedOnFirstDiff) {
    this(itemRangeInsertedOnFirstDiff, null);
  }

  /**
   * Constructs a new instance with an {@link ItemChangedDetector}
   *
   * @param detector that is responsible to determine whether an item has been changed (internal
   * data changed) or not
   */
  public DiffCommandsCalculator(ItemChangedDetector<T> detector) {
    this(false, detector);
  }

  /**
   * Creates a new instance.
   *
   * @param itemRangeInsertedOnFirstDiff if <b>true</b> {@link ItemRangeInsertedCommand} will be
   * used which cause a RecyclerView item animations. Use <b>false</b> if {@link
   * EntireDataSetChangedCommand} should be used (no RecyclerView item animations).
   * @param detector that is responsible to determine whether an item has been changed (internal
   * data changed or not)
   */
  public DiffCommandsCalculator(boolean itemRangeInsertedOnFirstDiff,
      @Nullable ItemChangedDetector<T> detector) {
    this.itemRangeInsertedOnFirstDiff = itemRangeInsertedOnFirstDiff;
    this.detector = detector;
  }

  /**
   * This method calculates the difference between two lists.
   * This method is <b>not thread safe</b>.
   *
   * @param newList The new items that we use to calculate the difference
   * @return List of commands
   * @see ThreadSafeDiffCommandsCalculator
   */
  public List<AdapterCommand> diff(@NonNull List<T> oldList, @NonNull List<T> newList) {
    this.oldList = oldList;
    return diff(newList);
  }

  /**
   * This method calculates the difference of previous list of items and the new list.
   * This method is <b>not thread safe</b>.
   *
   * @param newList The new items that we use to calculate the difference
   * @return List of commands
   * @see ThreadSafeDiffCommandsCalculator
   */
  public List<AdapterCommand> diff(@NonNull List<T> newList) {

    if (newList == null) {
      throw new NullPointerException("newList == null");
    }

    int newSize = newList.size();
    // first time called
    if (oldList == null) {
      oldList = new ArrayList<>();
      oldList.addAll(newList);

      List<AdapterCommand> commands = new ArrayList<>(1);

      if (newSize == 0 || !itemRangeInsertedOnFirstDiff) {
        commands.add(new EntireDataSetChangedCommand());
      } else {
        commands.add(new ItemRangeInsertedCommand(0, newSize));
      }
      return commands;
    }

    // new list empty
    if (newList.isEmpty()) {
      if (oldList.isEmpty()){
        return Collections.emptyList();
      }
      List<AdapterCommand> commands = new ArrayList<>(1);
      commands.add(new ItemRangeRemovedCommand(0, oldList.size()));
      oldList.clear(); // for next call
      return commands;
    }

    List<AdapterCommand> commands = new ArrayList<>(newSize);

    int M = oldList.size();
    int N = newList.size();

    // opt[i][j] = length of LCS of oldList[i..M] and y[j..N]
    int[][] opt = new int[M + 1][N + 1];

    // compute length of LCS and all subproblems via dynamic programming
    for (int i = M - 1; i >= 0; i--) {
      for (int j = N - 1; j >= 0; j--) {
        if (oldList.get(i).equals(newList.get(j))) {
          opt[i][j] = opt[i + 1][j + 1] + 1;
        } else {
          opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
        }
      }
    }

    // LinkedHashMap<T, ItemInsertedCommand> insertCommands = new LinkedHashMap<>();
    // LinkedHashMap<T, ItemRemovedCommand> removeCommands = new LinkedHashMap<>();

    int insertRemoveOffset = 0;
    // recover LCS itself and print out non-matching lines to standard output
    int i = 0, j = 0;
    while (i < M && j < N) {
      T oldItem = oldList.get(i);
      T newItem = newList.get(j);
      if (oldItem.equals(newItem)) {
        if (detector != null && detector.hasChanged(oldItem, newItem)) {
          commands.add(new ItemChangedCommand(j));
        }
        i++;
        j++;
      } else if (opt[i + 1][j] >= opt[i][j + 1]) {
        //T item = oldList.get(i);
        //handleRemoveCommand(item, i + insertRemoveOffset, insertCommands, removeCommands, commands);
        commands.add(new ItemRemovedCommand(i + insertRemoveOffset));
        insertRemoveOffset--;
        i++;
      } else {
        //T item = newList.get(j);
        //handleInsertCommand(item, j, insertCommands, removeCommands, commands);
        commands.add(new ItemInsertedCommand(j));
        insertRemoveOffset++;
        j++;
      }
    }

    // dump out one remainder of one string if the other is exhausted
    while (i < M || j < N) {
      if (i == M) {
        // T item = newList.get(j);
        // handleInsertCommand(item, j, insertCommands, removeCommands, commands);
        commands.add(new ItemInsertedCommand(j));
        insertRemoveOffset++;
        j++;
      } else if (j == N) {
        // T item = oldList.get(i);
        // handleRemoveCommand(item, i + insertRemoveOffset, insertCommands, removeCommands, commands);
        commands.add(new ItemRemovedCommand(i + insertRemoveOffset));
        insertRemoveOffset--;
        i++;
      }
    }

    oldList.clear();
    oldList.addAll(newList);

    // TODO batch commands (see batching branch).
    // TODO move commands (see handleRemoveCommand() methods etc.)

    return commands;
  }

  /*
  private void handleRemoveCommand(T item, int removePosition,
      Map<T, ItemInsertedCommand> insertCommands, Map<T, ItemRemovedCommand> removeCommands,
      List<AdapterCommand> commands) {

    //ItemInsertedCommand iCommand = insertCommands.get(item);
    ItemInsertedCommand iCommand = null;
    if (iCommand != null) {
      ItemMovedCommand mCommand = new ItemMovedCommand(removePosition, iCommand.position);
      commands.remove(iCommand);
      for (int i = 0; i < commands.size(); i++) {
        if (iCommand == commands.get(i)) {
          commands.set(i, mCommand);
          break;
        }
      }
      insertCommands.remove(item);
      Log.d("Items",
          "Alg: Move item (" + item + ") from " + removePosition + " to " + iCommand.position);
    } else {
      ItemRemovedCommand rCommand = new ItemRemovedCommand(removePosition);
      removeCommands.put(item, rCommand);
      commands.add(rCommand);
      Log.d("Items", "Alg: removed item (" + item + ") at position " + removePosition);
    }
  }

  private void handleInsertCommand(T item, int insertPosition,
      Map<T, ItemInsertedCommand> insertCommands, Map<T, ItemRemovedCommand> removeCommands,
      List<AdapterCommand> commands) {

    // ItemRemovedCommand rCommand = removeCommands.get(item);
    ItemRemovedCommand rCommand = null;
    if (rCommand != null) {
      insertCommands.remove(item);
      ItemMovedCommand mCommand = new ItemMovedCommand(rCommand.position, insertPosition);
      for (int i = 0; i < commands.size(); i++) {
        if (rCommand == commands.get(i)) {
          commands.set(i, mCommand);
          break;
        }
      }
      commands.remove(rCommand);
      Log.d("Items",
          "Alg: Move item (" + item + ") from " + rCommand.position + " to " + insertPosition);
    } else {
      ItemInsertedCommand iCommand = new ItemInsertedCommand(insertPosition);
      insertCommands.put(item, iCommand);
      commands.add(iCommand);
      Log.d("Items", "Alg: insert item (" + item + ") at position " + insertPosition);
    }
  }
  */
}
