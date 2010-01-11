#!/bin/sh

export CLASSPATH="weka.jar:."
javac *.java && java wekaDT
