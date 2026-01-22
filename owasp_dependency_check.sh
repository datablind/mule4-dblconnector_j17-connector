#!/bin/bash

JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

java -version
mvn -v

read -p "Are you good with the Java & maven version? " -r
if [[ $REPLY =~ ^[Yy]$ ]]
then
        echo "***** Okay, I will proceed with this Java version *****"
        echo "***** Installing Connectot in local maven repository *****"
else
        echo "***** Change the Java version and then run this script *****"
        echo "Exiting"
        exit 1
fi

# Validate required environment variables for MUnit tests
if [ -z "$nvd_api_key" ]; then
        echo "***** ERROR: nvd_api_key environment variable is not set *****"
        echo "***** Please set it before running this script: *****"
        echo "   export nvd_api_key=your nvd.api.key"
        echo "Exiting"
        exit 1
fi

mvn org.owasp:dependency-check-maven:check -Dnvd.api.key=${nvd_api_key}
#