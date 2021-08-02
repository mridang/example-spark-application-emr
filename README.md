# Glowplug

```
./gradlew emr:dependencies
```

```
./gradlew emr:dependencyInsight --configuration runtimeClasspath --dependency findbugs-annotations
```

```
./gradlew jarHell --info
```

```
    modules {
        module('com.sun.jersey:jersey-server') {
            //noinspection GradlePackageUpdate
            replacedBy('org.glassfish.jersey.core:jersey-server')
        }
    }
```

```groovy
components.all(JacksonAlignmentRule)
```

## License

Apache-2.0 License

Copyright (c) 2021 Mridang Agarwalla

[see the GitHub docs]: https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages#installing-a-package
