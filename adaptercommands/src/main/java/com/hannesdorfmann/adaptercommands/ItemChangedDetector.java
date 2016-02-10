package com.hannesdorfmann.adaptercommands;

/**
 * Responsible to determine whether an item has been changed or not.
 *
 * @author Hannes Dorfmann
 * @since 1.0
 */
public interface ItemChangedDetector<T> {

  /**
   * Determinew whether an item has been changed or not by comparing the oldItem with the newItem
   *
   * @param oldItem The old item
   * @param newItem the new item
   * @return true if changed, otherwise false
   */
  public boolean hasChanged(T oldItem, T newItem);
}
