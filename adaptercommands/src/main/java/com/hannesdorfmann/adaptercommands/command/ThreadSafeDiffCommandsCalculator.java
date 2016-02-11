package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.hannesdorfmann.adaptercommands.ItemChangedDetector;
import java.util.List;

/**
 * This class is responsible to calculate the difference between two lists and returns a list of
 * {@link AdapterCommand} that can be executed to enable RecyclerView animations.
 *
 * @author Hannes Dorfmann
 * @since 1.0.2
 */
public class ThreadSafeDiffCommandsCalculator<T> extends DiffCommandsCalculator<T> {

  public ThreadSafeDiffCommandsCalculator() {
    super();
  }

  public ThreadSafeDiffCommandsCalculator(boolean itemRangeInsertedOnFirstDiff) {
    super(itemRangeInsertedOnFirstDiff);
  }

  public ThreadSafeDiffCommandsCalculator(ItemChangedDetector<T> detector) {
    super(detector);
  }

  public ThreadSafeDiffCommandsCalculator(boolean itemRangeInsertedOnFirstDiff,
      @Nullable ItemChangedDetector<T> detector) {
    super(itemRangeInsertedOnFirstDiff, detector);
  }

  /**
   * This method calculates the difference of previous list of items and the new list.
   * This call is thread safe
   *
   * @param newList The new items that we use to calculate the difference
   * @return List of commands
   */
  @Override public synchronized List<AdapterCommand> diff(@NonNull List<T> newList) {
    return super.diff(newList);
  }
}
