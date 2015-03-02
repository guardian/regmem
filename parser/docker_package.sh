#!/bin/sh

sbt docker:stage && cd target/docker && zip -r ../../ebimage.zip *

echo now upload ebimage.zip as a new version to elasticbeanstalk!


