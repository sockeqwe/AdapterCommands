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

package com.hannesdorfmann.adaptercommands.example;

/**
 * @author Hannes Dorfmann
 */
public class Item {

  int id;
  int color;

  public Item(int id, int color) {
    this.id = id;
    this.color = color;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Item item = (Item) o;

    return id == item.id;
  }

  @Override public int hashCode() {
    return id;
  }

  @Override public String toString() {
    return Integer.toString(id);
  }

  public Item copy() {
    return new Item(id, color);
  }
}
