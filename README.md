Orienteer
=========

Extreemly powerfull Data Warehouse system based on OrientDB

Installation
------------
Orienteer is in early better: that's why installation is a little bit complicated.

### Prerequisetes
1. java 6+
2. git
3. maven
4. OrientDB, if you want to use OrientDB remotely

### Install [wicket-orientdb](https://github.com/PhantomYdn/wicket-orientdb) library in your maven repository
```
git clone https://github.com/PhantomYdn/wicket-orientdb.git
cd wicket-orientdb
mvn clean install
```
### Install Orienteer
```
cd ..
git clone https://github.com/PhantomYdn/Orienteer.git
cd Orienteer
mvn clean install
```
### Edit orienteer.properties file to reflect connection properties and some additional parameters
### Run jetty server by command
```
mvn jetty:run
```
### Goto the application
Open http://localhost:8080 is in your browser

