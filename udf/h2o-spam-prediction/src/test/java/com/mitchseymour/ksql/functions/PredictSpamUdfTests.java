package com.mitchseymour.ksql.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class PredictSpamUdfTests {

  @ParameterizedTest
  @MethodSource("testCases")
  void predictSpam(
    final String text,
    final String expectedResult
  ) {
    final PredictSpamUdf udf = new PredictSpamUdf();
    final String actualResult = udf.predict(text);
    final String errorMessage = String.format("expected prediction for '%s' is '%s', not '%s'",
        text,
        expectedResult,
        actualResult);
    assertEquals(expectedResult, actualResult, errorMessage);
  }
  
  static Stream<Arguments> testCases() {
    return Stream.of(
      arguments(
        "hey there. just wanted to follow up on what we discussed in yesterday's meeting",
        "ham"
      ),
      arguments(
        "You have a new form to complete and sign for your life insurance application. Please review the entire form and fill in all applicable fields prior to signing. If there are any fields you believe do not apply to you they can be left blank or filled with \"n/a\"",
        "ham"
      ),
      arguments(
        "click here to improve your wellbeing today your one stop prescription shop !",
        "spam"
      ),
      arguments(
        "buy cheap viagra through us. hi,cwe have a new offer for you . buy cheap viagra through our online store . - private online ordering - no prescription required - world wide shipping order your drugs offshore and save over 70%! click here : http://zap-internet.com/meds/ best regards",
        "spam"
      )
    );
  }
}