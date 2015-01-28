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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ListBuildersTest {
  @Test
  public void testStrictImmutableArrayListBuilder() {
    final ListBuilder l = ListBuilders.SIA;
    final Integer[] array = new Integer[] {1, 2, 3};
    final List<Integer> list = l.mk(array);
    assertEquals("Produced list supports equals", list, l.mk(array));
    assertNotSame("List builder always produces a new list", list, l.mk(array));
    assertEquals("List builder yields an immutable list", ImmutableListWrapper.class, l.mk(array).getClass());
    assertEquals("Wrapped class is an ArrayList", ArrayList.class, ((ImmutableListWrapper) l.mk(array)).getWrapped().getClass());
    array[0] = 2;
    assertNotEquals("Underlying array can be modified safely", list, l.mk(array));
  }

  @Test
  public void testLooseImmutableArrayListBuilder() {
    final ListBuilder l = ListBuilders.LIA;
    final Integer[] array = new Integer[] {1, 2, 3};
    final List<Integer> list = l.mk(array);
    assertEquals("Produced list supports equals", list, l.mk(array));
    assertNotSame("List builder always produces a new list", list, l.mk(array));
    assertEquals("List builder yields an ImmutableArrayAdapter", ImmutableListArrayAdapter.class, l.mk(array).getClass());
    array[0] = 2;
    assertEquals("Modifying the underlying array is not safe", list, l.mk(array));
  }
}
