package de.dbo.tools.mvn.plugin.properties.test;

import static de.dbo.tools.utils.print.Print.lines;
import static de.dbo.tools.utils.print.Print.padLeft;

import de.dbo.tools.mvn.plugin.properties.ReadPropertiesMojo;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadPropertiesMojoTest extends AbstractMojoTestCase {
	private static final Logger log = LoggerFactory.getLogger(ReadPropertiesMojoTest.class);

	private static final String MAVEN_PLUGIN = "mvn-properties-maven-plugin";
	private static final String ARTIFACT_ID_TEST = "de.dbo.mvn.test:mvn-properties-maven-plugin-test:jar:0.0.0-SNAPSHOT";
	private static final String ARTIFACT_POM_TEST =  getBasedir() + "/src/test/resources/test-pom.xml";
			
	public ReadPropertiesMojoTest() {
		log.info("using POM = " + ARTIFACT_POM_TEST);
	}
	
	public void testPropertiesMojo() throws Exception {
		
		// Test-pom
		final File pomFile = new File(ARTIFACT_POM_TEST);
		assertTrue("Pom-file  doesn't exist" + ARTIFACT_POM_TEST, pomFile.exists());
		assertTrue("Pom-file is not readable" + ARTIFACT_POM_TEST, pomFile.canRead());
		
		// Maven-project from the Test-pom
		final MavenProject mavenProject = newMavenProject(pomFile);
		log.info("Maven-project from test-pom: " + print(mavenProject).toString());
		assertEquals(ARTIFACT_ID_TEST, mavenProject.getId());
		assertTrue("No properties in the initial maven-project", !mavenProject.getProperties().isEmpty());
		
		// Test context
		final Map<String, Object> pluginContext = new HashMap<>();
		pluginContext.put("test_maven_project", mavenProject);
		executeMojo(pluginContext, pomFile);
		
		// Assertions after running the plug-in
		final Properties properties = mavenProject.getProperties();
		assertTrue("No properties in the maven-project", !properties.isEmpty());
		log.info("Maven-project properties after using "+MAVEN_PLUGIN+": " + lines(properties,"-version"));
		assertEquals("1.0-resource",properties.getProperty("spring-version", null));
		assertEquals("4.0.0-resource",properties.getProperty("mysql-version", null));
	}

	private void executeMojo(final Map<String, ?> pluginContext, final File testPomFile) throws Exception {
		final ReadPropertiesMojo mojo = new ReadPropertiesMojo();
		configureMojo(mojo, MAVEN_PLUGIN, testPomFile);
		mojo.setPluginContext(pluginContext);
		mojo.setLog( new ReadPropertiesMojoTestLog() );
		mojo.execute();
	}
	
	private MavenProject newMavenProject(final File pomFile) throws Exception{
		if (null==pomFile) {
			log.warn("POM for maven-project is null!");
			return null;
		}
		final Model model;
		FileReader reader = null;
		final MavenXpp3Reader mavenreader = new MavenXpp3Reader();
		try {
			reader = new FileReader(pomFile);
			model = mavenreader.read(reader);
		} catch (Exception e) {
			log.error("Can't create model from POM " + pomFile.getAbsolutePath(), e);
			throw e;
		}  finally {
			if (null!=reader) {
				try {
					reader.close();
				} catch (Exception e) {
					log.error("Can't close reader from POM "+ pomFile.getAbsolutePath(),e);
				}
			}
		}
		return new MavenProject(model);
	}
	
	private static StringBuilder print(final MavenProject mavenProject) {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\t - Id           : " + mavenProject.getId());
		sb.append("\n\t - Version      : " + mavenProject.getVersion());
		sb.append("\n\t - Parent       : " + mavenProject.getParent());
		sb.append("\n\t - Artifact     : " + mavenProject.getArtifact());
		sb.append("\n\t - Properties   : " + printProperties(mavenProject));
		sb.append("\n\t - Dependencies : " + printDependencies(mavenProject));
        return sb;
	}
	
	private static final StringBuilder printDependencies(final MavenProject mavenProject) {
		@SuppressWarnings("unchecked")
		final List<Dependency> dependencies = (List<Dependency>) mavenProject.getDependencies();
		final StringBuilder sb = new StringBuilder();
		if (null==dependencies) {
			sb.append("NULL");
		}
		else if (dependencies.isEmpty()) {
			sb.append("[]");
		} else {
			for (final Dependency dependency:dependencies) {
				sb.append("\n\t\t -- " + padLeft(dependency.getArtifactId(),30) );
			}
		}
		return sb;
	}
	
	private static final StringBuilder printProperties(final MavenProject mavenProject) {
		final Properties properties =  mavenProject.getProperties();
		final StringBuilder sb = new StringBuilder();
		if (null==properties) {
			sb.append("NULL");
		}
		else if (properties.isEmpty()) {
			sb.append("[]");
		} else {
			for (final Object key: properties.keySet()) {
				sb.append("\n\t\t -- " + key +  " = " + properties.getProperty((String)key));
			}
		}
		return sb;
	}
}
