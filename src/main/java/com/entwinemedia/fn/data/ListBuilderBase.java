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
import java.util.List;

/**
 * Implementation of list builder which only wraps the underlying data structures into an immutable list as far
 * as possible.
 */
public abstract class ListBuilderBase implements ListBuilder {
  protected final ListFactory f;

  protected ListBuilderBase(ListFactory f) {
    this.f = f;
  }

  @Override public <A> List<A> nil() {
    return f.nil();
  }

  @Override public <A> List<A> nil(Class<A> t) {
    return f.nil();
  }

  @Override public <A> List<A> mk(A a) {
    return ListBuilderUtils.createNew(f, a);
  }

  /** Concatenate an array of collections to a new list. */
  @Override public <A> List<A> concat2(final Collection<? extends A>... xss) {
    return ListBuilderUtils.concat(f, xss);
  }

  /** Concatenate a collection of collections to a new list. */
  @Override public <A> List<A> concat(Collection<? extends Collection<A>> xss) {
    return ListBuilderUtils.concat2(f, xss);
  }

  /** Concatenate an array of iterables to a new list. */
  @Override public <A> List<A> concat2(Iterable<? extends A>... xss) {
    return ListBuilderUtils.concat(f, xss);
  }

  /** Concatenate an iterable of iterables to a new list. */
  @Override public <A> List<A> concat(Iterable<? extends Iterable<A>> xss) {
    return ListBuilderUtils.concat2(f, xss);
  }
}
