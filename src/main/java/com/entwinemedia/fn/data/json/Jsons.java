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
import com.entwinemedia.fn.Prelude;
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

  public static final Zero ZERO = new Zero();
  public static final JNull NULL = new JNull();
  public static final JBoolean TRUE = new JBoolean(true);
  public static final JBoolean FALSE = new JBoolean(false);
  public static final JString BLANK = new JString("");
  public static final JObject EMPTY = new JObject(new HashMap<String, Field>());

  //
  // Objects
  //

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
    return arr(l.<JValue>nil());
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

  /** Check if an object is zero. Being zero is defined as all field values recursively being zero. */
  public static boolean isZero(JObject obj) {
    return !$(obj).exists(not(Functions.isFieldZero));
  }

  /** Check if an object is zero. An array is considered to be <i>zero</i> when all elements are zero. */
  public static boolean isZero(JArray arr) {
    return !$(arr).exists(not(Functions.isZero));
  }

  public static boolean isZero(Field f) {
    return isZero(f.value());
  }

  public static boolean isZero(JValue v) {
    if (v instanceof JPrimitive) {
      return false;
    } else if (v instanceof JNull) {
      return false;
    } else if (v instanceof JArray) {
      return isZero((JArray) v);
    } else if (v instanceof JObject) {
      return isZero((JObject) v);
    } else if (v instanceof Zero) {
      return true;
    } else {
      return Prelude.unexhaustiveMatch(v);
    }
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
        @Override public A apply(JPrimitive<A> j) {
          return j.value();
        }
      };
    }

    /**
     * Create a function that returns the value of a {@link Field}.
     */
    public static final Fn<Field, JValue> valueOfField = new Fn<Field, JValue>() {
      @Override public JValue apply(Field j) {
        return j.value();
      }
    };

    /**
     * Create a function that returns the key of a {@link Field}.
     */
    public static final Fn<Field, String> keyOfField = new Fn<Field, String>() {
      @Override public String apply(Field j) {
        return j.key();
      }
    };

    /**
     * Create a function that returns the value wrapped in a list if it is a {@link JPrimitive}
     * or an empty list otherwise.
     */
    public static final Fn<JValue, List<Object>> valueOfPrimitive = new Fn<JValue, List<Object>>() {
      @Override public List<Object> apply(JValue j) {
        if (j instanceof JPrimitive) {
          return l.mk(((JPrimitive) j).value());
        } else {
          return l.nil();
        }
      }
    };

    public static final Pred<JValue> isZero = new Pred<JValue>() {
      @Override public Boolean apply(JValue v) {
        return isZero(v);
      }
    };

    public static final Pred<Field> isFieldZero = new Pred<Field>() {
      @Override public Boolean apply(Field f) {
        return isZero(f);
      }
    };

    /**
     * Create a function that converts a map entry into a {@link Field}.
     */
    public static final Fn<Entry<String, JValue>, Field> entryToJField = new Fn<Entry<String, JValue>, Field>() {
      @Override public Field apply(Entry<String, JValue> e) {
        return new Field(e.getKey(), e.getValue());
      }
    };

    public static final Fn<String, JValue> stringToJValue = new Fn<String, JValue>() {
      @Override public JValue apply(String s) {
        return v(s);
      }
    };

    public static final Fn<Number, JValue> numberToJValue = new Fn<Number, JValue>() {
      @Override public JValue apply(Number s) {
        return v(s);
      }
    };

    public static final Fn<Boolean, JValue> booleanToJValue = new Fn<Boolean, JValue>() {
      @Override public JValue apply(Boolean s) {
        return v(s);
      }
    };
  }
}
