!/bin/sh

JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
export JAVA_HOME
PATH="$JAVA_HOME/bin:$PATH"
java -version
mvn -v
