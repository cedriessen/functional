/*
 * Copyright 2015 Entwine AG, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.entwinemedia.fn.data;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * List iterator implementation for lists that provide element access in O(1) constant time like
 * {@link java.util.ArrayList}.
 * <p/>
 * <em>Attention:</em> The iterable can still be modified through the wrapped list.
 */
public final class ImmutableListIteratorListAdapter<A> extends ImmutableListIteratorBase<A> {
  private final List<A> list;
  private int index;

  public ImmutableListIteratorListAdapter(List<A> list, int index) {
    if (index < -1 || index >= list.size()) {
      throw new IndexOutOfBoundsException();
    }
    this.list = list;
    this.index = index;
  }

  public static <A> ImmutableListIteratorListAdapter<A> mk(List<A> list) {
    return new ImmutableListIteratorListAdapter<A>(list, -1);
  }

  /**
   * @throws java.lang.IndexOutOfBoundsException
   */
  public static <A> ImmutableListIteratorListAdapter<A> mk(List<A> list, int index) {
    return new ImmutableListIteratorListAdapter<A>(list, index);
  }

  @Override public boolean hasNext() {
    return index + 1 < list.size();
  }

  @Override public A next() {
    if (hasNext()) {
      return list.get(++index);
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override public boolean hasPrevious() {
    return index > 0;
  }

  @Override public A previous() {
    if (hasPrevious()) {
      return list.get(--index);
    } else {
      throw new NoSuchElementException();
    }
  }

  @Override public int nextIndex() {
    return Math.min(index + 1, list.size());
  }

  @Override public int previousIndex() {
    return Math.max(index - 1, -1);
  }
}
