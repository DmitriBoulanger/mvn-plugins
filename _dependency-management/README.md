Convert maven properties in properties
===============================================

Example

    		<java.version>1.7</java.version>
    		<slf4j.version>1.7.6</slf4j.version>
    		<hsqldb.version>1.8.0.7</hsqldb.version>
    		
    		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    		<project.reporting.outputEncoding>UTF-8</<project.reporting.outputEncoding>

1. Replace [</] with [@]
2. Replace [@] with [      @]
3. Use RegEx [@.*version=\s] to be replaced with []
4. Replace [>] with [=]
5. Drop blank at the left 