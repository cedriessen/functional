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

package com.entwinemedia.fn.data.json;

import static com.entwinemedia.fn.Equality.eq;
import static com.entwinemedia.fn.Equality.hash;

/**
 * Abstract base class for JSON primitives.
 */
abstract class JPrimitive<A> implements JValue {
  private A value;

  protected JPrimitive(A value) {
    this.value = value;
  }

  /** Get the wrapped value. */
  public A value() {
    return value;
  }

  @Override public int hashCode() {
    return hash(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object that) {
    return (this == that) || (this.getClass().isAssignableFrom(that.getClass()) && (eqFields((JPrimitive) that)));
  }

  private boolean eqFields(JPrimitive that) {
    return eq(value, that.value);
  }
}
