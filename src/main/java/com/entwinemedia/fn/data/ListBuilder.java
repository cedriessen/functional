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

public interface ListBuilder {
  /** Return an empty list. */
  <A> List<A> nil();

  /** Return an empty list. */
  <A> List<A> nil(Class<A> t);

  /** Make a single element list. */
  <A> List<A> mk(A a);

  /** Make a list from an array. */
  <A> List<A> mk(A... xs);

  /** Make a list from a collection. */
  <A> List<A> mk(Collection<? extends A> xs);

  /** Make a list from an iterator. */
  <A> List<A> mk(Iterator<? extends A> xs);

  /** Make a list from an iterable. */
  <A> List<A> mk(Iterable<? extends A> xs);

  /** Make a list from an iterator and a size hint. */
  <A> List<A> mk(int size, Iterator<? extends A> xs);

  /** Make a list from an iterable and a size hint. */
  <A> List<A> mk(int size, Iterable<? extends A> xs);

  /** Concat a collection of collections. [[x]] -> [x] */
  <A> List<A> concat(Collection<? extends Collection<A>> xss);

  /** Concat an array of collections. [[x]] -> [x] */
  <A> List<A> concat2(Collection<? extends A>... xss);

  /** Concat an iterable of iterables. [[x]] -> [x] */
  <A> List<A> concat(Iterable<? extends Iterable<A>> xss);

  /** Concat an array of iterables. [[x]] -> [x] */
  <A> List<A> concat2(Iterable<? extends A>... xss);
}
