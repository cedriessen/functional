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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static com.entwinemedia.fn.Equality.eq;

import org.junit.Test;

public class P2Test {
  @Test
  public void testEquality1() {
    final P2<Integer, String> x = Products.E.p2(1, "2");
    final P2<Integer, String> y = new P2<Integer, String>() {
      @Override public Integer _1() {
        return 1;
      }

      @Override public String _2() {
        return "2";
      }
    };
    final P2<Integer, String> z = Products.E.p2(1, "2");
    // reflexive
    assertEquals(x, x);
    assertEquals(x, Products.E.p2(1, "2"));
    // symmetric
    assertEquals(x, y);
    assertEquals(y, x);
    // transitive
    assertEquals(x, y);
    assertEquals(y, z);
    assertEquals(x, z);
    // consistent
    for (int i = 0; i < 10; i++) {
      assertEquals(x, y);
    }
  }

  @Test
  public void testEquality2() {
    final P2<Integer, String> x = new ExtendedP2<Integer, String>(1, "2", "hint");
    final P2<Integer, String> y = Products.E.p2(1, "2");
    // reflexive
    assertEquals(x, x);
    assertEquals(x, new ExtendedP2<Integer, String>(1, "2", "hint"));
    // symmetric
    assertNotEquals(y, x);
    assertNotEquals(x, y);
  }

  static final class ExtendedP2<A, B> extends P2<A, B> {
    private A _1;
    private B _2;
    private B hint;

    ExtendedP2(A _1, B _2, B hint) {
      this._1 = _1;
      this._2 = _2;
      this.hint = hint;
    }

    @Override public A _1() {
      return _1;
    }

    @Override public B _2() {
      return _2;
    }

    public B getHint() {
      return hint;
    }

    @Override public boolean equals(Object that) {
      return (this == that) || (that instanceof ExtendedP2 && eqFields((ExtendedP2) that) && super.equals(that));
    }

    @Override public boolean canEqual(Object that) {
      return that instanceof ExtendedP2;
    }

    private boolean eqFields(ExtendedP2 that) {
      return eq(hint, that.hint);
    }
  }
}
