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
import com.entwinemedia.fn.data.Opt;

/** Functions on {@link com.entwinemedia.fn.data.Opt}s. */
public final class Opts {
  private Opts() {
  }

  public static <A> Fn<Opt<A>, A> getOr(final A a) {
    return new Fn<Opt<A>, A>() {
      @Override public A apply(Opt<A> o) {
        return o.getOr(a);
      }
    };
  }

  public static <A, B> Fn<Opt<A>, Opt<B>> lift(final Fn<? super A, ? extends B> f) {
    return new Fn<Opt<A>, Opt<B>>() {
      @Override public Opt<B> apply(Opt<A> as) {
        return as.map(f);
      }
    };
  }
}
