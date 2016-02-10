package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * This command simply calls {@link RecyclerView.Adapter#notifyItemChanged(int)}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class ItemChangedCommand implements AdapterCommand {

  final int position;

  public ItemChangedCommand(int position) {
    if (position < 0) {
      throw new IllegalArgumentException("position < 0");
    }

    this.position = position;
  }

  @MainThread @Override public void execute(@NonNull RecyclerView.Adapter<?> adapter) {
    adapter.notifyItemChanged(position);
  }

  @Override public String toString() {
    return "ItemChangedCommand{" +
        "position=" + position +
        '}';
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemChangedCommand that = (ItemChangedCommand) o;

    return position == that.position;
  }

  @Override public int hashCode() {
    return position;
  }
}
