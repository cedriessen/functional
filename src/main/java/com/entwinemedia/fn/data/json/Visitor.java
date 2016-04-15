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

package com.entwinemedia.fn.data.json;

/**
 * Created by ced on 15.04.16.
 */
public interface Visitor<A> {
  A visit(A fold, JString a);
  A visit(A fold, JNumber a);
  A visit(A fold, JBoolean a);
  A visit(A fold, JNull a);
  A visit(A fold, JObject a);
  A visit(A fold, JArray a);
  A visit(A fold, Zero a);
}
