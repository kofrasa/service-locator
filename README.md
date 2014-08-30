service-locator
===============

A simple lightweight service locator for registering and managing application-wide services.
This provides an explicit alternative inversion of control pattern with a dependence on this library


##Features
- Provides a factory interface to facilitate creating services dependent on other contexts
- Support for loading services from META-INF/services as will the system ServiceLoader
- Includes a standard set of interfaces for queueing systems which implement the AMQP protocol (more later)
- Simple service-locator configuration using java.util.Properties 


##Usage
Download the latest stable build from the ```./build``` directory.
Alternatively, you may clone the project, build, and install directly to your local maven repo.

```
git clone git://github.com/kofrasa/service-locator
cd service-locator/
mvn install
```


##License
MIT
