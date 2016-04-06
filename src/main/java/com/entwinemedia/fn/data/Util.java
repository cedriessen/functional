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

import com.entwinemedia.fn.Fn2;
import com.entwinemedia.fn.StreamFold;

import java.util.Collection;

public final class Util {
  private Util() {
  }

  private static final Fn2<Integer, Collection, Integer> sumSizeFn = new Fn2<Integer, Collection, Integer>() {
    @Override public Integer apply(Integer sum, Collection xs) {
      return sum + xs.size();
    }
  };

  public static final StreamFold<Collection, Integer> sumSizeFold = StreamFold.foldl(0, sumSizeFn);

  /**
   * Create a string that could be returned by wrappers.
   */
  static String createToString(Object wrapperInstance, Object wrappedInstance) {
    return wrapperInstance.getClass().getSimpleName() +
        "@" + System.identityHashCode(wrapperInstance) +
        "{" + wrappedInstance.toString() + "}";
  }
}
