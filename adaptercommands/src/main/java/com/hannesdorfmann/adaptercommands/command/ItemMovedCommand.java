package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * This command simply calls {@link RecyclerView.Adapter#notifyItemMoved(int, int)}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class ItemMovedCommand implements AdapterCommand {

  final int fromPosition;
  final int toPosition;

  public ItemMovedCommand(int fromPosition, int toPosition) {
    if (fromPosition < 0) {
      throw new IllegalArgumentException("fromPosition < 0");
    }
    if (toPosition < 0) {
      throw new IllegalArgumentException("toPosition < 0");
    }

    this.fromPosition = fromPosition;
    this.toPosition = toPosition;
  }

  @MainThread @Override public void execute(@NonNull RecyclerView.Adapter<?> adapter) {
    adapter.notifyItemMoved(fromPosition, toPosition);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemMovedCommand that = (ItemMovedCommand) o;

    if (fromPosition != that.fromPosition) return false;
    return toPosition == that.toPosition;
  }

  @Override public int hashCode() {
    int result = fromPosition;
    result = 31 * result + toPosition;
    return result;
  }

  @Override public String toString() {
    return "ItemMovedCommand{" +
        "fromPosition=" + fromPosition +
        ", toPosition=" + toPosition +
        '}';
  }
}
