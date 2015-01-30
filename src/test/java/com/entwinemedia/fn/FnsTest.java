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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.entwinemedia.fn.data.Opt;
import org.junit.Test;

import java.io.IOException;

public class FnsTest {
  @Test
  public void testVary() throws Exception {
    final Fn<String, Number> f = Fns.<String, Number>vy(new Fn<Object, Integer>() {
      @Override public Integer ap(Object s) {
        return s.hashCode() + s.hashCode();
      }
    });
    assertTrue(f.ap("bla") instanceof Number);
  }

  @Test
  public void testFnX() {
    final Fn<Integer, String> f = new FnX<Integer, String>() {
      @Override public String apx(Integer integer) throws IOException {
        throw new IOException();
      }
    };
    try {
      f.ap(10);
      fail();
    } catch (Exception ignore) {
    }
    assertEquals(Opt.<String>none(), f.tryOpt().ap(10));
  }
}
