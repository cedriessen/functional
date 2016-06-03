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

/** Product of arity 3. */
public final class P3<A, B, C> {
  public final A _1;
  public final B _2;
  public final C _3;

  public P3(A _1, B _2, C _3) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
  }

  public static <A, B, C> P3<A, B, C> p3(A _1, B _2, C _3) {
    return new P3<>(_1, _2, _3);
  }

  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof P3 && eqFields((P3) that));
  }

  private boolean eqFields(P3 that) {
    return eq(_1, that._1)
        && eq(_2, that._2)
        && eq(_3, that._3);
  }

  @Override public int hashCode() {
    return hash(_1, _2, _3);
  }

  @Override public String toString() {
    return "{" + _1 + "," + _2 + "," + _3 + "}";
  }
}
