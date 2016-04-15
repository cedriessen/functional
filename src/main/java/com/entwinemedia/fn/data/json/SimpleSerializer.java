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

package com.entwinemedia.fn.data.json;

import static com.entwinemedia.fn.Equality.ne;
import static com.entwinemedia.fn.Prelude.chuck;

import com.entwinemedia.fn.Fx;
import com.entwinemedia.fn.Prelude;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * Serialization is not part of the JSON AST classes to allow for different serializer implementations.
 */
public class SimpleSerializer implements Serializer {
  @Override
  public boolean toJson(OutputStream out, JValue v) {
    final Writer writer = new OutputStreamWriter(out);
    final boolean r = objectToJson(writer, false, v);
    try {
      writer.flush();
    } catch (IOException ignore) {
    }
    return r;
  }

  public boolean toJson(Writer writer, JValue v) {
    return objectToJson(writer, false, v);
  }

  public String toJson(JValue v) {
    try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      toJson(out, v);
      return out.toString("UTF-8");
    } catch (IOException e) {
      return chuck(e);
    }
  }

  private boolean objectToJson(Writer writer, boolean notFirst, Object v) {
    if (v instanceof JString) {
      return toJson(writer, notFirst, (JString) v);
    } else if (v instanceof JPrimitive) {
      return toJson(writer, notFirst, (JPrimitive) v);
    } else if (v instanceof JObject) {
      return toJson(writer, notFirst, (JObject) v);
    } else if (v instanceof Field) {
      return toJson(writer, notFirst, (Field) v);
    } else if (v instanceof JArray) {
      return toJson(writer, notFirst, (JArray) v);
    } else if (v instanceof Zero) {
      return toJson(writer, (Zero) v);
    } else if (v instanceof JNull) {
      return toJson(writer, notFirst, (JNull) v);
    } else {
      return Prelude.<Boolean>unexhaustiveMatch(v);
    }
  }

  private boolean toJson(Writer writer, boolean notFirst, JObject obj) {
    if (notFirst) {
      write(writer, ",");
    }
    write(writer, "{");
    objectsToJson(writer, obj.iterator());
    write(writer, "}");
    return true;
  }

  private boolean toJson(Writer writer, boolean notFirst, JArray j) {
    if (notFirst) {
      write(writer, ",");
    }
    write(writer, "[");
    objectsToJson(writer, j.iterator());
    write(writer, "]");
    return true;
  }

  private boolean toJson(Writer writer, boolean notFirst, Field f) {
    if (ne(f.value(), Jsons.ZERO)) {
      writeQuoted(writer, notFirst, f.key());
      write(writer, ":");
      return toJson(writer, f.value());
    } else {
      return false;
    }
  }

  private boolean toJson(Writer writer, boolean notFirst, JString j) {
    writeQuoted(writer, notFirst, j.value());
    return true;
  }

  private boolean toJson(Writer writer, boolean notFirst, JNull j) {
    writeSimple(writer, notFirst, "null");
    return true;
  }

  private boolean toJson(Writer writer, Zero j) {
    return false;
  }

  private <A> boolean toJson(Writer writer, boolean notFirst, JPrimitive<A> j) {
    writeSimple(writer, notFirst, j.value().toString());
    return true;
  }

  private void objectsToJson(Writer writer, Iterator<?> it) {
    boolean hasBeenWritten = false;
    while (it.hasNext()) {
      hasBeenWritten = objectToJson(writer, hasBeenWritten, it.next()) || hasBeenWritten;
    }
  }

  private void writeQuoted(Writer writer, boolean notFirst, String s) {
    if (notFirst) {
      write(writer, ",");
    }
    write(writer, "\"");
    write(writer, Util.escape(s));
    write(writer, "\"");
  }

  private void writeSimple(Writer writer, boolean notFirst, String s) {
    if (notFirst) {
      write(writer, ",");
    }
    write(writer, s);
  }

  private void write(Writer writer, String s) {
    try {
      writer.write(s);
    } catch (IOException e) {
      chuck(e);
    }
  }

  public final class Functions {
    private Functions() {
    }

    /** {@link SimpleSerializer#toJson(java.io.OutputStream, JValue)} as an effect. */
    public Fx<OutputStream> toJson(final JValue v) {
      return new Fx<OutputStream>() {
        @Override public void apply(OutputStream out) {
          SimpleSerializer.this.toJson(out, v);
        }
      };
    }
  }
}
