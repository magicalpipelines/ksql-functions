[![Maven Central](https://img.shields.io/maven-central/v/com.mitchseymour/ksql-udf-entity-sentiment-analysis.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.mitchseymour%22%20AND%20a:%22ksql-udf-sentiment-analysis%22)

# Entity Sentiment Analysis
The `ENTITY_SENTIMENT` UDF uses the Google Cloud Natural Language API to detect entity-level sentiment in a string of text.

# Prerequisites
- Create or select a GCP project from [Google Cloud Console][google_cloud_console]
- Enable Cloud Natural Language API
- Create a service account
- Download the private key (JSON). you will need this for the configuration step (see below)

Once you've completed this list of prequisites, you can install the `ENTITY_SENTIMENT` analysis UDF (see below).

[gcloud]: https://cloud.google.com/sdk/gcloud/
[google_cloud_console]: https://console.cloud.google.com

# Configuration
The following configs should be added to your KSQL server.properties file.

- `ksql.functions.entity_sentiment.credentials.file`: a path to the file containing your GCP service account credentials

# Installation
Download the [JAR][jar] from Maven Central and copy it to the KSQL extension directory (see [here][main-readme] for more details).

[jar]: https://search.maven.org/artifact/com.mitchseymour/ksql-udf-entity-sentiment-analysis
[main-readme]: https://github.com/magicalpipelines/ksql-functions#installation

# Authentication
Set the following environment variable wherever your KSQL server instances are running.
```bash
$ export GOOGLE_APPLICATION_CREDENTIALS=/path/to/privatekey.json
```

# Example usage
```sql
select text, entity_sentiment(text) from tweets ;

# sample output
bitcoin is really cool | {bitcoin={salience=1.0, score=0.8999999761581421, success=1.0, mentions=1.0, magnitude=0.8999999761581421}}
```
