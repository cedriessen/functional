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

package com.entwinemedia.fn.parser;

import com.entwinemedia.fn.Fn;

/** Functional style parser. */
public abstract class Parser<A> {
  /** Parse <code>input</code> to a result of type <code>A</code>. */
  public abstract Result<A> parse(String input);

  /** Monadic bind operation. */
  public <B> Parser<B> bind(final Fn<? super A, Parser<B>> p) {
    return new Parser<B>() {
      @Override public Result<B> parse(String input) {
        final Result<A> r = Parser.this.parse(input);
        return r.isDefined() ? p.apply(r.getResult()).parse(r.getRest()) : fail();
      }
    };
  }

  /** If this parser does not succeed run parser <code>p</code>. */
  public Parser<A> or(final Parser<A> p) {
    return new Parser<A>() {
      @Override public Result<A> parse(String input) {
        final Result<A> r = Parser.this.parse(input);
        return r.isDefined() ? r : p.parse(input);
      }
    };
  }

  protected Result<A> result(A result, String rest) {
    return new Result<A>(result, rest);
  }

  @SuppressWarnings("unchecked")
  private static final Result FAIL = new Result(null, null);

  @SuppressWarnings("unchecked")
  protected Result<A> fail() {
    return FAIL;
  }
}
