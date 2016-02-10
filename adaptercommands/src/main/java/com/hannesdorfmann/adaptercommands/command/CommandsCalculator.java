package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.NonNull;
import com.hannesdorfmann.adaptercommands.ItemChangeDetector;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to calculate the difference between two lists and returns a list of
 * {@link AdapterCommand} that can be executed to enable RecyclerView animations
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class CommandsCalculator<T> {

  // TODO considering using guavas BiMap
  private List<T> oldList;

  private int insertStartIndex = -1;
  private int insertLastIndex = -1;
  private int changedStartIndex = -1;
  private int changedLastIndex = -1;

  /**
   * This method calculates the difference of previous list of items and the new list.
   * This method is <b>not thread safe</b>
   *
   * @param newList The new items that we use to calculate the difference
   * @param detector that is responsible to determine whether an item has been changed (internal
   * data changed or not)
   * @return List of commands
   */
  public List<AdapterCommand> calculateDiff(@NonNull List<T> newList,
      ItemChangeDetector<T> detector) {

    int newSize = newList.size();
    // first time called
    if (oldList == null) {
      oldList = new ArrayList<>();
      oldList.addAll(newList);

      List<AdapterCommand> commands = new ArrayList<>(1);
      commands.add(new EntireDataSetChangedCommand());
      return commands;
    }

    // new list empty
    if (newList.isEmpty()) {
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

    // batch commands

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
