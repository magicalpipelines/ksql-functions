[![Maven Central](https://img.shields.io/maven-central/v/com.mitchseymour/ksql-udf-dialogflow.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.mitchseymour%22%20AND%20a:%22ksql-udf-sentiment-analysis%22)

# Dialogflow
The `DIALOGFLOW` UDF uses [Google Dialogflow][dialogflow] for building virtual assistants, intelligent chatbots, and other types of conversational interfaces.

[dialogflow]: https://dialogflow.com/

# Prerequisites
- Follow the official [getting started steps][dialogflow_getting_started] to signup for a Dialogflow account and create a [Dialogflow agent][agent]
- Download the private key (JSON) for your Dialogflow service account. You will need this when configuring the UDF

[agent]: https://dialogflow.com/docs/agents/
[dialogflow_getting_started]: https://dialogflow.com/docs

# Configuration
The following configs should be added to your KSQL server.properties file.

- `ksql.functions.dialogflow.credentials.file`: a path to the file containing your GCP service account credentials
- `ksql.functions.dialogflow.project.id`: the GCP project ID
- `ksql.functions.dialogflow.language.code`: the language that should be used for conversations. see [supported languages][dialogflow_language_codes]


[dialogflow_language_codes]: https://dialogflow.com/docs/languages

# Installation
Download the [JAR][jar] from Maven Central and copy it to the KSQL extension directory (see [here][main-readme] for more details).

[jar]: https://search.maven.org/artifact/com.mitchseymour/ksql-udf-dialogflow
[main-readme]: https://github.com/magicalpipelines/ksql-functions#installation


# Example usage
```sql
SELECT DIALOGFLOW(text, sessionId) FROM SOME_STREAM ;

# sample input:
DIALOGFLOW('I would like to book a room', 'user2')

sample output:
'I can help with that. Where would you like to reserve a room?'
```

The above example uses a prebuilt room booking agent. You can checkout other prebuild agents [here][prebuilt_agents], or [train your own agent][train].

[prebuilt_agents]: https://dialogflow.com/docs/samples
[train]: https://dialogflow.com/docs/training-analytics/training
