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

import com.entwinemedia.fn.data.ImmutableIterators;
import com.entwinemedia.fn.data.Iterators;

import java.util.Iterator;

/** A parse result. */
public final class Result<A> implements Iterable<A> {
  private final A result;
  private final String rest;

  Result(A result, String rest) {
    this.result = result;
    this.rest = rest;
  }

  public boolean isDefined() {
    return result != null;
  }

  public A getResult() {
    if (isDefined()) {
      return result;
    } else {
      throw new RuntimeException("Parse failure");
    }
  }

  public String getRest() {
    if (isDefined()) {
      return rest;
    } else {
      throw new RuntimeException("Parse failure");
    }
  }

  @Override public Iterator<A> iterator() {
    return isDefined() ? ImmutableIterators.mk(result) : Iterators.<A>empty();
  }

  @Override public String toString() {
    return isDefined() ? "(" + result + ",\"" + rest + "\")" : "FAIL";
  }
}
