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

package com.entwinemedia.fn.fns;

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.P1;
import com.entwinemedia.fn.P2;

public final class Products {
  private Products() {
  }

  public static <A> Fn<P1<A>, A> p1_1() {
    return new Fn<P1<A>, A>() {
      @Override public A ap(P1<A> p) {
        return p._1();
      }
    };
  }

  public static <A> Fn<P2<A, ?>, A> p2_1() {
    return new Fn<P2<A, ?>, A>() {
      @Override public A ap(P2<A, ?> p) {
        return p._1();
      }
    };
  }

  public static <B> Fn<P2<?, B>, B> p2_2() {
    return new Fn<P2<?, B>, B>() {
      @Override public B ap(P2<?, B> p) {
        return p._2();
      }
    };
  }
}
