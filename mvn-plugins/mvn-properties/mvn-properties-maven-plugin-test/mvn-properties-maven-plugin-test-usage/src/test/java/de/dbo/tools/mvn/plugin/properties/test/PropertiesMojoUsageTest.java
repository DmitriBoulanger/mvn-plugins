package de.dbo.tools.mvn.plugin.properties.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test only runs if it is started using Maven
 * since the required properties are only set by the properties-maven-plugin
 *
 * @author Dmitri Boulanger, Hombach
 *
 * D. Knuth: Programs are meant to be read by humans and
 *           only incidentally for computers to execute
 *
 */
public class PropertiesMojoUsageTest {
    private static final Logger log = LoggerFactory.getLogger( PropertiesMojoUsageTest.class);

    @Test
    public void properties() {
	final PropertiesMojoUsage propertiesMojoUsage = new PropertiesMojoUsage();
	propertiesMojoUsage.logMavenProperties();
	if ( null == System.getProperties().getProperty("maven.home", null)) {
	    log.warn("No assertions (no actual test possible). Try to run the Maven-test ...");
	    return;
	}
	propertiesMojoUsage.logVersionProperties();
	assertEquals("0.0.0-SNAPSHOT", System.getProperties().getProperty("dbo.version", null));
	assertEquals("5.1.6", System.getProperties().getProperty("mysql.version", null));
    }
}