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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Functions on iterators. */
public final class Iterators {
  private static final class EmptyIterator extends ImmutableIteratorBase {
    @Override public boolean hasNext() {
      return false;
    }

    @Override public Object next() {
      throw new NoSuchElementException();
    }

    @Override public int hashCode() {
      return -1;
    }

    @Override public boolean equals(Object that) {
      return that instanceof EmptyIterator;
    }
  }

  private static final Iterator EMPTY_ITERATOR = new EmptyIterator();

  /** Return an empty iterator. */
  @SuppressWarnings("unchecked")
  public static <A> Iterator<A> empty() {
    return EMPTY_ITERATOR;
  }

  /** Join two iterators into a new immutable iterator. */
  public static <A> Iterator<A> join(final Iterator<? extends A> a, final Iterator<? extends A> b) {
    return new ImmutableIteratorBase<A>() {
      @Override
      public boolean hasNext() {
        return a.hasNext() || b.hasNext();
      }

      @Override
      public A next() {
        return a.hasNext() ? (A) a.next() : (A) b.next();
      }
    };
  }

  /** Concatenate a list of iterators. */
  @SuppressWarnings("unchecked")
  public static <A> Iterator<A> concat(final Iterator<A>... as) {
    return concat(new ImmutableIteratorArrayAdapter<Iterator<A>>(as));
  }

  /** Concatenate a list of iterators. */
  public static <A> Iterator<A> concat(final List<Iterator<A>> as) {
    return concat(as.iterator());
  }

  /** Concatenate a list of iterators. */
  public static <A> Iterator<A> concat(final Iterator<Iterator<A>> as) {
    return new ImmutableIteratorBase<A>() {
      Iterator<A> current;

      @Override public boolean hasNext() {
        if (current == null) {
          if (as.hasNext()) {
            current = as.next();
          } else {
            return false;
          }
        }
        if (!current.hasNext()) {
          current = null;
          return hasNext();
        } else {
          return current.hasNext();
        }
      }

      @Override public A next() {
        if (hasNext()) {
          return current.next();
        } else {
          throw new NoSuchElementException();
        }
      }
    };
  }

  /**
   * Test if both iterators yield the same elements.
   * This method is not defined in {@link com.entwinemedia.fn.Equality} because equality on
   * iterators should not be defined generally. Iterators may be infinite so testing them for equality
   * should be used with care.
   */
  public static <A> boolean eq(Iterator<? extends A> a, Iterator<? extends A> b) {
    while (true) {
      if (a.hasNext() && b.hasNext()) {
        if (Equality.ne(a.next(), b.next())) {
          return false;
        }
      } else return !(a.hasNext() || b.hasNext());
    }
  }
}
