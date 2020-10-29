# Personium Engine

Additional module for personium-core to enable server-side JavaScript execution.

  - Copyright 2014-2019 Personium Project
    - FUJITSU LIMITED
    - (Contributors, add your name)


## Documentation

    http://personium.io/docs/

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Testing

1. Place `personium-ex-mailsender` jar file and `Ext_MailSender.properties` file in `/personium/personium-engine/`
1. Launch prequire software with `docker-compose up`
1. Launch tomcat9 locally
1. Deploy `personium-core` tomcat locally with option `io.personium.core.pathBasedCellUrl.enabled=true`
1. Execute tests with maven.

## Build and setup

1. Use maven to build personium-engine.war file to run on a servlet container.
1. DO NOT make the Engine endpoints public. Personim Engine is designed to run behind the Personium Core.
1. Configure the personium-core to point to the Personium engine root.
1. Use reverse proxy like defined in https://github.com/personium/ansible/ for example.
1. The HTTP relay flow will be as follows:
  (Reverse Proxy) - (personium-core) - (personium-engine)
