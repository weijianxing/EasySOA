# EasySOA Registry API

## Description

This bundle contains common resources used by the service registry, but is also intended to be used as a Java library for remote clients. It contains:

* The definition of all custom document types ;
* A basic way to access the EasySOA Discovery API, in order to register applications (_Appli Impl._), api (_ServiceAPI_) & services (_Service_ doctype).

Note that the REST client is very basic, and will eventually be changed for something more solid (like a library based on a full-featured generic REST client, able to parse WADLs).

## Building & Using the API as a self-sufficient JAR

To build the API as a reusable JAR, type:

`mvn clean package`

Then pick **easysoa-registry-api-SOMEVERSION-jar-with-dependencies.jar** and include it in your project.