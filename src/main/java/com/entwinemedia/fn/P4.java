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

/** Product of arity 4. */
public final class P4<A, B, C, D> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;

  public P4(A _1, B _2, C _3, D _4) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
  }

  public static <A, B, C, D> P4<A, B, C, D> p4(A _1, B _2, C _3, D _4) {
    return new P4<>(_1, _2, _3, _4);
  }

  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof P4 && eqFields((P4) that));
  }

  private boolean eqFields(P4 that) {
    return eq(_1, that._1)
        && eq(_2, that._2)
        && eq(_3, that._3)
        && eq(_4, that._4);
  }

  @Override public int hashCode() {
    return hash(_1, _2, _3, _4);
  }

  @Override public String toString() {
    return "(" + _1 + "," + _2 + "," + _3 + "," + _4 + ")";
  }
}
