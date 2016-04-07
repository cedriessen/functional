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

/**
 * Collection of functions and methods to build JSON structures.
 */
public final class Jsons {
  // loose immutable
  private static final ListBuilder l = ListBuilders.looseImmutableArray;

  public static final JZero jz = new JZero();

  public static final JNull jn = new JNull();


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

  public static JBoolean v(boolean value) {
    return new JBoolean(value);
  }

  public static JString v(String value) {
    return new JString(value);
  }

  public static JNumber v(Number value) {
    return new JNumber(value);
  }

  /**
   * Wrap the given string in an JSON value and changing null value to empty string.
   *
   * @param value
   *          the value to wrap in a JSON value
   * @return JSON value with the value inside or an empty string if the given value was null
   */
  public static JValue vN(Object value) {
    final JValue jval;

    if (value == null) {
      jval = v("");
    } else if (value instanceof String) {
      jval = v((String) value);
    } else if (value instanceof Number) {
      jval = v((Number) value);
    } else if (value instanceof Boolean) {
      jval = v((Boolean) value);
    } else {
      jval = v(value.toString());
    }

    return jval;
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
