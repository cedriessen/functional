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

import com.entwinemedia.fn.data.ImmutableMapWrapper;
import com.entwinemedia.fn.data.ListFactory;
import com.entwinemedia.fn.data.Opt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class StreamFold<A, B> {
  /** Apply the fold to stream <code>s</code>. */
  public abstract B apply(Stream<? extends A> s);

  public Fn<Stream<A>, B> toFn() {
    return new Fn<Stream<A>, B>() {
      @Override public B apply(Stream<A> s) {
        return StreamFold.this.apply(s);
      }
    };
  }

  /** Create a stream fold from a function. */
  public static <A, B> StreamFold<A, B> mk(final Fn<Stream<A>, ? extends B> f) {
    return new StreamFold<A, B>() {
      @SuppressWarnings("unchecked")
      @Override public B apply(Stream<? extends A> s) {
        return f.apply((Stream<A>) s);
      }
    };
  }

  /** Map function <code>f</code> over the result of this fold. */
  public <C> StreamFold<A, C> fmap(final Fn<? super B, ? extends C> f) {
    return new StreamFold<A, C>() {
      @Override public C apply(Stream<? extends A> s) {
        return f.apply(StreamFold.this.apply(s));
      }
    };
  }

  public static <A, B> StreamFold<A, B> foldl(final B zero, final Fn2<? super B, ? super A, ? extends B> f) {
    return new StreamFold<A, B>() {
      @Override public B apply(Stream<? extends A> s) {
        return foldl(zero, f, s);
      }
    };
  }

  public static <A, B> B foldl(final B zero, final Fn2<? super B, ? super A, ? extends B> f, final Stream<A> s) {
    B fold = zero;
    for (A a : s) {
      fold = f.apply(fold, a);
    }
    return fold;
  }

  public static <A> StreamFold<A, String> mkString(final String sep) {
    return new StreamFold<A, String>() {
      @Override public String apply(Stream<? extends A> s) {
        return mkString(sep, s);
      }
    };
  }

  /** Concatenate to a string separating each element by <code>sep</code>. */
  @SuppressWarnings("unchecked")
  public static <A> String mkString(String sep, Stream<? extends A> s) {
    final StringBuilder b = new StringBuilder();
    final Iterator<A> as = (Iterator<A>) s.iterator();
    if (as.hasNext()) {
      b.append(as.next());
    }
    while (as.hasNext()) {
      b.append(sep).append(as.next());
    }
    return b.toString();
  }

  public static <A> StreamFold<A, Boolean> exists(final Fn<? super A, Boolean> p) {
    return new StreamFold<A, Boolean>() {
      @Override public Boolean apply(Stream<? extends A> s) {
        return StreamFold.exists(p, s);
      }
    };
  }

  public static <A> boolean exists(Fn<? super A, Boolean> p, Stream<? extends A> s) {
    for (A a : s) {
      if (p.apply(a)) return true;
    }
    return false;
  }

  public static <A> StreamFold<A, Opt<A>> find(final Fn<? super A, Boolean> p) {
    return new StreamFold<A, Opt<A>>() {
      @Override public Opt<A> apply(Stream<? extends A> s) {
        return StreamFold.find(p, s);
      }
    };
  }

  public static <A> Opt<A> find(Fn<? super A, Boolean> p, Stream<? extends A> s) {
    for (A a : s) {
      if (p.apply(a)) return Opt.some(a);
    }
    return Opt.none();
  }

  /** Map function <code>p</code> over the elements of the stream unless <code>p</code> yields a some. */
  public static <A, B> StreamFold<A, Opt<B>> findMap(final Fn<? super A, Opt<B>> p) {
    return new StreamFold<A, Opt<B>>() {
      @Override public Opt<B> apply(Stream<? extends A> s) {
        return StreamFold.findMap(p, s);
      }
    };
  }

  public static <A, B> Opt<B> findMap(Fn<? super A, Opt<B>> p, Stream<? extends A> s) {
    for (A a : s) {
      final Opt<B> r = p.apply(a);
      if (r.isSome()) {
        return r;
      }
    }
    return Opt.none();
  }

  public static <A> StreamFold<A, Opt<A>> head() {
    return new StreamFold<A, Opt<A>>() {
      @Override public Opt<A> apply(Stream<? extends A> s) {
        return StreamFold.head(s);
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static <A> Opt<A> head(Stream<? extends A> s) {
    final Iterator<A> i = (Iterator<A>) s.iterator();
    return i.hasNext() ? Opt.some(i.next()) : Opt.<A>none();
  }

  public static <A> StreamFold<A, A> head2() {
    return new StreamFold<A, A>() {
      @Override public A apply(Stream<? extends A> s) {
        return StreamFold.head2(s);
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static <A> A head2(Stream<? extends A> s) {
    final Iterator<A> i = (Iterator<A>) s.iterator();
    if (i.hasNext()) {
      return i.next();
    } else {
      throw new NoSuchElementException();
    }
  }

  public static <A> StreamFold<A, A> sum(final Monoid<A> m) {
    return new StreamFold<A, A>() {
      @Override public A apply(Stream<? extends A> s) {
        return StreamFold.sum(m, s);
      }
    };
  }

  public static <A> A sum(Monoid<A> m, Stream<? extends A> s) {
    return foldl(m.identity(), m.op(), s);
  }

  // todo use MapBuilder
  public static <B, A> StreamFold<A, Map<B, A>> group(final Fn<? super A, ? extends B> key) {
    return new StreamFold<A, Map<B, A>>() {
      @Override public Map<B, A> apply(Stream<? extends A> s) {
        return StreamFold.group(key, s);
      }
    };
  }

  // todo use MapBuilder
  public static <B, A> Map<B, A> group(final Fn<? super A, ? extends B> key, final Stream<? extends A> s) {
    final Map<B, A> sum = foldl(new HashMap<B, A>(), new Fn2<Map<B, A>, A, Map<B, A>>() {
      @Override public Map<B, A> apply(Map<B, A> sum, A a) {
        sum.put(key.apply(a), a);
        return sum;
      }
    }, s);
    return new ImmutableMapWrapper<B, A>(sum);
  }

  // todo use MapBuilder
  public static <C, B, A> StreamFold<A, Map<B, C>> group(final Fn<? super A, ? extends B> key,
                                                         final Fn<? super A, ? extends C> value) {
    return new StreamFold<A, Map<B, C>>() {
      @Override public Map<B, C> apply(Stream<? extends A> s) {
        return StreamFold.group(key, value, s);
      }
    };
  }

  // todo use MapBuilder
  public static <C, B, A> Map<B, C> group(final Fn<? super A, ? extends B> key,
                                          final Fn<? super A, ? extends C> value,
                                          final Stream<? extends A> s) {
    final Map<B, C> sum = foldl(new HashMap<B, C>(), new Fn2<Map<B, C>, A, Map<B, C>>() {
      @Override public Map<B, C> apply(Map<B, C> sum, A a) {
        sum.put(key.apply(a), value.apply(a));
        return sum;
      }
    }, s);
    return new ImmutableMapWrapper<B, C>(sum);
  }

  // todo use MapBuilder
  public static <B, A> StreamFold<A, Map<B, List<A>>> groupMulti(
          final ListFactory f, final Fn<? super A, ? extends B> key) {
    return new StreamFold<A, Map<B, List<A>>>() {
      @Override public Map<B, List<A>> apply(Stream<? extends A> s) {
        return StreamFold.groupMulti(f, key, s);
      }
    };
  }

  // todo use MapBuilder
  public static <B, A> Map<B, List<A>> groupMulti(
          final ListFactory f, final Fn<? super A, ? extends B> key, final Stream<? extends A> s) {
    final Map<B, List<A>> sum = foldl(new HashMap<B, List<A>>(), new Fn2<Map<B, List<A>>, A, Map<B, List<A>>>() {
      @Override public Map<B, List<A>> apply(Map<B, List<A>> sum, A a) {
        final B k = key.apply(a);
        if (sum.containsKey(k)) {
          sum.get(k).add(a);
        } else {
          final List<A> buf = f.buffer();
          buf.add(a);
          sum.put(k, buf);
        }
        return sum;
      }
    }, s);
    // finalize lists
    for (Map.Entry<B, List<A>> e : sum.entrySet()) {
      e.setValue(f.toList(e.getValue()));
    }
    return new ImmutableMapWrapper<B, List<A>>(sum);
  }
}
