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

import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImmutableListWrapperTest {
  @Test(expected = UnsupportedOperationException.class)
  public void testWrapper1() {
    final List<Integer> m = ListBuilders.SMA.mk(1, 2, 3);
    final List<Integer> i = new ImmutableListWrapper<Integer>(m);
    final Iterator<Integer> it = i.iterator();
    it.next();
    it.remove();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testWrapper2() {
    final List<Integer> m = ListBuilders.SMA.mk(1, 2, 3);
    final List<Integer> i = new ImmutableListWrapper<Integer>(m);
    final ListIterator<Integer> it = i.listIterator();
    it.next();
    it.remove();
  }
}
