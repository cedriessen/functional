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

package com.entwinemedia.fn.parser;

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Fns;
import com.entwinemedia.fn.P2;
import com.entwinemedia.fn.P3;
import com.entwinemedia.fn.Products;
import com.entwinemedia.fn.Unit;
import com.entwinemedia.fn.data.Opt;
import com.entwinemedia.fn.fns.Booleans;
import com.entwinemedia.fn.fns.Characters;
import com.entwinemedia.fn.fns.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Collection of parsers and useful functions. */
public final class Parsers {
  private Parsers() {
  }

  // -- bind functions

  /** Parser that returns {@link com.entwinemedia.fn.Unit}. */
  public static Fn<Object, Parser<Unit>> fnNothing = new Fn<Object, Parser<Unit>>() {
    @Override public Parser<Unit> _(Object ignore) {
      return yield(Unit.unit);
    }
  };

  /** Run parser <code>p</code> but pass through the result of the previous parser. */
  public static <A, B> Fn<A, Parser<A>> fnPassThrough(final Parser<B> p) {
    return new Fn<A, Parser<A>>() {
      @Override public Parser<A> _(final A a) {
        return p.bind(new Fn<B, Parser<A>>() {
          @Override public Parser<A> _(B b) {
            return yield(a);
          }
        });
      }
    };
  }

  /** Ignore the result of the previous parser, then run parser <code>p</code>. */
  public static <A, B> Fn<A, Parser<B>> fnIgnorePrevious(final Parser<B> p) {
    return new Fn<A, Parser<B>>() {
      @Override public Parser<B> _(A a) {
        return p;
      }
    };
  }

  /** Apply function <code>f</code> to the value returned by the previous parser and return it. */
  public static <A, B> Fn<A, Parser<B>> fnYield(final Fn<A, B> f) {
    return new Fn<A, Parser<B>>() {
      @Override public Parser<B> _(A a) {
        return yield(f._(a));
      }
    };
  }

  public static <A, B> Fn<A, Parser<P2<A, B>>> fnCollect(final Parser<B> p) {
    return new Fn<A, Parser<P2<A, B>>>() {
      @Override public Parser<P2<A, B>> _(final A a) {
        return p.bind(new Fn<B, Parser<P2<A, B>>>() {
          @Override public Parser<P2<A, B>> _(B b) {
            return yield(Products.E.p2(a, b));
          }
        });
      }
    };
  }

  public static <A, B, C> Fn<P2<A, B>, Parser<P3<A, B, C>>> fnCollect2(final Parser<C> p) {
    return new Fn<P2<A, B>, Parser<P3<A, B, C>>>() {
      @Override public Parser<P3<A, B, C>> _(final P2<A, B> a) {
        return p.bind(new Fn<C, Parser<P3<A, B, C>>>() {
          @Override public Parser<P3<A, B, C>> _(C c) {
            return yield(Products.E.p3(a._1(), a._2(), c));
          }
        });
      }
    };
  }

  // -- base parsers

  /** Parser that returns <code>a</code> and does not consume any input. */
  public static <A> Parser<A> yield(final A a) {
    return new Parser<A>() {
      @Override public Result<A> parse(String input) {
        return result(a, input);
      }
    };
  }

  private static final Parser FAILURE = new Parser() {
    @Override public Result parse(String input) {
      return fail();
    }
  };

  /** Parser that always returns a failure. */
  @SuppressWarnings("unchecked")
  public static <A> Parser<A> failure() {
    return FAILURE;
  }

  /** Parser that returns the next character. */
  public static final Parser<Character> item = new Parser<Character>() {
    @Override public Result<Character> parse(String s) {
      return s.length() > 0 ? result(s.charAt(0), s.substring(1, s.length())) : fail();
    }
  };

  // -- combined parsers

  /** Parser that only succeeds if <code>p</code> matches the next character. */
  public static Parser<Character> match(final Fn<Character, Boolean> p) {
    return item.bind(new Fn<Character, Parser<Character>>() {
      @Override public Parser<Character> _(Character c) {
        return p._(c) ? yield(c) : Parsers.<Character>failure();
      }
    });
  }

  public static Parser<Character> character(Character c) {
    return match(Characters.isCharacter(c));
  }

  public static Parser<Character> digit = match(Characters.isDigit);
  public static Parser<Character> lower = match(Characters.isLower);
  public static Parser<Character> letter = match(Characters.isLetter);
  public static Parser<Character> alphaNum = match(Characters.isAlphaNum);
  public static Parser<Character> space = match(Characters.isSpace);
  public static Parser<Character> parenthesisOpen = character('(');
  public static Parser<Character> parenthesisClose = character(')');

  /** Matches only positive integers including 0 and returns them as a string. */
  public static Parser<String> natString = many1(digit).bind(new Fn<List<Character>, Parser<String>>() {
    @Override public Parser<String> _(List<Character> characters) {
      return yield(Characters.mkString._(characters));
    }
  });

  /** Matches integers and returns them as a string. */
  public static Parser<String> integerString = opt(character('-')).bind(new Fn<Opt<Character>, Parser<String>>() {
    @Override public Parser<String> _(final Opt<Character> minus) {
      return natString.bind(new Fn<String, Parser<String>>() {
        @Override public Parser<String> _(String s) {
          return yield(minus.isSome() ? minus._() + s : s);
        }
      });
    }
  });

  public static Parser<Integer> integer = integerString.bind(fnYield(Strings.toIntF));

  public static Parser<Long> lng = integerString.bind(fnYield(Strings.toLongF));

  public static Parser<Double> dbl = integerString
          .bind(new Fn<String, Parser<Double>>() {
            @Override public Parser<Double> _(final String pre) {
              return character('.').bind(new Fn<Character, Parser<Double>>() {
                @Override public Parser<Double> _(final Character ignore) {
                  return natString.bind(new Fn<String, Parser<Double>>() {
                    @Override public Parser<Double> _(final String post) {
                      return yield(Double.parseDouble(pre + "." + post));
                    }
                  });
                }
              }).or(yield(Double.parseDouble(pre)));
            }
          });

  /** Parser that consumes any amount of spaces. */
  public static Parser<Unit> skipSpaces = many(space).bind(fnNothing);

  /** Parser that only succeeds of the input starts with string <code>s</code>. */
  public static Parser<String> string(final String s) {
    return new Parser<String>() {
      @Override public Result<String> parse(String input) {
        return input.startsWith(s) ? result(s, input.substring(s.length(), input.length())) : fail();
      }
    };
  }

  /** Like {@link #string(String)} but regardless of case. */
  public static Parser<String> stringIgnoreCase(final String s) {
    return new Parser<String>() {
      @Override public Result<String> parse(String input) {
        return input.length() >= s.length() && input.substring(0, s.length()).equalsIgnoreCase(s)
                ? result(s, input.substring(s.length(), input.length()))
                : fail();
      }
    };
  }

  /** A regular expression based parser. */
  public static Parser<String> regex(final Pattern p) {
    return regex(p, Fns.<String>id());
  }

  /** A regular expression based parser. */
  public static <A> Parser<A> regex(final Pattern p, final Fn<String, ? extends A> f) {
    return new Parser<A>() {
      @Override public Result<A> parse(String input) {
        final Matcher m = p.matcher(input);
        return m.lookingAt() ? result(f._(m.group()), input.substring(m.group().length())) : fail();
      }
    };
  }

  /** A symbol parser matching "true" and "false". */
  public static Parser<Boolean> bool =
          token(stringIgnoreCase("true")).or(token(stringIgnoreCase("false"))).bind(fnYield(Booleans.parseBoolean));

  /** A symbol parser. Matches <code>symbol</code> with surrounding spaces. */
  public static Parser<String> symbol(String symbol) {
    return token(string(symbol));
  }

  /** Token parser. Skips leading and trailing spaces. */
  public static <A> Parser<A> token(final Parser<A> p) {
    return skipSpaces.bind(fnIgnorePrevious(p)).bind(Parsers.<A, Unit>fnPassThrough(skipSpaces));
  }

  /** Parser that succeeds if <code>p</code> succeeds zero or more times. */
  public static <A> Parser<List<A>> many(final Parser<A> p) {
    return many(false, p);
  }

  /** Parser that succeeds if <code>p</code> succeeds at least one time. */
  public static <A> Parser<List<A>> many1(final Parser<A> p) {
    return many(true, p);
  }

  /** Parser that returns some value if <code>p</code> succeeds or none. */
  public static <A> Parser<Opt<A>> opt(final Parser<A> p) {
    return p.bind(new Fn<A, Parser<Opt<A>>>() {
      @Override public Parser<Opt<A>> _(A a) {
        return yield(Opt.some(a));
      }
    }).or(yield(Opt.<A>none()));
  }

  private static <A> Parser<List<A>> many(final boolean atLeastOnce, final Parser<A> p) {
    return new Parser<List<A>>() {
      @Override public Result<List<A>> parse(String input) {
        final List<A> lst = new ArrayList<A>();
        Result<A> r;
        String in = input;
        while (true) {
          r = p.parse(in);
          if (r.isDefined()) {
            lst.add(r.getResult());
            in = r.getRest();
          } else {
            break;
          }
        }
        return atLeastOnce && lst.isEmpty() ? fail() : result(lst, in);
      }
    };
  }

  // -- playground

  public static final Parser<Character> second = item.bind(new Fn<Character, Parser<Character>>() {
    @Override public Parser<Character> _(Character character) {
      return item;
    }
  });
}
