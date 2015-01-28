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

package com.entwinemedia.fn.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.entwinemedia.fn.bool.Expression.and;
import static com.entwinemedia.fn.bool.Expression.not;
import static com.entwinemedia.fn.bool.Expression.or;
import static com.entwinemedia.fn.bool.Expression.value;

import com.entwinemedia.fn.data.ListBuilder;
import com.entwinemedia.fn.data.ListBuilders;

import org.junit.Test;

import java.util.regex.Pattern;

public class ParserTest {
  private static final ListBuilder l = ListBuilders.LIA;

  @Test
  public void testEval() {
    assertTrue(or(and(value(true), value(false)), value(true)).eval());
    assertTrue(or(and(value(false), and(value(true), value(false))), value(true)).eval());
    assertTrue(value(true).and(value(false).or(value(true))).eval());
    assertFalse(not(value(true).and(value(false).or(value(true)))).eval());
  }

  @Test
  public void testParser() {
    assertFalse(Parsers.string("bla").parse("xblabla").isDefined());
    assertEquals(l.mk("bla", "bla", "bla"), Parsers.many1(Parsers.string("bla")).parse("blablablaxyz").getResult());
    assertFalse(Parsers.many1(Parsers.string("bla")).parse("bablablaxyz").isDefined());
    assertEquals(l.mk("bla"), Parsers.many1(Parsers.string("bla")).parse("blabablaxyz").getResult());
    assertTrue(Parsers.bool.parse("true").getResult());
    assertFalse(Parsers.bool.parse("false").getResult());
    assertEquals("false ", Parsers.skipSpaces.parse("    false ").getRest());
    assertEquals("false ", Parsers.skipSpaces.parse("false ").getRest());
    assertEquals("", Parsers.bool.parse("  false  ").getRest());
    assertEquals(false, Parsers.bool.parse("  false  ").getResult());
    assertEquals(l.mk(false, true, false), Parsers.many1(Parsers.bool).parse("   false true  false ").getResult());
    assertEquals("false", Parsers.regex(Pattern.compile("(true|false)")).parse("false").getResult());
    assertEquals("true", Parsers.regex(Pattern.compile("(true|false)")).parse("true").getResult());
    assertFalse(Parsers.regex(Pattern.compile("(true|false)")).parse("TRUE").isDefined());
    assertEquals(new Integer(1511), Parsers.integer.parse("1511").getResult());
    assertEquals(new Integer(-1511), Parsers.integer.parse("-1511").getResult());
    assertEquals(new Double(1511197012313123d), Parsers.dbl.parse("1511197012313123").getResult());
    assertEquals(new Double(1511197012313.123), Parsers.dbl.parse("1511197012313.123").getResult());
    assertEquals(new Double(15.11), Parsers.dbl.parse("15.11").getResult());
    assertEquals(new Double(1764.97813), Parsers.dbl.parse("1764.97813").getResult());
    assertEquals(new Double(1764), Parsers.dbl.parse("1764").getResult());
    assertEquals(new Double(-1511.19), Parsers.dbl.parse("-1511.19").getResult());
    assertEquals(new Double(-1.10319), Parsers.dbl.parse("-1.103190000000000").getResult());
    assertFalse(Parsers.dbl.parse("-1511.-20").getRest().isEmpty());
  }
}
