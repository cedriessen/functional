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

/** Home of some predefined {@link ListBuilder} instances. */
public final class ListBuilders {
  /** Strict immutable list builder based on {@link java.util.ArrayList}. */
  public static final StrictListBuilder SIA = new StrictListBuilder(new ImmutableArrayListFactory());

  /** Same as {@link #SIA} just with a more descriptive name. */
  public static final StrictListBuilder strictImmutableArray = SIA;

  /** Strict immutable list builder based on {@link java.util.LinkedList}. */
  public static final StrictListBuilder SIL = new StrictListBuilder(new ImmutableLinkedListFactory());

  /** Same as {@link #SIL} just with a more descriptive name. */
  public static final StrictListBuilder strictImmutableLinked = SIL;

  /** Strict mutable list builder based on {@link java.util.ArrayList}. */
  public static final StrictListBuilder SMA = new StrictListBuilder(new MutableArrayListFactory());

  /** Same as {@link #SMA} just with a more descriptive name. */
  public static final StrictListBuilder strictMutableArray = SMA;

  /** Strict mutable list builder based on {@link java.util.LinkedList}. */
  public static final StrictListBuilder SML = new StrictListBuilder(new MutableLinkedListFactory());

  /** Same as {@link #SML} just with a more descriptive name. */
  public static final StrictListBuilder strictMutableLinked = SML;

  /** Loose immutable list builder based on {@link java.util.ArrayList}. */
  public static final ImmutableLooseListBuilder LIA = new ImmutableLooseListBuilder(new ImmutableArrayListFactory());

  /** Same as {@link #LIA} just with a more descriptive name. */
  public static final ImmutableLooseListBuilder looseImmutableArray = LIA;

  /** Loose immutable list builder based on {@link java.util.LinkedList}. */
  public static final ImmutableLooseListBuilder LIL = new ImmutableLooseListBuilder(new ImmutableLinkedListFactory());

  /** Same as {@link #LIL} just with a more descriptive name. */
  public static final ImmutableLooseListBuilder looseImmutableLinked = LIL;
}
