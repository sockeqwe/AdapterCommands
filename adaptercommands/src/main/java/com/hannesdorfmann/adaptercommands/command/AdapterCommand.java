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
