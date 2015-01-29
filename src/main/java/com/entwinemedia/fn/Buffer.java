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

import java.util.ArrayList;

/**
 * @param <C> the container type, e.g. ArrayList<A>
 */
public abstract class Buffer<C, A> {
  public abstract Buffer<C, A> append(A a);
  public abstract Buffer<C, A> concat(Buffer<? extends C, ? extends A> a);
  public abstract Buffer<C, A> concat(C a);

  public abstract C buf();

  public static <A> Buffer<ArrayList<A>, A> arrayList() {
    final ArrayList<A> buf = new ArrayList<A>();
    return new Buffer<ArrayList<A>, A>() {
      @Override public Buffer<ArrayList<A>, A> append(A a) {
        buf.add(a);
        return this;
      }

      @Override public Buffer<ArrayList<A>, A> concat(Buffer<? extends ArrayList<A>, ? extends A> a) {
        buf.addAll(a.buf());
        return this;
      }

      @Override public Buffer<ArrayList<A>, A> concat(ArrayList<A> a) {
        buf.addAll(a);
        return this;
      }

      @Override public ArrayList<A> buf() {
        return buf;
      }
    };
  }
}
