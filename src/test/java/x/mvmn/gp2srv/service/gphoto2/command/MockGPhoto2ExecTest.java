package x.mvmn.gp2srv.service.gphoto2.command;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import x.mvmn.gp2srv.service.MockExecService;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandService;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2ExecService;
import x.mvmn.log.AbstractLogger;
import x.mvmn.log.api.Logger;

public abstract class MockGPhoto2ExecTest {

	protected static final StringBuilder LOG = new StringBuilder();

	protected static final Logger LOGGER = new AbstractLogger() {

		public Logger log(LogLevel level, Throwable t) {
			LOG.append(level.name()).append(" ").append(t.toString()).append("\n");
			return this;
		}

		public Logger log(LogLevel level, String text, Throwable t) {
			LOG.append(level.name()).append(" ").append(text).append(" ").append(t.toString()).append("\n");
			return this;
		}

		public Logger log(LogLevel level, String text) {
			LOG.append(level.name()).append(" ").append(text).append("\n");
			return this;
		}
	};
	protected static final Properties MOCK_PROPERTIES;
	protected static final MockExecService MOCK_EXEC_SERVICE;
	protected static final GPhoto2CommandService MOCK_GPHOTO2_COMMAND_SERVICE;
	static {
		MOCK_PROPERTIES = new Properties();
		try {
			MOCK_PROPERTIES.load(MockGPhoto2ExecTest.class.getResourceAsStream("/x/mvmn/gp2srv/service/gphoto2/gphoto2mocks.properties"));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load resource /x/mvmn/gp2srv/service/gphoto2/gphoto2mocks.properties", e);
		}

		MOCK_EXEC_SERVICE = new MockExecService(MOCK_PROPERTIES, LOGGER);
		MOCK_GPHOTO2_COMMAND_SERVICE = new GPhoto2CommandService(new GPhoto2ExecService(MOCK_EXEC_SERVICE, "/path/to/mock/gphoto2", new File("/path/to/mock/"),
				new File("/path/to/mock/images")));
	}
}
