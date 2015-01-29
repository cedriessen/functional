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

public final class Iterables {
  public static <A> Iterable<A> join(final Iterable<? extends A> a, final Iterable<? extends A> b) {
    return new Iterable<A>() {
      @Override public Iterator<A> iterator() {
        return Iterators.join(a.iterator(), b.iterator());
      }
    };
  }

  /** Create a string by concatenating all elements of <code>as</code> by <code>sep</code>. */
  public static String mkString(Iterable<?> as, String sep) {
    final StringBuilder b = new StringBuilder();
    for (Iterator<?> i = as.iterator(); i.hasNext();) {
      b.append(i.next());
      if (i.hasNext()) {
        b.append(sep);
      }
    }
    return b.toString();
  }

  /**
   * Make a string from an iterable separating each element by <code>sep</code>.
   * The string is surrounded by <code>pre</code> and <code>post</code>.
   */
  public static String mkString(Iterable<?> as, String sep, String pre, String post) {
    return pre + mkString(as, sep) + post;
  }

  /** Return an iterator as an iterable to make it usable in a for comprehension. */
  public static <A> Iterable<A> asIterable(final Iterator<A> i) {
    return new Iterable<A>() {
      @Override public Iterator<A> iterator() {
        return i;
      }
    };
  }

  /** @see Iterators#eq(java.util.Iterator, java.util.Iterator) */
  public static <A> boolean eq(Iterable<? extends A> a, Iterable<? extends A> b) {
    return Iterators.eq(a.iterator(), b.iterator());
  }
}
