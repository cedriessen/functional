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

public final class Variance {
  private Variance() {
  }

  /** Covary an iterator. */
  @SuppressWarnings("unchecked")
  public static <A> Iterator<A> vy(Iterator<? extends A> a) {
    return (Iterator<A>) a;
  }

  /** Covary an iterator. */
  @SuppressWarnings("unchecked")
  public static <A> Iterator<A> vy(Class<A> t, Iterator<? extends A> a) {
    return (Iterator<A>) a;
  }

  /** Covary a collection. */
  @SuppressWarnings("unchecked")
  public static <A> Collection<A> vy(Collection<? extends A> a) {
    return (Collection<A>) a;
  }

  /** Covary a collection. */
  @SuppressWarnings("unchecked")
  public static <A> Collection<A> vy(Class<A> t, Collection<? extends A> a) {
    return (Collection<A>) a;
  }
}
