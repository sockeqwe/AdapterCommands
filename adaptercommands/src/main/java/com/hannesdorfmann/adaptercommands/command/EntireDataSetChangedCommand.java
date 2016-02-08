package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * This command simply calls {@link RecyclerView.Adapter#notifyDataSetChanged()}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class EntireDataSetChangedCommand implements AdapterCommand {

  @MainThread @Override public void execute(@NonNull RecyclerView.Adapter<?> adapter) {
    adapter.notifyDataSetChanged();
  }

  @Override public String toString() {
    return "EntireDataSetChangedCommand{}";
  }
}
