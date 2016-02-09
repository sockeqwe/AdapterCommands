package com.hannesdorfmann.adaptercommands;

import android.support.annotation.NonNull;
import android.util.Log;
import com.hannesdorfmann.adaptercommands.command.AdapterCommand;
import com.hannesdorfmann.adaptercommands.command.EntireDataSetChangedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemChangedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemInsertedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemRangeChangedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemRangeInsertedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemRangeRemovedCommand;
import com.hannesdorfmann.adaptercommands.command.ItemRemovedCommand;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible to calculate the difference between two lists and returns a list of
 * {@link AdapterCommand} that can be executed to enable RecyclerView animations
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class CommandsCalculator<T extends List> {

  // TODO considering using guavas BiMap
  private T oldList;

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
  public List<AdapterCommand> calculateDiff(@NonNull T newList, ItemChangeDetector<T> detector) {

    int newSize = newList.size();
    // first time called
    if (oldList == null) {
      oldList = (T) new ArrayList<>();
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

    int inserRemoveOffset = 0;
    // recover LCS itself and print out non-matching lines to standard output
    int i = 0, j = 0;
    while (i < M && j < N) {
      if (oldList.get(i).equals(newList.get(j))) {
        i++;
        j++;
      } else if (opt[i + 1][j] >= opt[i][j + 1]) {
        commands.add(new ItemRemovedCommand(i + inserRemoveOffset));
        Log.d("Items", "Alg: removed item at " + (i));
        inserRemoveOffset--;
        i++;
      } else {
        commands.add(new ItemInsertedCommand(j));
        Log.d("Items", "Alg: inserted item at " + (j));
        inserRemoveOffset++;
        j++;
      }
    }

    // dump out one remainder of one string if the other is exhausted
    while (i < M || j < N) {
      if (i == M) {
        commands.add(new ItemInsertedCommand(j));
        inserRemoveOffset++;
        Log.d("Items", "Alg: inserted item  at " + (j));
        j++;
      } else if (j == N) {
        commands.add(new ItemRemovedCommand(i+inserRemoveOffset));
        inserRemoveOffset--;
        Log.d("Items", "Alg: removed item at " + (i));
        i++;
      }
    }

    oldList.clear();
    oldList.addAll(newList);

    // batch commands
    for (int k = 0; k < commands.size(); k++) {

    }

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
