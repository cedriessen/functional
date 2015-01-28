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

import com.entwinemedia.fn.data.Iterables;

import java.util.Iterator;

/**
 * Write implementation for JSON arrays. With only the efficient creation and writing of arrays in mind it
 * does not provide indexed access to the array's elements.
 */
final class JArrayWrite implements JValue, Iterable<JValue> {
  private Iterable<JValue> values;

  @SuppressWarnings("unchecked")
  public JArrayWrite(Iterable<JValue> values) {
    this.values = values;
  }

  @Override public Iterator<JValue> iterator() {
    return values.iterator();
  }

  public JArrayWrite append(Iterable<JValue> array) {
    return new JArrayWrite(Iterables.join(this, array));
  }

  public boolean isEmpty() {
    return values.iterator().hasNext();
  }

  /** Delegated to the wrapped iterable. */
  @Override public int hashCode() {
    return values.hashCode();
  }

  /** Delegated to the wrapped iterable. */
  @Override public boolean equals(Object that) {
    return that instanceof JObjectWrite && eq(values, ((JArrayWrite) that).values);
  }
}
