[![Build Status](https://travis-ci.org/OrienteerBAP/Orienteer.svg?branch=master)](https://travis-ci.org/OrienteerBAP/Orienteer) [![Coverage Status](https://coveralls.io/repos/github/OrienteerBAP/Orienteer/badge.svg?branch=master)](https://coveralls.io/github/OrienteerBAP/Orienteer?branch=master) [![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/OrienteerBAP/Orienteer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge) [![Dependency Status](https://www.versioneye.com/user/projects/572bd228a0ca350034be6f9d/badge.svg?style=flat)](https://www.versioneye.com/user/projects/572bd228a0ca350034be6f9d)

## Orienteer

### What is Orienteer 

**Orienteer** is Business Application Platform: 

* Easy creation of business applications
* Extendable to fit your needs
* Dynamic datamodel
* Rest/JSON enabled
* Developers friendly
* Scalling and cloud ready (support of Docker)

[Orienteer Guidebook](https://orienteer.gitbooks.io/orienteer/content/)

### Demo

- [Master Demo Server](http://demo.orienteer.org)

### Orienteer installation

There are two options for Orienteer installation:

- Embedded (on application server)
- Standalone (no need in application server)

#### Embedded

Orienteer is Java Servlet 3.1 web application and can be installed on all famous compatible containers:

- Jboss
- Weblogic
- IBM WebSphere
- Tomcat
- Jetty
- and etc.

To install Orienteer in embedded mode:
- Download latest orienteer.war
- Put orienteer.war into deployment folder for your application server
- Configure orienteer.properties according to your environment and place it in the same directory or above
- Run application server

#### Standalone

Orienteer in standalone mode use embedded jetty server to run yourself. To install Orienteer in standalone mode:
- Download latest orienteer-standalone.jar
- Put orienteer-standalone.jar into any directory
- Optionally configure orienteer.properties accordging to your environment and place it in the same directory or above
  - By default, Orienteer, will run OrientDB database embedded
- Run Orinteer as ```java -Xmx512m -Xms512m -jar orienteer-standalone.jar```. JVM parameters can be adjusted accordingly. Additional application parameters can be supplied:
  - ``` --config=<filename>``` - specification of path to orienteer configuration file
  - ``` --embedded``` - run embedded OrientDB database
  - ``` --port=<port number>``` - run Orienteer on specified port (Default: 8080)
  - ``` --help``` - display help

#### Orienteer initial configuration

**orienteer.properties** is the main file to store initial configuration paramenters for your installation. Sample properties file can be always found [here](https://github.com/OrienteerDW/Orienteer/blob/master/orienteer.properties.sample).

```properties
orienteer.production=false  //Run Orienteer in production mode or not
orientdb.embedded=false     //Run embedded OrientDB server?
orientdb.url=remote:localhost/Orienteer   //OrientDB server URL
orientdb.guest.username=reader               //Default OrientDB user (will be used for guests as well)
orientdb.guest.password=reader               //Password for default OrientDB user
orientdb.admin.username=admin    //OrientDB user to user for administrative stuff
orientdb.admin.password=admin    //Password for OrientDB user used for administrative stuff

# Optional properties

#orientdb.rest.url=http://localhost:2480
#plantuml.url=http://custom-plantuml-url
#plantuml.showuml=false;

#webjars.readFromCacheTimeout=5 seconds
#webjars.useCdnResources=true
#webjars.cdnUrl=//maxcdn.bootstrapcdn.com:80
```

### Setup of development environment 

#### Prerequisites
1. java 7+
2. git
3. maven
4. OrientDB, if you want to use OrientDB remotely

#### Steps

##### Install of [wicket-orientdb](https://github.com/OrienteerDW/wicket-orientdb) github SNAPSHOT

This step is optional: [wicket-orientdb](https://github.com/OrienteerDW/wicket-orientdb) SNAPSHOT always available on Maven central

```
git clone <your fork URL for wicket-orientdb>
cd wicket-orientdb
mvn clean install
```

##### Install Orienteer
```
cd ..
git clone <your fork URL for Orienteer>
cd Orienteer
mvn clean install
```
##### Modify orienteer.properties file
See configuration section above

##### Code compilation
```
mvn clean install
```
##### Run jetty server by command
```
mvn jetty:run
```
##### Goto the application
Open http://localhost:8080 is in your browser

