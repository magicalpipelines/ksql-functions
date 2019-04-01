package com.mitchseymour.ksql.functions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;;

@UdfDescription(
    name = "sentiment",
    description = "Sentiment analysis of text",
    version = "0.1.0",
    author = "Mitch Seymour"
)
public class AnalyzeSentimentUdf {
  static LanguageServiceClient language = getClient();

  static LanguageServiceClient getClient() {
    // Instantiate the Natural Language API client
    try {
      return LanguageServiceClient.create();
    } catch (IOException io) {
      throw new RuntimeException("Could not instantiate Natural Language API client", io);
    }
  }

  @Udf(description = "Detect the sentiment of a string of text")
  public Map<String, Double> getSentiment(
    @UdfParameter(value = "text", description = "the text to analyze")
    final String text) {
    
    final Document doc = Document.newBuilder()
        .setContent(text)
        .setType(Type.PLAIN_TEXT)
        .build();

    // Detects the sentiment of the text
    final Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
    
    // Build the result object
    final Map<String, Double> result = new HashMap<>();
    result.put("score", Double.valueOf(sentiment.getScore()));
    result.put("magnitude", Double.valueOf(sentiment.getMagnitude()));
    return result;
  }
}
