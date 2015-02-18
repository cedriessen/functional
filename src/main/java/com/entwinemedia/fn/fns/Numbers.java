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

package com.entwinemedia.fn.fns;

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Fn2;

public final class Numbers {
  public static Fn<Number, String> toString = new Fn<Number, String>() {
    @Override public String apply(Number number) {
      return number.toString();
    }
  };

  public static Fn2<Integer, Integer, Integer> intPlus = new Fn2<Integer, Integer, Integer>() {
    @Override public Integer apply(Integer a, Integer b) {
      return a + b;
    }
  };

  public static Fn2<Double, Double, Double> doublePlus = new Fn2<Double, Double, Double>() {
    @Override public Double apply(Double a, Double b) {
      return a + b;
    }
  };

  public static Fn2<Integer, Integer, Integer> intMinus = new Fn2<Integer, Integer, Integer>() {
    @Override public Integer apply(Integer a, Integer b) {
      return a - b;
    }
  };

  public static Fn2<Double, Double, Double> doubleMinus = new Fn2<Double, Double, Double>() {
    @Override public Double apply(Double a, Double b) {
      return a - b;
    }
  };

  public static Fn2<Integer, Integer, Integer> intMult = new Fn2<Integer, Integer, Integer>() {
    @Override public Integer apply(Integer a, Integer b) {
      return a * b;
    }
  };

  public static Fn2<Double, Double, Double> doubleMult = new Fn2<Double, Double, Double>() {
    @Override public Double apply(Double a, Double b) {
      return a * b;
    }
  };

  public static Fn2<Integer, Integer, Integer> intDiv = new Fn2<Integer, Integer, Integer>() {
    @Override public Integer apply(Integer a, Integer b) {
      return a / b;
    }
  };

  public static Fn2<Double, Double, Double> doubleDiv = new Fn2<Double, Double, Double>() {
    @Override public Double apply(Double a, Double b) {
      return a / b;
    }
  };
}
