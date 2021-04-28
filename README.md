# Personium Engine

Additional module for personium-core to enable server-side JavaScript execution.

## Documentation

    http://personium.io/docs/

## Requirements

The component `personium-engine` works behind of `personium-core` component. So, you have to launch `personium-core` and need backends, then you have to configure `personium-core` to use `personium-engine` you launched.

Please refer to [personium/ansible](https://github.com/personium/ansible) to prepare them manually.

## Launch

There are two options to launch `personium-engine`. DO NOT make the engine endpoints public. `personium-engine` is designed to run behind the `personium-core`.

### Using war file

You can use maven to build personium-engine.war file to run on a servlet container.

```bash
git clone https://github.com/personium/personium-engine
cd personium-engine
mvn package
```

After compiling, you can get war file on target folder. ( `target/personium-engine.war` )

### Using docker image

You can build docker container image including `personium-engine` based on Tomcat image with below command.

```bash
docker build . -t personium-engine
```

After building, you can launch personium-engine in docker container.

```bash
docker run -d -p 8080:8080 personium-engine
```

You can mount volume to use specified configuration.

```bash
docker run -d -p 8080:8080 -v /path/to/config.properties:/personium/personium-engine/conf/personium-unit-config.properties personium-engine
```

## Testing

1. Place `personium-ex-mailsender` jar file and `Ext_MailSender.properties` file in `/personium/personium-engine/`
1. Launch required software with `docker-compose up`
1. Launch `personium-core` on Tomcat9 locally.  
For example, with below comand. ( Set `$TOMCAT_DIR` environment value to path of directory which Tomcat9 installed, and place `personium-core.war` on `$TOMCAT_DIR/webapps/personium-core.war`. )

    ```
    JAVA_OPTS="-Djava.util.logging.config.file=$TOMCAT_DIR/conf/logging.properties -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djdk.tls.ephemeralDHKeySize=2048 -Djava.protocol.handler.pkgs=org.apache.catalina.webresources -Dorg.apache.catalina.security.SecurityListener.UMASK=0027 -Dcatalina.base=$TOMCAT_DIR -Dcatalina.home=$TOMCAT_DIR -Djava.io.tmpdir=$TOMCAT_DIR/temp -Dio.personium.configurationFile=$PWD/src/test/resources/personium-core-unit-config.properties" catalina.sh run
    ```

1. Execute tests with maven.

    ```
    mvn -f pom.xml test
    ```

## License

Personium is licensed under the Apache License, Version 2.0. See [LICENSE.txt](./LICENSE.txt)
