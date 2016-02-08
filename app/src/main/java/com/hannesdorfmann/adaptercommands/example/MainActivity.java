package com.hannesdorfmann.adaptercommands.example;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.hannesdorfmann.adaptercommands.AdapterCommandProcessor;
import com.hannesdorfmann.adaptercommands.CommandsCalculator;
import com.hannesdorfmann.adaptercommands.ItemChangeDetector;
import com.hannesdorfmann.adaptercommands.command.AdapterCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
    implements SwipeRefreshLayout.OnRefreshListener {

  @Bind(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;
  @Bind(R.id.recyclerView) RecyclerView recyclerView;
  @BindInt(R.integer.columns) int columns;
  @BindColor(R.color.amber) int amber;
  @BindColor(R.color.green) int green;
  @BindColor(R.color.purple) int purple;

  Runnable refreshRunnable = new Runnable() {
    @Override public void run() {
      for (int i = 0; i < columns - 1; i++) {
        items.add(i, new Item(id(), randomColor()));
      }
      refreshLayout.setRefreshing(false);
      updateAdapter();
    }
  };

  List<Item> items = new ArrayList<Item>();
  Random random = new Random();
  int lastId = 0;
  ItemAdapter adapter;
  AdapterCommandProcessor commandProcessor;
  CommandsCalculator commandsCalculator = new CommandsCalculator();

  ItemChangeDetector<Item> changeDetector = new ItemChangeDetector<Item>() {
    @Override public boolean hasChanged(Item oldItem, Item newItem) {
      return oldItem.color != newItem.color;
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    refreshLayout.setOnRefreshListener(this);

    adapter = new ItemAdapter(this, items);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new GridLayoutManager(this, columns));

    commandProcessor = new AdapterCommandProcessor(adapter);

    // initial items
    items.add(new Item(id(), amber));
    items.add(new Item(id(), green));
    items.add(new Item(id(), purple));
    updateAdapter();
  }

  @OnClick(R.id.add) public void addClicked() {

    int addCount = random.nextInt(3) + 1;

    for (int i = 0; i < addCount; i++) {
      int position = random.nextInt(items.size());
      Item item = new Item(id(), randomColor());
      items.add(position, item);
      Log.d("Items", "added Item(" + item + ") at position" + position);
    }

    updateAdapter();
  }

  @OnClick(R.id.remove) public void removeClicked() {

  }

  @OnClick(R.id.move) public void moveClicked() {
    Item i = items.remove(0);
    items.add(i);
    Log.d("Items", "Moved Item(" + i + ") from position 0 to " + (items.size() - 1));
    updateAdapter();
  }

  @OnClick(R.id.change) public void changeClicked() {
    int changeCount = random.nextInt(3) + 1;
    for (int i = 0; i < changeCount; i++) {
      int position = random.nextInt(items.size());
      Item item = items.get(position).copy();
      item.color = randomColor();
      items.set(position, item);
      Log.d("Items", "changed Item(" + item + ") at position" + position);
    }

    updateAdapter();
  }

  @OnClick(R.id.notifyChanged) public void notifyChangedClicked() {
    adapter.notifyDataSetChanged();
  }

  @OnClick(R.id.clear) public void clearClicked() {
    items.clear();
    updateAdapter();
  }

  private void updateAdapter() {

    //adapter.notifyDataSetChanged();
    List<AdapterCommand> commands = commandsCalculator.calculateDiff(items, changeDetector);
    commandProcessor.execute(commands);
  }

  private int id() {
    return lastId++;
  }

  @ColorInt private int randomColor() {
    return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
  }

  @Override public void onRefresh() {
    refreshLayout.postDelayed(refreshRunnable, 1500);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    refreshLayout.removeCallbacks(refreshRunnable);
  }
}
