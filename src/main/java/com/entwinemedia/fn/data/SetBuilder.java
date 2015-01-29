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

import com.entwinemedia.fn.Stream;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Set builder.
 * todo follow the ListBuilder pattern
 */
public abstract class SetBuilder {
  public abstract <A> Set<A> empty();

  protected abstract <A> Set<A> finish(Set<A> buf);

  protected abstract <A> Set<A> create(int length);

  protected abstract <A> Set<A> create();

  /** Create a new set from an array of elements. */
  public <A> Set<A> _(A... as) {
    final Set<A> buf = create(as.length);
    Collections.addAll(buf, as);
    return finish(buf);
  }

  /**
   * Create a new set from a collection of elements.
   * <p/>
   * An immutable set is simply returned as is.
   * To force the creation of a new set use {@link #force(java.util.Collection)}.
   */
  @SuppressWarnings("unchecked")
  public <A> Set<A> _(Collection<? extends A> as) {
    if (as instanceof Set && as instanceof Immutable) {
      return (Set<A>) as;
    } else {
      return force(as);
    }
  }

  /** Create a new set from an iterator. */
  public <A> Set<A> _(final Iterator<? extends A> as) {
    return fillAndFinish(this.<A>create(), as);
  }

  /** Create a new set from an iterator using a size hint. */
  public <A> Set<A> _(int size, final Iterator<? extends A> as) {
    return fillAndFinish(this.<A>create(size), as);
  }

  private <A> Set<A> fillAndFinish(Set<A> buf, Iterator<? extends A> as) {
    while (as.hasNext()) {
      buf.add(as.next());
    }
    return finish(buf);
  }

  /** Create a new set from an iterable. */
  public <A> Set<A> _(final Iterable<? extends A> as) {
    return _(as.iterator());
  }

  /** Create a new set from an iterable using a size hint. */
  public <A> Set<A> _(int size, final Iterable<? extends A> as) {
    return _(size, as.iterator());
  }

  /** Force the creation of a new set from <code>as</code>. */
  public <A> Set<A> force(Collection<? extends A> as) {
    return _(as.size(), as.iterator());
  }

  /** Concatenate a set of collections into a new list. */
  public <A> Set<A> concat(final Collection<? extends A>... ass) {
    final Set<A> buf = create(Stream.$(ass)._(Util.sumSizeFold));
    for (Collection<? extends A> as : ass) {
      buf.addAll(as);
    }
    return finish(buf);
  }

  /** Concatenate a collection of collections into a new set. */
  public <A> Set<A> concat(Collection<Collection<A>> ass) {
    final Set<A> buf = create(Stream.$(ass)._(Util.sumSizeFold));
    for (Collection<A> as : ass) {
      buf.addAll(as);
    }
    return finish(buf);
  }

  /** Concatenate a list of iterables into a new set. */
  public <A> Set<A> concat(Iterable<? extends A>... ass) {
    final Set<A> buf = create();
    for (Iterable<? extends A> as : ass) {
      for (A a : as) {
        buf.add(a);
      }
    }
    return finish(buf);
  }

  /** Return an empty set. */
  public <A> Set<A> empty(Class<A> t) {
    return empty();
  }
}
