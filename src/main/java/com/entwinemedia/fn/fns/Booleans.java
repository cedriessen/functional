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
import com.entwinemedia.fn.Pred;

import javax.annotation.ParametersAreNonnullByDefault;

/** Functions on booleans. */
@ParametersAreNonnullByDefault
public final class Booleans {
  private static final Pred yes = new Pred() {
    @Override public Boolean def(Object a) {
      return true;
    }
  };

  @SuppressWarnings("unchecked")
  public static <A> Pred<A> yes() {
    return yes;
  }

  @SuppressWarnings("unchecked")
  private static final Pred no = not(yes);

  @SuppressWarnings("unchecked")
  public static <A> Pred<A> no() {
    return no;
  }

  public static <A extends Comparable<A>, B extends A> Fn2<A, B, Boolean> lt() {
    return new Fn2<A, B, Boolean>() {
      @Override public Boolean def(A a, B b) {
        return a.compareTo(b) < 0;
      }
    };
  }

  public static <A extends Comparable<A>, B extends A> Pred<A> lt(final B b) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return a.compareTo(b) < 0;
      }
    };
  }

  public static <A extends Comparable<A>, B extends A> Fn2<A, B, Boolean> le() {
    return new Fn2<A, B, Boolean>() {
      @Override public Boolean def(A a, B b) {
        return a.compareTo(b) <= 0;
      }
    };
  }

  public static <A extends Comparable<A>, B extends A> Pred<A> le(final B b) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return a.compareTo(b) <= 0;
      }
    };
  }

  public static <A extends Comparable<A>, B extends A> Fn2<A, B, Boolean> gt() {
    return new Fn2<A, B, Boolean>() {
      @Override public Boolean def(A a, B b) {
        return a.compareTo(b) > 0;
      }
    };
  }

  public static <A extends Comparable<A>, B extends A> Pred<A> gt(final B b) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return a.compareTo(b) > 0;
      }
    };
  }

  public static <A extends Comparable<A>, B extends A> Fn2<A, B, Boolean> ge() {
    return new Fn2<A, B, Boolean>() {
      @Override public Boolean def(A a, B b) {
        return a.compareTo(b) >= 0;
      }
    };
  }

  public static <A extends Comparable<A>, B extends A> Pred<A> ge(final B b) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return a.compareTo(b) >= 0;
      }
    };
  }

  public static <A, B> Fn2<A, B, Boolean> eq() {
    return new Fn2<A, B, Boolean>() {
      @Override public Boolean def(A a, B b) {
        return a.equals(b);
      }
    };
  }

  public static <A> Pred<A> eq(final A b) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return a.equals(b);
      }
    };
  }

  public static <A, B> Fn2<A, B, Boolean> ne() {
    return new Fn2<A, B, Boolean>() {
      @Override public Boolean def(A a, B b) {
        return !a.equals(b);
      }
    };
  }
  public static <A> Pred<A> ne(final A b) {
    return not(eq(b));
  }

  /** Create a function that checks if argument 1 is an instance of argument 2. */
  public static Fn2<Object, Class<?>, Boolean> isInstanceOf() {
    return new Fn2<Object, Class<?>, Boolean>() {
      @Override public Boolean def(Object a, Class<?> b) {
        return b.isAssignableFrom(a.getClass());
      }
    };
  }

  /** Create a predicate that checks if its argument is an instance of <code>b</code>. */
  public static Pred<Object> isInstanceOf(final Class<?> b) {
    return new Pred<Object>() {
      @Override public Boolean def(Object a) {
        return b.isAssignableFrom(a.getClass());
      }
    };
  }

  public static <A> Pred<A> or(final Fn<? super A, Boolean> p1, final Fn<? super A, Boolean> p2) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return p1.apply(a) || p2.apply(a);
      }
    };
  }

  public static <A> Pred<A> and(final Fn<? super A, Boolean> p1, final Fn<? super A, Boolean> p2) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return p1.apply(a) && p2.apply(a);
      }
    };
  }

  public static <A> Pred<A> not(final Fn<? super A, Boolean> p) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        return !p.apply(a);
      }
    };
  }

  public static Fn<Boolean, Boolean> not = new Fn<Boolean, Boolean>() {
    @Override public Boolean def(Boolean a) {
      return !a;
    }
  };

  @SafeVarargs
  public static <A> Pred<A> all(final Fn<? super A, Boolean>... ps) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        for (Fn<? super A, Boolean> p : ps) {
          if (!p.apply(a)) {
            return false;
          }
        }
        return true;
      }
    };
  }

  @SafeVarargs
  public static <A> Pred<A> one(final Fn<? super A, Boolean>... ps) {
    return new Pred<A>() {
      @Override public Boolean def(A a) {
        for (Fn<? super A, Boolean> p : ps) {
          if (p.apply(a)) {
            return true;
          }
        }
        return false;
      }
    };
  }

  public static Fn<String, Boolean> parseBoolean = new Fn<String, Boolean>() {
    @Override public Boolean def(String s) {
      return Boolean.parseBoolean(s);
    }
  };
}
