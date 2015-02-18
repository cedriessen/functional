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

import com.entwinemedia.fn.data.ImmutableIteratorBase;
import com.entwinemedia.fn.data.Iterators;
import com.entwinemedia.fn.data.ListBuilders;
import com.entwinemedia.fn.fns.Maps;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class StreamOp<A, B> extends Fn<Stream<? extends A>, Stream<B>> {
  private static final ProductBuilder p = Products.E;

  /** Apply the operation to stream <code>s</code>. */
  public abstract Stream<B> apply(Stream<? extends A> s);

  /** Create a new identity stream operation. */
  public static <A> StreamOp<A, A> id() {
    return new StreamOp<A, A>() {
      @Override public Stream<A> apply(final Stream<? extends A> s) {
        return new Stream<A>(s.getSizeHint()) {
          @Override public Iterator<A> iterator() {
            return (Iterator<A>) s.iterator();
          }
        };
      }
    };
  }

  /**
   * Composition of stream operations. Like function composition.
   * <p/>
   * <code>this.compose(op) == this . op == this(op(<i>stream</i>))</code>
   * <p/>
   * <code>op</code> gets applied first, the resulting stream then gets applied to <code>this</code>.
   */
  public <C> StreamOp<C, B> compose(final StreamOp<? super C, ? extends A> op) {
    return new StreamOp<C, B>() {
      @Override public Stream<B> apply(Stream<? extends C> s) {
        return StreamOp.this.apply(op.apply(s));
      }
    };
  }

  /** @see #compose(StreamOp) */
  public <C> StreamOp<C, B> o(final StreamOp<? super C, ? extends A> op) {
    return compose(op);
  }

  /** Left to right composition with a stream fold. */
  public <C> StreamFold<A, C> then(final StreamFold<? super B, ? extends C> fold) {
    return new StreamFold<A, C>() {
      @Override public C apply(Stream<? extends A> s) {
        return fold.apply(StreamOp.this.apply(s));
      }
    };
  }

  /** Map function <code>f</code> over the elements of a stream. */
  public <C> StreamOp<A, C> fmap(final Fn<? super B, ? extends C> f) {
    return new StreamOp<A, C>() {
      @Override public Stream<C> apply(final Stream<? extends A> s) {
        return StreamOp.fmap(f, StreamOp.this.apply(s));
      }
    };
  }

  /** Map function <code>f</code> over the elements of stream <code>s</code>. */
  public static <A, B> Stream<B> fmap(final Fn<? super A, ? extends B> f, final Stream<A> s) {
    return new Stream<B>(s.getSizeHint()) {
      @Override public Iterator<B> iterator() {
        return new Iterate<A, B>(s.iterator()) {
          @Override protected B apply(A b) {
            return f.apply(b);
          }
        };
      }
    };
  }

  /** Map function <code>f</code> over the elements of a stream and concatenate the results. */
  public <C> StreamOp<A, C> bind(final Fn<? super B, ? extends Iterable<C>> f) {
    return new StreamOp<A, C>() {
      @Override public Stream<C> apply(Stream<? extends A> s) {
        return StreamOp.bind(f, StreamOp.this.apply(s));
      }
    };
  }

  /** Map function <code>f</code> over the elements of stream <code>s</code> and concatenate the results. */
  public static <A, B> Stream<B> bind(final Fn<? super A, ? extends Iterable<B>> f, final Stream<? extends A> s) {
    return new Stream<B>(s.getSizeHint()) {
      @SuppressWarnings("unchecked")
      @Override public Iterator<B> iterator() {
        return new Mapper<A, B>((Iterator<A>) s.iterator()) {
          @Override
          public boolean hasNext() {
            return step.hasNext() || step().hasNext();
          }

          @Override
          public B next() {
            if (step.hasNext()) {
              return step.next();
            } else {
              return step().next();
            }
          }

          // iterator state management
          private Iterator<B> step = Iterators.empty();

          private Iterator<B> step() {
            while (!step.hasNext() && wrapped.hasNext()) {
              step = f.apply(wrapped.next()).iterator();
            }
            return step;
          }
        };
      }
    };
  }

  /** Take <code>n</code> elements from the head of a stream. */
  public StreamOp<A, B> take(final int n) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(final Stream<? extends A> s) {
        return StreamOp.take(n, StreamOp.this.apply(s));
      }
    };
  }

  /** Take <code>n</code> elements from the head of stream <code>s</code>. */
  public static <A> Stream<A> take(final int n, final Stream<A> s) {
    return new Stream<A>(n) {
      @Override public Iterator<A> iterator() {
        return new IdentityIterate<A>(s.iterator()) {
          private int count = n;

          @Override protected boolean cont() {
            if (count > 0) {
              count--;
              return true;
            } else {
              return false;
            }
          }
        };
      }
    };
  }

  /** Drop <code>n</code> elements from the head of a stream. */
  public StreamOp<A, B> drop(final int n) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.drop(n, StreamOp.this.apply(s));
      }
    };
  }

  /** Drop <code>n</code> elements from the head of stream <code>s</code>. */
  public static <A> Stream<A> drop(final int n, final Stream<A> s) {
    return new Stream<A>(s.getSizeHint() - n) {
      @Override public Iterator<A> iterator() {
        return new IdentityIterate<A>(s.iterator()) {
          private int count = n;

          @Override protected A apply(A a) {
            if (count == 0) {
              return a;
            } else {
              count--;
              return null;
            }
          }
        };
      }
    };
  }

  public StreamOp<A, B> dropWhile(final Fn<? super B, Boolean> p) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.dropWhile(p, StreamOp.this.apply(s));
      }
    };
  }

  public static <A> Stream<A> dropWhile(final Fn<? super A, Boolean> p, final Stream<A> s) {
    return new Stream<A>(s.getSizeHint()) {
      @Override public Iterator<A> iterator() {
        return new IdentityIterate<A>(s.iterator()) {
          boolean take = false;

          @Override protected A apply(A a) {
            if (take) {
              return a;
            } else if (p.apply(a)) {
              return null;
            } else {
              take = true;
              return a;
            }
          }
        };
      }
    };
  }

  public StreamOp<A, P2<B, Integer>> zipWithIndex() {
    return new StreamOp<A, P2<B, Integer>>() {
      @Override public Stream<P2<B, Integer>> apply(Stream<? extends A> s) {
        return zipWithIndex(StreamOp.this.apply(s));
      }
    };
  }

  public static <A> Stream<P2<A, Integer>> zipWithIndex(final Stream<A> s) {
    return new Stream<P2<A, Integer>>(s.getSizeHint()) {
      @Override public Iterator<P2<A, Integer>> iterator() {
        return new Iterate<A, P2<A, Integer>>(s.iterator()) {
          private int index = 0;

          @Override protected P2<A, Integer> apply(A a) {
            return p.p2(a, index++);
          }
        };
      }
    };
  }

  public <C> StreamOp<A, P2<B, C>> zip(final Stream<? extends C> b) {
    return new StreamOp<A, P2<B, C>>() {
      @Override public Stream<P2<B, C>> apply(Stream<? extends A> s) {
        return zip(b, StreamOp.this.apply(s));
      }
    };
  }

  public static <A, B> Stream<P2<A, B>> zip(final Iterable<? extends B> b, final Stream<? extends A> s) {
    return new Stream<P2<A, B>>(s.getSizeHint()) {
      @Override public Iterator<P2<A, B>> iterator() {
        final Iterator<? extends A> itA = s.iterator();
        final Iterator<? extends B> itB = b.iterator();
        return new ImmutableIteratorBase<P2<A, B>>() {
          @Override public boolean hasNext() {
            return itA.hasNext() && itB.hasNext();
          }

          @Override public P2<A, B> next() {
            return p.p2(itA.next(), itB.next());
          }
        };
      }
    };
  }

  /** Take elements from the head of a stream until predicate <code>p</code> yields false. */
  public StreamOp<A, B> takeWhile(final Fn<? super B, Boolean> p) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.takeWhile(p, StreamOp.this.apply(s));
      }
    };
  }

  /** Take elements from the head of stream <code>s</code> until predicate <code>p</code> yields false. */
  public static <A> Stream<A> takeWhile(final Fn<? super A, Boolean> p, final Stream<A> s) {
    return new Stream<A>(s.getSizeHint()) {
      @Override public Iterator<A> iterator() {
        return new IdentityIterate<A>(s.iterator()) {
          @Override protected A apply(A a) throws Exit {
            return p.apply(a) ? a : StreamOp.<A>exit();
          }
        };
      }
    };
  }

  /** Keep all elements of a stream that match predicate <code>p</code>. */
  public StreamOp<A, B> filter(final Fn<? super B, Boolean> p) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.filter(p, StreamOp.this.apply(s));
      }
    };
  }

  /** Keep all elements of stream <code>s</code> that match predicate <code>p</code>. */
  public static <A> Stream<A> filter(final Fn<? super A, Boolean> p, final Stream<A> s) {
    return new Stream<A>(s.getSizeHint()) {
      @Override public Iterator<A> iterator() {
        return new IdentityIterate<A>(s.iterator()) {
          @Override protected A apply(A a) {
            return p.apply(a) ? a : null;
          }
        };
      }
    };
  }

  /** Reverse a stream. */
  public StreamOp<A, B> reverse() {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.reverse(StreamOp.this.apply(s));
      }
    };
  }

  /**
   * Reverse stream <code>s</code>.
   * <p/>
   * <em>Implementation note:</em> Creates an intermediate collection.
   */
  public static <A> Stream<A> reverse(final Stream<A> s) {
    return new OrderChangingStream<A>(s) {
      @Override protected void changeOrder(List<A> list) {
        Collections.reverse(list);
      }
    };
  }

  /** Sort a stream. */
  public StreamOp<A, B> sort(final Comparator<B> order) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.sort(order, StreamOp.this.apply(s));
      }
    };
  }

  /**
   * Sort stream <code>s</code>.
   * <p/>
   * <em>Implementation note:</em> Creates an intermediate collection.
   */
  public static <A> Stream<A> sort(final Comparator<A> order, final Stream<A> s) {
    return new OrderChangingStream<A>(s) {
      @Override protected void changeOrder(List<A> list) {
        Collections.sort(list, order);
      }
    };
  }

  public StreamOp<A, A> append(final Stream<? extends A> s) {
    return new StreamOp<A, A>() {
      @Override public Stream<A> apply(Stream<? extends A> u) {
        return StreamOp.append(u, s);
      }
    };
  }

  public static <A> Stream<A> append(final Stream<? extends A> a, final Stream<? extends A> b) {
    return new Stream<A>(a.getSizeHint() + b.getSizeHint()) {
      @Override public Iterator<A> iterator() {
        return Iterators.join(a.iterator(), b.iterator());
      }
    };
  }

  public StreamOp<A, A> append(final Iterable<? extends A> s) {
    return new StreamOp<A, A>() {
      @Override public Stream<A> apply(Stream<? extends A> u) {
        return StreamOp.append(u, s);
      }
    };
  }

  public static <A> Stream<A> append(final Stream<? extends A> a, final Iterable<? extends A> b) {
    return new Stream<A>(a.getSizeHint()) {
      @Override public Iterator<A> iterator() {
        return Iterators.join(a.iterator(), b.iterator());
      }
    };
  }

  public StreamOp<A, B> inject(final B a) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.inject(a, StreamOp.this.apply(s));
      }
    };
  }

  public static <A> Stream<A> inject(final A a, final Stream<A> s) {
    return new Stream<A>((s.getSizeHint() * 2) - 1) {
      @Override public Iterator<A> iterator() {
        return new Mapper<A, A>(s.iterator()) {
          private boolean elem = true;

          @Override public boolean hasNext() {
            return wrapped.hasNext();
          }

          @Override public A next() {
            if (elem) {
              elem = false;
              return wrapped.next();
            } else {
              elem = true;
              return a;
            }
          }
        };
      }
    };
  }

  public StreamOp<A, B> wrap(final B pre, final B post) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.wrap(pre, post, StreamOp.this.apply(s));
      }
    };
  }

  /**
   * Wrap the stream with <code>pre</code> and <code>post</code>. If the stream is infinite <code>post</code> will
   * never be yielded. If the stream is empty the resulting stream consists only of <code>pre</code> and <code>post</code>.
   */
  public static <A> Stream<A> wrap(final A pre, final A post, final Stream<A> s) {
    return new Stream<A>(s.getSizeHint() + 2) {
      @Override public Iterator<A> iterator() {
        return new Mapper<A, A>(s.iterator()) {
          private int wrap = 2;

          @Override public boolean hasNext() {
            return wrap > 0 || wrapped.hasNext();
          }

          @Override public A next() {
            if (wrap == 2) {
              wrap--;
              return pre;
            } else if (wrapped.hasNext()) {
              return wrapped.next();
            } else if (wrap == 1) {
              wrap--;
              return post;
            } else {
              throw new NoSuchElementException();
            }
          }
        };
      }
    };
  }

  public StreamOp<A, B> each(final Fx<? super B> f) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.each(f, StreamOp.this.apply(s));
      }
    };
  }

  public static <A> Stream<A> each(final Fx<? super A> f, final Stream<A> s) {
    return new Stream<A>(s.getSizeHint()) {
      @Override public Iterator<A> iterator() {
        return new IdentityIterate<A>(s.iterator()) {
          @Override protected A apply(A a) throws Exit {
            f.apply(a);
            return a;
          }
        };
      }
    };
  }

  public <C> StreamOp<A, P2<C, B>> group(final Fn<? super B, ? extends C> key) {
    return new StreamOp<A, P2<C, B>>() {
      @Override public Stream<P2<C, B>> apply(Stream<? extends A> s) {
        return StreamOp.group(key, StreamOp.this.apply(s));
      }
    };
  }

  public static <B, A> Stream<P2<B, A>> group(final Fn<? super A, ? extends B> key, final Stream<A> s) {
    return new Stream<P2<B, A>>(s.getSizeHint()) {
      @Override public Iterator<P2<B, A>> iterator() {
        return $(StreamFold.group(key, s).entrySet()).map(Maps.<B, A>toP2()).iterator();
      }
    };
  }

  public StreamOp<A, List<B>> partition(final int size) {
    return new StreamOp<A, List<B>>() {
      @Override public Stream<List<B>> apply(Stream<? extends A> s) {
        return StreamOp.partition(size, StreamOp.this.apply(s));
      }
    };
  }

  /** Partition a list into slices of size <code>size</code>. The last slice may contain less elements. */
  public static <A> Stream<List<A>> partition(final int size, final Stream<A> s) {
    if (size <= 0) {
      throw new IllegalArgumentException("size must be greater than 0");
    } else {
      return new Stream<List<A>>(s.getSizeHint() > 0 ? s.getSizeHint() / size : Stream.NO_SIZE_HINT) {
        @Override public Iterator<List<A>> iterator() {
          final Stream<A> cont = $(new NonResettingIterable<A>(s.iterator()));
          return new ImmutableIteratorBase<List<A>>() {
            private List<A> next;

            @Override public boolean hasNext() {
              if (next != null) {
                return !next.isEmpty();
              } else {
                next = cont.take(size).toList();
                return hasNext();
              }
            }

            @Override public List<A> next() {
              if (hasNext()) {
                final List<A> r = next;
                next = null;
                return r;
              } else {
                throw new NoSuchElementException();
              }
            }
          };
        }
      };
    }
  }

  public StreamOp<A, B> repeat(final int times) {
    return new StreamOp<A, B>() {
      @Override public Stream<B> apply(Stream<? extends A> s) {
        return StreamOp.repeat(times, StreamOp.this.apply(s));
      }
    };
  }

  public static <A> Stream<A> repeat(final int times, final Stream<A> s) {
    if (times < 0) {
      throw new IllegalArgumentException("times must be greater or equal 0");
    } else if (times == 0 || s.isEmpty()) {
      return Stream.empty();
    } else {
      return new Stream<A>(s.getSizeHint() * times) {
        @Override public Iterator<A> iterator() {
          return new ImmutableIteratorBase<A>() {
            private Iterator<A> it = s.iterator();
            private int count = times - 1;

            @Override public boolean hasNext() {
              if (it.hasNext()) {
                return true;
              } else if (count > 0) {
                count--;
                it = s.iterator();
                return true;
              } else {
                return false;
              }
            }

            @Override public A next() {
              if (hasNext()) {
                return it.next();
              } else {
                throw new NoSuchElementException();
              }
            }
          };
        }
      };
    }
  }

  // --

  /** Stream implementation that relies on mutably changing collection operations. */
  private static abstract class OrderChangingStream<A> extends Stream<A> {
    private List<A> intermediate;
    private final Stream<A> s;

    private OrderChangingStream(Stream<A> s) {
      super(s.getSizeHint());
      this.s = s;
    }

    private List<A> intermediate() {
      if (intermediate == null) {
        final List<A> i = s.toList(ListBuilders.strictMutableArray);
        changeOrder(i);
        intermediate = ListBuilders.looseImmutableArray.mk(i);
      }
      return intermediate;
    }

    protected abstract void changeOrder(List<A> list);

    @Override public Iterator<A> iterator() {
      return intermediate().iterator();
    }

    @Override protected Iterable<A> getSrcHint() {
      return intermediate();
    }
  }

  /** An iterable that yields the same iterator on each call to {@link Iterable#iterator}. */
  private static class NonResettingIterable<A> implements Iterable<A> {
    private final Iterator<A> it;

    private NonResettingIterable(Iterator<A> it) {
      this.it = it;
    }

    @Override public Iterator<A> iterator() {
      return it;
    }
  }

  /** Immutable iterator mapping from type A to B. */
  private static abstract class Mapper<A, B> extends ImmutableIteratorBase<B> {
    protected final Iterator<A> wrapped;

    protected Mapper(Iterator<A> it) {
      this.wrapped = it;
    }
  }

  private static abstract class Iterate<A, B> extends ImmutableIteratorBase<B> {
    private Iterator<A> wrapped;
    private B next;

    protected Iterate(Iterator<A> it) {
      this.wrapped = it;
    }

    /** Return either a value, null to skip <code>a</code> or throw an exception to stop processing. */
    protected abstract B apply(A a) throws Exit;

    /** Return false to early exit processing. The next element up the chain will not be evaluated anymore. */
    protected boolean cont() {
      return true;
    }

    @Override public final boolean hasNext() {
      try {
        while (next == null && cont() && wrapped.hasNext()) {
          next = apply(wrapped.next());
        }
        return next != null;
      } catch (Exit e) {
        return false;
      }
    }

    @Override public final B next() {
      try {
        if (next != null) {
          return next;
        } else {
          throw new NoSuchElementException();
        }
      } finally {
        next = null;
      }
    }
  }

  /** {@link Iterate} that does not map between types. */
  private static abstract class IdentityIterate<A> extends Iterate<A, A> {
    protected IdentityIterate(Iterator<A> it) {
      super(it);
    }

    @Override protected A apply(A a) throws Exit {
      return a;
    }
  }

  private static final class Exit extends Exception {
  }

  private static <A> A exit() throws Exit {
    throw new Exit();
  }
}
