package com.hannesdorfmann.adaptercommands.command;

import android.support.annotation.MainThread;
import android.support.v7.widget.RecyclerView;
import com.hannesdorfmann.adaptercommands.AdapterCommandProcessor;

/**
 * This interface represents a Command. This command will be executed by a {@link
 * AdapterCommandProcessor} to notify the adapter about changes in the dataset and to kick in the
 * item animator by invoking corresponding adapters notify() method (like notifyItemInsterted())
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public interface AdapterCommand {

  /**
   * Executes this command by calling the corresponding method on the given {@link
   * RecyclerView.Adapter}
   *
   * @param adapter The adapter
   */
  @MainThread public void execute(RecyclerView.Adapter<?> adapter);
}
