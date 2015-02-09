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

import static com.entwinemedia.fn.Stream.$;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class ListBuilderUtils {
  private ListBuilderUtils() {
  }

  /** Immutable empty list. */
  @SuppressWarnings("unchecked")
  private static final List NIL = new ImmutableListWrapper(Collections.EMPTY_LIST);

  /** Return an immutable empty list. */
  @SuppressWarnings("unchecked")
  public static <A> List<A> nil() {
    return NIL;
  }

  public static <A> List<A> createNew(ListFactory f, A x) {
    final List<A> buf = f.buffer(1);
    buf.add(x);
    return f.toList(buf);
  }

  public static <A> List<A> createNew(ListFactory f, A... as) {
    final List<A> buf = f.buffer(as.length);
    Collections.addAll(buf, as);
    return f.toList(buf);
  }

  public static <A> List<A> createNew(ListFactory f, Collection<? extends A> as) {
    final List<A> buf = f.buffer(as.size());
    buf.addAll(as);
    return f.toList(buf);
  }

  public static <A> List<A> createNew(ListFactory f, final Iterator<? extends A> as) {
    return fillAndFinish(f, f.<A>buffer(), as);
  }

  /** Create a new list from an iterator using a size hint. */
  public static <A> List<A> createNew(ListFactory f, int size, final Iterator<? extends A> as) {
    return fillAndFinish(f, f.<A>buffer(size), as);
  }

  /** Create a new list from an iterable. */
  public static <A> List<A> createNew(ListFactory f, final Iterable<? extends A> as) {
    return createNew(f, as.iterator());
  }

  /** Create a new list from an iterable using a size hint. */
  public static <A> List<A> createNew(ListFactory f, int size, final Iterable<? extends A> as) {
    return createNew(f, size, as.iterator());
  }

  /** Concatenate an array of collections to a new list. */
  @SuppressWarnings("unchecked")
  public static <A> List<A> concatNew(ListFactory f, final Collection<? extends A>... ass) {
    return concatNew2(f, new ImmutableListArrayAdapter(ass));
  }

  /** Concatenate a collection of collections to a new list. */
  public static <A> List<A> concatNew2(ListFactory f, Collection<? extends Collection<A>> ass) {
    final List<A> buf = f.buffer($(ass).apply(Util.sumSizeFold));
    for (Collection<A> as : ass) {
      buf.addAll(as);
    }
    return f.toList(buf);
  }

  /** Concatenate an array of iterables to a new list. */
  @SuppressWarnings("unchecked")
  public static <A> List<A> concat(ListFactory f, Iterable<? extends A>... ass) {
    return concat2(f, new ImmutableIterableArrayAdapter(ass));
  }

  /** Concatenate an iterable of iterables to a new list. */
  public static <A> List<A> concat2(ListFactory f, Iterable<? extends Iterable<A>> ass) {
    final List<A> buf = f.buffer();
    for (Iterable<? extends A> as : ass) {
      for (A a : as) {
        buf.add(a);
      }
    }
    return f.toList(buf);
  }

  // --

  private static <A> List<A> fillAndFinish(ListFactory f, List<A> buf, Iterator<? extends A> as) {
    while (as.hasNext()) {
      buf.add(as.next());
    }
    return f.toList(buf);
  }
}
