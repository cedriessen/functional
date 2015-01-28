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

import java.util.NoSuchElementException;

/**
 * Iterator implementation for lists that provide element access in O(1) constant time like
 * {@link java.util.ArrayList}.
 * <p/>
 * <em>Attention:</em> The iterable can still be modified through the wrapped array.
 */
public final class ImmutableIteratorArrayAdapter<A> extends ImmutableIteratorBase<A> {
  private final A[] array;
  private int index = -1;

  public ImmutableIteratorArrayAdapter(A[] array) {
    this.array = array;
  }

  @Override public boolean hasNext() {
    return index + 1 < array.length;
  }

  @Override public A next() {
    if (hasNext()) {
      return array[++index];
    } else {
      throw new NoSuchElementException();
    }
  }
}
