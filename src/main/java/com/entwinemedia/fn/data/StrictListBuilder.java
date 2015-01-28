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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class StrictListBuilder extends ListBuilderBase {
  public StrictListBuilder(ListFactory f) {
    super(f);
  }

  @Override public <A> List<A> mk(A... xs) {
    return ListBuilderUtils.createNew(f, xs);
  }

  @Override public <A> List<A> mk(Collection<? extends A> xs) {
    return ListBuilderUtils.createNew(f, xs);
  }

  @Override public <A> List<A> mk(Iterator<? extends A> xs) {
    return ListBuilderUtils.createNew(f, xs);
  }

  @Override public <A> List<A> mk(Iterable<? extends A> xs) {
    return ListBuilderUtils.createNew(f, xs);
  }

  @Override public <A> List<A> mk(int size, Iterator<? extends A> xs) {
    return ListBuilderUtils.createNew(f, size, xs);
  }

  @Override public <A> List<A> mk(int size, Iterable<? extends A> xs) {
    return ListBuilderUtils.createNew(f, size, xs);
  }
}
