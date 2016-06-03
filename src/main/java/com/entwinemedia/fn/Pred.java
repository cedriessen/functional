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

package com.entwinemedia.fn;

import com.entwinemedia.fn.fns.Booleans;

/**
 * Predicate function.
 */
public abstract class Pred<A> extends Fn<A, Boolean> {
  /** Convert a function into a predicate. */
  public static <A> Pred<A> mk(final Fn<A, Boolean> f) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return f.apply(a);
      }
    };
  }

  public Pred<A> or(final Fn<? super A, Boolean> f) {
    return Booleans.or(this, f);
  }

  public Pred<A> and(final Fn<? super A, Boolean> f) {
    return Booleans.and(this, f);
  }

  public Pred<A> not() {
    return Booleans.not(this);
  }
}
