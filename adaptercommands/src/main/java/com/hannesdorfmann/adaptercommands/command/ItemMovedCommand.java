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
 * This command simply calls {@link RecyclerView.Adapter#notifyItemMoved(int, int)}
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public class ItemMovedCommand implements AdapterCommand {

  final int fromPosition;
  final int toPosition;

  public ItemMovedCommand(int fromPosition, int toPosition) {
    if (fromPosition < 0) {
      throw new IllegalArgumentException("fromPosition < 0");
    }
    if (toPosition < 0) {
      throw new IllegalArgumentException("toPosition < 0");
    }

    this.fromPosition = fromPosition;
    this.toPosition = toPosition;
  }

  @MainThread @Override public void execute(@NonNull RecyclerView.Adapter<?> adapter) {
    adapter.notifyItemMoved(fromPosition, toPosition);
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ItemMovedCommand that = (ItemMovedCommand) o;

    if (fromPosition != that.fromPosition) return false;
    return toPosition == that.toPosition;
  }

  @Override public int hashCode() {
    int result = fromPosition;
    result = 31 * result + toPosition;
    return result;
  }

  @Override public String toString() {
    return "ItemMovedCommand{" +
        "fromPosition=" + fromPosition +
        ", toPosition=" + toPosition +
        '}';
  }
}
