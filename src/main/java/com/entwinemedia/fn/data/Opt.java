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

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Fx;
import com.entwinemedia.fn.P1;
import com.entwinemedia.fn.P2;
import com.entwinemedia.fn.Stream;
import com.entwinemedia.fn.StreamFold;
import com.entwinemedia.fn.StreamOp;

import java.util.Iterator;
import java.util.List;

/**
 * The option type encapsulates on optional value. It contains either some value or is empty.
 * Please make sure to NEVER wrap null into a some. Instead use none.
 */
public abstract class Opt<A> implements Iterable<A> {
  private static final ListBuilder l = ListBuilders.LIA;

  private Opt() {
  }

  public abstract A get();

  public abstract boolean isSome();

  public boolean isNone() {
    return !isSome();
  }

  /** Safe decomposition of the option type using functions. */
  public <B> B fold(Fn<? super A, ? extends B> some, P1<? extends B> none) {
    // cannot be written using the ternary operator ?: because of type inference issues
    if (isSome()) {
      return some.apply(get());
    } else {
      return none.get1();
    }
  }

  public <B> B fold(P1<? extends B> some, P1<? extends B> none) {
    // cannot be written using the ternary operator ?: because of type inference issues
    if (isSome()) {
      return some.get1();
    } else {
      return none.get1();
    }
  }

  public <B> B fold(P2<? extends B, ? extends B> someNone) {
    // cannot be written using the ternary operator ?: because of type inference issues
    if (isSome()) {
      return someNone.get1();
    } else {
      return someNone.get2();
    }
  }

//  public abstract Option<A> foreach(Function<A, Void> f);

  public <B> Opt<B> fmap(Fn<? super A, ? extends B> f) {
    return isSome() ? some(f.apply(get())) : Opt.<B>none();
  }

  /** @see #fmap(com.entwinemedia.fn.Fn) */
  public <B> Opt<B> map(Fn<? super A, ? extends B> f) {
    return fmap(f);
  }

  /** Monadic bind operation <code>m a -> (a -> m b) -> m b</code>. */
  public <B> Opt<B> bind(Fn<? super A, Opt<B>> f) {
    return isSome() ? f.apply(get()) : Opt.<B>none();
  }

  /** @see #bind(com.entwinemedia.fn.Fn) */
  public <B> Opt<B> flatMap(Fn<? super A, Opt<B>> f) {
    return bind(f);
  }

  /** Run side effect <code>f</code> on the value of a some; do nothing otherwise. */
  public final Opt<A> each(Fx<? super A> f) {
    if (isSome()) {
      f.apply(get());
    }
    return this;
  }

  /** If predicate <code>p</code> does not match return none. */
  public Opt<A> filter(Fn<? super A, Boolean> p) {
    return isSome() && p.apply(get()) ? this : Opt.<A>none();
  }

  /** Throw <code>none</code> if none. */
  public <T extends Throwable> Opt<A> orError(T none) throws T {
    if (isSome()) return this;
    else throw none;
  }

//  /** Throw <code>none</code> if none. */
//  public <T extends Throwable> Opt<A> orError(Class<T> none) throws T {
//    if (isSome()) return this;
//    else {
//      T t;
//      try {
//        t = none.newInstance();
//      } catch (InstantiationException e) {
//        return chuck(new Error("Error creating exception", e));
//      } catch (IllegalAccessException e) {
//        return chuck(new Error("Error creating exception", e));
//      }
//      throw t;
//    }
//  }

  /** Throw exception returned by <code>none</code> if none. */
//  public <T extends Throwable> Opt<A> orError(Function0<T> none) throws T {
//    if (isSome()) return this;
//    else throw none.apply();
//  }

  /** Get the contained value in case of being "some" or return parameter <code>none</code> otherwise. */
  public A or(A none) {
    return isSome() ? get() : none;
  }

  /** Get the contained value in case of being "some" or return the result of evaluating <code>none</code> otherwise. */
  public A or(P1<A> none) {
    return isSome() ? get() : none.get1();
  }

  /** To interface with legacy applications or frameworks that still use <code>null</code> values. */
  public A orNull() {
    return isSome() ? get() : null;
  }

  /** Short hand methods for <code>toStream().apply(op)</code>. */
  public <B> Stream<B> apply(StreamOp<? super A, B> op) {
    return toStream().apply(op);
  }

  /** Short hand methods for <code>toStream().apply(fold)</code>. */
  public <B> B apply(StreamFold<? super A, B> fold) {
    return toStream().apply(fold);
  }

  /** Transform the option into a monadic list. */
  @SuppressWarnings("unchecked")
  public Stream<A> toStream() {
    return isSome() ? Stream.mk(get()) : Stream.<A>empty();
  }

  /** Transform an option into an immutable list, either with a single element or an empty list. */
  @SuppressWarnings("unchecked")
  public List<A> toList() {
    return isSome() ? l.mk(get()) : l.<A>nil();
  }

  /**
   * Left projection of this option. If the option is <code>some</code> return the
   * value in an {@link Either#left(Object)} else return <code>right</code> in an {@link Either#right(Object)}.
   */
//  public abstract <B> Either<A, B> left(B right);

  /**
   * Right projection of this optio. If the option is <code>some</code> return the
   * value in an {@link Either#left(Object)} else return <code>right</code> in an {@link Either#right(Object)}.
   */
//  public abstract <B> Either<B, A> right(B left);

  /** Inversion. If some return none. If none return some(zero). */
  public Opt<A> inv(A zero) {
    return isSome() ? Opt.<A>none() : some(zero);
  }

  @Override
  public Iterator<A> iterator() {
    return toList().iterator();
  }

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object o);

  @Override
  public abstract String toString();

  // -- implementations

  private static final class Some<A> extends Opt<A> {
    private final A a;

    private Some(A a) {
      this.a = a;
    }

    @Override public A get() {
      return a;
    }

    @Override public boolean isSome() {
      return true;
    }

    @Override public int hashCode() {
      return a.hashCode();
    }

    @Override public boolean equals(Object o) {
      // since an Option does NEVER contain any null this is safe
      return o instanceof Some && a.equals(((Some) o).get());
    }

    @Override public String toString() {
      return "Some(" + a + ")";
    }
  }

  private static final Opt NONE = new Opt() {
    @Override public Object get() {
      throw new Error("a none does not contain a value");
    }

    @Override public boolean isSome() {
      return false;
    }

    @Override public int hashCode() {
      return -1;
    }

    @Override public boolean equals(Object o) {
      return o instanceof Opt && ((Opt) o).isNone();
    }

    @Override public String toString() {
      return "None";
    }
  };

  // -- constructor functions

  public static <A> Opt<A> some(final A a) {
    if (a == null) {
      throw new Error("null must not be wrapped in a some");
    }
    return new Some<A>(a);
  }

  @SuppressWarnings("unchecked")
  public static <A> Opt<A> none() {
    return NONE;
  }

  @SuppressWarnings("unchecked")
  public static <A> Opt<A> none(Class<A> t) {
    return NONE;
  }

  /** Return some(a) if a is not null, none otherwise. */
  public static <A> Opt<A> nul(A a) {
    return a != null ? some(a) : Opt.<A>none();
  }
}
