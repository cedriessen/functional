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

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Fn2;
import com.entwinemedia.fn.data.ListBuilder;
import com.entwinemedia.fn.data.ListBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Collection of functions and methods to build JSON structures.
 */
@ParametersAreNonnullByDefault
public final class Jsons {
  // loose immutable
  private static final ListBuilder l = ListBuilders.looseImmutableArray;

  public static final JZero ZERO = new JZero();
  public static final JNull NULL = new JNull();
  public static final JBoolean TRUE = new JBoolean(true);
  public static final JBoolean FALSE = new JBoolean(false);
  public static final JString EMPTY = new JString("");

  public static JObjectWrite j(JField... fields) {
    return j($(fields));
  }

  public static JObjectWrite j(Iterable<? extends JField> fields) {
    return new JObjectWrite($(fields).group(Jsons.keyOfFieldFn));
  }

  public static Map<String, JField> transformMap(Map<String, JValue> fields) {
    return $(fields.entrySet())
            .foldl(new HashMap<String, JField>(),
                   new Fn2<HashMap<String, JField>, Entry<String, JValue>, HashMap<String, JField>>() {
                     @Override
                     public HashMap<String, JField> apply(HashMap<String, JField> sum, Entry<String, JValue> e) {
                       sum.put(e.getKey(), f(e.getKey(), e.getValue()));
                       return sum;
                     }
                   });
  }

  public static JObjectWrite j(Map<String, JField> fields) {
    return new JObjectWrite(fields);
  }

  public static JField f(String key, JValue value) {
    return new JField(key, value);
  }

  public static JArrayWrite a(JValue... values) {
    return a(l.mk(values));
  }

  @SuppressWarnings("unchecked")
  public static JArrayWrite a(Iterable<? extends JValue> values) {
    return new JArrayWrite((Iterable<JValue>) values);
  }

  public static JBoolean v(Boolean value) {
    return new JBoolean(value);
  }

  public static JString v(String value) {
    return new JString(value);
  }

  public static JNumber v(Number value) {
    return new JNumber(value);
  }

  /**
   * Create a JSON value from an arbitrary object.
   * <ul>
   *   <li>null -> <code>nullValue</code></li>
   *   <li>{@link String} -> {@link JString}</li>
   *   <li>{@link Number} -> {@link JNumber}</li>
   *   <li>{@link Boolean} -> {@link JBoolean}</li>
   *   <li>everything else -> string representation as {@link JString}</li>
   * </ul>
   */
  public static JValue v(@Nullable Object value, JValue nullValue) {
    if (value == null) {
      return nullValue;
    } else if (value instanceof String) {
      return v((String) value);
    } else if (value instanceof Number) {
      return v((Number) value);
    } else if (value instanceof Boolean) {
      return v((Boolean) value);
    } else {
      return v(value.toString());
    }
  }

  public static <A> Fn<JPrimitive<A>, A> valueFn() {
    return new Fn<JPrimitive<A>, A>() {
      @Override public A apply(JPrimitive<A> j) {
        return j.getValue();
      }
    };
  }

  public static Fn<JField, JValue> valueOfFieldFn = new Fn<JField, JValue>() {
    @Override public JValue apply(JField j) {
      return j.getValue();
    }
  };

  public static Fn<JField, String> keyOfFieldFn = new Fn<JField, String>() {
    @Override public String apply(JField j) {
      return j.getKey();
    }
  };

  public static Fn<JValue, List<Object>> valueOfPrimitiveFn = new Fn<JValue, List<Object>>() {
    @Override public List<Object> apply(JValue j) {
      if (j instanceof JPrimitive) {
        return l.mk(((JPrimitive) j).getValue());
      } else {
        return ListBuilders.SIA.nil();
      }
    }
  };

  public static Fn<Entry<String, JValue>, JField> entryToJFieldFn = new Fn<Entry<String, JValue>, JField>() {
    @Override public JField apply(Entry<String, JValue> e) {
      return new JField(e.getKey(), e.getValue());
    }
  };

  public static Fn<String, JValue> stringToJValueFn = new Fn<String, JValue>() {
    @Override public JValue apply(String s) {
      return v(s);
    }
  };

  /**
   * Create a JSON Array with the given list.
   *
   * @param list
   *          The list of values
   * @return a JSON array as {@link JValue}
   */
  public static JValue jsonArrayFromList(List<String> list) {
    if (list == null || list.isEmpty())
      return a();

    List<JValue> jsonArray = new ArrayList<JValue>();
    for (String item : list) {
      jsonArray.add(v(item));
    }
    return a(jsonArray);
  }
}
