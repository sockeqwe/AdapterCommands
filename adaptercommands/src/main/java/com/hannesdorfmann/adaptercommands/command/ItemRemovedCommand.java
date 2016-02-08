package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * This command simply calls {@link RecyclerView.Adapter#notifyItemRemoved(int)} (int)}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class ItemRemovedCommand implements AdapterCommand {

  final int position;

  public ItemRemovedCommand(int position) {
    if (position < 0) {
      throw new IllegalArgumentException("position < 0");
    }

    this.position = position;
  }

  @MainThread @Override public void execute(@NonNull RecyclerView.Adapter<?> adapter) {
    adapter.notifyItemRemoved(position);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemRemovedCommand that = (ItemRemovedCommand) o;

    return position == that.position;
  }

  @Override public int hashCode() {
    return position;
  }

  @Override public String toString() {
    return "ItemRemovedCommand{" +
        "position=" + position +
        '}';
  }
}
