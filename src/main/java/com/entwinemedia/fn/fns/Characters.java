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
import com.entwinemedia.fn.Pred;

/** Functions on {@link java.lang.Character}s. */
public final class Characters {
  private Characters() {
  }

  public static final Pred<Character> isDigit = new Pred<Character>() {
    @Override public Boolean ap(Character character) {
      return Character.isDigit(character);
    }
  };

  public static final Pred<Character> isLower = new Pred<Character>() {
    @Override public Boolean ap(Character character) {
      return Character.isLowerCase(character);
    }
  };

  public static final Pred<Character> isUpper = new Pred<Character>() {
    @Override public Boolean ap(Character character) {
      return Character.isUpperCase(character);
    }
  };

  public static final Pred<Character> isLetter = new Pred<Character>() {
    @Override public Boolean ap(Character character) {
      return Character.isLetter(character);
    }
  };

  public static final Pred<Character> isAlphaNum = new Pred<Character>() {
    @Override public Boolean ap(Character character) {
      return Character.isLetterOrDigit(character);
    }
  };

  public static final Pred<Character> isWhitespace = new Pred<Character>() {
    @Override public Boolean ap(Character character) {
      return Character.isWhitespace(character);
    }
  };

  public static final Pred<Character> isSpace = new Pred<Character>() {
    @Override public Boolean ap(Character character) {
      return Character.isSpaceChar(character);
    }
  };

  public static Pred<Character> isCharacter(final char c) {
    return new Pred<Character>() {
      @Override public Boolean ap(Character character) {
        return character == c;
      }
    };
  }

  public static final Fn<Iterable<Character>, String> mkString = new Fn<Iterable<Character>, String>() {
    @Override public String ap(Iterable<Character> characters) {
      final StringBuilder b = new StringBuilder();
      for (Character c : characters) {
        b.append(c);
      }
      return b.toString();
    }
  };
}
