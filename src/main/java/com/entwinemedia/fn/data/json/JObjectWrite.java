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

import com.entwinemedia.fn.Fn2;

import com.entwinemedia.fn.Equality;
import com.entwinemedia.fn.Stream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Write implementation for JSON objects. With only the efficient creation and writing of objects in mind it
 * does not provide any access methods.
 */
public final class JObjectWrite implements JValue, Iterable<JField> {
  private final Map<String, JField> fields;

  public JObjectWrite(Map<String, JField> fields) {
    this.fields = fields;
  }

  @Override public Iterator<JField> iterator() {
    return fields.values().iterator();
  }

  /** Merge with <code>obj</code>. Fields in <code>obj</code> override fields in this object. */
  public JObjectWrite override(Iterable<JField> obj) {
    return Jsons.j(Stream.$(this).append(obj));
  }

  /** Merge with <code>obj</code>. Fields in <code>obj</code> overwrite fields in this object. */
  public JObjectWrite merge(Iterable<JField> obj) {
    final Map<String, JField> m = Stream.$(Stream.$(this).append(obj).groupMulti(Jsons.keyOfFieldFn).values())
            .foldl(new HashMap<String, JField>(), new Fn2<HashMap<String, JField>, List<JField>, HashMap<String, JField>>() {
              @Override public HashMap<String, JField> ap(HashMap<String, JField> sum, List<JField> fields) {
                // list "fields" has at least one element
                final JField head = fields.get(0);
                if (fields.size() > 1) {
                  sum.put(head.getKey(), Jsons.f(head.getKey(), Jsons.a(Stream.$(fields).map(Jsons.valueOfFieldFn))));
                } else {
                  sum.put(head.getKey(), head);
                }
                return sum;
              }
            });
    return Jsons.j(m);
  }

  public boolean isEmpty() {
    return fields.isEmpty();
  }

  /** Delegated to the wrapped iterable. */
  @Override public int hashCode() {
    return fields.hashCode();
  }

  /** Delegated to the wrapped iterable. */
  @Override public boolean equals(Object that) {
    return that instanceof JObjectWrite && Equality.eq(fields, ((JObjectWrite) that).fields);
  }
}
