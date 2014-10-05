package de.dbo.tools.mvn.plugin.properties.test;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReadPropertiesMojoTestLog extends SystemStreamLog {
	private static final Logger log = LoggerFactory.getLogger(ReadPropertiesMojoTestLog.class);
	
	@Override
	public void info(final CharSequence content) {
		log.info(content.toString());
	}
	
	@Override
	public void warn(final CharSequence content) {
		log.warn(content.toString());
	}
	
	@Override
	public void debug(final CharSequence content) {
		log.debug(content.toString());
	}
}
