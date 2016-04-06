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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Immutable wrapper for lists.
 * <p/>
 * <em>Attention:</em> The list can still be modified through the wrapped list.
 */
public final class ImmutableListWrapper<A> extends ImmutableListBase<A> {
  private final List<A> wrapped;

  public ImmutableListWrapper(List<A> wrapped) {
    this.wrapped = wrapped;
  }

  /** For testing purposes only. */
  public List<A> getWrapped() {
    return wrapped;
  }

  @Override public int size() {return wrapped.size();}

  @Override public boolean isEmpty() {return wrapped.isEmpty();}

  @Override public boolean contains(Object o) {return wrapped.contains(o);}

  @Override public Iterator<A> iterator() {return new ImmutableIteratorWrapper<A>(wrapped.iterator());}

  @Override public Object[] toArray() {return wrapped.toArray();}

  @Override public <T> T[] toArray(T[] ts) {return wrapped.toArray(ts);}

  @Override public boolean containsAll(Collection<?> objects) {return wrapped.containsAll(objects);}

  @Override public A get(int i) {return wrapped.get(i);}

  @Override public int indexOf(Object o) {return wrapped.indexOf(o);}

  @Override public int lastIndexOf(Object o) {return wrapped.lastIndexOf(o);}

  @Override public ListIterator<A> listIterator() {return new ImmutableListIteratorWrapper<A>(wrapped.listIterator());}

  @Override
  public ListIterator<A> listIterator(int i) {return new ImmutableListIteratorWrapper<A>(wrapped.listIterator(i));}

  @Override public List<A> subList(int i, int i2) {return new ImmutableListWrapper<A>(wrapped.subList(i, i2));}

  @Override public int hashCode() {
    return wrapped.hashCode();
  }

  @Override public boolean equals(Object o) {
    return wrapped.equals(o);
  }

  @Override public String toString() {
    return Util.createToString(this, wrapped);
  }
}
