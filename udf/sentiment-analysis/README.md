[![Maven Central](https://img.shields.io/maven-central/v/com.mitchseymour/ksql-udf-sentiment-analysis.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.mitchseymour%22%20AND%20a:%22ksql-udaf-summary-stats%22)

# Sentiment Analysis
The `SENTIMENT` UDF uses the Google Cloud Natural Language API to detect sentiment in a string of text.

# Installation
Download the [JAR][jar] from Maven Central and copy it to the KSQL extension directory (see [here][main-readme] for more details).

[jar]: https://search.maven.org/artifact/com.mitchseymour/ksql-udf-sentiment-analysis
[main-readme]: https://github.com/magicalpipelines/ksql-functions#installation

# Example usage
```sql
SELECT SENTIMENT(my_column) FROM SOME_STREAM ;

# sample output
{score=0.8999999761581421, magnitude=0.8999999761581421}
```
