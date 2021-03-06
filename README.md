# Custom KSQL UDFs and UDAFs
This repository contains custom KSQL UDFs and UDAFs built by the [Magical Pipelines][magical-pipelines] team. An overview of each function, including usage details, can be found in the function's subdirectory.

[magical-pipelines]: https://github.com/orgs/magicalpipelines/people

# Installation
All of the JARs are deployed to Maven central, and can be viewed [here][maven-central].
To install any of these KSQL UDFs / UDAFs, simply download the appropriate JAR file from Maven central and place it in the
KSQL extension directory; If you're not sure what the KSQL extension directory is set to for your KSQL deployment, run
the following query from the KSQL CLI.

[maven-central]: https://search.maven.org/search?q=g:com.mitchseymour%20AND%20a:ksql*%20AND%20NOT%20ksql-udf-quickstart

```sql
ksql> SHOW PROPERTIES ;

 Property                                               | Default override | Effective Value
-------------------------------------------------------------------------------------------------------------------------------------------
 ksql.extension.dir                                     | SERVER           | /tmp/ext
 
 ...
 ...
 ```
 
# Custom functions
 Below are the custom functions that are currently available.
 
 * [`COLOR_TO_HEX`][color-to-hex]: experimental polyglot (ruby) UDF that converts colors to hex strings
 * [`DIALOGFLOW`][dialogflow]: build conversational interfaces (virtual assistants, intelligent chatbots, etc)
 * [`PREDICT_SPAM`][predict-spam]: experimental UDF that predicts whether or not a string of text is spam
 * [`SENTIMENT`][sentiment-analysis]: perform sentiment analysis on streams of text
 * [`SUMMARY_STATS`][summary-stats]: calculate summary statistics (mean, standard deviation, sample size, etc) for streams of data.
 
 [color-to-hex]: https://github.com/magicalpipelines/ksql-functions/tree/master/udf/color-to-hex
 [dialogflow]: https://github.com/magicalpipelines/ksql-functions/tree/master/udf/dialogflow
 [predict-spam]: https://github.com/magicalpipelines/ksql-functions/tree/master/udf/h2o-spam-prediction
 [summary-stats]: https://github.com/magicalpipelines/ksql-functions/tree/master/udaf/summary-stats
 [sentiment-analysis]: https://github.com/magicalpipelines/ksql-functions/tree/master/udf/sentiment-analysis
