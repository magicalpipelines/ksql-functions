# Predict Spam
The `PREDICT_SPAM` UDF uses an embedded [h2o][h2o] model to make predictions about whether or not a string of text (e.g. the body of an email) contains spam. It was trained on a subset of the Enron email data published [here][enron_email_data].

This UDF is experimental and was built for demonstration purposes.

[h2o]: https://www.h2o.ai/
[enron_email_data]: http://www2.aueb.gr/users/ion/data/enron-spam/

# Installation
TODO. I will publish this soon to Maven Central.


# Example usage
```sql
SELECT PREDICT_SPAM(body) FROM emails ;

# sample input:
PREDICT_SPAM("hey, how is it going? its been awhile... hope you are doing okay")
PREDICT_SPAM("click here to improve your wellbeing today your one stop prescription shop!")

# sample output:
ham
spam
```
