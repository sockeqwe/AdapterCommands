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
