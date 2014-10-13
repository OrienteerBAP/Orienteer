[![Build Status](https://travis-ci.org/PhantomYdn/Orienteer.svg?branch=master)](https://travis-ci.org/PhantomYdn/Orienteer) [![Coverage Status](https://img.shields.io/coveralls/PhantomYdn/Orienteer.svg)](https://coveralls.io/r/PhantomYdn/Orienteer)  [![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/PhantomYdn/Orienteer?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

## Orienteer
![Orinenteer](http://orienteer.org/images/orienteer.png)

### What is Orienteer 

**Orienteer** is Data Warehouse System on top of OrientDB. It allows you:

* Manage you schema
* Create/Read/Update/Delete documents
* Create and view customizable reports
* Extend functionality by flexible visualizations

### Demo

[Demo server](http://demo.orienteer.org)

Default users:
- admin/admin
- reader/reader
- writer/writer

> Demo might be unavailable. Please consider to use localbuild and run.

### Orienteer installation

There are two options for Orienteer installation:

- Embedded (on application server)
- Standalone (no need in application server)

#### Embedded

Orienteer is J2EE compatible Web Application and can be installed on all famous J2EE containers:

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
- Download latest orienteer-standalone.war
- Put orienteer-standalone.war into any directory
- Optionally configure orienteer.properties accordging to your environment and place it in the same directory or above
  - By default, Orienteer, will run OrientDB database embedded
- Run Orinteer as ```bash java -Xm512m -Xms512m -jar orienteer-standalone.war```. JVM parameters can be adjusted accordingly. Additional application parameters can be supplied:
  - ``` --config=<filename>``` - specification of path to orienteer configuration file
  - ``` --embedded``` - run embedded OrientDB database
  - ``` --port=<port number>``` - run Orienteer on specified port (Default: 8080)
  - ``` --help``` - display help

#### Orienteer initial configuration

**orienteer.properties** is the main file to store initial configuration paramenters for your installation. Sample properties file can be always found [here](https://github.com/PhantomYdn/Orienteer/blob/master/orienteer.properties.sample).

```properties
orienteer.production=false  //Run Orienteer in production mode or not
orientdb.embedded=false     //Run embedded OrientDB server?
orientdb.url=remote:localhost/Orienteer   //OrientDB server URL
orientdb.db.username=reader               //Default OrientDB user (will be used for guests as well)
orientdb.db.password=reader               //Password for default OrientDB user
orientdb.db.installator.username=admin    //OrientDB user to user for administrative stuff
orientdb.db.installator.password=admin    //Password for OrientDB user used for administrative stuff 
```

### Setup of development environment 

#### Prerequisites
1. java 6+
2. git
3. maven
4. OrientDB, if you want to use OrientDB remotely

#### Steps

##### Install of [wicket-orientdb](https://github.com/PhantomYdn/wicket-orientdb) github SNAPSHOT

This step is optional: [wicket-orientdb](https://github.com/PhantomYdn/wicket-orientdb) SNAPSHOT always available on Maven central

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

