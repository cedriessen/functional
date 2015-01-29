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

package com.entwinemedia.fn.fns;

import static org.junit.Assert.assertEquals;
import static com.entwinemedia.fn.Stream.$;

import com.entwinemedia.fn.data.ListBuilder;
import com.entwinemedia.fn.data.ListBuilders;
import com.entwinemedia.fn.data.Opt;

import org.junit.Assert;
import org.junit.Test;

public class StringsTest {
  private static final ListBuilder l = ListBuilders.SIA;

  @Test
  public void testSplitStream() throws Exception {
    assertEquals(l.mk("this", "that", "apples", "oranges"),
                 $("this,that", "apples,oranges").bind(Strings.split(",")).toList());
    assertEquals(l.mk("this", "apples"),
                 $("this", "apples").bind(Strings.split(",")).toList());
    assertEquals(l.mk(""),
                 $("").bind(Strings.split(",")).toList());
    assertEquals(l.mk("this"),
                 $("this,").bind(Strings.split(",")).toList());
    assertEquals(l.mk("", "this"),
                 $(",this").bind(Strings.split(",")).toList());
    assertEquals(l.mk("", "this"),
                 $(",this,,,").bind(Strings.split(",")).toList());
    System.out.println($(",this,,, ").bind(Strings.split(",")).map(Strings.format("(%s)")).mkString(","));
    assertEquals(l.mk("", "this", "", "", " "),
                 $(",this,,, ").bind(Strings.split(",")).toList());
    assertEquals(l.mk("", "this"),
                 $(",this,,, ").bind(Strings.splitCsv).toList());
    assertEquals(l.mk("", "this", "", "", "x"),
                 $(",this,,,x").bind(Strings.split(",")).toList());
    assertEquals(l.mk("", "", "", "this"),
                 $(",,,this").bind(Strings.split(",")).toList());
    assertEquals(l.mk(" ", "this"),
                 $(",, ,this").bind(Strings.split(","))._(Strings.removeEmptySO).toList());
    assertEquals(l.mk("this"),
                 $(",, ,this").bind(Strings.splitCsv)._(Strings.removeEmptySO).toList());
    assertEquals(l.mk("this"),
                 $(",   , ,this, ").bind(Strings.splitCsv)._(Strings.removeBlankSO).toList());
  }

  @Test
  public void testBlankToNone() {
    Assert.assertEquals(Opt.<String>none(), Opt.some("").bind(Strings.blankToNone));
    Assert.assertEquals(Opt.<String>none(), Opt.some("  ").bind(Strings.blankToNone));
    Assert.assertEquals(Opt.some(" r "), Opt.some(" r ").bind(Strings.blankToNone));
  }

  @Test
  public void testTrimToNone() {
    Assert.assertEquals(Opt.<String>none(), Opt.some("").bind(Strings.trimToNone));
    Assert.assertEquals(Opt.<String>none(), Opt.some("  ").bind(Strings.trimToNone));
    Assert.assertEquals(Opt.some("r"), Opt.some(" r ").bind(Strings.trimToNone));
  }
}
