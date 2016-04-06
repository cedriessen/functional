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
import java.util.Map;
import java.util.Set;

/**
 * Immutable wrapper for maps.
 * <p/>
 * <em>Attention:</em> The maps can still be modified through the wrapped map.
 */
public final class ImmutableMapWrapper<A, B> implements Map<A, B>, Immutable {
  private final Map<A, B> wrapped;

  public ImmutableMapWrapper(Map<A, B> wrapped) {
    this.wrapped = wrapped;
  }

  /** For testing purposes only. */
  public Map<A, B> getWrapped() {
    return wrapped;
  }

  @Override public int size() {
    return wrapped.size();
  }

  @Override public boolean isEmpty() {
    return wrapped.isEmpty();
  }

  @Override public boolean containsKey(Object o) {
    return wrapped.containsKey(o);
  }

  @Override public boolean containsValue(Object o) {
    return wrapped.containsValue(o);
  }

  @Override public B get(Object o) {
    return wrapped.get(o);
  }

  @Override public Set<A> keySet() {
    return new ImmutableSetWrapper<A>(wrapped.keySet());
  }

  @Override public Collection<B> values() {
    return new ImmutableCollectionWrapper<B>(wrapped.values());
  }

  @Override public Set<Entry<A, B>> entrySet() {
    return new ImmutableSetWrapper<Entry<A, B>>(wrapped.entrySet());
  }

  @Override public B put(A a, B b) {
    throw new UnsupportedOperationException();
  }

  @Override public B remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override public void putAll(Map<? extends A, ? extends B> map) {
    throw new UnsupportedOperationException();
  }

  @Override public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override public int hashCode() {
    return wrapped.hashCode();
  }

  @Override public boolean equals(Object obj) {
    return wrapped.equals(obj);
  }

  @Override public String toString() {
    return Util.createToString(this, wrapped);
  }
}
