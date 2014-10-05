package de.dbo.tools.mvn.plugin.properties.test;

import de.dbo.tools.mvn.plugin.properties.test.PropertiesMojoUsage;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

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
		final PropertiesMojoUsage propertiesMojoUsage = new  PropertiesMojoUsage();
		propertiesMojoUsage.logMavenProperties();
		if ( null == System.getProperties().getProperty("maven.home", null)) {
			log.warn("No assertions (no actual test possible). Try to run the Maven-test ...");
			return;
		}
		propertiesMojoUsage.logVersionProperties();
		assertEquals("1.0-resource",System.getProperties().getProperty("spring-version", null));
		assertEquals("4.0.0-resource",System.getProperties().getProperty("mysql-version", null));
	}
}