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

/**
 * A partial function. Partial functions are not defined for any possible value of the type they accept.
 * That means their domain is a subset of the domain of the type.
 * <p/>
 * In case the function is not defined for a value {@code x} a {@link DomainException} is raised.
 */
public abstract class PartialFn<A, B> extends Fn<A, B> {
  /** @return null to indicate that the function is not defined at <code>a</code>. */
  protected abstract B defPartial(A a) throws Exception;

  public boolean isDefinedAt(A a) {
    try {
      apply(a);
      return true;
    } catch (DomainException e) {
      return false;
    }
  }

  @Override protected final B def(A a) throws Exception {
    final B b = defPartial(a);
    if (b != null) {
      return b;
    } else {
      throw new DomainException("Partial function " + this + " is not defined at " + a);
    }
  }

  public PartialFn<A, B> or(final PartialFn<? super A, ? extends B> f) {
    return new PartialFn<A, B>() {
      @Override public B defPartial(A a) throws Exception {
        final B b = PartialFn.this.defPartial(a);
        return b != null ? b : f.apply(a);
      }
    };
  }

  /** Convert into a function that returns an option. */
  public Fn<A, Opt<B>> lift() {
    return new Fn<A, Opt<B>>() {
      @Override public Opt<B> def(A a) throws Exception {
        return Opt.nul(PartialFn.this.defPartial(a));
      }
    };
  }
}
