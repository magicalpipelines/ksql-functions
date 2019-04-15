package com.mitchseymour.ksql.functions.polyglot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
public class ColorToHexUdfTests {

  @ParameterizedTest(name = "color_to_hex({0})= {1}")
  @CsvSource({
    "deepskyblue, #00bfff",
    "peachpuff, #ffdab9",
    "ghostwhite, #f8f8ff",
    "seagreen, #2e8b57"
  })
  void run(String color, String hex) {
    final ColorToHexUdf udf = new ColorToHexUdf();
    assertEquals(hex, udf.run(color));
  }
}

