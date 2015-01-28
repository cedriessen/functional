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

package com.entwinemedia.fn.data;

import java.util.UUID;

/**
 * Wrapper for types that do not implement {@link #equals(Object)} and {@link #hashCode()}.
 * The purpose is to give an arbitrary object an ID where the object ID is not sufficient.
 *
 * @param <A>
 *         the wrapped type
 */
public final class Id<A> {
  private final A a;
  private final String id;

  public Id(A a) {
    this.a = a;
    this.id = UUID.randomUUID().toString();
  }

  public static <A> Id<A> id(A a) {
    return new Id<A>(a);
  }

  @Override public int hashCode() {
    return id.hashCode();
  }

  @Override public boolean equals(Object o) {
    return o instanceof Id && id.equals(((Id) o).id);
  }

  public A _() {
    return a;
  }
}
