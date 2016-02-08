package com.hannesdorfmann.adaptercommands;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import com.hannesdorfmann.adaptercommands.command.AdapterCommand;
import com.hannesdorfmann.adaptercommands.command.EntireDataSetChangedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemChangedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemInsertedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemMovedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemRangeChangedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemRangeInsertedCommand;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to calculate the difference between two lists and returns a list of
 * {@link AdapterCommand} that can be executed to enable RecyclerView animations
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class CommandsCalculator {

  // TODO considering using guavas BiMap
  private ArrayMap<Object, Integer> oldPositions;

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
  public <T> List<AdapterCommand> calculateDiff(@NonNull List<T> newList,
      ItemChangeDetector<T> detector) {

    int newSize = newList.size();
    // first time called
    if (oldPositions == null) {
      oldPositions = new ArrayMap<>(newSize);

      for (int i = 0; i < newSize; i++) {
        oldPositions.put(newList.get(i), i);
      }

      List<AdapterCommand> commands = new ArrayList<>(1);
      commands.add(new EntireDataSetChangedCommand());
      return commands;
    }


    insertStartIndex = -1;
    insertLastIndex = -1;
    changedStartIndex = -1;
    changedLastIndex = -1;
    int insertCount = 0;

    List<AdapterCommand> commands = new ArrayList<>(newSize);
    ArrayMap<Object, Integer> newPositions = new ArrayMap<>(newSize);

    for (int i = 0; i < newSize; i++) {
      Object newItem = newList.get(i);
      newPositions.put(newItem, i);

      // Search if position has changed
      Integer oldPositionWrapper = oldPositions.get(newItem);
      if (oldPositionWrapper == null) {

        // Any ChangeCommands left?
        addChangeCommandsIfNeeded(commands);

        // Not in the previous list
        if (insertStartIndex == -1) {
          insertStartIndex = i;
        }
        insertLastIndex = i;
      } else {
        // unbox int wrapper
        int oldPos = oldPositionWrapper;

        // Any Insert-Commands left?
        insertCount += addInsertCommandsIfNeeded(commands);

        // Has an item changed?
        if (oldPos == i) {
          // Item changed?
          if (detector != null) {
            int keyIndex = oldPositions.indexOfKey(newItem);
            Object oldItem = oldPositions.keyAt(keyIndex);
            if (detector.hasChanged((T) oldItem, (T) newItem)) {
              if (changedStartIndex == -1) {
                changedStartIndex = i;
              }
              changedLastIndex = i;
            } else {
              addChangeCommandsIfNeeded(commands);
            }
          }
        } else {
          addChangeCommandsIfNeeded(commands);

          // Item moved
          int beforeInsertsPosition = i - insertCount;
          if (beforeInsertsPosition != oldPos) {
            commands.add(new ItemMovedCommand(oldPos, i));
          }
        }
      }
    }

    //
    // Are some operations left that we haven't transformed to commands yet?
    //
    addInsertCommandsIfNeeded(commands);
    addChangeCommandsIfNeeded(commands);

    // TODO remove commands

    oldPositions = newPositions;
    return commands;
  }

  /**
   * Add {@link ItemInsertedCommand} or {@link ItemRangeInsertedCommand} if necessary
   *
   * @param commands the list of commands where we will add the new command
   */
  private int addInsertCommandsIfNeeded(List<AdapterCommand> commands) {
    if (insertStartIndex != -1) {
      if (insertStartIndex == insertLastIndex) {
        // Previous was just one single insert and not a range
        commands.add(new ItemInsertedCommand(insertLastIndex));
        insertStartIndex = -1;
        insertLastIndex = -1;
        return 1;
      } else {
        commands.add(
            new ItemRangeInsertedCommand(insertStartIndex, insertLastIndex - insertStartIndex + 1));
      }
      int inserted = insertLastIndex - insertStartIndex + 1;
      insertStartIndex = -1;
      insertLastIndex = -1;
      return inserted;
    }

    return 0;
  }

  /**
   * Add {@link ItemChangedCommand} or  {@link ItemRangeChangedCommand} if necessary
   *
   * @param commands the list of commands where we will add the new command
   */
  private void addChangeCommandsIfNeeded(List<AdapterCommand> commands) {
    if (changedStartIndex != -1) {
      if (changedStartIndex == changedLastIndex) {
        commands.add(new ItemChangedCommand(changedLastIndex));
      } else {
        commands.add(new ItemRangeChangedCommand(changedStartIndex,
            changedLastIndex - changedStartIndex + 1));
      }

      changedLastIndex = -1;
      changedStartIndex = -1;
    }
  }
}
