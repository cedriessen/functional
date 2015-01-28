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

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static com.entwinemedia.fn.Prelude.cast;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class PreludeTest {
  @Test
  @Parameters
  public void testCast(Object from, Class to, Object expected) {
    Object casted = cast(from, to);
    assertTrue(expected.getClass().equals(casted.getClass()));
    assertEquals(expected, casted);
  }

  private Object[] parametersForTestCast() {
    return $($(1d, Integer.class, 1),
             $(1f, Double.class, 1d),
             $((byte) 1, Integer.class, 1),
             $(1, Byte.class, (byte) 1),
             $("x", Object.class, "x"),
             $(1d, Short.class, (short) 1),
             $((short) 1, Float.class, 1f),
             $((short) 1, Byte.class, (byte) 1));
  }

  @Test(expected = ClassCastException.class)
  @Parameters
  public void testCastException(Object from, Class to) {
    cast(from, to);
  }

  private Object[] parametersForTestCastException() {
    return $($(1d, String.class),
             $("bla", Integer.class),
             $(new Exception(), Error.class));
  }
}
