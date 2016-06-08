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

import static com.entwinemedia.fn.Prelude.chuck;

/** Effect */
public abstract class Fx<A> {
  /** Effect definition. */
  protected abstract void def(A a) throws Exception;

  /**
   * Effect application.
   * Any exception thrown during application will be caught and rethrown using {@link Prelude#chuck(Throwable)}.
   */
  public final void apply(A a) {
    try {
      def(a);
    } catch (Exception e) {
      chuck(e);
    }
  }

  public Fx<A> then(final Fx<? super A>... es) {
    return new Fx<A>() {
      @Override public void def(A a) throws Exception {
        Fx.this.apply(a);
        for (Fx<? super A> e : es) {
          e.apply(a);
        }
      }
    };
  }

  public Fn<A, Unit> toFn() {
    return new Fn<A, Unit>() {
      @Override public Unit def(A a) throws Exception {
        Fx.this.apply(a);
        return Unit.unit;
      }
    };
  }
}
