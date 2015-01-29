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
import static com.entwinemedia.fn.data.Variance.vy;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class IteratorsTest {
  private static final ListBuilder l = ListBuilders.LIA;

  @Test
  public void testJoin() throws Exception {

  }

  @Test
  public void testConcatListOf1() throws Exception {
    final Iterator<Integer> i1 = l.mk(1, 2, 3).iterator();
    final Iterator<Integer> i2 = l.mk(5, 6, 7).iterator();
    final Iterator<Integer> i3 = l.mk(9, 10).iterator();
    final List<Integer> joined = l.mk(Iterators.concat(l.mk(i1, i2, i3)));
    assertEquals(l.mk(1, 2, 3, 5, 6, 7, 9, 10), joined);
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
    assertFalse(i3.hasNext());
  }

  @Test
  public void testJoin1() throws Exception {
    final Iterator<Integer> i1 = l.mk(1, 2, 3).iterator();
    final Iterator<String> i2 = l.mk("5", "6").iterator();
    final List<Object> joined = l.mk(Iterators.concat(l.mk(Variance.vy(Object.class, i1), Variance.vy(Object.class, i2))));
    assertEquals(l.<Object>mk(1, 2, 3, "5", "6"), joined);
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
  }

  @Test
  public void testJoin2() throws Exception {
    final Iterator<Integer> i1 = l.<Integer>nil().iterator();
    final Iterator<Integer> i2 = l.mk(5, 6).iterator();
    final List<Integer> joined = l.mk(Iterators.join(i1, i2));
    assertEquals(l.mk(5, 6), joined);
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
  }

  @Test
  public void testJoin3() throws Exception {
    final Iterator<Integer> i1 = l.<Integer>nil().iterator();
    final Iterator<Integer> i2 = l.<Integer>nil().iterator();
    final List<Integer> joined = l.mk(Iterators.join(i1, i2));
    assertEquals(l.<Integer>nil(), joined);
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
  }

  @Test
  public void testJoin4() throws Exception {
    final Iterator<Integer> i1 = l.<Integer>nil().iterator();
    final Iterator<Integer> i2 = l.<Integer>nil().iterator();
    final Iterator<Integer> joined = Iterators.join(i1, i2);
    assertFalse(joined.hasNext());
    assertFalse(joined.hasNext());
    assertEquals(l.<Integer>nil(), l.mk(joined));
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
  }

  @Test
  public void testConcatListOf2() throws Exception {
    final Iterator<Integer> i1 = l.mk(1, 2, 3).iterator();
    final Iterator<Integer> i2 = l.mk(5, 6, 7).iterator();
    final Iterator<Integer> i3 = l.mk(9, 10).iterator();
    final Iterator<Integer> joined = Iterators.concat(l.mk(i1, i2, i3));
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertEquals(new Integer(1), joined.next());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertEquals(l.mk(2, 3, 5, 6, 7, 9, 10), l.mk(joined));
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
    assertFalse(i3.hasNext());
    assertFalse(joined.hasNext());
  }

  @Test
  public void testConcatIteratorOf1() throws Exception {
    final Iterator<Integer> i1 = l.mk(1, 2, 3).iterator();
    final Iterator<Integer> i2 = l.mk(5, 6, 7).iterator();
    final Iterator<Integer> i3 = l.mk(9, 10).iterator();
    final Iterator<Integer> joined = Iterators.concat(l.mk(i1, i2, i3).iterator());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertEquals(new Integer(1), joined.next());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertEquals(l.mk(2, 3, 5, 6, 7, 9, 10), l.mk(joined));
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
    assertFalse(i3.hasNext());
    assertFalse(joined.hasNext());
  }

  @Test
  public void testConcatArrayOf1() throws Exception {
    final Iterator<Integer> i1 = l.mk(1, 2, 3).iterator();
    final Iterator<Integer> i2 = l.mk(5, 6, 7).iterator();
    final Iterator<Integer> i3 = l.mk(9, 10).iterator();
    final Iterator<Integer> joined = Iterators.concat(i1, i2, i3);
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertEquals(new Integer(1), joined.next());
    assertTrue(joined.hasNext());
    assertTrue(joined.hasNext());
    assertEquals(l.mk(2, 3, 5, 6, 7, 9, 10), l.mk(joined));
    assertFalse(i1.hasNext());
    assertFalse(i2.hasNext());
    assertFalse(i3.hasNext());
    assertFalse(joined.hasNext());
  }
}
