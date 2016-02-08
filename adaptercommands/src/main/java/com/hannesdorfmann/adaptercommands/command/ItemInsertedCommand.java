package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * This command simply calls {@link RecyclerView.Adapter#notifyItemInserted(int)}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class ItemInsertedCommand implements AdapterCommand {

  final int position;

  public ItemInsertedCommand(int position) {
    if (position < 0) {
      throw new IllegalArgumentException("position < 0");
    }

    this.position = position;
  }

  @MainThread @Override public void execute(@NonNull RecyclerView.Adapter<?> adapter) {
    adapter.notifyItemInserted(position);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemInsertedCommand that = (ItemInsertedCommand) o;

    return position == that.position;
  }

  @Override public int hashCode() {
    return position;
  }

  @Override public String toString() {
    return "ItemInsertedCommand{" +
        "position=" + position +
        '}';
  }
}
