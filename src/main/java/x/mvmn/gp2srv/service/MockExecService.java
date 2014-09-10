package x.mvmn.gp2srv.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import x.mvmn.log.api.Logger;

public class MockExecService extends AbstractExecService {

	protected final Map<String, ProcessAccess> mockResults;

	public MockExecService(final Properties mockResultsProps, final Logger logger) {
		super(logger);
		final Map<String, ProcessAccess> mockResults = new HashMap<String, AbstractExecService.ProcessAccess>();
		for (final String key : mockResultsProps.stringPropertyNames()) {
			final String value = mockResultsProps.getProperty(key);
			mockResults.put(key, new ProcessAccess() {
				public InputStream getStandardOutput() {
					try {
						return new ByteArrayInputStream(value.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						return null;
					}
				}

				public int getProcessExitCode() {
					return 0;
				}

				public InputStream getErrorOutput() {
					return new ByteArrayInputStream(new byte[0]);
				}
			});
		}
		this.mockResults = mockResults;
	}

	public MockExecService(final Map<String, ProcessAccess> mockResults, final Logger logger) {
		super(logger);
		this.mockResults = mockResults;
	}

	public Process getCurrentProcess() {
		return null;
	}

	@Override
	protected ProcessAccess startProcess(String[] command, String[] envVars, File dir) throws IOException {
		final StringBuilder commandAsStr = new StringBuilder();
		for (int i = 1; i < command.length; i++) {
			commandAsStr.append(command[i]).append(" ");
		}
		return mockResults.get(commandAsStr.toString().trim());
	}

	@Override
	protected void afterProcessFinish() {
	}

	public boolean isProcessRunning() {
		return false;
	}
}
