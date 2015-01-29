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

import java.util.ArrayList;
import java.util.List;

public class MutableArrayListFactory implements ListFactory {
  @Override
  public <A> List<A> nil() {
    return new ArrayList<A>();
  }

  @Override public <A> List<A> buffer() {
    return new ArrayList<A>();
  }

  @Override public <A> List<A> buffer(int size) {
    return new ArrayList<A>(size);
  }

  @Override public <A> List<A> toList(List<A> buf) {
    return buf;
  }
}
