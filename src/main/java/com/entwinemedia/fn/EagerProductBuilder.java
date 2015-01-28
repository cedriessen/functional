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

public class EagerProductBuilder implements ProductBuilder {
  @Override public <A> P1<A> p1(A a) {
    return new P1Eager<A>(a);
  }

  @Override public <A, B> P2<A, B> p2(A a, B b) {
    return new P2Eager<A, B>(a, b);
  }

  @Override public <A, B, C> P3<A, B, C> p3(final A a, final B b, final C c) {
    return new P3<A, B, C>() {
      @Override public A get1() {
        return a;
      }

      @Override public B get2() {
        return b;
      }

      @Override public C get3() {
        return c;
      }
    };
  }

  @Override public <A, B, C, D> P4<A, B, C, D> p4(final A a, final B b, final C c, final D d) {
    return new P4<A, B, C, D>() {
      @Override public A get1() {
        return a;
      }

      @Override public B get2() {
        return b;
      }

      @Override public C get3() {
        return c;
      }

      @Override public D get4() {
        return d;
      }
    };
  }
}
