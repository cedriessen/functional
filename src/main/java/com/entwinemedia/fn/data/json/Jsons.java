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
import static com.entwinemedia.fn.fns.Booleans.not;

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Pred;
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

  /** The identity object. */
  public static final Zero ZERO = new Zero();

  /** JSON null. */
  public static final JNull NULL = new JNull();

  /** JSON true. */
  public static final JBoolean TRUE = new JBoolean(true);

  /** JSON false. */
  public static final JBoolean FALSE = new JBoolean(false);

  /** A blank string. */
  public static final JString BLANK = new JString("");

  /** An empty object. */
  private static final JObject EMPTY_OBJ = new JObject(new HashMap<String, Field>());

  /** An empty array. */
  private static final JArray EMPTY_ARR = new JArray(new ArrayList<JValue>());

  //
  // Objects
  //

  public static JObject obj() {
    return EMPTY_OBJ;
  }

  public static JObject obj(Field... fields) {
    return obj($(fields));
  }

  public static JObject obj(Iterable<? extends Field> fields) {
    return new JObject($(fields).filter(not(Functions.isFieldZero)).group(Jsons.Functions.keyOfField));
  }

  /**
   * Create a new {@link JObject} from a map.
   * Please note that the map will be copied.
   */
  public static JObject obj(Map<String, Field> fields) {
    return new JObject(new HashMap<>(fields));
  }

  public static Field f(String key, JValue value) {
    return new Field(key, value);
  }

  public static Field f(String key, String value) {
    return f(key, v(value));
  }

  public static Field f(String key, Number value) {
    return f(key, v(value));
  }

  public static Field f(String key, Boolean value) {
    return f(key, v(value));
  }

  //
  // Arrays
  //

  public static JArray arr() {
    return EMPTY_ARR;
  }

  public static JArray arr(JValue... values) {
    return arr(l.mk(values));
  }

  @SuppressWarnings("unchecked")
  public static JArray arr(Iterable<? extends JValue> values) {
    return new JArray($(values).filter(not(Functions.isZero)));
  }

  public static JArray arr(String... values) {
    return arr($(values).map(Functions.stringToJValue));
  }

  public static JArray arr(Number... values) {
    return arr($(values).map(Functions.numberToJValue));
  }

  public static JArray arr(Boolean... values) {
    return arr($(values).map(Functions.booleanToJValue));
  }

  //
  // Primitives
  //

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

  /** A field is considered zero if its value is zero. */
  public static boolean isZero(Field f) {
    return isZero(f.value());
  }

  /**
   * Check if a value is zero. Returns true only for {@link Zero} itself
   * or a {@link JObject} which contains only fields whose values are zero.
   */
  public static boolean isZero(JValue v) {
    return
        v instanceof Zero
            || (v instanceof JObject
            && !$((JObject) v).exists(not(Functions.isFieldZero)));
  }

  public static Iterable<Field> removeZeros(Iterable<Field> fields) {
    return $(fields).filter(not(Functions.isFieldZero));
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
        @Override public A def(JPrimitive<A> j) {
          return j.value();
        }
      };
    }

    /**
     * Create a function that returns the value of a {@link Field}.
     */
    public static final Fn<Field, JValue> valueOfField = new Fn<Field, JValue>() {
      @Override public JValue def(Field j) {
        return j.value();
      }
    };

    /**
     * Create a function that returns the key of a {@link Field}.
     */
    public static final Fn<Field, String> keyOfField = new Fn<Field, String>() {
      @Override public String def(Field j) {
        return j.key();
      }
    };

    /**
     * Create a function that returns the value wrapped in a list if it is a {@link JPrimitive}
     * or an empty list otherwise.
     */
    public static final Fn<JValue, List<Object>> valueOfPrimitive = new Fn<JValue, List<Object>>() {
      @Override public List<Object> def(JValue j) {
        if (j instanceof JPrimitive) {
          return l.mk(((JPrimitive) j).value());
        } else {
          return l.nil();
        }
      }
    };

    public static final Pred<JValue> isZero = new Pred<JValue>() {
      @Override public Boolean def(JValue v) {
        return isZero(v);
      }
    };

    public static final Pred<Field> isFieldZero = new Pred<Field>() {
      @Override public Boolean def(Field f) {
        return isZero(f);
      }
    };

    /**
     * Create a function that converts a map entry into a {@link Field}.
     */
    public static final Fn<Entry<String, JValue>, Field> entryToJField = new Fn<Entry<String, JValue>, Field>() {
      @Override public Field def(Entry<String, JValue> e) {
        return new Field(e.getKey(), e.getValue());
      }
    };

    public static final Fn<String, JValue> stringToJValue = new Fn<String, JValue>() {
      @Override public JValue def(String s) {
        return v(s);
      }
    };

    public static final Fn<Number, JValue> numberToJValue = new Fn<Number, JValue>() {
      @Override public JValue def(Number s) {
        return v(s);
      }
    };

    public static final Fn<Boolean, JValue> booleanToJValue = new Fn<Boolean, JValue>() {
      @Override public JValue def(Boolean s) {
        return v(s);
      }
    };
  }
}
