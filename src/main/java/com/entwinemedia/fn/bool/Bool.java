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

package com.entwinemedia.fn.bool;

import static com.entwinemedia.fn.Stream.$;

import com.entwinemedia.fn.Fn;
import com.entwinemedia.fn.Fn2;
import com.entwinemedia.fn.fns.Booleans;
import com.entwinemedia.fn.fns.Numbers;
import com.entwinemedia.fn.parser.Parser;
import com.entwinemedia.fn.parser.Parsers;
import com.entwinemedia.fn.parser.Result;

import java.util.List;

/**
 * Evaluator for boolean expressions.
 * Supports:
 * <ul>
 * <li>!, not</li>
 * <li>and</li>
 * <li>or</li>
 * <li>(, )</li>
 * <li>doubles</li>
 * <li>&gt;, &gt;=, &lt;, &lt;=, ==, !=</li>
 * <li>+, -, *, /</li>
 * </ul>
 */
public final class Bool {
  @SuppressWarnings("unchecked")
  private final Parser<Boolean>[] expression = new Parser[1];
  @SuppressWarnings("unchecked")
  private final Parser<Boolean>[] term = new Parser[1];

  // boolean operations
  private final Parser<String> andLiteral = Parsers.token(Parsers.stringIgnoreCase("and"));
  private final Parser<String> orLiteral = Parsers.token(Parsers.stringIgnoreCase("or"));
  private final Parser<String> notLiteral = Parsers.token(Parsers.stringIgnoreCase("not").or(Parsers.string("!")));
  // relations
  private final Parser<Fn2<Double, Double, Boolean>> gtLiteral = relationLiteral(">", Booleans.<Double, Double>gt());
  private final Parser<Fn2<Double, Double, Boolean>> geLiteral = relationLiteral(">=", Booleans.<Double, Double>ge());
  private final Parser<Fn2<Double, Double, Boolean>> ltLiteral = relationLiteral("<", Booleans.<Double, Double>lt());
  private final Parser<Fn2<Double, Double, Boolean>> leLiteral = relationLiteral("<=", Booleans.<Double, Double>le());
  private final Parser<Fn2<Double, Double, Boolean>> eqLiteral = relationLiteral("==", Booleans.<Double, Double>eq());
  private final Parser<Fn2<Double, Double, Boolean>> neLiteral = relationLiteral("!=", Booleans.<Double, Double>ne());
  // order is important! (ge before gt, le before lt)
  private final Parser<Fn2<Double, Double, Boolean>> relationLiteral =
          geLiteral.or(gtLiteral).or(leLiteral).or(ltLiteral).or(eqLiteral).or(neLiteral);
  // number operations
  private final Parser<Fn2<Double, Double, Double>> plusLiteral = numberOperationLiteral("+", Numbers.doublePlus);
  private final Parser<Fn2<Double, Double, Double>> minusLiteral = numberOperationLiteral("-", Numbers.doubleMinus);
  private final Parser<Fn2<Double, Double, Double>> multiplicationLiteral = numberOperationLiteral("*", Numbers.doubleMult);
  private final Parser<Fn2<Double, Double, Double>> divisionLiteral = numberOperationLiteral("/", Numbers.doubleDiv);
  private final Parser<Fn2<Double, Double, Double>> operationLiteral =
          plusLiteral.or(minusLiteral).or(multiplicationLiteral).or(divisionLiteral);
  //
  private final Parser<Double> number = Parsers.token(Parsers.dbl);

  private Parser<Fn2<Double, Double, Boolean>> relationLiteral(String token, Fn2<Double, Double, Boolean> f) {
    return Parsers.token(Parsers.string(token)).bind(Parsers.<String, Fn2<Double, Double, Boolean>>ignorePrevious(Parsers.yield(f)));
  }

  private Parser<Fn2<Double, Double, Double>> numberOperationLiteral(
          String token, Fn2<Double, Double, Double> f) {
    return Parsers.token(Parsers.string(token)).bind(Parsers.<String, Fn2<Double, Double, Double>>ignorePrevious(Parsers.yield(f)));
  }

  /** Create a parser for operations. */
  private <A, B> Parser<B> op(final Parser<A> value, final Parser<Fn2<A, A, B>> opLiteral) {
    return value.bind(new Fn<A, Parser<B>>() {
      @Override public Parser<B> apply(final A a) {
        return opLiteral.bind(new Fn<Fn2<A, A, B>, Parser<B>>() {
          @Override public Parser<B> apply(final Fn2<A, A, B> op) {
            return value.bind(new Fn<A, Parser<B>>() {
              @Override public Parser<B> apply(A b) {
                return Parsers.yield(op.apply(a, b));
              }
            });
          }
        });
      }
    });
  }

  /*
    Boolean expression

    <expression> ::= <term> ["OR" <expression>]
    <term> ::= <value> ["AND" <term>]
    <value> ::= ["NOT"]* ( "(" <expression> ")" | <relation> | <bool-literal> )
    <relation> ::= <relation-factor> <rel-literal> <relation-factor>
    <relation-factor> ::= <operation> | <number>
    <operation> ::= <number> <op-literal> <number>
    <rel-literal> ::= ">=" | ">" | "<=" | "<" | "=" | "!="
    <op-literal> ::= "+" | "-" | "*" | "/"
    <bool-literal> ::= "true" | "false"
   */
  public Bool(List<Parser<Boolean>> valueParser) {
    // operation
    final Parser<Double> operation = op(number, operationLiteral);
    // relation
    final Parser<Boolean> relation = op(operation.or(number), relationLiteral);
    // expression in parenthesis
    final Parser<Boolean> parenthesizedExpression = Parsers.symbol("(")
            .bind(new Fn<String, Parser<Boolean>>() {
              @Override public Parser<Boolean> apply(String ignore) {
                return expression[0];
              }
            })
            .bind(Parsers.<Boolean, String>ignore(Parsers.symbol(")")));
    // base value parser
    final Parser<Boolean> valueBase = Parsers.many(notLiteral).bind(new Fn<List<String>, Parser<Boolean>>() {
      @Override public Parser<Boolean> apply(final List<String> not) {
        return parenthesizedExpression.or(relation).or(Parsers.bool).bind(new Fn<Boolean, Parser<Boolean>>() {
          @Override public Parser<Boolean> apply(Boolean a) {
            final boolean negate = not.size() % 2 == 1;
            return Parsers.yield(a ^ negate);
          }
        });
      }
    });
    // enrich boolean base value parser
    final Parser<Boolean> value = $(valueParser).foldl(valueBase, new Fn2<Parser<Boolean>, Parser<Boolean>, Parser<Boolean>>() {
      @Override public Parser<Boolean> apply(Parser<Boolean> sum, Parser<Boolean> p) {
        return sum.or(p);
      }
    });
    // a term
    term[0] = value.bind(new Fn<Boolean, Parser<Boolean>>() {
      @Override public Parser<Boolean> apply(final Boolean a) {
        return andLiteral
                .bind(Parsers.ignorePrevious(term[0]))
                .bind(new Fn<Boolean, Parser<Boolean>>() {
                  @Override public Parser<Boolean> apply(Boolean b) {
                    return Parsers.yield(a && b);
                  }
                })
                .or(Parsers.yield(a));
      }
    });
    // an expression
    expression[0] = term[0].bind(new Fn<Boolean, Parser<Boolean>>() {
      @Override public Parser<Boolean> apply(final Boolean a) {
        return orLiteral
                .bind(Parsers.ignorePrevious(expression[0]))
                .bind(new Fn<Boolean, Parser<Boolean>>() {
                  @Override public Parser<Boolean> apply(Boolean b) {
                    return Parsers.yield(a || b);
                  }
                })
                .or(Parsers.yield(a));
      }
    });
  }

  /** Evaluate an expression. */
  public Result<Boolean> eval(String expr) {
    return expression[0].parse(expr);
  }
}
