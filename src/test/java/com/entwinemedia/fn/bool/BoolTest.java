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

package com.entwinemedia.fn.bool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.entwinemedia.fn.data.ListBuilders.LIA;
import static com.entwinemedia.fn.parser.Parsers.fnIgnorePrevious;
import static com.entwinemedia.fn.parser.Parsers.regex;
import static com.entwinemedia.fn.parser.Parsers.token;
import static com.entwinemedia.fn.parser.Parsers.yield;

import com.entwinemedia.fn.parser.Result;

import org.junit.Test;

import java.util.regex.Pattern;

public class BoolTest {
  final Bool b = new Bool(LIA.mk(token(regex(Pattern.compile("\\$\\{.*?\\}"))).bind(fnIgnorePrevious(yield(false)))));

  @Test
  public void testEval() throws Exception {
    test(false, "(false OR   true ) and (False oR (FAlse and true))");
    test(true, "(false  OR true ) and (true and true or false)");
    test(false, "!(false OR not !!false)");
    test(false, "!(${bla} OR not !!false)");
    test(true, "!true or true");
    test(true, "!true or !false");
    test(true, "!false and !false");
    test(true, "!false or !true");
    test(true, "!(false or !true)");
    test(true, "true and false or true");
    test(true, "true or false and true");
    test(true, "20 > 10");
    test(false, "20 > 10 and false");
    test(false, "20 > 10 and 10 > 20");
    test(true, "10 < 20");
    test(true, "10 >= 10");
    test(true, "10 >= 10 and (20 < 30)");
    test(true, "10 + 3 < 20");
    test(false, "10 + 10 < 20");
    test(true, "9.9 + 10.1 < 20.0 and false or 10 * 20.0 == 200");
    test(true, "100 / 3 < 33.5");
    test(false, "100 / 3 < 33.332");
    test(false, "${var}");
    test(false, "${var} and true");
    test(false, "true and ${var}");
    test(false, "false or ${var}");
    test(false, "${param} and ${var}");
  }

  private void test(boolean expected, String expr) {
    final Result<Boolean> r = b.eval(expr);
    assertTrue(r.isDefined());
    assertEquals(expected, r.getResult());
    assertTrue(r.getRest().isEmpty());
  }
}
