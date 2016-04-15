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
import static com.entwinemedia.fn.data.json.Jsons.BLANK;
import static com.entwinemedia.fn.data.json.Jsons.NULL;
import static com.entwinemedia.fn.data.json.Jsons.ZERO;
import static com.entwinemedia.fn.data.json.Jsons.arr;
import static com.entwinemedia.fn.data.json.Jsons.f;
import static com.entwinemedia.fn.data.json.Jsons.isZero;
import static com.entwinemedia.fn.data.json.Jsons.obj;
import static com.entwinemedia.fn.data.json.Jsons.v;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.entwinemedia.fn.data.Iterables;
import com.entwinemedia.fn.data.ListBuilders;
import com.jayway.jsonassert.JsonAssert;
import org.junit.Assert;
import org.junit.Test;

public class JsonsTest {
  private final SimpleSerializer ser = new SimpleSerializer();

  @Test
  public void testEquality() throws Exception {
    Assert.assertEquals(NULL, NULL);
    Assert.assertNotEquals(NULL, new JBoolean(true));
    Assert.assertNotEquals(NULL, ZERO);
    assertEquals(new JString("test"), new JString("test"));
    assertNotEquals(new JNumber(1), new JString("1"));
    assertNotEquals(new JNumber(1), new JNumber(1.0));
    assertEquals(new JNumber(1.0), new JNumber(1.0));
    assertNotEquals(new JNumber(1), new JNumber(1l));
    assertNotEquals(new JNumber(1), new JNumber((short) 1));
    assertTrue(Iterables.eq(arr(v(10), v(20)), arr(v(10), v(20))));
    assertFalse(Iterables.eq(arr(v(10), v(20)), arr(v(20), v(20))));
    assertFalse(Iterables.eq(arr(v(10), v(20)), arr(v(10), v(20), v("x"))));
    //
    assertEquals(arr(v("1")), arr(v("1")));
    assertEquals(arr(v("1"), v("2")), arr(v("1"), v("2")));
    assertNotEquals(arr(v("1"), v("3")), arr(v("1"), v("2")));
    //
    assertEquals(obj(f("test", v("test"))), obj(f("test", v("test"))));
    assertNotEquals(obj(f("test", v("test"))), obj(f("test2", v("test2"))));
    //
    final JObject o1 =
        obj(f("title", v("some title")), f("subjects", v("subject no 1"))).merge(obj(f("subjects", v("subject no 2"))));
    final JObject o2 =
        obj(f("title", v("some title")), f("subjects", arr(v("subject no 1"), v("subject no 2"))));
    assertEquals(o1, o2);
  }

  @Test
  public void testIsZero() throws Exception {
    assertTrue(isZero(ZERO));
    assertTrue(isZero(arr(ZERO)));
    assertFalse(isZero(arr(1)));
    assertFalse(isZero(arr("one")));
    assertFalse(isZero(arr(ZERO, v("one"))));
    assertTrue(isZero(obj(f("key", ZERO))));
    assertTrue(isZero(obj(f("key", arr(ZERO)))));
    assertTrue(isZero(obj(f("key", obj(f("key", ZERO))))));
    assertTrue(isZero(obj(f("key", obj(f("key", arr(ZERO)))))));
    assertTrue(isZero(obj(f("key", obj(f("key", arr(ZERO, ZERO)))))));
  }

  // see for json-path examples https://code.google.com/p/json-path/
  @Test
  public void testSerialization() {
    final JObject o =
        obj(f("key", v(10)),
            f("bla", v("hallo")),
            f("remove", ZERO),
            f("array",
              arr(v("sad,,,asd"),
                  v(10),
                  ZERO,
                  v(20.34),
                  v(true),
                  obj(f("key", v(true))))));
    final String json = ser.toJson(o);
    System.out.println(json);
    JsonAssert.with(json).assertThat("$.key", equalTo(10))
        .assertThat("$.bla", equalTo("hallo"))
        .assertNotDefined("$.remove")
        .assertThat("$.array", hasSize(5))
        .assertThat("$.array", hasItems("sad,,,asd", 10, 20.34, true))
        .assertThat("$.array[4].key", equalTo(true));
    JsonAssert.with(ser.toJson(obj(f("a", v(10)), f("b", v(20))).override(obj(f("a", v(30))))))
        .assertThat("$.a", equalTo(30))
        .assertThat("$.b", equalTo(20));
    JsonAssert.with(ser.toJson(obj(f("a", v(10)), f("b", v(20))).merge(obj(f("a", v(30))))))
        .assertThat("$.a", contains(10, 30))
        .assertThat("$.a", hasSize(2))
        .assertThat("$.b", equalTo(20));
  }

  @Test
  public void testAppend() {
    final JArray a = arr(v(10), v(20));
    final JArray b = arr(v(20), v(30));
    assertEquals(4, ListBuilders.SIA.mk(a.append(b)).size());
    assertEquals(2, ListBuilders.SIA.mk(a).size());
    assertEquals(2, ListBuilders.SIA.mk(b).size());
    assertEquals(ListBuilders.LIA.<Object>mk(10, 20, 20, 30), $(a.append(b)).bind(Jsons.Functions.valueOfPrimitive).toList());
  }

  @Test
  public void testMerge() {
    {
      final JObject expected = obj(
          f("company", v("Extron")),
          f("city", arr(v("Anaheim"), v("Zurich"), v("Raleigh")))
      );
      final JObject base = obj(
          f("company", v("Extron")),
          f("city", v("Anaheim"))
      );
      final JObject merge1 = base.merge(obj(f("city", v("Zurich"))));
      final JObject merge2 = merge1.merge(obj(f("city", v("Raleigh"))));
      assertEquals(expected, merge2);
      // make sure the base object hasn't been modified by the merge
      assertEquals(
          obj(
              f("company", v("Extron")),
              f("city", v("Anaheim"))
          ),
          base
      );
    }
    assertEquals(
        obj(
            f("company", v("Extron")),
            f("city", arr(v("Anaheim"), v("Raleigh"))),
            f("products", obj(
                f("hardware", v("SMP")),
                f("software", v("Entwine EMP"))
            ))
        ),
        obj(
            f("company", v("Extron")),
            f("city", v("Anaheim")),
            f("products", obj(
                f("hardware", v("SMP"))
            ))
        ).merge(
            obj(f("city", v("Raleigh")))
        ).merge(
            obj(
                f("products", obj(
                    f("software", v("Entwine EMP"))
                ))
            )
        )
    );
    assertEquals(
        obj(
            f("company", v("Extron")),
            f("address", obj(
                f("city", arr(v("Anaheim"), v("Zurich"), v("Raleigh"))))
            )
        ),
        obj(
            f("company", v("Extron"))
        ).merge(
            obj(
                f("address", obj(f("city", v("Anaheim"))))
            ).merge(
                obj(f("address", obj(f("city", v("Zurich")))))
            )
        ).merge(
            obj(f("address", obj(f("city", v("Raleigh")))))
        )
    );
    assertEquals(
        obj(
            f("company", v("Extron")),
            f("address", arr(
                v("unknown"),
                obj(f("city", v("Zurich"))),
                obj(f("city", v("Anaheim"))),
                obj(f("city", v("Raleigh")))
            ))
        ),
        obj(
            f("company", v("Extron"))
        ).merge(
            obj(f("address", v("unknown")))
        ).merge(
            obj(f("address", obj(f("city", v("Zurich")))))
        ).merge(
            obj(f("address", obj(f("city", v("Anaheim")))))
        ).merge(
            obj(f("address", obj(f("city", v("Raleigh")))))
        )
    );
    assertEquals(
        obj(f("city", arr("Bochum", "Zurich"))),
        obj(f("city", arr("Bochum"))).merge(obj(f("city", arr("Zurich"))))
    );
    assertEquals(
        obj(f("city", arr("Bochum", "Zurich"))),
        obj(f("city", arr("Bochum"))).merge(f("city", arr("Zurich")))
    );
  }

  @Test
  public void testMergeWithZero() {
    // ZERO in an array
    //
    assertNotEquals(
        "The inner data structures are NOT the same. The second object contains a ZERO.",
        obj(f("city", arr("Bochum"))),
        obj(f("city", "Bochum")).merge(obj(f("city", arr(ZERO))))
    );
    assertEquals(
        "Merging should not produce an array since value of the second object is an array that contains only a ZERO.",
        ser.toJson(obj(f("city", "Bochum"))),
        ser.toJson(obj(f("city", "Bochum")).merge(obj(f("city", arr(ZERO)))))
    );
    // Plain ZERO
    //
    Object x = obj(f("city", "Bochum")).merge(obj(f("city", ZERO)));
    assertNotEquals(
        "The inner data structures are NOT the same. The second object contains a ZERO.",
        obj(f("city", "Bochum")),
        obj(f("city", "Bochum")).merge(obj(f("city", ZERO)))
    );
    assertEquals(
        "Merging should not produce an array since the value of the second object is only a ZERO",
        ser.toJson(obj(f("city", "Bochum"))),
        ser.toJson(obj(f("city", "Bochum")).merge(obj(f("city", ZERO))))
    );
    //
    assertNotEquals(
        "The inner data structures are NOT the same. The second object contains a ZERO.",
        obj(f("city", arr("Bochum"))),
        obj(f("city", arr("Bochum"))).merge(obj(f("city", arr(ZERO))))
    );
    assertNotEquals(
        "Serialized structure should be equal.",
        ser.toJson(obj(f("city", arr("Bochum")))),
        ser.toJson(obj(f("city", arr("Bochum"))).merge(obj(f("city", arr(ZERO)))))
    );
  }

  @Test
  public void testOverride() {
    assertEquals(
        obj(f("name", v("Karl")),
            f("age", v(10))),

        obj(f("name", v("Peter")))
            .override(
                obj(f("name", v("Karl")),
                    f("age", v(10)))));

    assertEquals(
        obj(f("address",
              obj(f("city", v("Bochum")),
                  f("street", v("Kortumstr"))))),

        obj(f("address",
              obj(f("city", v("Zurich")))))
            .override(
                obj(f("address",
                      obj(f("city", v("Bochum")),
                          f("street", v("Kortumstr")))))));
  }

  @Test
  public void testIdentityElement() {
    final JObject a =
        obj(f("string", v(null, ZERO)),
            f("number", v(15, BLANK)));
    JsonAssert.with(ser.toJson(a))
        .assertNotDefined("$.string")
        .assertThat("$.number", equalTo(15));
    //
    assertEquals("[]", ser.toJson(arr(ZERO)));
    //
    assertEquals("[\"one\",\"two\"]", (ser.toJson(arr(v("one"), v("two"), ZERO))));
    assertEquals("[\"one\",\"two\"]", (ser.toJson(arr(ZERO, v("one"), v("two")))));
    assertEquals("[\"one\",\"two\"]", (ser.toJson(arr(v("one"), ZERO, v("two")))));
  }

  @Test
  public void testNullSafeValueHandling() {
    final JObject a =
        obj(f("string", v("String", BLANK)),
            f("number", v(15, BLANK)),
            f("bool", v(true, BLANK)),
            f("null", v(null, BLANK)));

    JsonAssert.with(ser.toJson(a))
        .assertThat("$.string", equalTo("String"))
        .assertThat("$.number", equalTo(15))
        .assertThat("$.null", equalTo(""));
  }
}
