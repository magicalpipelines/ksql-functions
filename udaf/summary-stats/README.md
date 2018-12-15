# Summary Stats
The `SUMMARY_STATS` UDAF computes summary statistics (mean, standard deviation, etc) for a stream of data. In this example,
we assume you have a topic called `api_logs` already created.

# Usage
First, register the stream like so:

```sql
CREATE STREAM api_requests (endpoint VARCHAR, response_code INT, response_time DOUBLE) \
WITH (kafka_topic='api_logs', value_format='JSON');
```

Then, create a query that gets the summary stats for each unique endpoint in our stream.

```sql
SELECT endpoint, SUMMARY_STATS(response_time) \
FROM api_requests \
GROUP BY endpoint ;
```

Now, we can use kafkacat to produce some messages into the `api_logs` topic. First, save some dummy messages in a file 
called `records.txt`:

```
# records.txt
{"endpoint": "/home", "response_code": 200, "response_time": 400}
{"endpoint": "/home", "response_code": 200, "response_time": 200}
{"endpoint": "/home", "response_code": 200, "response_time": 100}
{"endpoint": "/search", "response_code": 200, "response_time": 900}
{"endpoint": "/search", "response_code": 200, "response_time": 700}
{"endpoint": "/search", "response_code": 500, "response_time": 2200}
```

To produce the above records to the `api_logs` topic, run the following:

```
cat records.txt  | kafkacat -q -b localhost:9092 -t api_logs -P -D "\n"
```

You should see the following query results:

```
/home | {sample_size=3.0, stddev_sample=152.75252316519467, mean=233.33333333333334, sum=700.0, sum_squares=210000.0, stddev_population=124.72191289246472}
/search | {sample_size=3.0, stddev_sample=814.4527815247076, mean=1266.6666666666667, sum=3800.0, sum_squares=6140000.0, stddev_population=664.9979114419999}
```
