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
