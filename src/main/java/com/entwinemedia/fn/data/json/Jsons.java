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

/** JSON builder based on json-simple. */
public final class Jsons {
  // loose immutable
  private static final ListBuilder l = ListBuilders.looseImmutableArray;

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
    String stringValue;

    if (value == null) {
      stringValue = "";
    } else if (value instanceof String) {
      stringValue = (String) value;
    } else {
      stringValue = value.toString();
    }

    return v(stringValue);
  }

  public static final JZero jz = new JZero();

  public static final JNull jn = new JNull();

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

//  private Jsons() {
//  }
//
//  /** Check if a value is not {@link #ZERO_VAL}. */
//  public static final Fn<Val, Boolean> notZero = new Fn<Val, Boolean>() {
//    @Override public Boolean mk(Val val) {
//      return !ZERO_VAL.equals(val);
//    }
//  };
//
//  /** Get the value from a property. */
//  public static final Fn<Prop, Val> getVal = new Fn<Prop, Val>() {
//    @Override public Val mk(Prop prop) {
//      return prop.getVal();
//    }
//  };
//
//  /** {@link #toJson(Jsons.Obj)} as a function. */
//  public static final Fn<Obj, String> toJson = new Fn<Obj, String>() {
//    @Override public String mk(Obj obj) {
//      return obj.toJson();
//    }
//  };
//
//  /** JSON null. */
//  public static final Val NULL = new Val() {
//  };
//
//  /** Identity for {@link Jsons.Val values}. */
//  public static final Val ZERO_VAL = new Val() {
//  };
//
//  /** Identity for {@link Jsons.Obj objects}. */
//  public static final Obj ZERO_OBJ = obj();
//
//  /** Identity for {@link Jsons.Arr arrays}. */
//  public static final Arr ZERO_ARR = arr();
//
//  /** An object property aka key value pair. */
//  public static final class Prop {
//    private final String name;
//    private final Val val;
//
//    private Prop(String name, Val val) {
//      this.name = name;
//      this.val = val;
//    }
//
//    public String getName() {
//      return name;
//    }
//
//    public Val getVal() {
//      return val;
//    }
//  }
//
//  // sum type
//  public abstract static class Val {
//  }
//
//  /** A primitive JSON value. String, Number, Boolean, null. */
//  private static final class Primitive extends Val {
//    private final Object val;
//
//    /** Constructor methods ensure type constraints for <code>val</code>. */
//    private Primitive(Object val) {
//      this.val = val;
//    }
//
//    public Object getVal() {
//      return val;
//    }
//
//    @Override public int hashCode() {
//      return hash(val);
//    }
//
//    @Override public boolean equals(Object that) {
//      return that instanceof Primitive && eq(val, ((Primitive) that).val);
//    }
//  }
//
//  public static final class Obj extends Val {
//    private final List<Prop> props;
//
//    private Obj(List<Prop> props) {
//      this.props = props;
//    }
//
//    public List<Prop> getProps() {
//      return props;
//    }
//
//    public Obj append(Obj o) {
//      if (!ZERO_OBJ.equals(o))
//        return new Obj(Collections.<Prop, List>concat(props, o.getProps()));
//      else
//        return o;
//    }
//
//    public String toJson() {
//      return Jsons.toJson(this);
//    }
//  }
//
//  public static final class Arr extends Val {
//    private final List<Val> vals;
//
//    public Arr(List<Val> vals) {
//      this.vals = vals;
//    }
//
//    public List<Val> getVals() {
//      return vals;
//    }
//
//    public Arr append(Arr a) {
//      if (!isEmpty() && !a.isEmpty())
//        return new Arr(Collections.<Val, List>concat(vals, a.getVals()));
//      else
//        return a;
//    }
//
//    public boolean isEmpty() {
//      return vals.isEmpty();
//    }
//
//    public String toJson() {
//      return Jsons.toJson(this);
//    }
//  }
//
//  //
//
//  public static String toJson(Obj obj) {
//    return toJsonSimple(obj).toString();
//  }
//
//  public static String toJson(Arr arr) {
//    return toJsonSimple(arr).toString();
//  }
//
//  private static JSONObject toJsonSimple(Obj obj) {
//    final JSONObject json = new JSONObject();
//    for (Prop p : obj.getProps()) {
//      json.put(p.getName(), toJsonSimple(p.getVal()));
//    }
//    return json;
//  }
//
//  private static JSONArray toJsonSimple(Arr arr) {
//    final JSONArray json = new JSONArray();
//    for (Val v : arr.getVals()) {
//      json.add(toJsonSimple(v));
//    }
//    return json;
//  }
//
//  private static Object toJsonSimple(Val val) {
//    if (val instanceof Primitive) {
//      return ((Primitive) val).getVal();
//    } else if (val instanceof Obj) {
//      return toJsonSimple((Obj) val);
//    } else if (val instanceof Arr) {
//      return toJsonSimple((Arr) val);
//    } else if (val.equals(NULL)) {
//      return null;
//    } else {
//      return Prelude.unexhaustiveMatch();
//    }
//  }
//
//  /** Create an object. */
//  public static Obj obj(Prop... ps) {
//    return new Obj(mlist(ps).filter(notZero.o(getVal)).value());
//  }
//
//  /** Create an array. */
//  public static Arr arr(Val... vs) {
//    return new Arr(mlist(vs).filter(notZero).value());
//  }
//
//  /** Create an array. */
//  public static Arr arr(List<Val> vs) {
//    return new Arr(mlist(vs).filter(notZero).value());
//  }
//
//  /** Create an array. */
//  public static Arr arr(Monadics.ListMonadic<Val> vs) {
//    return new Arr(vs.filter(notZero).value());
//  }
//
//  public static Val v(Number v) {
//    return new Primitive(v);
//  }
//
//  public static Val v(String v) {
//    return new Primitive(v);
//  }
//
//  public static final Fn<String, Val> stringVal = new Fn<String, Val>() {
//    @Override public Val mk(String s) {
//      return v(s);
//    }
//  };
//
//  public static Val v(Boolean v) {
//    return new Primitive(v);
//  }
//
//  public static Val v(Date v) {
//    return new Primitive(DateTimeSupport.toUTC(v.getTime()));
//  }
//
//  /** Create a property. */
//  public static Prop p(String key, Val val) {
//    return new Prop(key, val);
//  }
//
//  /** Create a property. Passing none is like setting {@link #ZERO_VAL} which erases the property. */
//  public static Prop p(String key, Option<Val> val) {
//    return new Prop(key, val.getOrElse(ZERO_VAL));
//  }
//
//  /** Create a property. Convenience. */
//  public static Prop p(String key, Number value) {
//    return new Prop(key, v(value));
//  }
//
//  /** Create a property. Convenience. */
//  public static Prop p(String key, String value) {
//    return new Prop(key, v(value));
//  }
//
//  /** Create a property. Convenience. */
//  public static Prop p(String key, Boolean value) {
//    return new Prop(key, v(value));
//  }
//
//  /** Create a property. Convenience. */
//  public static Prop p(String key, Date value) {
//    return new Prop(key, v(value));
//  }
//
//  /** Merge a list of objects into one (last one wins). */
//  public static Obj append(Obj... os) {
//    final List<Prop> props = new ArrayList<Prop>(os.length);
//    for (Obj o : os) {
//      props.addAll(o.getProps());
//    }
//    return new Obj(props);
//  }
//
//  /** Merge a list of objects into one (last one wins). */
//  public static Obj append(Collection<Obj> os) {
//    final List<Prop> props = new ArrayList<Prop>(os.size());
//    for (Obj o : os) {
//      props.addAll(o.getProps());
//    }
//    return new Obj(props);
//  }
//
//  /** Append a list of arrays into one. */
//  public static Arr append(Arr... as) {
//    final List<Val> vals = new ArrayList<Val>(as.length);
//    for (Arr a : as) {
//      vals.addAll(a.getVals());
//    }
//    return new Arr(vals);
//  }
//
//  /** Append a list of arrays into one. */
//  public static Arr append(Collection<Arr> as) {
//    final List<Val> vals = new ArrayList<Val>(as.size());
//    for (Arr a : as) {
//      vals.addAll(a.getVals());
//    }
//    return new Arr(vals);
//  }
}
