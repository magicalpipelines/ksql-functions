# Contributing

Thanks for helping us build KSQL functions! Simply fork this repo, make your changes, and submit a PR. Someone from [magicalpipelines][mp] will review your PR, merge your changes, and deploy the new or updated artifacts to Maven Central.

[mp]: https://github.com/magicalpipelines

## Maintainer note

Assuming you've created your GPG key and can sign the artifacts, simply run the following to publish new versions of a UDF to Maven Central.

```bash
$ export GPG_TTY=$(tty)
$ mvn clean deploy
```
