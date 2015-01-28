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
import java.util.Set;

/**
 * Immutable wrapper for sets.
 * <p/>
 * <em>Attention:</em> The set can still be modified through the wrapped set.
 */
public final class ImmutableSetWrapper<A> implements Set<A>, Immutable {
  private final Set<A> wrapped;

  public ImmutableSetWrapper(Set<A> wrapped) {
    this.wrapped = wrapped;
  }

  @Override public int size() {return wrapped.size();}

  @Override public boolean isEmpty() {return wrapped.isEmpty();}

  @Override public boolean contains(Object o) {return wrapped.contains(o);}

  @Override public Iterator<A> iterator() {return new ImmutableIteratorWrapper<A>(wrapped.iterator());}

  @Override public Object[] toArray() {return wrapped.toArray();}

  @Override public <T> T[] toArray(T[] ts) {return wrapped.toArray(ts);}

  public boolean add(A a) {throw new UnsupportedOperationException();}

  @Override public boolean remove(Object o) {throw new UnsupportedOperationException();}

  @Override public boolean containsAll(Collection<?> objects) {return wrapped.containsAll(objects);}

  public boolean addAll(Collection<? extends A> as) {throw new UnsupportedOperationException();}

  @Override public boolean removeAll(Collection<?> objects) {throw new UnsupportedOperationException();}

  @Override public boolean retainAll(Collection<?> objects) {throw new UnsupportedOperationException();}

  @Override public void clear() {throw new UnsupportedOperationException();}

  @Override public boolean equals(Object o) {return wrapped.equals(o);}

  @Override public int hashCode() {return wrapped.hashCode();}
}
