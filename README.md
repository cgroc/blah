# Blah

Simple example of setting up kafka producer and consumer using the fs2 kafka libraries.

+ https://github.com/fd4s/fs2-kafka for the library itself
+ https://github.com/ovotech/kafka-serialization for serializer/deserializer support

Note that this requires a full kafka stack with schema registry to work. Using `docker-compose`
and the docker-compose files at the following repo worked well for me.

+ https://github.com/simplesteph/kafka-stack-docker-compose

