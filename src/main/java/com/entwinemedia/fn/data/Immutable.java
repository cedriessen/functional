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

/**
 * Marker interface for immutable data structures. Implementations must only guarantee immutability
 * by their own means. For example if a data structure simply acts as a wrapper it must <em>not</em> copy the
 * original structure to prevent mutability that may happen by modifying the wrapped structure.
 * The client of such an "immutable" data structure has to make sure that this modifying from behind does not happen.
 */
public interface Immutable {
}
