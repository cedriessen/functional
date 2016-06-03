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

import static com.entwinemedia.fn.Stream.$;

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Fn2;
import com.entwinemedia.fn.Pred;
import com.entwinemedia.fn.Stream;
import com.entwinemedia.fn.StreamOp;
import com.entwinemedia.fn.data.Opt;

import java.util.regex.Pattern;

public final class Strings {
  private Strings() {
  }

  public static final Fn<String, Integer> len = new Fn<String, Integer>() {
    @Override public Integer def(String s) {
      return s.length();
    }
  };

  public static final Fn<String, String> trim = new Fn<String, String>() {
    @Override public String def(String s) {
      return s.trim();
    }
  };

  /**
   * Return some, if the string is not blank, none otherwise.
   * In contrast to {@link #trimToNone} strings are not trimmed.
   */
  public static final Fn<String, Opt<String>> blankToNone = new Fn<String, Opt<String>>() {
    @Override public Opt<String> def(String s) {
      return s.trim().isEmpty() ? Opt.<String>none() : Opt.some(s);
    }
  };

  public static final Fn<String, Opt<String>> emptyToNone = new Fn<String, Opt<String>>() {
    @Override public Opt<String> def(String s) {
      return !s.isEmpty() ? Opt.some(s) : Opt.<String>none();
    }
  };

  /** Trim the string and return it wrapped in a some if not empty. Return none otherwise. */
  public static final Fn<String, Opt<String>> trimToNone = emptyToNone.o(trim);

  public static Fn<String, String> wrap(final String pre, final String post) {
    return new Fn<String, String>() {
      @Override public String def(String s) {
        return pre + s + post;
      }
    };
  }

  public static final Fn<String, String> toLowerCase = new Fn<String, String>() {
    @Override public String def(String s) {
      return s.toLowerCase();
    }
  };

  public static Fn<Object, String> format(final String format) {
    return new Fn<Object, String>() {
      @Override public String def(Object s) {
        return String.format(format, s);
      }
    };
  }

  /** @see java.util.regex.Matcher#matches() */
  public static Pred<String> matches(final String pattern) {
    return matches(Pattern.compile(pattern));
  }

  /** @see java.util.regex.Matcher#matches() */
  public static Pred<String> matches(final Pattern pattern) {
    return new Pred<String>() {
      @Override public Boolean def(String s) {
        return pattern.matcher(s).matches();
      }
    };
  }

  public static Fn<String, Stream<String>> split(final String pattern) {
    return split(Pattern.compile(pattern));
  }

  public static Fn<String, Stream<String>> split(final Pattern pattern) {
    return new Fn<String, Stream<String>>() {
      @Override public Stream<String> def(String s) {
        return $(pattern.split(s));
      }
    };
  }

  private static final Pattern CSV = Pattern.compile("\\s*,\\s*");

  public static Fn<String, Stream<String>> splitCsv = split(CSV);

  /** Split a string at new line characters and trim each line. Empty lines are kept. */
  public static StreamOp<String, String> soSplitNewLineTrim = StreamOp.<String>id().bind(split("\n")).fmap(trim);

  public static final Pred<String> isEmpty = new Pred<String>() {
    @Override public Boolean def(String s) {
      return s.isEmpty();
    }
  };

  public static final Pred<String> isNotEmpty = Booleans.not(isEmpty);

  public static final StreamOp<String, String> removeEmptySO = StreamOp.<String>id().filter(isNotEmpty);

  public static final Pred<String> isBlank = new Pred<String>() {
    @Override public Boolean def(String s) {
      return s.trim().isEmpty();
    }
  };

  public static final Pred<String> isNotBlank = Booleans.not(isBlank);

  public static final StreamOp<String, String> removeBlankSO = StreamOp.<String>id().filter(isNotBlank);

  public static final Fn2<String, String, String> concat = new Fn2<String, String, String>() {
    @Override public String def(String a, String b) {
      return a + b;
    }
  };

  /** Convert a string into a long if possible. */
  public static final Fn<String, Opt<Long>> toLong = new Fn<String, Opt<Long>>() {
    @Override public Opt<Long> def(String s) {
      try {
        return Opt.some(Long.parseLong(s));
      } catch (NumberFormatException e) {
        return Opt.none();
      }
    }
  };

  /** Convert a string into a long if possible. */
  public static final Fn<String, Long> toLongF = new Fn<String, Long>() {
    @Override public Long def(String s) {
      return Long.parseLong(s);
    }
  };

  /** Convert a string into an integer if possible. */
  public static final Fn<String, Opt<Integer>> toInt = new Fn<String, Opt<Integer>>() {
    @Override public Opt<Integer> def(String s) {
      try {
        return Opt.some(Integer.parseInt(s));
      } catch (NumberFormatException e) {
        return Opt.none();
      }
    }
  };

  /** Convert a string into an integer if possible. */
  public static final Fn<String, Integer> toIntF = new Fn<String, Integer>() {
    @Override public Integer def(String s) {
      return Integer.parseInt(s);
    }
  };
}
