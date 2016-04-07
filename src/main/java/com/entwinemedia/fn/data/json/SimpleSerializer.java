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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * Serialization is not part of the JSON AST classes to allow for different serializer implementations.
 */
public class SimpleSerializer implements Serializer {
  /** {@link SimpleSerializer#toJson(java.io.Writer, JValue)} as an effect. */
  public Fx<Writer> toJsonFx(final JValue v) {
    return new Fx<Writer>() {
      @Override public void apply(Writer writer) {
        toJson(writer, v);
      }
    };
  }

  public String toJson(JValue v) {
    final StringWriter writer = new StringWriter();
    toJson(writer, v);
    return writer.toString();
  }

  @Override
  public boolean toJson(Writer writer, JValue v) {
    return objectToJson(writer, v);
  }

  private boolean objectToJson(Writer writer, Object v) {
    if (v instanceof JString) {
      return toJson(writer, (JString) v);
    } else if (v instanceof JPrimitive) {
      return toJson(writer, (JPrimitive) v);
    } else if (v instanceof JObjectWrite) {
      return toJson(writer, (JObjectWrite) v);
    } else if (v instanceof JField) {
      return toJson(writer, (JField) v);
    } else if (v instanceof JArrayWrite) {
      return toJson(writer, (JArrayWrite) v);
    } else if (v instanceof JZero) {
      return toJson(writer, (JZero) v);
    } else if (v instanceof JNull) {
      return toJson(writer, (JNull) v);
    } else {
      return Prelude.<Boolean>unexhaustiveMatch(v);
    }
  }

  public boolean toJson(Writer writer, JObjectWrite obj) {
    write(writer, "{");
    objectsToJson(writer, obj.iterator());
    write(writer, "}");
    return true;
  }

  public boolean toJson(Writer writer, JArrayWrite j) {
    write(writer, "[");
    objectsToJson(writer, j.iterator());
    write(writer, "]");
    return true;
  }

  public boolean toJson(Writer writer, JField f) {
    if (ne(f.value(), Jsons.ZERO)) {
      writeString(writer, f.key());
      write(writer, ":");
      return toJson(writer, f.value());
    } else {
      return false;
    }
  }

  public boolean toJson(Writer writer, JString j) {
    writeString(writer, j.value());
    return true;
  }

  public boolean toJson(Writer writer, JNull j) {
    write(writer, "null");
    return true;
  }

  public boolean toJson(Writer writer, JZero j) {
    return false;
  }

  public <A> boolean toJson(Writer writer, JPrimitive<A> j) {
    write(writer, j.value().toString());
    return true;
  }

  private void objectsToJson(Writer writer, Iterator<?> it) {
    boolean writeSep = false;
    if (it.hasNext()) {
      writeSep = objectToJson(writer, it.next());
    }
    while (it.hasNext()) {
      if (writeSep) {
        write(writer, ",");
      }
      writeSep = objectToJson(writer, it.next());
    }
  }

  private void writeString(Writer writer, String s) {
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
}
