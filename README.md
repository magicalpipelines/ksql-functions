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
 
 * [`SUMMARY_STATS`][summary-stats]: calculate summary statistics (mean, standard deviation, sample size, etc) for streams of data.
 
 [summary-stats]: https://github.com/magicalpipelines/ksql-functions/tree/master/udaf/summary-stats
