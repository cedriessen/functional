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

import java.util.ListIterator;

/**
 * Immutable wrapper for list iterators.
 * <p/>
 * <em>Attention:</em> The iterator can still be modified through the wrapped iterator.
 */
public class ImmutableListIteratorWrapper<A> extends ImmutableListIteratorBase<A> {
  private final ListIterator<A> wrapped;

  public ImmutableListIteratorWrapper(ListIterator<A> wrapped) {
    this.wrapped = wrapped;
  }

  @Override public boolean hasNext() {
    return wrapped.hasNext();
  }

  @Override public A next() {
    return wrapped.next();
  }

  @Override public boolean hasPrevious() {return wrapped.hasPrevious();}

  @Override public A previous() {return wrapped.previous();}

  @Override public int nextIndex() {return wrapped.nextIndex();}

  @Override public int previousIndex() {return wrapped.previousIndex();}
}
