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

import com.entwinemedia.fn.Stream;

import java.util.Collection;
import java.util.Iterator;

/**
 * Immutable wrapper for collections.
 * <p/>
 * <em>Attention:</em> The collection can still be modified through the wrapped collection.
 */
public final class ImmutableCollectionWrapper<A> implements Collection<A>, Immutable {
  private final Collection<A> wrapped;

  public ImmutableCollectionWrapper(Collection<A> wrapped) {
    this.wrapped = wrapped;
  }

  /** For testing purposes only. */
  public Collection<A> getWrapped() {
    return wrapped;
  }

  @Override public int size() {return wrapped.size();}

  @Override public boolean isEmpty() {return wrapped.isEmpty();}

  @Override public boolean contains(Object o) {return wrapped.contains(o);}

  @Override public Iterator<A> iterator() {return new ImmutableIteratorWrapper<A>(wrapped.iterator());}

  @Override public Object[] toArray() {return wrapped.toArray();}

  @Override public <T> T[] toArray(T[] ts) {return wrapped.toArray(ts);}

  @Override public boolean containsAll(Collection<?> objects) {return wrapped.containsAll(objects);}

  @Override public boolean add(A a) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean addAll(Collection<? extends A> as) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean removeAll(Collection<?> objects) {
    throw new UnsupportedOperationException();
  }

  @Override public boolean retainAll(Collection<?> objects) {
    throw new UnsupportedOperationException();
  }

  @Override public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override public int hashCode() {
    return wrapped.hashCode();
  }

  @Override public boolean equals(Object o) {
    return wrapped.equals(o);
  }

  @Override public String toString() {
    if (wrapped.size() < 100) {
      return Stream.<Object>$(wrapped).inject(",").wrap("ImmutableCollectionWrapper(", ")").mkString();
    } else {
      return super.toString();
    }
  }
}
