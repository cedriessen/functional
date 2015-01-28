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

import java.util.List;

public interface ListFactory {
  /** Create an empty list. */
  <A> List<A> nil();

  /** Create a new list buffer. */
  <A> List<A> buffer();

  /** Create a new list buffer of a given size. */
  <A> List<A> buffer(int size);

  /** Turn the list buffer into a list that can be returned to the {@link ListBuilder}'s client. */
  <A> List<A> toList(List<A> buf);
}

