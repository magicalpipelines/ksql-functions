[![Maven Central](https://img.shields.io/maven-central/v/com.mitchseymour/ksql-udf-sentiment-analysis.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.mitchseymour%22%20AND%20a:%22ksql-udf-sentiment-analysis%22)

# Sentiment Analysis
The `SENTIMENT` UDF uses the Google Cloud Natural Language API to detect sentiment in a string of text.

# Prerequisites
- Create or select a GCP project from [Google Cloud Console][google_cloud_console]
- Enable Cloud Natural Language API
- Create a service account
- Download the private key (JSON)
- Set the following environment variable wherever your KSQL server instances are running:

  ```bash
  $ export GOOGLE_APPLICATION_CREDENTIALS=/path/to/key.json
  ```
- If you have [gcloud][gcloud] installed, you can easily test that the above credentials work by running:
   ```bash
    $ gcloud ml language analyze-sentiment --content="I love pizza"
   ```

Once you've completed this list of prequisites, you can install the `SENTIMENT` analysis UDF (see below).

[gcloud]: https://cloud.google.com/sdk/gcloud/
[google_cloud_console]: https://console.cloud.google.com

# Installation
Download the [JAR][jar] from Maven Central and copy it to the KSQL extension directory (see [here][main-readme] for more details).

[jar]: https://search.maven.org/artifact/com.mitchseymour/ksql-udf-sentiment-analysis
[main-readme]: https://github.com/magicalpipelines/ksql-functions#installation

# Authentication
Set the following environment variable wherever your KSQL server instances are running.
```bash
$ export GOOGLE_APPLICATION_CREDENTIALS=/path/to/privatekey.json
```

# Example usage
```sql
SELECT SENTIMENT(my_column) FROM SOME_STREAM ;

# sample output
{score=0.8999999761581421, magnitude=0.8999999761581421}
```