package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * This command simply calls {@link RecyclerView.Adapter#notifyItemRangeChanged(int, int)}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class ItemRangeChangedCommand implements AdapterCommand {

  final int startPosition;
  final int itemCount;

  public ItemRangeChangedCommand(int startPosition, int itemCount) {
    if (startPosition < 0) {
      throw new IllegalArgumentException("startPosition < 0");
    }

    if (itemCount <= 0) {
      throw new IllegalArgumentException("itemCount <= 0");
    }

    this.startPosition = startPosition;
    this.itemCount = itemCount;
  }

  @MainThread @Override public void execute(@NonNull RecyclerView.Adapter<?> adapter) {
    adapter.notifyItemRangeChanged(startPosition, itemCount);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemRangeChangedCommand that = (ItemRangeChangedCommand) o;

    if (startPosition != that.startPosition) return false;
    return itemCount == that.itemCount;
  }

  @Override public int hashCode() {
    int result = startPosition;
    result = 31 * result + itemCount;
    return result;
  }

  @Override public String toString() {
    return "ItemRangeChangedCommand{" +
        "startPosition=" + startPosition +
        ", itemCount=" + itemCount +
        '}';
  }
}
