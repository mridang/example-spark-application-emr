FROM bde2020/spark-worker:3.0.1-hadoop3.2

RUN mv /spark/conf/spark-defaults.conf.template /spark/conf/spark-defaults.conf

COPY spark-defaults.conf /spark/conf/spark-defaults.conf
COPY /jars/. /spark/jars
RUN rm /spark/jars/guava-14.0.1.jar