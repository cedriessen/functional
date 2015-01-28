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

/**
 * Implementation of {@link P1} with eager evaluation of the encapsulated value {@link P1#_1()}.
 */
public final class P1Eager<A> extends P1<A> {
  private final A a;

  public P1Eager(A a) {
    this.a = a;
  }

  public static <A> P1<A> p(A a) {
    return new P1Eager<A>(a);
  }

  @Override public A _1() {
    return a;
  }

  @Override public <B> P1<B> fmap(Fn<? super A, ? extends B> f) {
    return new P1Eager<B>(f.ap(a));
  }

  @Override public <B> P1<B> bind(Fn<? super A, P1<B>> f) {
    return f.ap(a);
  }
}
