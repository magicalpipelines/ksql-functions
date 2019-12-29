# Contributing

Thanks for helping us build KSQL functions! Simply fork this repo, make your changes, and submit a PR. Someone from [magicalpipelines][mp] will review your PR, merge your changes, and deploy the new or updated artifacts to Maven Central.

[mp]: https://github.com/magicalpipelines

## Maintainer note

Assuming you've created your GPG key and can sign the artifacts, and have also added the `ossrh` credentials to your `~/.m2/settings.xml` file (these are the same credentials you would use to access the [staging repositories][staging-repo]), e.g.
```
    <server>
      <id>ossrh</id>
      <username>...</username>
      <password>...</password>
    </server>  
```
simply run the following to publish new versions of a UDF to Maven Central.

```bash
$ export GPG_TTY=$(tty)
$ mvn clean deploy
```

[staging-repo]: https://oss.sonatype.org/#stagingRepositories
