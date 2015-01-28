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

import com.entwinemedia.fn.*;

import java.util.Map;
import java.util.Map.Entry;

public final class Maps {
  private Maps() {
  }

  public static <A, B> Fn<Map.Entry<A, B>, P2<A, B>> toP2() {
    return new Fn<Entry<A, B>, P2<A, B>>() {
      @Override public P2<A, B> _(Entry<A, B> e) {
        return com.entwinemedia.fn.Products.E.p2(e.getKey(), e.getValue());
      }
    };
  }
}
