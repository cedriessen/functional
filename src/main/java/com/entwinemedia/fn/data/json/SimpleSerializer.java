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
  /** Functions */
  public final Functions fn = new Functions();

  @Override
  public void toJson(OutputStream out, JValue v) {
    final Writer writer = new OutputStreamWriter(out);
    objectToJson(writer, v);
    try {
      writer.flush();
    } catch (IOException ignore) {
    }
  }

  public void toJson(Writer writer, JValue v) {
    objectToJson(writer, v);
  }

  public String toJson(JValue v) {
    try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      toJson(out, v);
      return out.toString("UTF-8");
    } catch (IOException e) {
      return chuck(e);
    }
  }

  private void objectToJson(Writer writer, Object v) {
    if (v instanceof JString) {
      toJson(writer, (JString) v);
    } else if (v instanceof JPrimitive) {
      toJson(writer, (JPrimitive) v);
    } else if (v instanceof JObject) {
      toJson(writer, (JObject) v);
    } else if (v instanceof Field) {
      toJson(writer, (Field) v);
    } else if (v instanceof JArray) {
      toJson(writer, (JArray) v);
    } else if (v instanceof Zero) {
      // do nothing
    } else if (v instanceof JNull) {
      toJson(writer, (JNull) v);
    } else {
      Prelude.<Boolean>unexhaustiveMatch(v);
    }
  }

  private void toJson(Writer writer, JObject obj) {
    write(writer, "{");
    objectsToJson(writer, obj.iterator());
    write(writer, "}");
  }

  private void toJson(Writer writer, JArray j) {
    write(writer, "[");
    objectsToJson(writer, j.iterator());
    write(writer, "]");
  }

  private void toJson(Writer writer, Field f) {
    if (ne(f.value(), Jsons.ZERO)) {
      writeQuoted(writer, f.key());
      write(writer, ":");
      toJson(writer, f.value());
    }
  }

  private void toJson(Writer writer, JString j) {
    writeQuoted(writer, j.value());
  }

  private void toJson(Writer writer, JNull j) {
    write(writer, "null");
  }

  private <A> void toJson(Writer writer, JPrimitive<A> j) {
    write(writer, j.value().toString());
  }

  private void objectsToJson(Writer writer, Iterator<?> it) {
    if (it.hasNext()) {
      objectToJson(writer, it.next());
    }
    while (it.hasNext()) {
      write(writer, ",");
      objectToJson(writer, it.next());
    }
  }

  private void writeQuoted(Writer writer, String s) {
    write(writer, "\"");
    write(writer, Util.escape(s));
    write(writer, "\"");
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
        @Override public void def(OutputStream out) {
          SimpleSerializer.this.toJson(out, v);
        }
      };
    }
  }
}
