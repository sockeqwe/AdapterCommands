package com.hannesdorfmann.adaptercommands.command;

import com.hannesdorfmann.adaptercommands.ItemChangedDetector;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Hannes Dorfmann
 */
public class DiffCommandsCalculatorTest {

  private DiffCommandsCalculator<Item> calculator;

  @Before public void init() {
    calculator = new DiffCommandsCalculator<Item>();
  }

  @Test public void firstTime() {

    List<Item> newItems = newList("a", "b");
    List<AdapterCommand> commands = calculator.diff(newItems, null);

    Assert.assertEquals(1, commands.size());
    Assert.assertTrue(commands.get(0) instanceof EntireDataSetChangedCommand);
  }

  @Test public void firstTimeItemRangeInserted() {
    DiffCommandsCalculator<Item> calculator = new DiffCommandsCalculator<>(true);
    List<Item> newItems = newList("a", "b");
    List<AdapterCommand> commands = calculator.diff(newItems, null);

    Assert.assertEquals(1, commands.size());
    assertContainCommand(commands, new ItemRangeInsertedCommand(0, 2));
  }

  @Test public void firstTimeEmptyList() {
    List<Item> newItems = new ArrayList<>();
    List<AdapterCommand> commands = calculator.diff(newItems, null);

    Assert.assertEquals(1, commands.size());
    Assert.assertTrue(commands.get(0) instanceof EntireDataSetChangedCommand);
  }

  @Test public void nullList() {
    try {
      calculator.diff(null);
      Assert.fail("NullPointerException expected");
    } catch (NullPointerException e) {
      Assert.assertEquals("newList == null", e.getMessage());
    }
  }

  @Test public void insertAndChange() {

    // Warmup
    ItemChangedDetector<Item> changeDetector = new Detector();
    List<Item> items = newList("a", "b", "c");
    calculator.diff(items, changeDetector);

    //
    // Test insert
    //
    items.add(1, new Item("a2"));
    items.add(3, new Item("b2"));
    items.add(4, new Item("b3"));
    items.add(5, new Item("b4"));
    items.add(new Item("c2"));

    List<AdapterCommand> commands = calculator.diff(items, changeDetector);
    Assert.assertEquals(5, commands.size());

    assertContainCommand(commands, new ItemInsertedCommand(1));
    assertContainCommand(commands, new ItemInsertedCommand(3));
    assertContainCommand(commands, new ItemInsertedCommand(4));
    assertContainCommand(commands, new ItemInsertedCommand(5));
    assertContainCommand(commands, new ItemInsertedCommand(7));

    //
    // Test changes
    //

    // Changes without detector
    items.set(0, new Item("a", "newValueA"));
    items.set(2, new Item("b", "newValueB"));
    items.set(3, new Item("b2", "newValueB2"));
    items.set(4, new Item("b3", "newValueB3"));
    items.set(7, new Item("c2", "newValuec2"));

    commands = calculator.diff(items);
    Assert.assertTrue(commands.isEmpty());

    // Changes with detector
    items.set(0, new Item("a", "newOtherValueA"));
    items.set(2, new Item("b", "newOtherValueB"));
    items.set(3, new Item("b2", "newOtherValueB2"));
    items.set(4, new Item("b3", "newOtherValueB3"));
    items.set(7, new Item("c2", "newOtherValuec2"));

    commands = calculator.diff(items, changeDetector);

    Assert.assertEquals(5, commands.size());
    assertContainCommand(commands, new ItemChangedCommand(0));
    assertContainCommand(commands, new ItemChangedCommand(2));
    assertContainCommand(commands, new ItemChangedCommand(3));
    assertContainCommand(commands, new ItemChangedCommand(4));
    assertContainCommand(commands, new ItemChangedCommand(7));
  }

  @Test public void move() {

    // Warmup
    ItemChangedDetector<Item> changeDetector = new Detector();
    List<Item> items = newList("a", "b", "c", "d", "e");
    calculator.diff(items, changeDetector);

    //
    // Move elements
    //
    Item a = items.remove(0);
    items.add(3, a);
    Item c = items.remove(1);
    items.add(4, c);

    List<AdapterCommand> commands = calculator.diff(items, changeDetector);

    // TODO update once move commands are implemented
    Assert.assertEquals(4, commands.size());
    assertContainCommand(commands, new ItemRemovedCommand(0));
    assertContainCommand(commands, new ItemInsertedCommand(2));
    assertContainCommand(commands, new ItemRemovedCommand(1));
    assertContainCommand(commands, new ItemInsertedCommand(4));
  }

  @Test
  public void remove(){
    // Warmup
    ItemChangedDetector<Item> changeDetector = new Detector();
    List<Item> items = newList("a", "b", "c", "d", "e", "f");
    calculator.diff(items);


    items.remove(1);
    items.remove(3);

    List<AdapterCommand> commands = calculator.diff(items);
    Assert.assertEquals(2, commands.size());
    assertContainCommand(commands, new ItemRemovedCommand(1));
    assertContainCommand(commands, new ItemRemovedCommand(3));
  }

  private List<Item> newList(String... items) {
    ArrayList<Item> list = new ArrayList<>();
    for (String item : items) {
      list.add(new Item(item));
    }
    return list;
  }

  private <T extends AdapterCommand> T assertContainCommand(List<AdapterCommand> commands,
      T equalsCommand) {

    for (AdapterCommand c : commands) {
      if (c.equals(equalsCommand)) {
        return (T) c;
      }
    }

    Assert.fail("Expected command " + equalsCommand + " but not found");
    return null;
  }


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

  static class Detector implements ItemChangedDetector<Item> {
    @Override public boolean hasChanged(Item oldItem, Item newItem) {
      return !oldItem.value.equals(newItem.value);
    }
  }
}
