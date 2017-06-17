# Introduction

This uses [Dropwizard](http://dropwizard.io) at the core.

# Overview

This has a models, serial and ws breakout. The models are shared between the serial and ws with more time can breakout into there own mvn projects. 
The serial


# Running The Application

To test the example application run the following commands.

* To package the example run the following from the root dropwizard directory.

        mvn package

* To run send data.(For this to work the process of setting up the aurdio serial must be done: http://playground.arduino.cc/Interfacing/Java) 
       
        java -cp target/io.anglehack.eso-1.0-SNAPSHOT.jar  io.anglehack.eso.tknkly.serial.SerialConnection

* To run the server run.
       
        java -jar target/io.anglehack.eso-1.0-SNAPSHOT.jar server example.yml

