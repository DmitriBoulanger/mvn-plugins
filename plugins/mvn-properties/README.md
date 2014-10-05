
# Setting properties in Maven projects #

A sample implementaion of the a Mojo-based plug-in that can be used to share values maven-varibales (properties) between different projects. The properies are supposed to be packed resource files in JAR-artifacts. The plug-in installs found properties as maven variables

The implementation is the project **mvn-properties-maven-plugin**. The projects *mvn-properties-test-container* and *mvn-properties-test-usage* are used to test the plug-in.

## How-To ##

1. Install this project from maven. The tests are executed automatically.
2. The project *mvn-properties-maven-plugin* has JUnit-test. Run it from Eclipse to see ho it works. 
3. Run the maven-test for project *mvn-properties-test-usage*. This project show "real" usage of the plug-in

 