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

import java.util.Iterator;

/**
 * Immutable wrapper for iterables.
 * <p/>
 * <em>Attention:</em> The iterable can still be modified through the wrapped iterable.
 */
public final class ImmutableIterableWrapper<A> implements Iterable<A>, Immutable {
  private final Iterable<A> wrapped;

  public ImmutableIterableWrapper(Iterable<A> wrapped) {
    this.wrapped = wrapped;
  }

  @Override public Iterator<A> iterator() {
    return new ImmutableIteratorWrapper<>(wrapped.iterator());
  }

  @Override public int hashCode() {
    return wrapped.hashCode();
  }

  @Override public boolean equals(Object obj) {
    return wrapped.equals(obj);
  }
}
