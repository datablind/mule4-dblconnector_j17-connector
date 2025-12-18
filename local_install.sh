!/bin/sh

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

mvn -X clean install -DskipTests
