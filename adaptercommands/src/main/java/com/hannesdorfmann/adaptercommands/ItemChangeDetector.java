package com.hannesdorfmann.adaptercommands;

/**
 * @author Hannes Dorfmann
 * @since 1.0
 */
public interface ItemChangeDetector<T> {

  public boolean hasChanged(T oldItem, T newItem);
}
