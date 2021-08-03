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

```
docker compose up --detach --build spark-master 
```


#### Using the Spark shell

In order to access the Spark shell, you'll need to run:

```
docker exec -it spark-master /spark/bin/spark-shell
```

⚠ Remember to start the container (and rebuild it too):

```
docker compose up --detach --build
docker compose stop
```

### Verifying

##### Sanity check access to S3

```scala
sc.textFile("s3a://glowplug/file.json").count()
```

touch file.json
aws s3 mb --endpoint-url http://s3.dev.nos.to:9991 s3://glowplug
aws s3 cp --endpoint-url http://localhost:9991 file.json s3://glowplug



##### Sanity check all classpath JARs

```scala
ClassLoader.getSystemClassLoader().asInstanceOf[java.net.URLClassLoader].getURLs().foreach { println }
```





## License

Apache-2.0 License

Copyright (c) 2021 Mridang Agarwalla

[see the GitHub docs]: https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages#installing-a-package
