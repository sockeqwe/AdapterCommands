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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hannesdorfmann.adaptercommands.ItemChangedDetector;

import java.util.List;

/**
 * This class is responsible to calculate the difference between two lists and returns a list of
 * {@link AdapterCommand} that can be executed to enable RecyclerView animations.
 *
 * @author Hannes Dorfmann
 * @since 1.0.2
 */
public class ThreadSafeDiffCommandsCalculator<T> extends DiffCommandsCalculator<T> {

    public ThreadSafeDiffCommandsCalculator() {
        super();
    }

    public ThreadSafeDiffCommandsCalculator(boolean itemRangeInsertedOnFirstDiff) {
        super(itemRangeInsertedOnFirstDiff);
    }

    public ThreadSafeDiffCommandsCalculator(ItemChangedDetector<T> detector) {
        super(detector);
    }

    public ThreadSafeDiffCommandsCalculator(boolean itemRangeInsertedOnFirstDiff,
                                            @Nullable ItemChangedDetector<T> detector) {
        super(itemRangeInsertedOnFirstDiff, detector);
    }

    @Override
    public synchronized List<AdapterCommand> diff(@NonNull List<T> oldList, @NonNull List<T> newList) {
        return super.diff(oldList, newList);
    }

    /**
     * This method calculates the difference of previous list of items and the new list.
     * This call is thread safe
     *
     * @param newList The new items that we use to calculate the difference
     * @return List of commands
     */
    @Override
    public synchronized List<AdapterCommand> diff(@NonNull List<T> newList) {
        return super.diff(newList);
    }
}
