package x.mvmn.gp2srv.service.gphoto2.command;

import org.junit.Test;

import junit.framework.TestCase;
import x.mvmn.log.AbstractLogger;
import x.mvmn.log.api.Logger;
import x.mvmn.log.api.Logger.LogLevel;

public class AbstractGPhoto2CommandTest {

	final StringBuilder log = new StringBuilder();
	final AbstractGPhoto2Command unit = new AbstractGPhoto2Command(new AbstractLogger() {
		public Logger log(LogLevel level, String text) {
			log.append(level.name()).append(" ").append(text).append("\n");
			return this;
		}

		public Logger log(LogLevel level, String text, Throwable t) {
			log.append(level.name()).append(" ").append(text).append(" ").append(t.toString()).append("\n");
			return null;
		}

		public Logger log(LogLevel level, Throwable t) {
			log.append(level.name()).append(" ").append(t.toString()).append("\n");
			return null;
		}
	}) {
		public String[] getCommandString() {
			return new String[] { "--fake-command", "fake arg" };
		}
	};

	@Test
	public void testSubmitError() {
		log.setLength(0);
		RuntimeException exc = new RuntimeException("Error Text");
		unit.submitError(new RuntimeException("Error Text"));
		TestCase.assertEquals(LogLevel.ERROR.name() + " [--fake-command, fake arg] " + exc.toString() + "\n", log.toString());
	}

	@Test
	public void testSubmitExitCode() {
		unit.submitExitCode(123);
		TestCase.assertEquals(123, unit.getExitCode());
	}

	@Test
	public void testSubmitResult() {
		unit.submitRawStandardOutput("one");
		unit.submitRawStandardOutput("two");
		TestCase.assertEquals("onetwo", unit.getRawStandardOutput());
	}
}
