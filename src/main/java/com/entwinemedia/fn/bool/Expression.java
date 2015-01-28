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

/**
 * AST for boolean expressions.
 * <p/>
 * Expr = Bool | And Expr Expr | Or Expr Expr | Not Expr
 */
public abstract class Expression {
  /** Evaluate the expression. */
  public abstract boolean eval();

  private static class Value extends Expression {
    private final boolean value;

    private Value(boolean value) {
      this.value = value;
    }

    @Override public boolean eval() {
      return value;
    }
  }

  /** A binary operation. */
  private abstract static class BinOp extends Expression {
    protected final Expression a;
    protected final Expression b;

    private BinOp(Expression a, Expression b) {
      this.a = a;
      this.b = b;
    }
  }

  /** Logical AND. */
  private static final class And extends BinOp {
    private And(Expression a, Expression b) {
      super(a, b);
    }

    @Override public boolean eval() {
      return a.eval() && b.eval();
    }
  }

  /** Logical OR. */
  private static final class Or extends BinOp {
    private Or(Expression a, Expression b) {
      super(a, b);
    }

    @Override public boolean eval() {
      return a.eval() || b.eval();
    }
  }

  /** Negation. */
  private static class Not extends Expression {
    private final Expression a;

    private Not(Expression a) {
      this.a = a;
    }

    @Override public boolean eval() {
      return !a.eval();
    }
  }

  // --

  public static Expression value(boolean value) {
    return new Value(value);
  }

  public static Expression and(Expression a, Expression b) {
    return new And(a, b);
  }

  public static Expression or(Expression a, Expression b) {
    return new Or(a, b);
  }

  public static Expression not(Expression a) {
    return new Not(a);
  }

  public Expression and(Expression a) {
    return and(this, a);
  }

  public Expression or(Expression a) {
    return or(this, a);
  }

  public Expression not() {
    return not(this);
  }

  public static Expression parse(String exp) {
    return null;
  }
}
