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

import java.util.Comparator;

/** Collection of {@link Comparator}s. */
public final class Orders {
  private Orders() {
  }

  public static <A extends Comparable<A>> Comparator<A> naturalOrder() {
    return new Comparator<A>() {
      @Override public int compare(A a, A b) {
        return a.compareTo(b);
      }
    };
  }

  /** Create a new comparator reversing the order of <code>c</code>. */
  public static <A> Comparator<A> reverse(final Comparator<A> c) {
    return new Comparator<A>() {
      @Override public int compare(A a, A b) {
        return c.compare(a, b) * -1;
      }
    };
  }

  public static final Comparator<Integer> intNaturalOrder = naturalOrder();
  public static final Comparator<Double> dblNaturalOrder = naturalOrder();
  public static final Comparator<Long> longNaturalOrder = naturalOrder();
  public static final Comparator<Boolean> boolNaturalOrder = naturalOrder();
  public static final Comparator<String> stringNaturalOrder = naturalOrder();
}
