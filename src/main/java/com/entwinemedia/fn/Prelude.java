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

import static com.entwinemedia.fn.Equality.eq;

/**
 * The prelude contains general purpose functions.
 */
public final class Prelude {
  private Prelude() {
  }

  /**
   * Java is not able to determine the exhaustiveness of a match. Use this function to throw a defined error and to
   * improve readability.
   */
  public static <A> A unexhaustiveMatch() {
    throw new Error("Unexhaustive match");
  }

  /**
   * Java is not able to determine the exhaustiveness of a match. Use this function to throw a defined error and to
   * improve readability.
   */
  public static <A> A unexhaustiveMatch(Object unmatched) {
    throw new Error("Unexhaustive match: " + unmatched);
  }

  /**
   * Java is not able to determine the exhaustiveness of a match. Use this function to throw a defined error and to
   * improve readability.
   */
  public static Error unexhaustiveMatchError() {
    return new Error("Unexhaustive match");
  }

  public static <A> A notYetImplemented() {
    throw new Error("not yet implemented");
  }

  /** Sleep for a while. Returns false if interrupted. */
  public static boolean sleep(long ms) {
    try {
      Thread.sleep(ms);
      return true;
    } catch (InterruptedException ignore) {
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  private static <T extends Throwable, A> A castGeneric(Throwable t) throws T {
    // Actually the cast to T does not happen here but _after_ returning from the method at _assignment_ time.
    // But variable assignment never happens since the Throwable is thrown.
    throw (T) t;
  }

  /**
   * Throw a checked exception like a RuntimeException removing any needs to declare a throws clause.
   * <p/>
   * This technique has been described by James Iry at
   * http://james-iry.blogspot.de/2010/08/on-removing-java-checked-exceptions-by.html
   */
  public static <A> A chuck(Throwable t) {
    return Prelude.<RuntimeException, A> castGeneric(t);
  }

  /** {@link #chuck(Throwable)} as a function. */
  public static <A extends Throwable, B> Fn<A, B> chuck() {
    return new Fn<A, B>() {
      @Override public B def(Throwable throwable) {
        return chuck(throwable);
      }
    };
  }

  /** Cast from A to B with special treatment of the Number classes. */
  @SuppressWarnings("unchecked")
  public static <A, B> B cast(A v, Class<B> to) {
    if (v instanceof Number) {
      if (eq(Integer.class, to)) {
        return (B) ((Object) (((Number) v).intValue()));
      } else if (eq(Long.class, to)) {
        return (B) ((Object) (((Number) v).longValue()));
      } else if (eq(Double.class, to)) {
        return (B) ((Object) (((Number) v).doubleValue()));
      } else if (eq(Float.class, to)) {
        return (B) ((Object) (((Number) v).floatValue()));
      } else if (eq(Short.class, to)) {
        return (B) ((Object) (((Number) v).shortValue()));
      } else if (eq(Byte.class, to)) {
        return (B) ((Object) (((Number) v).byteValue()));
      }
    }
    if (to.isAssignableFrom(v.getClass())) {
      return (B) v;
    } else {
      throw new ClassCastException(v.getClass().getName() + " is not of type " + to.getName());
    }
  }
}
