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

package com.entwinemedia.fn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.entwinemedia.fn.data.Opt;
import org.junit.Test;

import java.io.IOException;

public class FnsTest {
  @Test
  public void testVary() throws Exception {
    final Fn<String, Number> f = Fns.<String, Number>vy(new Fn<Object, Integer>() {
      @Override public Integer def(Object s) {
        return s.hashCode() + s.hashCode();
      }
    });
    assertTrue(f.apply("bla") instanceof Number);
  }

  @Test
  public void testFn() {
    final Fn<Integer, String> f = new Fn<Integer, String>() {
      @Override public String def(Integer integer) throws IOException {
        throw new IOException();
      }
    };
    try {
      f.apply(10);
      fail();
    } catch (Exception ignore) {
    }
    assertEquals(Opt.<String>none(), f.tryOpt().apply(10));
  }

  @Test
  public void partial() throws Exception {
    final PartialFn<String, String> abc = new PartialFn<String, String>() {
      @Override protected String defPartial(String s) throws Exception {
        return s.startsWith("abc") ? s : null;
      }
    };
    final PartialFn<String, String> bcd = new PartialFn<String, String>() {
      @Override protected String defPartial(String s) throws Exception {
        return s.startsWith("bcd") ? s : null;
      }
    };
    try {
      abc.apply("bla");
      fail();
    } catch (DomainException expect) {
    }
    assertEquals(abc.apply("abc"), "abc");
    assertTrue(abc.isDefinedAt("abc"));
    assertFalse(abc.isDefinedAt("bcd"));
    assertEquals("bcd", abc.or(bcd).apply("bcd"));
    assertTrue("bcd", abc.or(bcd).isDefinedAt("bcd"));
    assertTrue("bcd", abc.or(bcd).isDefinedAt("abc"));
    assertFalse("bcd", abc.or(bcd).isDefinedAt("cde"));
    try {
      abc.or(bcd).apply("cde");
      fail();
    } catch (DomainException expect) {
    }
    assertEquals(Opt.none(), abc.lift().apply("bcd"));
    assertEquals(Opt.some("abc"), abc.lift().apply("abc"));
    assertEquals(Opt.some("abc"), abc.or(bcd).lift().apply("abc"));
  }
}
