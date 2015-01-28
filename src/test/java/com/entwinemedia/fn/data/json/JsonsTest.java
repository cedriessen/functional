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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static com.entwinemedia.fn.Stream.$;
import static com.entwinemedia.fn.data.json.Jsons.j;
import static com.entwinemedia.fn.data.json.Jsons.v;

import com.entwinemedia.fn.data.Iterables;
import com.entwinemedia.fn.data.ListBuilders;

import com.jayway.jsonassert.JsonAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class JsonsTest {
  private final SimpleSerializer serializer = new SimpleSerializer();

  @Test
  public void testEquality() throws Exception {
    Assert.assertEquals(Jsons.jn, Jsons.jn);
    Assert.assertNotEquals(Jsons.jn, new JBoolean(true));
    Assert.assertNotEquals(Jsons.jn, Jsons.jz);
    assertEquals(new JString("test"), new JString("test"));
    assertNotEquals(new JNumber(1), new JString("1"));
    assertNotEquals(new JNumber(1), new JNumber(1.0));
    assertEquals(new JNumber(1.0), new JNumber(1.0));
    assertNotEquals(new JNumber(1), new JNumber(1l));
    assertNotEquals(new JNumber(1), new JNumber((short) 1));
    assertTrue(Iterables.eq(Jsons.a(Jsons.v(10), Jsons.v(20)), Jsons.a(Jsons.v(10), Jsons.v(20))));
    assertFalse(Iterables.eq(Jsons.a(Jsons.v(10), Jsons.v(20)), Jsons.a(Jsons.v(20), Jsons.v(20))));
    assertFalse(Iterables.eq(Jsons.a(Jsons.v(10), Jsons.v(20)), Jsons.a(Jsons.v(10), Jsons.v(20), Jsons.v("x"))));
  }

  // see for json-path examples https://code.google.com/p/json-path/
  @Test
  public void testSerialization() {
    final JObjectWrite o = Jsons.j(Jsons.f("key", Jsons.v(10)),
                                   Jsons.f("bla", Jsons.v("hallo")),
                                   Jsons.f("remove", Jsons.jz),
                                   Jsons.f("array", Jsons.a(Jsons.v("sad,,,asd"),
                                                            Jsons.v(10),
                                                            Jsons.jz,
                                                            Jsons.v(20.34),
                                                            Jsons.v(true),
                                                            Jsons.j(Jsons.f("key", Jsons.v(true))))));
    final String json = serializer.toJson(o);
    System.out.println(json);
    JsonAssert.with(json).assertThat("$.key", equalTo(10))
            .assertThat("$.bla", equalTo("hallo"))
            .assertNull("$.remove")
            .assertThat("$.array", hasSize(5))
            .assertThat("$.array", hasItems("sad,,,asd", 10, 20.34, true))
            .assertThat("$.array[4].key", equalTo(true));
    JsonAssert.with(serializer.toJson(Jsons.j(Jsons.f("a", Jsons.v(10)), Jsons.f("b", Jsons.v(20))).override(Jsons.j(Jsons.f("a", Jsons.v(30))))))
            .assertThat("$.a", equalTo(30))
            .assertThat("$.b", equalTo(20));
    JsonAssert.with(serializer.toJson(Jsons.j(Jsons.f("a", Jsons.v(10)), Jsons.f("b", Jsons.v(20))).merge(Jsons.j(Jsons.f("a", Jsons.v(30))))))
            .assertThat("$.a", contains(10, 30))
            .assertThat("$.a", hasSize(2))
            .assertThat("$.b", equalTo(20));
  }

  @Test
  public void testAppend() {
    final JArrayWrite a = Jsons.a(Jsons.v(10), Jsons.v(20));
    final JArrayWrite b = Jsons.a(Jsons.v(20), Jsons.v(30));
    assertEquals(4, ListBuilders.SIA.mk(a.append(b)).size());
    assertEquals(2, ListBuilders.SIA.mk(a).size());
    assertEquals(2, ListBuilders.SIA.mk(b).size());
    assertEquals(ListBuilders.LIA.<Object>mk(10, 20, 20, 30), $(a.append(b)).bind(Jsons.valueOfPrimitiveFn).toList());
  }

  @Test
  public void testMerge() {
    final JObjectWrite a = Jsons.j(Jsons.f("name", Jsons.v("karl")), Jsons.f("surname", Jsons.v("lagerfeld")));
    final JObjectWrite b = Jsons.j(Jsons.f("name", Jsons.v("karl")), Jsons.f("surname", Jsons.v("krause")), Jsons.f("city", Jsons.v("herne")));
    System.out.println($(a.override(b)).map(serializer.jFieldToJson()).mkString(","));
    assertEquals(3, ListBuilders.SIA.mk(a.override(b)).size());
    assertEquals(2, ListBuilders.SIA.mk(a).size());
    assertEquals(3, ListBuilders.SIA.mk(b).size());
    assertThat($(a.override(b)).map(Jsons.valueOfFieldFn).bind(Jsons.valueOfPrimitiveFn).toList(),
               Matchers.<Object>containsInAnyOrder("karl", "krause", "herne"));
  }
}
