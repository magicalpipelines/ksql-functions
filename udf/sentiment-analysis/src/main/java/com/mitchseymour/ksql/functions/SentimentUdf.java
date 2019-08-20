package com.mitchseymour.ksql.functions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.AnalyzeSentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.common.collect.Lists;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.language.v1.Sentiment;

import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.kafka.common.Configurable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UdfDescription(
    name = "sentiment",
    description = "Sentiment analysis of text",
    version = "0.3.0",
    author = "Mitch Seymour"
)
public class SentimentUdf implements Configurable {
  private static final String CONFIG_PREFIX = "ksql.functions.sentiment.";
  private static final Logger log = LoggerFactory.getLogger(SentimentUdf.class);

  private static final String SCORE_KEY = "score";
  private static final String MAGNITUDE_KEY = "magnitude";
  private static final String SUCCESS_KEY = "success";

  private LanguageServiceClient language;
  private boolean configured = false;

  @Override
  public void configure(final Map<String, ?> map) {
    log.info("Configuring sentiment UDF");
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
      configured = true;
    } catch (IOException io) {
      final String error = "Could not create GCP Natural Language API client";
      log.error(error, io);
      throw new RuntimeException(error);
    }
  }


  @Udf(description = "Detect the sentiment of a string of text")
  public Map<String, Double> getSentiment(
    @UdfParameter(value = "text", description = "the text to analyze")
    final String text) {
    
    if (!configured) {
      final String error =
        String.join(
            " ",
            "The sentiment UDF has not been configured!",
            "This should have happened automatically when KSQL instantiated the UDF.",
            "This indicates a breaking change in the KSQL library");
      log.error(error);
      return errorResult();
    }
    final Map<String, Double> result = new HashMap<>();
    try {
      final Document doc = Document.newBuilder()
      .setContent(text)
      .setType(Type.PLAIN_TEXT)
      .build();

      // Detects the sentiment of the text
      final AnalyzeSentimentResponse response = language.analyzeSentiment(doc);
      final Sentiment sentiment = response.getDocumentSentiment();
      // Build the result object
      result.put(SCORE_KEY, Double.valueOf(sentiment.getScore()));
      result.put(MAGNITUDE_KEY, Double.valueOf(sentiment.getMagnitude()));
      result.put(SUCCESS_KEY, 1.0);
    } catch (Exception e) {
      log.error("Could not detect sentiment for provided text: {}", text, e);
      return errorResult();
    }
    return result;
  }

  private Map<String, Double> errorResult() {
    final Map<String, Double> result = new HashMap<>();
    result.put(SCORE_KEY, 0.0);
    result.put(MAGNITUDE_KEY, 0.0);
    result.put(SUCCESS_KEY, 0.0);
    return result;
  }
}
