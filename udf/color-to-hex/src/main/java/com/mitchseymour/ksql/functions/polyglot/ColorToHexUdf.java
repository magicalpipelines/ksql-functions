package com.mitchseymour.ksql.functions.polyglot;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

/*
gu install ruby
rvm mount ~/.jenv/versions/1.8/jre/languages/ruby/ -n truffleruby
gem install chroma
*/
@UdfDescription(
    name = "color_to_hex",
    description = "Polyglot UDF that converts colors to hex codes)",
    version = "0.1.0",
    author = "Mitch Seymour"
)
public class ColorToHexUdf {
  Context context = Context.newBuilder(new String[]{"ruby"})
    .engine(Engine.create())
    .allowIO(true)
    .allowNativeAccess(true)
    .build();

  @Udf(description = "Get the color")
  public String run(String color) {
    try {
      return context
        .eval("ruby", "lambda { |x| require 'chroma'; x.paint.to_hex }")
        .execute(color)
        .as(String.class);
    } catch (Exception e) {
      throw new RuntimeException("Could execute polyglot function", e);
    }
  }
}

