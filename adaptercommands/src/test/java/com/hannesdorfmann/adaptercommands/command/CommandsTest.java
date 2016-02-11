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

import android.support.v7.widget.RecyclerView;
import com.hannesdorfmann.adaptercommands.AdapterCommandProcessor;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Hannes Dorfmann
 */
@RunWith(PowerMockRunner.class) @PrepareForTest(RecyclerView.Adapter.class)
public class CommandsTest {

  private AdapterCommandProcessor processor;
  private RecyclerView.Adapter adapter;
  private List<AdapterCommand> commands = new ArrayList<>();

  @Before public void init() {
    adapter = PowerMockito.mock(RecyclerView.Adapter.class);
    processor = new AdapterCommandProcessor(adapter);
    commands.clear();
  }

  @Test public void entireDatasetChangedCommand() {
    commands.add(new EntireDataSetChangedCommand());
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyDataSetChanged();
  }

  @Test public void itemChanged() {
    commands.add(new ItemChangedCommand(1));
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyItemChanged(1);
  }

  @Test public void itemInserted() {
    commands.add(new ItemInsertedCommand(1));
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyItemInserted(1);
  }

  @Test public void itemMoved() {
    commands.add(new ItemMovedCommand(1, 2));
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyItemMoved(Mockito.eq(1), Mockito.eq(2));
  }


  @Test public void itemRemoved() {
    commands.add(new ItemRemovedCommand(1));
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyItemRemoved(1);
  }

  @Test public void itemRangeChanged() {
    commands.add(new ItemRangeChangedCommand(1, 2));
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyItemRangeChanged(Mockito.eq(1), Mockito.eq(2));
  }

  @Test public void itemRangeInserted() {
    commands.add(new ItemRangeInsertedCommand(1, 2));
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyItemRangeInserted(Mockito.eq(1), Mockito.eq(2));
  }

  @Test public void itemRangeRemoved() {
    commands.add(new ItemRangeRemovedCommand(1, 2));
    processor.execute(commands);
    Mockito.verify(adapter, Mockito.only()).notifyItemRangeRemoved(Mockito.eq(1), Mockito.eq(2));
  }
}
