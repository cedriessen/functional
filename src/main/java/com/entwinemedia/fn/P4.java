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

import static com.entwinemedia.fn.Equality.hash;

/** Product of arity 4. */
public abstract class P4<A, B, C, D> {
  public abstract A _1();
  public abstract B _2();
  public abstract C _3();
  public abstract D _4();


  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof P4 && eqFields((P4) that));
  }

  public boolean canEqual(Object that) {
    return that instanceof P4;
  }

  private boolean eqFields(P4 that) {
    return that.canEqual(this)
            && Equality.eq(_1(), that._1()) && Equality.eq(_2(), that._2()) && Equality.eq(_3(), that._3()) && Equality.eq(_4(), that._4());
  }

  @Override public int hashCode() {
    return Equality.hash(_1(), _2(), _3(), _4());
  }

  @Override public String toString() {
    return "(" + _1() + "," + _2() + "," + _3() + "," + _4() + ")";
  }
}
