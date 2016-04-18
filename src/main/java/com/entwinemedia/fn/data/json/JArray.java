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

import static com.entwinemedia.fn.Stream.$;

import com.entwinemedia.fn.Prelude;
import com.entwinemedia.fn.data.ImmutableIteratorWrapper;
import com.entwinemedia.fn.data.Iterables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Write implementation for JSON arrays. With only the efficient creation and writing of arrays in mind it
 * does not provide indexed access to the array's elements.
 */
public final class JArray implements JValue, Iterable<JValue> {
  private Iterable<JValue> values;

  @SuppressWarnings("unchecked") JArray(Iterable<JValue> values) {
    this.values = values;
  }

  @Override public Iterator<JValue> iterator() {
    return new ImmutableIteratorWrapper<>(values.iterator());
  }

  /** Append values to this array. */
  public JArray append(Iterable<JValue> array) {
    return new JArray($(this).append(array));
  }

  /** Check if the array contains any values. */
  public boolean isEmpty() {
    return values.iterator().hasNext();
  }

  public Object[] toArray() {
    final Iterator<JValue> arrayIterator = iterator();
    final List<Object> values = new ArrayList<>();
    while (arrayIterator.hasNext()) {
      final JValue val = arrayIterator.next();
      if (val instanceof JPrimitive) {
        values.add(((JPrimitive) val).value());
      } else if (val instanceof JObject) {
        values.add(((JObject) val).toMap());
      } else if (val instanceof JArray) {
        values.add(((JArray) val).toArray());
      } else if (val instanceof JNull) {
        values.add(null);
      } else if (val instanceof Zero) {
        // skip zeros
      } else {
        Prelude.unexhaustiveMatch(val.getClass());
      }
    }
    return values.toArray();
  }

  @Override public int hashCode() {
    return values.hashCode();
  }

  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof JArray && eqFields((JArray) that));
  }

  private boolean eqFields(JArray that) {
    return Iterables.eq(values, that.values);
  }

  /**
   * Print a JSON representation.
   * <p/>
   * For debugging purposes only! Please use a {@link Serializer} implementation for production use.
   */
  @Override public String toString() {
    return new SimpleSerializer().toJson(this);
  }
}
