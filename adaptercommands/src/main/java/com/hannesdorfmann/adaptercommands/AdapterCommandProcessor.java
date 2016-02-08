package com.hannesdorfmann.adaptercommands;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import com.hannesdorfmann.adaptercommands.command.AdapterCommand;
import java.util.List;

/**
 * Executes a list of {@link AdapterCommand}s by calling {@link #execute(List)}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class AdapterCommandProcessor {

  private final RecyclerView.Adapter<?> adapter;

  public AdapterCommandProcessor(@NonNull RecyclerView.Adapter<?> adapter) {
    if (adapter == null) {
      throw new NullPointerException("adapter == null");
    }

    this.adapter = adapter;
  }

  /**
   * Executes the passed list of adapter commands by calling {@link AdapterCommand#execute(RecyclerView.Adapter)}
   * on each command in the list
   *
   * @param commands The comands to execute
   */
  @MainThread public void execute(@NonNull List<AdapterCommand> commands) {
    if (commands == null) {
      throw new NullPointerException("commands == null");
    }
    for (int i = 0; i < commands.size(); i++) {
      commands.get(i).execute(adapter);
    }
  }
}
