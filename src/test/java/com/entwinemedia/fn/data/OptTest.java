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

package com.entwinemedia.fn.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.entwinemedia.fn.data.Opt.none;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class OptTest {

//  @Test
//  public void testOption() {
//    Opt<String> s = some("test");
//    Opt<String> n = none();
//    assertTrue(s.isSome());
//    assertTrue(n.isNone());
//    assertEquals("a test", s.fold(new Match<String, String>() {
//      public String some(String s) {
//        return "a " + s;
//      }
//
//      public String none() {
//        return "";
//      }
//    }));
//    assertEquals("none", n.fold(new Match<String, String>() {
//      public String some(String s) {
//        return s;
//      }
//
//      public String none() {
//        return "none";
//      }
//    }));
//    for (String x : n) {
//      fail("should not happen");
//    }
//    String r = null;
//    for (String x : s) {
//      r = x;
//    }
//    assertEquals("test", r);
//    assertEquals("test", s.or(""));
//    assertEquals("", n.or(""));
//    assertTrue(s.map(Strings.len).or(-1) == 4);
//    assertTrue(n.map(Strings.len).or(-1) == -1);
//  }

  /**
   * Test the hash and equals methods.
   */
  @Test
  public void testHashEquals() {
    Opt<String> a = Opt.some("a");
    Opt<String> a1 = Opt.some("a");
    Opt<String> b = Opt.some("b");
    Opt<String> c = Opt.some("c");
    Opt<String> n = Opt.none();
    assertTrue(a.equals(a1));
    assertFalse(a.equals(b));
    assertFalse(b.equals(c));
    assertFalse(c.equals(n));
    Set<Opt<String>> set = new HashSet<Opt<String>>();
    set.add(a);
    assertTrue(set.contains(a));
    assertEquals(1, set.size());
    set.add(b);
    assertTrue(set.contains(b));
    assertEquals(2, set.size());
    set.add(c);
    assertTrue(set.contains(c));
    assertEquals(3, set.size());
    set.add(n);
    assertTrue(set.contains(n));
    assertEquals(4, set.size());
    //
    set.remove(n);
    assertFalse(set.contains(n));
    assertEquals(3, set.size());
    set.remove(c);
    assertFalse(set.contains(c));
    assertEquals(2, set.size());
    set.remove(b);
    assertFalse(set.contains(b));
    assertEquals(1, set.size());
    set.remove(a);
    assertFalse(set.contains(a));
    assertEquals(0, set.size());
  }

  @Test
  public void testVavrSome() {
    assertTrue(Opt.some("x").toVavr().isDefined());
    assertEquals("x", Opt.some("x").toVavr().get());
  }

  @Test
  public void testVavrNone() {
    assertFalse(Opt.none(String.class).toVavr().isDefined());
  }
}
