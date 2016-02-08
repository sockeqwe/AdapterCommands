package com.hannesdorfmann.adaptercommands.command;

import com.hannesdorfmann.adaptercommands.CommandsCalculator;
import com.hannesdorfmann.adaptercommands.ItemChangeDetector;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class CommandsCalculatorTest {

  static class Item {
    public String id;
    public String value;

    public Item(String id) {
      this(id, id);
    }

    public Item(String id, String value) {
      this.id = id;
      this.value = value;
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Item item = (Item) o;

      return !(id != null ? !id.equals(item.id) : item.id != null);
    }

    @Override public int hashCode() {
      return id != null ? id.hashCode() : 0;
    }

    @Override public String toString() {
      return id;
    }
  }

  static class Detector implements ItemChangeDetector<Item> {
    @Override public boolean hasChanged(Item oldItem, Item newItem) {
      return !oldItem.value.equals(newItem.value);
    }
  }

  private CommandsCalculator calculator;

  @Before public void init() {
    calculator = new CommandsCalculator();
  }

  @Test public void firstTime() {

    List<Item> newItems = newList("a", "b");
    List<AdapterCommand> commands = calculator.calculateDiff(newItems, null);

    Assert.assertEquals(1, commands.size());
    Assert.assertTrue(commands.get(0) instanceof EntireDataSetChangedCommand);
  }

  @Test public void insertAndChange() {

    // Warmup
    ItemChangeDetector<Item> changeDetector = new Detector();
    List<Item> items = newList("a", "b", "c");
    calculator.calculateDiff(items, changeDetector);

    //
    // Test insert
    //
    items.add(1, new Item("a2"));
    items.add(3, new Item("b2"));
    items.add(4, new Item("b3"));
    items.add(5, new Item("b4"));
    items.add(new Item("c2"));

    List<AdapterCommand> commands = calculator.calculateDiff(items, changeDetector);
    Assert.assertEquals(3, commands.size());

    ItemInsertedCommand insertedA = (ItemInsertedCommand) commands.get(0);
    Assert.assertEquals(insertedA.position, 1);

    ItemRangeInsertedCommand insertB = (ItemRangeInsertedCommand) commands.get(1);
    Assert.assertEquals(insertB.startPosition, 3);
    Assert.assertEquals(insertB.itemCount, 3);

    ItemInsertedCommand insertedC = (ItemInsertedCommand) commands.get(2);
    Assert.assertEquals(insertedC.position, 7);

    //
    // Test changes
    //
    items.set(0, new Item("a", "newValueA"));
    items.set(2, new Item("b", "newValueB"));
    items.set(3, new Item("b2", "newValueB2"));
    items.set(4, new Item("b3", "newValueB3"));
    items.set(7, new Item("c2", "newValuec2"));

    commands = calculator.calculateDiff(items, changeDetector);
    Assert.assertEquals(3, commands.size());

    ItemChangedCommand changedA = (ItemChangedCommand) commands.get(0);
    Assert.assertEquals(0, changedA.position);

    ItemRangeChangedCommand changedB = (ItemRangeChangedCommand) commands.get(1);
    Assert.assertEquals(2, changedB.startPosition);
    Assert.assertEquals(3, changedB.itemCount);

    ItemChangedCommand changeC = (ItemChangedCommand) commands.get(2);
    Assert.assertEquals(7, changeC.position);
  }

  @Test public void move() {

    // Warmup
    ItemChangeDetector<Item> changeDetector = new Detector();
    List<Item> items = newList("a", "b", "c");
    calculator.calculateDiff(items, changeDetector);

    //
    // Move elements
    //
    Item a = items.remove(0);
    items.add(1, a);

    List<AdapterCommand> commands = calculator.calculateDiff(items, changeDetector);
    Assert.assertEquals(2, commands.size());

    checkContainsCommand(commands, new ItemMovedCommand(1, 0));
    checkContainsCommand(commands, new ItemMovedCommand(0, 1));
  }

  @Test public void moveFirstToEnd() {
    // Warmup
    ItemChangeDetector<Item> changeDetector = new Detector();
    List<Item> items = newList("a", "b", "c");
    calculator.calculateDiff(items, changeDetector);

    // Move to the end

    Item first = items.remove(0);
    items.add(first);

    List<AdapterCommand> commands = calculator.calculateDiff(items, changeDetector);
    Assert.assertEquals(3, commands.size());

    checkContainsCommand(commands, new ItemMovedCommand(0, 2));
    checkContainsCommand(commands, new ItemMovedCommand(1, 0));
    checkContainsCommand(commands, new ItemMovedCommand(2, 1));
  }

  private <T extends AdapterCommand> T checkContainsCommand(List<AdapterCommand> commands,
      T equalsCommand) {

    for (AdapterCommand c : commands) {
      if (c.equals(equalsCommand)) {
        return (T) c;
      }
    }

    Assert.fail("Expected command " + equalsCommand + " but not found");
    return null;
  }

  private List<Item> newList(String... items) {
    ArrayList<Item> list = new ArrayList<>();
    for (String item : items) {
      list.add(new Item(item));
    }
    return list;
  }
}
