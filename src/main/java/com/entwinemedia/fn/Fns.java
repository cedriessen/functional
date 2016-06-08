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

import com.entwinemedia.fn.data.Opt;

/** General purpose functions. */
public final class Fns {
  private Fns() {
  }

  private static final Fn ID = new Fn() {
    @Override public Object def(Object a) {
      return a;
    }
  };

  @SuppressWarnings("unchecked")
  public static <A> Fn<A, A> id() {
    return ID;
  }

  private static final Fx NOP = new Fx() {
    @Override public void def(Object o) {
    }
  };

  @SuppressWarnings("unchecked")
  public static <A> Fx<A> nop() {
    return NOP;
  }

  /** Function composition: <code>f . g = f(g(x)) = o(f, g)</code> */
  public static <A, B, C> Fn<A, C> o(
          final Fn<? super B, ? extends C> f,
          final Fn<? super A, ? extends B> g) {
    return new Fn<A, C>() {
      @Override
      public C def(A a) {
        return f.apply(g.apply(a));
      }
    };
  }

  /** Function composition. <code>f . g . h</code> */
  public static <A, B, C, D> Fn<A, D> o(
          final Fn<? super C, ? extends D> f,
          final Fn<? super B, ? extends C> g,
          final Fn<? super A, ? extends B> h) {
    return new Fn<A, D>() {
      @Override
      public D def(A a) {
        return f.apply(g.apply(h.apply(a)));
      }
    };
  }

  /** Function composition. <code>f . g . h . i</code> */
  public static <A, B, C, D, E> Fn<A, E> o(
          final Fn<? super D, ? extends E> f,
          final Fn<? super C, ? extends D> g,
          final Fn<? super B, ? extends C> h,
          final Fn<? super A, ? extends B> i) {
    return new Fn<A, E>() {
      @Override
      public E def(A a) {
        return f.apply(g.apply(h.apply(i.apply(a))));
      }
    };
  }

  /** Left to right function composition: <code>f then g = g . f = g(f(x))</code> */
  public static <A, B, C> Fn<A, C> then(
          final Fn<? super A, ? extends B> f,
          final Fn<? super B, ? extends C> g) {
    return new Fn<A, C>() {
      @Override
      public C def(A a) {
        return g.apply(f.apply(a));
      }
    };
  }

  /** Left to right function composition: <code>f then g then h = h . g . f</code> */
  public static <A, B, C, D> Fn<A, D> then(
          final Fn<? super A, ? extends B> f,
          final Fn<? super B, ? extends C> g,
          final Fn<? super C, ? extends D> h) {
    return new Fn<A, D>() {
      @Override
      public D def(A a) {
        return h.apply(g.apply(f.apply(a)));
      }
    };
  }

  /** Turn a function into a partial function. */
  public static <A, B> PartialFn<A, B> toPartial(final Fn<A, B> f) {
    return new PartialFn<A, B>() {
      @Override protected B defPartial(A a) {
        return f.apply(a);
      }
    };
  }

  /** Curry a function of arity 2. */
  public static <A, B, C> Fn<A, Fn<B, C>> curry(final Fn2<? super A, ? super B, ? extends C> f) {
    return new Fn<A, Fn<B, C>>() {
      @Override public Fn<B, C> def(final A a) {
        return new Fn<B, C>() {
          @Override public C def(B b) {
            return f.apply(a, b);
          }
        };
      }
    };
  }

  /** Uncurry to a function of arity 2. */
  public static <A, B, C> Fn2<A, B, C> uncurry(final Fn<A, Fn<B, C>> f) {
    return new Fn2<A, B, C>() {
      @Override public C def(A a, B b) {
        return f.apply(a).apply(b);
      }
    };
  }

  /** Flip arguments of a function of arity 2. */
  public static <A, B, C> Fn2<B, A, C> flip(final Fn2<? super A, ? super B, ? extends C> f) {
    return new Fn2<B, A, C>() {
      @Override public C def(B b, A a) {
        return f.apply(a, b);
      }
    };
  }

  /** Partial application of argument 1. */
  public static <A, B, C> Fn<B, C> _1p(final Fn2<? super A, ? super B, ? extends C> f, final A a) {
    return new Fn<B, C>() {
      @Override public C def(B b) {
        return f.apply(a, b);
      }
    };
  }

  /** Partial application of argument 2. */
  public static <A, B, C> Fn<A, C> _2p(final Fn2<? super A, ? super B, ? extends C> f, final B b) {
    return new Fn<A, C>() {
      @Override public C def(A a) {
        return f.apply(a, b);
      }
    };
  }

  /** Partial application of argument 2. */
  public static <A, B, C> Fn<A, C> _2p(final Fn<? super A, Fn<? super B, ? extends C>> f, final B b) {
    return new Fn<A, C>() {
      @Override public C def(A a) {
        return f.apply(a).apply(b);
      }
    };
  }

  /** Covary and contravary a function. */
  @SuppressWarnings("unchecked")
  public static <A, B> Fn<A, B> vy(Fn<? super A, ? extends B> f) {
    return (Fn<A, B>) f;
  }

  public static <A, B> Fn<A, Opt<B>> tryOpt(final Fn<? super A, ? extends B> f) {
    return new Fn<A, Opt<B>>() {
      @Override public Opt<B> def(A a) {
        try {
          return Opt.some(f.apply(a));
        } catch (Exception e) {
          return Opt.none();
        }
      }
    };
  }

  /**
   * Concat an array of partial functions into one.
   * The resulting partial functions applies <code>fs</code>
   * in order unless one is defined at the argument.
   */
  public static <A, B> PartialFn<A, B> or(final PartialFn<? super A, ? extends B>... fs) {
    return new PartialFn<A, B>() {
      @Override protected B defPartial(A a) throws Exception {
        for (PartialFn<? super A, ? extends B> f : fs) {
          final B b = f.defPartial(a);
          if (b != null) {
            return b;
          }
        }
        return null;
      }
    };
  }

  /** Turn a function into an effect by discarding its result. */
  public static <A, B> Fx<A> toFx(final Fn<? super A, ? extends B> f) {
    return new Fx<A>() {
      @Override public void def(A a) {
        f.apply(a);
      }
    };
  }

  /** Create an effect that applies its argument to all <code>fx</code> in order. */
  public static <A> Fx<A> all(final Iterable<Fx<A>> fx) {
    return new Fx<A>() {
      @Override public void def(A a) {
        for (Fx<A> f : fx) {
          f.apply(a);
        }
      }
    };
  }

  /** Create a function that applies its argument to <code>fx</code> and then to <code>f</code>. */
  public static <A, B> Fn<A, B> tee(final Fn<? super A, ? extends B> f, final Fx<? super A> fx) {
    return new Fn<A, B>() {
      @Override public B def(A a) {
        fx.apply(a);
        return f.apply(a);
      }
    };
  }

//  /**
//   * Create a new Fun from <code>f</code> decorated with an exception transformer. Any exception that occurs during
//   * application of <code>f</code> is passed to <code>transformer</code> whose return value is then being thrown.
//   */
//  public static <A, B> Fn<A, B> rethrow(
//          final Fn<? super A, ? extends B> f,
//          final Fn<? super Exception, ? extends Exception> transformer) {
//    return new Fn<A, B>() {
//      @Override
//      public B p4(A a) {
//        try {
//          return f.p4(a);
//        } catch (Exception e) {
//          return chuck(transformer.p4(e));
//        }
//      }
//    };
//  }
//
//  /**
//   * Create a new Fun from <code>f</code> decorated with an exception handler. Any exception that occurs during
//   * application of <code>f</code> is passed to <code>handler</code> whose return value is then being returned.
//   */
//  public static <A, B> Fn<A, B> handle(
//          final Fn<? super A, ? extends B> f,
//          final Fn<? super Exception, ? extends B> handler) {
//    return new Fn<A, B>() {
//      @Override
//      public B p4(A a) {
//        try {
//          return f.p4(a);
//        } catch (Exception e) {
//          return handler.p4(e);
//        }
//      }
//    };
//  }
//
//  /**
//   * Create a new Fun from <code>f</code> decorated with an exception handler. The new Fun either returns the
//   * value of <code>f</code> or in case of an exception being thrown on the application of <code>f</code> the return
//   * value of <code>handler</code>.
//   */
//  public static <A, B, C> Fn<A, Either<C, B>> either(
//          final Fn<? super A, ? extends B> f,
//          final Fn<? super Exception, ? extends C> handler) {
//    return new Fn<A, Either<C, B>>() {
//      @Override
//      public Either<C, B> p4(A a) {
//        try {
//          return right(f.p4(a));
//        } catch (Exception e) {
//          return left(handler.p4(e));
//        }
//      }
//    };
//  }
//
//  /** Curry a function of arity 2. */
//  public static <A, B, C> Fn<B, C> curry(final Fn2<? super A, ? super B, ? extends C> f, final A a) {
//    return new Fn<B, C>() {
//      @Override public C p4(B b) {
//        return f.p4(a, b);
//      }
//    };
//  }
//
//  /** Curry a function of arity 2. */
//  public static <A, B, C> Fn<A, Fn<B, C>> curry(final Fn2<? super A, ? super B, ? extends C> f) {
//    return new Fn<A, Fn<B, C>>() {
//      @Override public Fn<B, C> p4(final A a) {
//        return curry(f, a);
//      }
//    };
//  }
//
//  /** Uncurry to a function of arity 2. */
//  public static <A, B, C> Fn2<A, B, C> uncurry(final Fn<? super A, Fn<B, C>> f) {
//    return new Fn2<A, B, C>() {
//      @Override
//      public C p4(A a, B b) {
//        return f.p4(a).p4(b);
//      }
//    };
//  }
//
//  /** Create a tupled version of a Fun of arity 2. */
//  public static <A, B, C> Fn<P2<A, B>, C> tuple(final Fn2<? super A, ? super B, ? extends C> f) {
//    return new Fn<P2<A, B>, C>() {
//      @Override
//      public C p4(P2<A, B> t) {
//        return f.p4(t._1(), t.get2());
//      }
//    };
//  }
//
//  public static <A, B, C> Fn2<A, B, C> untuple(final Fn<P2<A, B>, ? extends C> f) {
//    return new Fn2<A, B, C>() {
//      @Override public C p4(A a, B b) {
//        return f.p4(P.p2(a, b));
//      }
//    };
//  }
//
//  /** Flip arguments of a Fun of arity 2. */
//  public static <A, B, C> Fn2<B, A, C> flip(final Fn2<? super A, ? super B, ? extends C> f) {
//    return new Fn2<B, A, C>() {
//      @Override
//      public C p4(B b, A a) {
//        return f.p4(a, b);
//      }
//    };
//  }
//

//
//  /** Turn a Fun of arity 2 into an effect by discarding its result. */
//  public static <A, B, C> Effect2<A, B> toEffect(final Fun2<? super A, ? super B, ? extends C> f) {
//    return new Effect2<A, B>() {
//      @Override
//      protected void run(A a, B b) {
//        f.p4(a, b);
//      }
//    };
//  }
//
//  public static <A> Predicate<A> toPredicate(final Fn<? super A, Boolean> f) {
//    return new Predicate<A>() {
//      @Override
//      public Boolean p4(A a) {
//        return f.p4(a);
//      }
//    };
//  }
//
//  /** Noop effect. */
//  public static final Effect0 noop = new Effect0() {
//    @Override
//    protected void run() {
//    }
//  };
//
//  /** Noop effect. */
//  public static <A> Effect<A> noop() {
//    return new Effect<A>() {
//      @Override
//      protected void run(A a) {
//      }
//    };
//  }
//
//  /** Identity Fun. */
//  public static <A> Fn<A, A> identity() {
//    return new Fn<A, A>() {
//      @Override
//      public A p4(A a) {
//        return a;
//      }
//    };
//  }
//
//  /**
//   * Identity function. The type is based on the type of the example object to save some nasty typing, e.g.
//   * <code>Fun.&lt;Integer&gt;identity()</code> vs. <code>identity(0)</code>
//   * <p/>
//   * Please note that this constructor is only due to Java's insufficient type inference.
//   */
//  public static <A> Fn<A, A> identity(A example) {
//    return identity();
//  }
//
//  /**
//   * Identity Fun.
//   *
//   * @param clazz
//   *         to describe the Funs's type
//   */
//  public static <A> Fn<A, A> identity(Class<A> clazz) {
//    return identity();
//  }
//
//  /** Return a function that returns <code>b</code> for all arguments. */
//  public static <A, B> Fn<A, B> constant(final B b) {
//    return new Fn<A, B>() {
//      @Override public B p4(A ignore) {
//        return b;
//      }
//    };
//  }
//
//  /** Promote Fun <code>a -&gt; b</code> to an {@link org.opencastproject.util.data.Option}. */
//  public static <A, B> Fn<Option<A>, Option<B>> liftOpt(final Fn<? super A, ? extends B> f) {
//    return new Fn<Option<A>, Option<B>>() {
//      @Override
//      public Option<B> p4(Option<A> a) {
//        return a.fmap(f);
//      }
//    };
//  }
//
//  /** Promote effect <code>a -&gt; ()</code> to an {@link org.opencastproject.util.data.Option}. */
//  public static <A> Fn<Option<A>, Option<A>> liftOpt(final Effect<? super A> f) {
//    return new Fn<Option<A>, Option<A>>() {
//      @Override
//      public Option<A> p4(Option<A> a) {
//        for (A x : a)
//          f.p4(x);
//        return a;
//      }
//    };
//  }
//
//  /** Create a bound version of <code>f</code> for {@link org.opencastproject.util.data.Option}. */
//  public static <A, B> Fn<Option<A>, Option<B>> bindOpt(final Fn<A, Option<B>> f) {
//    return new Fn<Option<A>, Option<B>>() {
//      @Override
//      public Option<B> p4(Option<A> a) {
//        return a.bind(f);
//      }
//    };
//  }
//
//  /** Promote Fun <code>a -&gt; b</code> to a {@link java.util.List}. */
//  public static <A, B> Fn<List<A>, List<B>> liftList(final Fn<A, B> f) {
//    return new Fn<List<A>, List<B>>() {
//      @Override
//      public List<B> p4(List<A> as) {
//        return mlist(as).fmap(f).value();
//      }
//    };
//  }
//
//  /** Create a bound version of <code>f</code> for {@link java.util.List}. */
//  public static <A, B> Fn<List<A>, List<B>> bind(final Fn<A, List<B>> f) {
//    return new Fn<List<A>, List<B>>() {
//      @Override
//      public List<B> p4(List<A> as) {
//        return mlist(as).bind(f).value();
//      }
//    };
//  }
//
//  /** Create an effect that runs its argument. */
//  public static final Effect<Effect0> run = new Effect<Effect0>() {
//    @Override
//    protected void run(Effect0 e) {
//      e.p4();
//    }
//  };
//
//  /** Create an effect that runs its argument passing in <code>a</code>. */
//  public static <A> Effect<Effect<A>> run(final A a) {
//    return new Effect<Effect<A>>() {
//      @Override
//      protected void run(Effect<A> e) {
//        e.p4(a);
//      }
//    };
//  }
//
//  /** Create an effect that runs all given effects in order. */
//  public static Effect0 all(final Effect0... es) {
//    return new Effect0() {
//      @Override
//      protected void run() {
//        mlist(es).each(run);
//      }
//    };
//  }
//
//  /** Create an effect that runs all given effects in order. */
//  public static <A> Effect<A> all(final Effect<? super A>... es) {
//    return new Effect<A>() {
//      @Override
//      protected void run(A a) {
//        for (Effect<? super A> e : es) {
//          e.p4(a);
//        }
//      }
//    };
//  }
//
//  /** Pure Funs are covariant in their result type. */
//  public static <A, B> Fn<A, B> co(Fn<? super A, ? extends B> f) {
//    return (Fn<A, B>) f;
//  }
//
//  public static <A, B> Fn<Fn<A, ? extends B>, Fn<A, B>> co() {
//    return new Fn<Fn<A, ? extends B>, Fn<A, B>>() {
//      @Override
//      public Fn<A, B> p4(Fn<A, ? extends B> f) {
//        return co(f);
//      }
//    };
//  }
//
//  /** Contra/co vary a function. */
//  public static <A, B> Fn<A, B> variant(Fn<? super A, ? extends B> f) {
//    return (Fn<A, B>) f;
//  }
//
//  /** Create a (partial) Fun from a map. */
//  public static <A, B> Fn<A, Option<B>> toFn(final Map<? extends A, ? extends B> m) {
//    return new Fn<A, Option<B>>() {
//      @Override
//      public Option<B> p4(A a) {
//        B b = m.get(a);
//        return option(b);
//      }
//    };
//  }
//
//  /**
//   * Treat <code>fs</code> as partial Funs. Apply the argument <code>a</code> of the returned Fun to the
//   * Funs <code>fs</code> in order unless some value is returned. Return <code>zero</code> if none of the Funs
//   * is defined at <code>a</code>.
//   */
//  public static <A, B> Fn<A, B> orElse(final B zero, final Fn<? super A, Option<B>>... fs) {
//    return new Fn<A, B>() {
//      @Override
//      public B p4(A a) {
//        for (Fn<? super A, Option<B>> f : fs) {
//          final Option<? extends B> r = f.p4(a);
//          if (r.isSome()) {
//            return r.get();
//          }
//        }
//        return zero;
//      }
//    };
//  }
}
