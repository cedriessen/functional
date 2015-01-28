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

public final class P2Eager<A, B> extends P2<A, B> {
  private final A a;
  private final B b;

  public P2Eager(A a, B b) {
    this.a = a;
    this.b = b;
  }

  @Override public A _1() {
    return a;
  }

  @Override public B _2() {
    return b;
  }
}
