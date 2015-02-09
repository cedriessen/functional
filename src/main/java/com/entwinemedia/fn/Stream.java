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

import com.entwinemedia.fn.data.Immutable;
import com.entwinemedia.fn.data.ImmutableArrayListFactory;
import com.entwinemedia.fn.data.ImmutableIteratorArrayAdapter;
import com.entwinemedia.fn.data.ImmutableIteratorBase;
import com.entwinemedia.fn.data.ImmutableIterators;
import com.entwinemedia.fn.data.ImmutableListWrapper;
import com.entwinemedia.fn.data.ImmutableSetWrapper;
import com.entwinemedia.fn.data.Iterators;
import com.entwinemedia.fn.data.ListBuilder;
import com.entwinemedia.fn.data.ListBuilders;
import com.entwinemedia.fn.data.Opt;
import com.entwinemedia.fn.data.SetB;
import com.entwinemedia.fn.data.SetBuilder;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Stream<A> implements Iterable<A> {
  public static final int NO_SIZE_HINT = -1;

  private int sizeHint;

  /** Create a new Stream with a size hint. A value < 0 means no hint. */
  protected Stream(int sizeHint) {
    this.sizeHint = Math.max(sizeHint, NO_SIZE_HINT);
  }

  /** Return a value >= 0. */
  public int getSizeHint() {
    return Math.max(sizeHint, 0);
  }

  /** Check if there is a size hint. */
  public boolean hasSizeHint() {
    return sizeHint >= 0;
  }

  /** Streams are implemented in terms of an iterator. */
  @Override public abstract Iterator<A> iterator();

  /**
   * Override and return the source iterable whenever it is safe to return it directly.
   * The returned iterable will be wrapped in an immutable wrapper.
   */
  protected Iterable<A> getSrcHint() {
    return null;
  }

  /**
   * Create a stream from an iterable. If <code>it</code> is null create an empty stream.
   * <p/>
   * <strong>Implementation note:</strong> The iterable will not be copied.
   * If the iterable is <em>not</em> immutable make sure not to modify it while the stream processes it.
   * If this cannot be guaranteed it should be converted to an immutable data structure first.
   */
  @SuppressWarnings("unchecked")
  public static <A> Stream<A> $(final Iterable<? extends A> it) {
    return mk(it);
  }

  /** @see #$(Iterable) */
  @SuppressWarnings("unchecked")
  public static <A> Stream<A> mk(final Iterable<? extends A> it) {
    if (it instanceof Stream) {
      return (Stream<A>) it;
    } else if (it != null) {
      final boolean immutable = it instanceof Immutable;
      return new Stream<A>(getSizeHint(it)) {
        @SuppressWarnings("unchecked")
        @Override public Iterator<A> iterator() {
          return immutable ? (Iterator<A>) it.iterator() : ImmutableIterators.mk(it.iterator());
        }

        @SuppressWarnings("unchecked")
        @Override protected Iterable<A> getSrcHint() {
          return immutable ? (Iterable<A>) it : null;
        }
      };
    } else {
      return empty();
    }
  }

  /**
   * Create a stream from an array. If <code>as</code> is null create an empty stream.
   * <p/>
   * <strong>Implementation note:</strong> The array will not be copied.
   * Make sure not to modify it while the stream processes it.
   * If this cannot be guaranteed it should be converted to an immutable data structure first.
   */
  public static <A> Stream<A> $(final A... as) {
    return mk(as);
  }

  /** @see #$(Object[]) */
  public static <A> Stream<A> mk(final A... as) {
    if (as != null && as.length > 0) {
      if (as.length > 1) {
        return new Stream<A>(as.length) {
          @Override public Iterator<A> iterator() {
            return new ImmutableIteratorArrayAdapter<A>(as);
          }
        };
      } else {
        return single(as[0]);
      }
    } else {
      return empty();
    }
  }

  /** Create a single element stream if <code>a</code> is not null. Return an empty stream otherwise. */
  public static <A> Stream<A> $$(A a) {
    return single(a);
  }

  /** @see #$$(Object) */
  public static <A> Stream<A> single(final A a) {
    if (a != null) {
      return new Stream<A>(1) {
        @Override public Iterator<A> iterator() {
          return ImmutableIterators.mk(a);
        }
      };
    } else {
      return empty();
    }
  }

  /** Create a continuous stream returning the value of product <code>p</code>. */
  public static <A> Stream<A> cont(final P1<A> p) {
    return new Stream<A>(NO_SIZE_HINT) {
      @Override public Iterator<A> iterator() {
        return new ImmutableIteratorBase<A>() {
          @Override public boolean hasNext() {
            return true;
          }

          @Override public A next() {
            return p.get1();
          }
        };
      }
    };
  }

  private static final Stream EMPTY = new Stream(0) {
    @Override public Iterator iterator() {
      return Iterators.empty();
    }

    @Override protected Iterable getSrcHint() {
      return ListBuilders.SIA.nil();
    }
  };

  /** Create an empty stream. */
  @SuppressWarnings("unchecked")
  public static <A> Stream<A> empty() {
    return EMPTY;
  }

  public boolean isEmpty() {
    return !iterator().hasNext();
  }

  // -- mappings

  /** @see StreamOp#fmap(Fn) */
  public final <B> Stream<B> fmap(final Fn<? super A, ? extends B> f) {
    return StreamOp.fmap(f, this);
  }

  /** @see StreamOp#fmap(Fn) */
  public final <B> Stream<B> map(Fn<? super A, ? extends B> f) {
    return fmap(f);
  }

  /** @see StreamOp#bind(Fn) */
  public final <B> Stream<B> bind(final Fn<? super A, ? extends Iterable<B>> f) {
    return StreamOp.bind(f, this);
  }

  /** @see StreamOp#filter(Fn) */
  public final Stream<A> filter(final Fn<? super A, Boolean> p) {
    return StreamOp.filter(p, this);
  }

  /** Take elements from the head of the list until predicate <code>p</code> yields false. */
  public final Stream<A> takeWhile(final Fn<? super A, Boolean> p) {
    return StreamOp.takeWhile(p, this);
  }

  /** Take <code>n</code> elements from the head. */
  public final Stream<A> take(final int n) {
    return StreamOp.take(n, this);
  }

  /** Drop <code>n</code> elements from the head. */
  public final Stream<A> drop(final int n) {
    return StreamOp.drop(n, this);
  }

  /** Drop elements from the head of the list until predicate <code>p</code> yields false. */
  public final Stream<A> dropWhile(final Fn<? super A, Boolean> p) {
    return StreamOp.dropWhile(p, this);
  }

  public final Stream<P2<A, Integer>> zipWithIndex() {
    return StreamOp.zipWithIndex(this);
  }

  public final <B> Stream<P2<A, B>> zip(final Iterable<? extends B> s) {
    return StreamOp.zip(s, this);
  }

  public final Stream<A> append(final Stream<? extends A> s) {
    return StreamOp.append(this, s);
  }

  public final Stream<A> append(final Iterable<? extends A> s) {
    return StreamOp.append(this, s);
  }

  /** Return the tail, i.e. everything but the {@link #head()}. */
  public final Stream<A> tail() {
    return drop(1);
  }

  public final Stream<A> inject(A a) {
    return StreamOp.inject(a, this);
  }

  public final Stream<A> wrap(A pre, A post) {
    return StreamOp.wrap(pre, post, this);
  }

  public final Stream<A> each(Fx<? super A> f) {
    return StreamOp.each(f, this);
  }

  /** @see {@link StreamOp#partition(int, Stream)}. */
  public final Stream<List<A>> partition(int size) {
    return StreamOp.partition(size, this);
  }

  /** Reverse the elements. */
  public final Stream<A> reverse() {
    return StreamOp.reverse(this);
  }

  public final Stream<A> sort(Comparator<A> order) {
    return StreamOp.sort(order, this);
  }

  public final Stream<A> repeat(int times) {
    return StreamOp.repeat(times, this);
  }

  @SuppressWarnings("unchecked")
  public final <B, A extends B> Stream<B> vary() {
    return (Stream<B>) this;
  }

  // -- folds

  /** Return the head element. */
  public final Opt<A> head() {
    return StreamFold.head(this);
  }

  /** Return the head element or throw an exception. */
  public final A head2() {
    return StreamFold.head2(this);
  }

  /**
   * Fold from left to right applying binary operator <code>f</code> starting with <code>zero</code>.
   * This method immediately evaluates <em>all</em> elements.
   */
  public final <B> B foldl(B zero, Fn2<? super B, ? super A, ? extends B> f) {
    return StreamFold.foldl(zero, f, this);
  }

  /**
   * Sum up the all elements of the stream.
   * This method evaluates immediately.
   */
  public final A sum(Monoid<A> m) {
    return StreamFold.sum(m, this);
  }

  /**
   * Check if at least one element satisfies predicate <code>p</code>.
   * This method evaluates immediately.
   */
  public final boolean exists(Fn<? super A, Boolean> p) {
    return StreamFold.exists(p, this);
  }

  /**
   * Return the first element that satisfies predicate <code>p</code>.
   * This method evaluates immediately.
   */
  public final Opt<A> find(Fn<? super A, Boolean> p) {
    return StreamFold.find(p, this);
  }

  /**
   * Apply function <code>p</code> to the elements of the stream until a some is yielded.
   * This method evaluates immediately.
   */
  public final <B> Opt<B> findMap(Fn<? super A, Opt<B>> p) {
    return StreamFold.findMap(p, this);
  }

  /** @see StreamFold#mkString(String) */
  public final String mkString(String sep) {
    return StreamFold.mkString(sep, this);
  }

  /** @see StreamFold#mkString(String) */
  public final String mkString() {
    return StreamFold.mkString("", this);
  }

  /** @see StreamFold#group(Fn) */
  public final <B> Map<B, A> group(Fn<? super A, ? extends B> key) {
    return StreamFold.group(key, this);
  }

  /** @see StreamFold#group(Fn, Fn) */
  public final <B, C> Map<B, C> group(Fn<? super A, ? extends B> key,
                                      Fn<? super A, ? extends C> value) {
    return StreamFold.group(key, value, this);
  }

  /**
   * Use an {@link com.entwinemedia.fn.data.ImmutableArrayListFactory}.
   *
   * @see StreamFold#groupMulti(com.entwinemedia.fn.data.ListFactory, Fn)
   */
  public final <B> Map<B, List<A>> groupMulti(Fn<? super A, ? extends B> key) {
    return StreamFold.groupMulti(ImmutableArrayListFactory.I, key, this);
  }

  // -- applications

  /** Apply a stream operation to this stream. */
  public final <B> Stream<B> apply(StreamOp<? super A, B> op) {
    return op.ap(this);
  }

  /** Apply a stream fold to this stream. */
  public final <B> B apply(StreamFold<? super A, B> fold) {
    return fold.apply(this);
  }

  /** Apply a stream fold to this stream. */
  public final <B> B apply(Fn<Stream<? super A>, B> fold) {
    return fold.ap(this);
  }

  // -- evaluations

  /** Evaluate to a loose immutable list. */
  public final List<A> toList() {
    final Iterable<A> wrapped = getSrcHint();
    if (wrapped instanceof List) {
      return wrapped instanceof Immutable ? (List<A>) wrapped : new ImmutableListWrapper<A>((List<A>) getSrcHint());
    } else {
      return toList(ListBuilders.LIA);
    }
  }

  /** Evaluate to a list created by the given list factory. */
  public final List<A> toList(ListBuilder b) {
    return hasSizeHint() ? b.mk(getSizeHint(), iterator()) : b.mk(iterator());
  }

  /** Evaluate to an immutable set. */
  public final Set<A> toSet() {
    final Iterable<A> wrapped = getSrcHint();
    if (wrapped instanceof Set) {
      return wrapped instanceof Immutable ? (Set<A>) wrapped : new ImmutableSetWrapper<A>((Set<A>) getSrcHint());
    } else {
      return toSet(SetB.IH);
    }
  }

  /** Evaluate to a list created by the given list factory. */
  public final Set<A> toSet(SetBuilder b) {
    return hasSizeHint() ? b.mk(getSizeHint(), iterator()) : b.mk(iterator());
  }

  /** Evaluate stream. */
  public final Stream<A> eval() {
    return $(toList());
  }

  /** Evaluate stream and ignore the result. */
  public final void run() {
    for (A a : this) {
    }
  }

  // --

  private static <A> int getSizeHint(Iterable<A> it) {
    if (it instanceof Collection) {
      return ((Collection) it).size();
    } else {
      return -1;
    }
  }
}
