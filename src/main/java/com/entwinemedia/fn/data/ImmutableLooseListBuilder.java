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
import java.util.List;

/**
 * Implementation of list builder which tries to only use wrappers as far as possible.
 * This safes memory since lists are not copied but has the risk of creating lists that
 * are still mutable through the wrapped data structure. Use only if it can be guaranteed that the source
 * data structure will not be mutated.
 */
public final class ImmutableLooseListBuilder extends ListBuilderBase {
  public ImmutableLooseListBuilder(ListFactory f) {
    super(f);
  }

  /** Wrap an array into a list. */
  @Override public <A> List<A> mk(A... xs) {
    return new ImmutableListArrayAdapter<A>(xs);
  }

  /**
   * Return the collection as is if it is an {@link Immutable} list. Wrap the collection into an
   * immutable list wrapper if <code>xs</code> is a list. Create a new immutable list otherwise.
   */
  @Override
  @SuppressWarnings("unchecked")
  public <A> List<A> mk(Collection<? extends A> xs) {
    if (xs instanceof List) {
      if (xs instanceof Immutable) {
        return (List<A>) xs;
      } else {
        return new ImmutableListWrapper<A>((List<A>) xs);
      }
    } else {
      return mk(xs.size(), xs.iterator());
    }
  }

  @Override public <A> List<A> mk(final Iterator<? extends A> xs) {
    return ListBuilderUtils.createNew(f, xs);
  }

  @Override public <A> List<A> mk(int size, final Iterator<? extends A> xs) {
    return ListBuilderUtils.createNew(f, size, xs);
  }

  /**
   * Return the collection as is if it is an {@link Immutable} list. Wrap the collection into an
   * immutable list wrapper if <code>xs</code> is a list. Create a new immutable list otherwise.
   */
  @Override
  @SuppressWarnings("unchecked")
  public <A> List<A> mk(final Iterable<? extends A> xs) {
    if (xs instanceof Collection) {
      return mk((Collection) xs);
    } else {
      return mk(xs.iterator());
    }
  }

  /**
   * Return the collection as is if it is an {@link Immutable} list. Wrap the collection into an
   * immutable list wrapper if <code>xs</code> is a list. Create a new immutable list otherwise.
   */
  @Override
  @SuppressWarnings("unchecked")
  public <A> List<A> mk(int size, final Iterable<? extends A> xs) {
    if (xs instanceof Collection) {
      return mk((Collection) xs);
    } else {
      return mk(size, xs.iterator());
    }
  }
}
