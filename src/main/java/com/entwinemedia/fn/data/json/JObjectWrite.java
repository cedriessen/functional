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
import static com.entwinemedia.fn.Stream.$;
import static com.entwinemedia.fn.data.json.Jsons.arr;
import static com.entwinemedia.fn.data.json.Jsons.obj;

import com.entwinemedia.fn.Fn2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Write implementation for JSON objects.
 * With only the efficient creation and writing of objects in mind it does not provide any access methods.
 */
public final class JObjectWrite implements JValue, Iterable<JField> {
  private final Map<String, JField> fields;

  JObjectWrite(Map<String, JField> fields) {
    this.fields = fields;
  }

  @Override public Iterator<JField> iterator() {
    return fields.values().iterator();
  }

  /**
   * Merge this object with <code>obj</code> using an override strategy.
   * <p/>
   * Fields in <code>obj</code> override, i.e. completely replace, fields in this object.
   * <pre>
   *   {"name": "Peter", age: 30} override {"name": "Klaus", "city": "Zurich"}
   *   ==> {"name": "Klaus", age: 30, "city": "Zurich"}
   * </pre>
   */
  public JObjectWrite override(Iterable<JField> obj) {
    return obj($(this).append(obj));
  }

  /**
   * Merge this object with <code>obj</code> after the following strategy.
   * <ul>
   * <li>If a key in <code>obj</code> does not exist in this object, add it.</li>
   * <li>If a key exists in both objects and both values are primitives, merge them into an array.</li>
   * <li>If a key exists in both objects and one of the values is an array, add the other value to it.</li>
   * <li>If a key exists in both objects and both values are objects, merge them recursively.</li>
   * </ul>
   * When merging into an array, the values of this object come first.
   */
  public JObjectWrite merge(Iterable<JField> obj) {
    final Map<String, JField> merged = $(obj).foldl(
        // merge into the existing data (copy of it to guarantee immutability)
        new HashMap<>(fields),
        new Fn2<HashMap<String, JField>, JField, HashMap<String, JField>>() {
          @Override public HashMap<String, JField> apply(HashMap<String, JField> sum, JField f) {
            final String key = f.key();
            if (sum.containsKey(key)) {
              // key exists, there is a need for a merge
              sum.put(key, doMerge(sum.get(key), f.value()));
            } else {
              // key does not exist yet so just put it in
              sum.put(key, f);
            }
            return sum;
          }
        });
    return obj(merged);
  }

  /**
   * Merge a value into a field.
   * Work horse function for {@link #merge(Iterable)}.
   */
  private JField doMerge(JField a, JValue b) {
    if (a.value() instanceof JArrayWrite) {
      return a.mk(((JArrayWrite) a.value()).append($(b)));
    } else if (b instanceof JArrayWrite) {
      return a.mk(((JArrayWrite) b).append($(a.value())));
    } else if (a.value() instanceof JObjectWrite && b instanceof JObjectWrite) {
      return a.mk(((JObjectWrite) a.value()).merge((JObjectWrite) b));
    } else {
      return a.mk(arr(a.value(), b));
    }
  }

  /** Check if the object contains any values. */
  public boolean isEmpty() {
    return fields.isEmpty();
  }

  @Override public int hashCode() {
    return fields.hashCode();
  }

  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof JObjectWrite && eqFields((JObjectWrite) that));
  }

  private boolean eqFields(JObjectWrite that) {
    return eq(fields, that.fields);
  }

  /** Print a JSON representation. */
  @Override public String toString() {
    return new SimpleSerializer().toJson(this);
  }
}
