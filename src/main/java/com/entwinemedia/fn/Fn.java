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

import static com.entwinemedia.fn.Stream.$;

import com.entwinemedia.fn.data.Opt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/** Function from A to B. */
public abstract class Fn<A, B> {
  /** Function application. */
  public abstract B ap(A a);

  /** Function composition. */
  public <C> Fn<C, B> o(final Fn<? super C, ? extends A> g) {
    return Fns.o(this, g);
  }

  /** Left to right function composition. */
  public <C> Fn<A, C> then(final Fn<? super B, ? extends C> g) {
    return Fns.then(this, g);
  }

  /** Turn this function into an effect by ignoring the functions result. */
  public Fx<A> toFx() {
    return Fns.toFx(this);
  }

  /** Convert this function into a partial function. */
  public PartialFn<A, B> toPartial() {
    return Fns.toPartial(this);
  }

  /** Apply this function and return its result wrapped in a some. Any exception returns a none. */
  public Fn<A, Opt<B>> tryOpt() {
    return Fns.tryOpt(this);
  }

  @Override public String toString() {
    try {
      final Type t = this.getClass().getGenericSuperclass();
      if (t instanceof ParameterizedType) {
        return $(((ParameterizedType) t).getActualTypeArguments()).mkString(" => ");
      } else {
        return super.toString();
      }
    } catch (Exception ignore) {
      return super.toString();
    }
  }
}
