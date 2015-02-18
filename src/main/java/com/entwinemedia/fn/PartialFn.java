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

import com.entwinemedia.fn.data.Opt;

/** A partial function. Partial functions are not defined at any argument. */
public abstract class PartialFn<A, B> extends Fn<A, B> {
  /** @return null to indicate that the function is not defined at <code>a</code>. */
  protected abstract B partial(A a);

  public Opt<B> isDefinedAt(A a) {
    return Opt.nul(partial(a));
  }

  @Override public final B apply(A a) {
    final B b = partial(a);
    if (b != null) {
      return b;
    } else {
      throw new RuntimeException("Partial function " + this + " is not defined at " + a);
    }
  }

  public PartialFn<A, B> or(final PartialFn<? super A, ? extends B> f) {
    return new PartialFn<A, B>() {
      @Override public B partial(A a) {
        final B b = PartialFn.this.partial(a);
        return b != null ? b : f.partial(a);
      }
    };
  }

  /** Convert into a function that returns an option. */
  public Fn<A, Opt<B>> lift() {
    return new Fn<A, Opt<B>>() {
      @Override public Opt<B> apply(A a) {
        return Opt.nul(PartialFn.this.partial(a));
      }
    };
  }
}
