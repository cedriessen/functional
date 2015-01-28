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

import com.entwinemedia.fn.data.ListBuilder;
import com.entwinemedia.fn.data.SetBuilder;
import com.entwinemedia.fn.fns.Numbers;
import com.entwinemedia.fn.fns.Strings;

import java.util.List;
import java.util.Set;

public final class Monoids {
  private Monoids() {
  }

  public static final Monoid<String> string = new Monoid<String>("", Strings.concat);

  public static final Monoid<Integer> intAddition = new Monoid<Integer>(0, Numbers.intPlus);

  public static final Monoid<Integer> intMultiplication = new Monoid<Integer>(1, Numbers.intMult);

  public static final Monoid<Double> doubleAddition = new Monoid<Double>(0.0, Numbers.doublePlus);

  public static final Monoid<Double> doubleMultiplication = new Monoid<Double>(1.0, Numbers.doubleMult);

  public static <A> Monoid<List<A>> list(final ListBuilder builder) {
    return new Monoid<List<A>>(builder.<A>nil(), new Fn2<List<A>, List<A>, List<A>>() {
      @Override
      @SuppressWarnings("unchecked")
      public List<A> ap(List<A> a, List<A> b) {
        return builder.concat2(a, b);
      }
    });
  }

  public static <A> Monoid<Set<A>> set(final SetBuilder builder) {
    return new Monoid<Set<A>>(builder.<A>empty(), new Fn2<Set<A>, Set<A>, Set<A>>() {
      @Override
      @SuppressWarnings("unchecked")
      public Set<A> ap(Set<A> a, Set<A> b) {
        return builder.concat(a, b);
      }
    });
  }
}
