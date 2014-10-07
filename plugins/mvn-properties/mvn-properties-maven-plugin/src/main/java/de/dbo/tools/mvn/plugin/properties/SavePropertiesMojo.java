package de.dbo.tools.mvn.plugin.properties;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.io.location.ClasspathResourceLocatorStrategy;
import org.apache.maven.shared.io.location.FileLocatorStrategy;
import org.apache.maven.shared.io.location.Location;
import org.apache.maven.shared.io.location.Locator;
import org.apache.maven.shared.io.location.LocatorStrategy;
import org.apache.maven.shared.io.location.URLLocatorStrategy;
import org.codehaus.plexus.util.cli.CommandLineUtils;

/*
 http://stackoverflow.com/questions/1440224/how-can-i-download-maven-artifacts-within-a-plugin
 
 
 */

/**
 * The read-resource-properties goal reads property files and stores the
 * properties as project properties. It serves as an alternate to specifying
 * properties in pom.xml.
 * 
 * @author Dmitri Boulanger, Hombach
 *
 *         D. Knuth: Programs are meant to be read by humans and only
 *         incidentally for computers to execute
 *
 
 *
 * @goal save-resource-properties
 */
public final class SavePropertiesMojo extends AbstractMojo {

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Optional paths to properties files to be used.
	 * 
	 * @parameter
	 */
	private String[] resourceNames;
	
	public void execute() throws MojoExecutionException {

		getLog().warn("Not implemented");
	 
	}

	/**
	 * Obtain the file from the local project or the classpath
	 * 
	 * @throws MojoExecutionException
	 */
	private File[] readPropertyFiles() throws MojoExecutionException {
		if (null==resourceNames) {
			resourceNames = new String[]{};
			getLog().warn("No resource-names specified");
		}

		final File[] allFiles = new File[resourceNames.length];
		for (int i = 0; i < resourceNames.length; i++) {
			Location location = getLocation(resourceNames[i], project);
			if (null == location) {
				throw new MojoExecutionException("Location is null: filePath=["
						+ resourceNames[i] + "]" + "project=["
						+ (null != project ? project.getId() : "NULL") + "]");
			}

			try {
				allFiles[i] = location.getFile();
			} catch (IOException e) {
				throw new MojoExecutionException(
						"unable to open properties file", e);
			}
		}

		// replace the original array with the merged results
		logPropertyFiles(allFiles);
		return allFiles;
	}

	/**
	 * Retrieves a property value, replacing values like ${token} using the
	 * Properties to look them up. Shamelessly adapted from:
	 * http://maven.apache.org/plugins/maven-war-plugin/xref/org/apache/maven/plugin
	 * /war/PropertyUtils.html
	 * 
	 * It will leave unresolved properties alone, trying for System properties,
	 * and environment variables and implements re-parsing (in the case that the
	 * value of a property contains a key), and will not loop endlessly on a
	 * pair like test = ${test}
	 * 
	 * @param k
	 *            property key
	 * @param p
	 *            project properties
	 * @param environment
	 *            environment variables
	 * @return resolved property value
	 */
	private String getPropertyValue(final String k, final Properties p, final Properties environment) {
		String v = p.getProperty(k);
		String ret = "";
		int idx, idx2;

		while ((idx = v.indexOf("${")) >= 0) {
			// append prefix to result
			ret += v.substring(0, idx);

			// strip prefix from original
			v = v.substring(idx + 2);

			idx2 = v.indexOf("}");

			// if no matching } then bail
			if (idx2 < 0) {
				break;
			}

			// strip out the key and resolve it
			// resolve the key/value for the ${statement}
			String nk = v.substring(0, idx2);
			v = v.substring(idx2 + 1);
			String nv = p.getProperty(nk);

			// try global environment
			if (nv == null) {
				nv = System.getProperty(nk);
			}

			// try environment variable
			if (nv == null && nk.startsWith("env.") && environment != null) {
				nv = environment.getProperty(nk.substring(4));
			}

			// if the key cannot be resolved,
			// leave it alone ( and don't parse again )
			// else prefix the original string with the
			// resolved property ( so it can be parsed further )
			// taking recursion into account.
			if (nv == null || nv.equals(nk)) {
				ret += "${" + nk + "}";
			} else {
				v = nv + v;
			}
		}
		return ret + v;
	}

	// Begin: RS addition
	/**
	 * Use various strategies to discover the file.
	 */
	public Location getLocation(String path, MavenProject project) {
		final LocatorStrategy classpathStrategy = new ClasspathResourceLocatorStrategy();

		final List<LocatorStrategy> strategies = new ArrayList<LocatorStrategy>();
		strategies.add(classpathStrategy);
		strategies.add(new FileLocatorStrategy());
		strategies.add(new URLLocatorStrategy());

		final List<LocatorStrategy> refStrategies = new ArrayList<LocatorStrategy>();
		refStrategies.add(classpathStrategy);

		final Locator locator = new Locator();
		locator.setStrategies(strategies);

		final Location location = locator.resolve(path);
		if (null == location) {
			getLog().warn("Location for path [" + path + "] is NULL");
		} else {
			try {
				getLog().debug("location=[" + location.getFile().toString()+ "]");
			} catch (IOException e) {
				getLog().error(e);
			}
		}
		return location;
	}
	
	//
	// Logging
	//
	
	private void logProjectProperties(final Properties properties) {
		final StringBuilder sb = new StringBuilder("Project properties:");
		if (null==properties) {
			sb.append("NULL");
		} else if (properties.isEmpty()) {
			sb.append("[]");
		}
		final List<String> keys = new ArrayList<String>();
		for (final Object o:properties.keySet()) {
			keys.add((String) o);
		}
		Collections.sort(keys);
		for (final String key:keys) {
			sb.append("\n\t - " + key + " = " + properties.getProperty(key));
		}
		getLog().info(sb.toString());
	}
	
	private void logProject() {
		getLog().debug("Maven test-project: ["
			+ (null != project ? project.getArtifactId() : "NULL") + "]");
	}

	private final void logPropertyFiles(File[] files) {
		final StringBuilder sb = new StringBuilder();
		if (null != files) {
			for (int i = 0; i < files.length; i++) {
				sb.append("\n\t - " + files[i].toString());
			}
		} else {
			sb.append("NULL");
		}
		getLog().debug("using " + files.length + " property-related files:" + sb.toString());
	}
}