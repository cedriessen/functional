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

import static com.entwinemedia.fn.Equality.eq;
import static com.entwinemedia.fn.Equality.ne;

import com.entwinemedia.fn.Equality;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImmutableListArraySliceAdapter<A> extends ImmutableListBase<A> {
  private final A[] array;
  private final int start, end;

  public ImmutableListArraySliceAdapter(A[] array, int start, int end) {
    if (start < 0 || end > array.length || start > end) {
      throw new IndexOutOfBoundsException();
    }
    this.array = array;
    this.start = start;
    this.end = end;
  }

  @Override public int size() {
    return end - start;
  }

  @Override public boolean isEmpty() {
    return size() == 0;
  }

  @Override public boolean contains(Object o) {
    return indexOf(o) != -1;
  }

  @Override public Iterator<A> iterator() {
    return ImmutableIterators.mk(this);
  }

  @Override public Object[] toArray() {
    final Object[] target = new Object[size()];
    System.arraycopy(array, start, target, 0, size());
    return target;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] ts) {
    if (ts.getClass().getComponentType().isAssignableFrom(array.getClass().getComponentType())) {
      return (T[]) toArray();
    } else {
      throw new ArrayStoreException();
    }
  }

  @Override public boolean containsAll(Collection<?> objects) {
    for (Object o : objects) {
      if (!contains(o))
        return false;
    }
    return true;
  }

  @Override public A get(int i) {
    try {
      return array[start + i];
    } catch (ArrayIndexOutOfBoundsException ignore) {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override public int indexOf(Object o) {
    for (int i = start; i < end; i++) {
      if (eq(array[i], o))
        return i - start;
    }
    return -1;
  }

  @Override public int lastIndexOf(Object o) {
    for (int i = size() - 1; i >= 0; i--) {
      if (eq(array[i], o))
        return i;
    }
    return -1;
  }

  @Override public ListIterator<A> listIterator() {
    return ImmutableListIteratorListAdapter.mk(this);
  }

  @Override public ListIterator<A> listIterator(int i) {
    return ImmutableListIteratorListAdapter.mk(this, i);
  }

  @Override public List<A> subList(int from, int to) {
    return new ImmutableListArraySliceAdapter<A>(array, start + from, start + to);
  }

  @Override public int hashCode() {
    return Equality.hash(this);
  }

  @Override public boolean equals(Object that) {
    return that instanceof List && eqElems((List) that);
  }

  private boolean eqElems(List that) {
    if (size() == that.size()) {
      final Iterator it = that.iterator();
      for (A a : this) {
        if (ne(a, it.next())) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }
}
