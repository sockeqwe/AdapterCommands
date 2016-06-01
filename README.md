#AdapterCommands
Drop in solution to animate RecyclerView's dataset changes by using the `command pattern` for adapters with **not stable ids**.
Read my [blog post](http://hannesdorfmann.com/android/adapter-commands) for more information.

Keep in mind that the runtime of `DiffCommandsCalculator` is `O(n*m)` (n = number of items in old list, m = number of items in new list).
So you better run this on a background thread if your data set contains many items.

##Dependencies

```groovy
compile 'com.hannesdorfmann.adaptercommands:adaptercommands:1.0.3'
```

## How to use
There are basically 2 components:
  - `DiffCommandsCalculator` that calculates the difference from previous data set to the new data set and returns `List<AdapterCommand>`. Please note that `DiffCommandsCalculator` is **not thread safe**. If you need a thread safe instance use `ThreadSafeDiffCommandsCalculator`.
  - `AdapterCommandProcessor` takes `List<AdapterCommand>` and executes each command to trigger RecyclerView's `ItemAnimator` to run animations.

```java
public class MainActivity extends AppCompatActivity {

  @Bind(R.id.recyclerView) RecyclerView recyclerView;

  List<Item> items = new ArrayList<Item>();
  Random random = new Random();
  ItemAdapter adapter; // RecyclerView adapter
  AdapterCommandProcessor commandProcessor;
  DiffCommandsCalculator<Item> commandsCalculator = new DiffCommandsCalculator<Item>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    adapter = new ItemAdapter(this, items);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

    commandProcessor = new AdapterCommandProcessor(adapter);
  }

  // Called when new items should be displayed in RecyclerView
  public void setItems(List<Item> newItems){
    adapter.setItems(newItems);
    List<AdapterCommand> commands = commandsCalculator.diff(newItems);
    commandProcessor.execute(commands); // executes commands that triggers animations
  }
```

## MVP
Best practise is to use a `PresentationModel` and `Model-View-Presenter`. See  my [blog post](http://hannesdorfmann.com/android/adapter-commands) for a concrete example.

## Customization
 - comparing items
 `DiffCommandsCalculator` uses standard java's `equals()` method to compare two items (one from old list, one from new list).
  So you have to override `equals()` and `hashCode()` in your model class (use IDE to generate that):
  ```java
public class Item {

  int id;
  String text;

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Item item = (Item) o;

    return id == item.id;
  }

  @Override public int hashCode() {
    return id;
  }
 ```
 As you might have noticed, we only use `Item.id` for equals. The reason is that if we have this item in old list `item { id = 1, text ="Foo"}` and the same item with the same id in the new list `{item { id = 1, text ="other"}` we just want to compare this items by the id.
 What here has happened was that the `item.text` has been changed from new list to old list, but we still compare two items just by `item.id` since this is the property we use in `equals()`.

 However, when a `item.text` has been changed, we have to detect this too because we have to call `adapter.notifyItemChanged(position)` (`ItemChangedCommand`).
 So we can provide an `ItemChangedDetector` that we can pass as constructor argument to `DiffCommandsCalculator`:

 ```java
class MyItemChangedDetector implements ItemChangedDetector<Item>() {
    @Override public boolean hasChanged(Item oldItem, Item newItem) {
      return !oldItem.text.equals(newItem.text);
    }
};
 ```
 and then use it like this:
 ```java
DiffCommandsCalculator<Item> calculator = new DiffCommandsCalculator<>(new MyItemChangedDetector());
 ```

 - We also can specify what exactly should happen on the first time we use `DiffCommandsCalculator` (there is no old list to compare to).
 In this case we either could call `adapter.notifyDatasetChanged()` (`EntireDatasetChangedCommand`) which is the default behaviour or `adapter.notifyItemRangeInserted(0, items.size())` (`ItemRangeInsertedCommand`) which then will run `ItemAnimator` so that items will animate in.
 You can specify the behaviour as constructor parameter `DiffCommandsCalculator(boolean itemRangeInsertedOnFirstDiff)`: `new DiffCommandsCalculator(false)` uses `EntireDatasetChangedCommand` (no animations, equivalent to `new DiffCommandsCalculator()`) whereas `new DiffCommandsCalculator(true)` uses `ItemRangeInsertedCommand` (animations).


## License
```
Copyright 2016 Hannes Dorfmann

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
