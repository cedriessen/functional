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

/** Builder for products. */
public interface ProductBuilder {
  <A> P1<A> p1(A a);
  <A, B> P2<A, B> p2(A a, B b);
  <A, B, C> P3<A, B, C> p3(A a, B b, C c);
  <A, B, C, D> P4<A, B, C, D> p4(A a, B b, C c, D d);
}
