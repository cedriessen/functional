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

/** Product of arity 2. */
public final class P2<A, B> {
  public final A _1;
  public final B _2;

  public P2(A _1, B _2) {
    this._1 = _1;
    this._2 = _2;
  }

  public static <A, B> P2<A, B> p2(A _1, B _2) {
    return new P2<>(_1, _2);
  }

  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof P2 && eqFields((P2) that));
  }

  private boolean eqFields(P2 that) {
    return eq(_1, that._1)
        && eq(_2, that._2);
  }

  @Override public int hashCode() {
    return hash(_1, _2);
  }

  @Override public String toString() {
    return "(" + _1 + "," + _2 + ")";
  }
}
