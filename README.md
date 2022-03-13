# Glowplug

Starting the docker containers will make the following endpoints accessible:

| URL                                  | Service                    |
|--------------------------------------|----------------------------|
| http://minio.docker.localhost/       | MinIO Administration UI    |
| http://zeppelin.docker.localhost/    | Zeppelin Notebook UI       |
| http://spark.docker.localhost/       | Spark Master UI            |
| http://webui.spark.docker.localhost/ | Spark Job Server UI        |
| http://webui.spark.docker.localhost/ | Spark History Server UI    |

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
ClassLoader.getSystemClassLoader.asInstanceOf[java.net.URLClassLoader].getURLs.foreach {
  println
}
```





## License

Apache-2.0 License

Copyright (c) 2021 Mridang Agarwalla

[see the GitHub docs]: https://docs.github.com/en/packages/guides/configuring-gradle-for-use-with-github-packages#installing-a-package



aws emr create-cluster --auto-scaling-role production-emr-auto-scaling --applications Name=Hadoop Name=Hive Name=Pig Name=Hue Name=Spark --bootstrap-actions '[{"Path":"s3://nosto-emr-bootstrap/sshkeys.sh","Name":"Add ssh keys"}]' --ebs-root-volume-size 20 --ec2-attributes '{"KeyName":"mridang","InstanceProfile":"production-emr","ServiceAccessSecurityGroup":"sg-025cd99e844eb8ee9","SubnetId":"subnet-de214af5","EmrManagedSlaveSecurityGroup":"sg-0548dc804984f9cf8","EmrManagedMasterSecurityGroup":"sg-0f5fc3a1f9be29054"}' --service-role production-emr-service --enable-debugging --release-label emr-6.2.0 --log-uri 's3://nosto-emr-logs/' --name 'MridangTest' --instance-groups '[{"InstanceCount":1,"EbsConfiguration":{"EbsBlockDeviceConfigs":[{"VolumeSpecification":{"SizeInGB":32,"VolumeType":"gp2"},"VolumesPerInstance":2}]},"InstanceGroupType":"CORE","InstanceType":"c5a.xlarge","Name":"Core - 2"},{"InstanceCount":1,"EbsConfiguration":{"EbsBlockDeviceConfigs":[{"VolumeSpecification":{"SizeInGB":32,"VolumeType":"gp2"},"VolumesPerInstance":2}]},"InstanceGroupType":"MASTER","InstanceType":"c5a.xlarge","Name":"Master - 1"}]' --scale-down-behavior TERMINATE_AT_TASK_COMPLETION --region us-east-1

aws emr ssh --cluster-id j-18YMPN7OKJPPC --key-pair-file=~/.ssh/emr
scp example/build/libs/example-all.jar hadoop@172.21.27.142:/home/hadoop

aws emr add-steps --cluster-id j-18YMPN7OKJPPC --steps 'Name="add emr step to run spark",Jar="command-runner.jar",Args=[spark-submit,--class,com.mridang.spark.Main,/home/hadoop/example-all.jar,10]'

aws emr describe-cluster --cluster-id j-18YMPN7OKJPPC | jq --raw-output '.Cluster .MasterPublicDnsName | sub("ip-(?<ip1>[0-9]*)-(?<ip2>[0-9]*)-(?<ip3>[0-9]*)-(?<ip4>[0-9]*).*$"; "\(.ip1).\(.ip2).\(.ip3).\(.ip4)")' | tr -d '\n\t' 
