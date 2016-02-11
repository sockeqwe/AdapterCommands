/*
 * Copyright (c) 2016 Hannes Dorfmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
