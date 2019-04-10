package com.mitchseymour.ksql.functions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.common.collect.Lists;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.language.v1.Sentiment;

import io.confluent.common.Configurable;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;;

@UdfDescription(
    name = "sentiment",
    description = "Sentiment analysis of text",
    version = "0.2.0",
    author = "Mitch Seymour"
)
public class SentimentUdf implements Configurable {
  private static final String CONFIG_PREFIX = "ksql.functions.sentiment.";
  private LanguageServiceClient language;

  @Override
  public void configure(final Map<String, ?> map) {
    LanguageServiceSettings settings;

    try {
       // check to see if we should pull GCP service account credentials from a file
      if (!map.containsKey(CONFIG_PREFIX + "credentials.file")) {
        // no credentials file. this is okay if the creds were set via:
        // export GOOGLE_APPLICATION_CREDENTIALS=/path/to/creds.json
        settings = LanguageServiceSettings.newBuilder().build();
      } else {
        final String path = (String) map.get(CONFIG_PREFIX + "credentials.file");
        final GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(path))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        settings = LanguageServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build();
      }
      // create the client
      language = LanguageServiceClient.create(settings);
    } catch (IOException io) {
      throw new RuntimeException("Could not create GCP Natural Language API client");
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
