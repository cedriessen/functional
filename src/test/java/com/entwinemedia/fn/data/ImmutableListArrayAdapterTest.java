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

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ImmutableListArrayAdapterTest {
  @Test
  public void testEquality() {
    final List<Integer> a = new ArrayList<Integer>();
    a.add(1);
    a.add(2);
    a.add(3);
    assertEquals(a, ImmutableListArrayAdapter.mk(1, 2, 3));
  }

  @Test
  public void testSlice() {
    final List<Integer> slice1 = ImmutableListArrayAdapter.mk(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).subList(1, 5);
    assertEquals(4, slice1.size());
    assertEquals(slice1, ListBuilders.LIA.mk(2, 3, 4, 5));
    final List<Integer> slice2 = slice1.subList(2, 4);
    assertEquals(2, slice2.size());
    assertEquals(slice2, ListBuilders.LIA.mk(4, 5));
    final List<Integer> slice3 = slice2.subList(0, 1);
    assertEquals(1, slice3.size());
    assertEquals(slice3, ListBuilders.LIA.mk(4));
    final List<Integer> slice4 = slice3.subList(1, 1);
    assertEquals(0, slice4.size());
    assertEquals(slice4, ListBuilders.LIA.<Integer>nil());
  }
}
