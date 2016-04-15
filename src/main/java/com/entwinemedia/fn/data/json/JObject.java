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
import com.entwinemedia.fn.Prelude;
import com.entwinemedia.fn.data.ImmutableIteratorWrapper;
import com.entwinemedia.fn.data.ImmutableMapWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * JSON object.
 */
public final class JObject implements JValue, Iterable<Field> {
  private final Map<String, Field> fields;

  JObject(Map<String, Field> fields) {
    this.fields = fields;
  }

  @Override public Iterator<Field> iterator() {
    return new ImmutableIteratorWrapper<>(fields.values().iterator());
  }

  //
  // Override
  //

  /**
   * Merge this object with {@code obj} using an override strategy.
   * <p/>
   * Fields in {@code obj} override, i.e. completely replace, fields in this object.
   * <pre>{@code
   *   {"name": "Peter", age: 30} override {"name": "Klaus", "city": "Zurich"}
   *   ==> {"name": "Klaus", age: 30, "city": "Zurich"}
   * }</pre>
   */
  public JObject override(Iterable<Field> obj) {
    return obj($(this).append(obj));
  }

  public JObject override(Field... fields) {
    return override($(fields));
  }

  //
  // Merge
  //

  /**
   * Merge with {@code obj} after the following strategy.
   * <ul>
   * <li>If a key in {@code obj} does not exist in this object, add it.</li>
   * <li>If a key exists in both objects and both values are primitives, merge them into an array.</li>
   * <li>If a key exists in both objects and one of the values is an array, add the other value to it.</li>
   * <li>If a key exists in both objects and both values are objects, merge them recursively.</li>
   * </ul>
   * When merging into an array, the values of this object come first.
   */
  public JObject merge(Iterable<Field> obj) {
    final Map<String, Field> merged = $(obj).foldl(
        // merge into the existing data (copy of it to guarantee immutability)
        new HashMap<>(fields),
        new Fn2<Map<String, Field>, Field, Map<String, Field>>() {
          @Override public Map<String, Field> apply(Map<String, Field> sum, Field f) {
            return mergeInto(sum, f);
          }
        });
    return new JObject(merged);
  }

  /**
   * Merge a field into an object following these {@linkplain #merge(Iterable) rules}.
   * A field can be regarded as an object with just that field.
   */
  public JObject merge(Field field) {
    return new JObject(mergeInto(new HashMap<>(fields), field));
  }

  public JObject merge(Field... fields) {
    return merge($(fields));
  }

  /** Merge {@code f} into {@code map}. Mutates {@code map}. */
  private Map<String, Field> mergeInto(Map<String, Field> map, Field f) {
    final String key = f.key();
    if (map.containsKey(key)) {
      // key exists, there is a need for a merge
      map.put(key, doMerge(map.get(key), f.value()));
    } else {
      // key does not exist yet so just put it in
      map.put(key, f);
    }
    return map;
  }

  /**
   * Merge a value into a field.
   * Work horse function for {@link #merge(Iterable)}.
   */
  private Field doMerge(Field a, JValue b) {
    if (Jsons.isZero(b)){
      return a;
    } if (a.value() instanceof JArray && b instanceof JArray) {
      return a.derive(((JArray) a.value()).append((JArray) b));
    } else if (a.value() instanceof JArray) {
      return a.derive(((JArray) a.value()).append($(b)));
    } else if (b instanceof JArray) {
      return a.derive(((JArray) b).append($(a.value())));
    } else if (a.value() instanceof JObject && b instanceof JObject) {
      return a.derive(((JObject) a.value()).merge((JObject) b));
    } else {
      return a.derive(arr(a.value(), b));
    }
  }

  //
  //

  public Map<String, Object> toMap() {
    final Map<String, Object> map = new HashMap<>();
    for (final Field field : this) {
      final JValue value = field.value();
      if (value instanceof JPrimitive) {
        map.put(field.key(), ((JString) field.value()).value());
      } else if (value instanceof JObject) {
        map.put(field.key(), ((JObject) value).toMap());
      } else if (value instanceof JArray) {
        map.put(field.key(), ((JArray) value).toArray());
      } else if (value instanceof JNull) {
        map.put(field.key(), null);
      } else if (value instanceof Zero) {
        // skip zeros
      } else {
        Prelude.unexhaustiveMatch(value.getClass());
      }
    }
    return new ImmutableMapWrapper<>(map);
  }

  //
  //

  /** Check if the object contains any values. */
  public boolean isEmpty() {
    return fields.isEmpty();
  }

  //
  //

  @Override public int hashCode() {
    return fields.hashCode();
  }

  @Override public boolean equals(Object that) {
    return (this == that) || (that instanceof JObject && eqFields((JObject) that));
  }

  private boolean eqFields(JObject that) {
    return eq(fields, that.fields);
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
