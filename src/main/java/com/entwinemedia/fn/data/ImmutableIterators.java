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
import java.util.List;
import java.util.NoSuchElementException;

/** Factory for immutable iterators. */
public final class ImmutableIterators {
  private ImmutableIterators() {
  }

  public static <A> Iterator<A> mk(Iterator<? extends A> wrapped) {
    return new ImmutableIteratorWrapper<A>(wrapped);
  }

  public static <A> Iterator<A> mk(List<? extends A> list) {
    return new ImmutableIteratorListAdapter<A>(list);
  }

  public static <A> Iterator<A> mk(A[] array) {
    return new ImmutableIteratorArrayAdapter<A>(array);
  }

  public static <A> Iterator<A> mk(final A a) {
    return new ImmutableIteratorBase<A>() {
      private boolean hasNext = true;

      @Override public boolean hasNext() {
        return hasNext;
      }

      @Override public A next() {
        if (hasNext) {
          hasNext = false;
          return a;
        } else {
          throw new NoSuchElementException();
        }
      }
    };
  }
}
