#!/usr/bin/sh
# Run from the root dir
./mvnw package
./mvnw install:install-file \
  -Dfile=out/RARS_Reborn_Core-1.0.jar \
  -DgroupId=org.rarsreborn \
  -DartifactId=core \
  -Dversion=1.0 \
  -Dpackaging=jar \
  -DgeneratePom=true
