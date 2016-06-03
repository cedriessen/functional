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

import static com.entwinemedia.fn.Equality.eq;
import static com.entwinemedia.fn.Equality.hash;

/** Product of arity 1. */
public final class P1<A> {
  public final A _1;

  public P1(A _1) {
    this._1 = _1;
  }

  public static <A> P1<A> p(A _1) {
    return new P1<>(_1);
  }

  public A get1() {
    return _1;
  }

  public <B> P1<B> fmap(final Fn<? super A, ? extends B> f) {
    return p(f.apply(_1));
  }

  public <B> P1<B> bind(final Fn<? super A, P1<B>> f) {
    return f.apply(_1);
  }

  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof P1 && eqFields((P1) that));
  }

  private boolean eqFields(P1 that) {
    return eq(_1, that._1);
  }

  @Override public int hashCode() {
    return hash(_1);
  }

  @Override public String toString() {
    return "(" + _1 + ")";
  }
}
