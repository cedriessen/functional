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

import com.entwinemedia.fn.Equality;
import com.entwinemedia.fn.Stream;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Makes an array available as an immutable list.
 * <p/>
 * <em>Attention:</em> The list can still be modified through the wrapped array.
 */
public final class ImmutableListArrayAdapter<A> extends ImmutableListBase<A> {
  private final A[] array;

  public ImmutableListArrayAdapter(A[] array) {
    this.array = array;
  }

  public static <A> ImmutableListArrayAdapter<A> mk(A... array) {
    return new ImmutableListArrayAdapter<A>(array);
  }

  @Override public int size() {
    return array.length;
  }

  @Override public boolean isEmpty() {
    return size() == 0;
  }

  @Override public boolean contains(Object o) {
    for (A a : array) {
      if (Equality.eq(a, o))
        return true;
    }
    return false;
  }

  @Override public Iterator<A> iterator() {
    return ImmutableIterators.mk(this);
  }

  @Override public Object[] toArray() {
    return array;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] ts) {
    if (ts.getClass().getComponentType().isAssignableFrom(array.getClass().getComponentType())) {
      return (T[]) array;
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
    return array[i];
  }

  @Override public int indexOf(Object o) {
    int i = 0;
    for (A a : array) {
      if (Equality.eq(a, o))
        return i;
      i++;
    }
    return -1;
  }

  @Override public int lastIndexOf(Object o) {
    for (int i = size() - 1; i >= 0; i--) {
      if (Equality.eq(array[i], o))
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
    return new ImmutableListArraySliceAdapter<A>(array, from, to);
  }

  @Override public int hashCode() {
    return Equality.hash(array);
  }

  @Override public boolean equals(Object that) {
    return that instanceof List && eqElems((List) that);
  }

  private boolean eqElems(List that) {
    if (size() == that.size()) {
      final Iterator it = that.iterator();
      for (A a : array) {
        if (Equality.ne(a, it.next())) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override public String toString() {
    if (array.length < 100) {
      return Stream.<Object>$(array).inject(",").wrap(getClass().getSimpleName() + "[", "]").mkString();
    } else {
      return super.toString();
    }
  }
}
