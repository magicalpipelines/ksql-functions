package com.mitchseymour.ksql.functions;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.AnalyzeEntitySentimentRequest;
import com.google.cloud.language.v1.AnalyzeEntitySentimentResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.LanguageServiceSettings;
import com.google.cloud.language.v1.Sentiment;
import com.google.common.collect.Lists;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.Configurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UdfDescription(
    name = "entity_sentiment",
    description = "Entity-level sentiment analysis of text",
    version = "0.1.0",
    author = "Mitch Seymour"
)
public class EntitySentimentUdf implements Configurable {
  private static final String CONFIG_PREFIX = "ksql.functions.sentiment.";
  private static final Logger log = LoggerFactory.getLogger(EntitySentimentUdf.class);

  private static final String MENTIONS_KEY = "mentions";
  private static final String SALIENCE_KEY = "salience";
  private static final String SCORE_KEY = "score";
  private static final String MAGNITUDE_KEY = "magnitude";
  private static final String SUCCESS_KEY = "success";

  private LanguageServiceClient language;
  private boolean configured = false;

  public static class EntitySentimentResult {
    private final String entity;
    private final Double salience;
    private final Double score;
    private final Double magnitude;
    private final Double mentions;
    private final Boolean success;

    EntitySentimentResult(
        String entity, Double salience, Double score, Double magnitude, Double mentions, Boolean success) {
      this.entity = entity;
      this.salience = salience;
      this.score = score;
      this.magnitude = magnitude;
      this.mentions = mentions;
      this.success = success;
    }

    public String getEntity() {
      return entity;
    }

    public Double getSalience() {
      return salience;
    }

    public Double getScore() {
      return score;
    }

    public Double getMagnitude() {
      return magnitude;
    }

    public Double getMentions() {
      return mentions;
    }

    public Boolean getSuccess() {
      return success;
    }
  }

  @Override
  public void configure(final Map<String, ?> map) {
    log.info("Configuring entity_sentiment UDF");
    LanguageServiceSettings settings;

    try {
      // check to see if we should pull GCP service account credentials from a file
      if (!map.containsKey(CONFIG_PREFIX + "credentials.file")) {
        // no credentials file. this is okay if the creds were set via:
        // export GOOGLE_APPLICATION_CREDENTIALS=/path/to/creds.json
        settings = LanguageServiceSettings.newBuilder().build();
      } else {
        final String path = (String) map.get(CONFIG_PREFIX + "credentials.file");
        final GoogleCredentials credentials =
            GoogleCredentials.fromStream(new FileInputStream(path))
                .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));

        settings =
            LanguageServiceSettings.newBuilder()
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

  @Udf(description = "Detect the entity-level sentiment in a string of text")
  public Map<String, Map<String, Double>> getEntitySentiment(
      @UdfParameter(value = "text", description = "the text to analyze") final String text) {

    final Map<String, Map<String, Double>> result = new HashMap<>();
    if (!configured) {
      final String error =
          String.join(
              " ",
              "The entity_sentiment UDF has not been configured!",
              "This should have happened automatically when KSQL instantiated the UDF.",
              "This indicates a breaking change in the KSQL library");
      log.error(error);
      return result;
    }

    try {
      final Document doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build();
      final AnalyzeEntitySentimentRequest request =
          AnalyzeEntitySentimentRequest.newBuilder()
              .setDocument(doc)
              .setEncodingType(EncodingType.UTF8)
              .build();
      // Detects the sentiment of the text
      final AnalyzeEntitySentimentResponse response = language.analyzeEntitySentiment(request);

      for (Entity entity : response.getEntitiesList()) {
        final Map<String, Double> scores = new HashMap<>();
        final Sentiment sentiment = entity.getSentiment();
        scores.put(SALIENCE_KEY, Double.valueOf(entity.getSalience()));
        scores.put(SCORE_KEY, Double.valueOf(sentiment.getScore()));
        scores.put(MAGNITUDE_KEY, Double.valueOf(sentiment.getScore()));
        scores.put(MENTIONS_KEY, Double.valueOf(entity.getMentionsList().size()));
        scores.put(SUCCESS_KEY, 1.0);
        result.put(entity.getName(), scores);
      }
    } catch (Exception e) {
      log.error("Could not detect sentiment for provided text: {}", text, e);
    }
    return result;
  }

  public List<EntitySentimentResult> getEntitySentimentResults(final String text) {
    List<EntitySentimentResult> results = new ArrayList<>();
    Map<String, Map<String, Double>> entityScores = getEntitySentiment(text);
    for (Map.Entry<String, Map<String, Double>> entry : entityScores.entrySet()) {
      String entity = entry.getKey().replace("#", "").toLowerCase();
      Map<String, Double> sentiment = entry.getValue();

      results.add(
          new EntitySentimentResult(
              entity,
              sentiment.get(SALIENCE_KEY),
              sentiment.get(SCORE_KEY),
              sentiment.get(MAGNITUDE_KEY),
              sentiment.get(MENTIONS_KEY),
              sentiment.get(SUCCESS_KEY).equals(1.0)));
    }
    return results;
  }
}
