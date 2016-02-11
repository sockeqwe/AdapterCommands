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

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/**
 * @author Hannes Dorfmann
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

  static class ItemViewHolder extends RecyclerView.ViewHolder {

    public ItemViewHolder(View itemView) {
      super(itemView);
    }
  }

  LayoutInflater inflater;
  List<Item> items;

  int rgb[] = new int[3];
  double rgbRes[] = new double[3];

  public ItemAdapter(Context context, List<Item> items) {
    this.inflater = LayoutInflater.from(context);
    this.items = items;
  }

  @Override public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ItemViewHolder(inflater.inflate(R.layout.item, parent, false));
  }

  @Override public void onBindViewHolder(ItemViewHolder holder, int position) {
    Item item = items.get(position);
    int color = item.color;
    TextView tv = (TextView) holder.itemView;
    tv.setBackgroundColor(color);
    tv.setText(Integer.toString(item.id));
    tv.setTextColor(calculateTextColor(color));
  }

  private int calculateTextColor(int color) {

    rgb[0] =  Color.red(color);
    rgb[1] =  Color.green(color);
    rgb[2] =  Color.blue(color);

    for (int i = 0; i < rgb.length; i++) {
      double c = rgb[i];
      c = c / 255.0;
      if (c <= 0.03928) {
        c = c / 12.92;
      } else {
        c = Math.pow((c + 0.055) / 1.055, 2.4);
      }
      rgbRes[i] = c;
    }

    double luminance = 0.2126 * rgbRes[0] + 0.7152 * rgbRes[1] + 0.0722 * rgbRes[2];

    if (luminance > 0.179) {
      return Color.BLACK;
    } else {
      return Color.WHITE;
    }

  }

  @Override public int getItemCount() {
    return items == null ? 0 : items.size();
  }
}
