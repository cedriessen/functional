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
import com.entwinemedia.fn.data.ListBuilder;
import com.entwinemedia.fn.data.ListBuilders;

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
  public static final JString BLANK = new JString("");
  public static final JObjectWrite EMPTY = new JObjectWrite(new HashMap<String, JField>());

  public static JObjectWrite obj(JField... fields) {
    return obj($(fields));
  }

  public static JObjectWrite obj(Iterable<? extends JField> fields) {
    return new JObjectWrite($(fields).group(Jsons.Functions.keyOfField));
  }

  public static JObjectWrite obj(Map<String, JField> fields) {
    return new JObjectWrite(fields);
  }

  public static JField f(String key, JValue value) {
    return new JField(key, value);
  }

  public static JArrayWrite arr(JValue... values) {
    return arr(l.mk(values));
  }

  @SuppressWarnings("unchecked")
  public static JArrayWrite arr(Iterable<? extends JValue> values) {
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

  /**
   * Functions.
   */
  public static final class Functions {
    private Functions() {
    }

    /**
     * Create a function that returns the value of a {@link JPrimitive}.
     */
    public static <A> Fn<JPrimitive<A>, A> value() {
      return new Fn<JPrimitive<A>, A>() {
        @Override public A apply(JPrimitive<A> j) {
          return j.value();
        }
      };
    }

    /**
     * Create a function that returns the value of a {@link JField}.
     */
    public static Fn<JField, JValue> valueOfField = new Fn<JField, JValue>() {
      @Override public JValue apply(JField j) {
        return j.value();
      }
    };

    /**
     * Create a function that returns the key of a {@link JField}.
     */
    public static Fn<JField, String> keyOfField = new Fn<JField, String>() {
      @Override public String apply(JField j) {
        return j.key();
      }
    };

    /**
     * Create a function that returns the value wrapped in a list if it is a {@link JPrimitive}
     * or an empty list otherwise.
     */
    public static Fn<JValue, List<Object>> valueOfPrimitive = new Fn<JValue, List<Object>>() {
      @Override public List<Object> apply(JValue j) {
        if (j instanceof JPrimitive) {
          return l.mk(((JPrimitive) j).value());
        } else {
          return l.nil();
        }
      }
    };

    /**
     * Create a function that converts a map entry into a {@link JField}.
     */
    public static Fn<Entry<String, JValue>, JField> entryToJField = new Fn<Entry<String, JValue>, JField>() {
      @Override public JField apply(Entry<String, JValue> e) {
        return new JField(e.getKey(), e.getValue());
      }
    };

    public static Fn<String, JValue> stringToJValue = new Fn<String, JValue>() {
      @Override public JValue apply(String s) {
        return v(s);
      }
    };
  }
}
