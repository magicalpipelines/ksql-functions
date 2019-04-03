package com.mitchseymour.ksql.functions;

import java.util.Map;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;

import io.confluent.common.Configurable;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

@UdfDescription(
    name = "dialogflow",
    description = "Conversational UDF that interfaces with Dialogflow",
    version = "0.1.0",
    author = "Mitch Seymour"
)
public class DialogflowUdf implements Configurable {
  private final String CONFIG_PREFIX = "ksql.functions.dialogflow.";

  private String projectId = "";
  private String languageCode = "en-US";

@Override
public void configure(final Map<String, ?> map) {
  // GCP project ID
  if (map.containsKey(CONFIG_PREFIX + "project.id")) {
    this.projectId = (String) map.get(CONFIG_PREFIX + "project.id");
  }

  // language code. e.g. en-US
  if (map.containsKey(CONFIG_PREFIX + "language.code")) {
    this.languageCode = (String) map.get(CONFIG_PREFIX + "language.code");
  }
}

@Udf(description = "Detect the sentiment of a string of text")
public String reply(
    @UdfParameter(value = "text", description = "the text to process")
    final String text,
    @UdfParameter(value = "sessionId", description = "a unique ID for this conversation")
    final String sessionId
    ) {
  
  return getFulfillmentText(text, sessionId);
}

public String getFulfillmentText(final String text, final String sessionId) {
    // Instantiate a client
    try (SessionsClient sessionsClient = SessionsClient.create()) {
      // Set the session name using the sessionId (UUID) and projectID (my-project-id)
      SessionName session = SessionName.of(projectId, sessionId);

      // Set the text and language code for the query
      Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

      // Build the query with the TextInput
      QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

      // Performs the detect intent request
      DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

      // Return the query result
      return response.getQueryResult().getFulfillmentText();
    } catch (Exception e) {
      e.printStackTrace();
      return "Sorry, something went wrong";
    }
  }
}
