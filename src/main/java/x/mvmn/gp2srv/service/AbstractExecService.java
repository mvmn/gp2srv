package x.mvmn.gp2srv.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import x.mvmn.log.api.Logger;
import x.mvmn.log.api.Logger.LogLevel;

public abstract class AbstractExecService implements ExecService {

	private final Object lockObject = new Object();

	private final Logger logger;

	public AbstractExecService(final Logger logger) {
		this.logger = logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.gp2srv.service.ExecService#execCommandSync(java.lang.String[], java.lang.String[], java.io.File)
	 */
	public ExecResult execCommandSync(final String[] command, final String[] envVars, final File dir) throws IOException {
		final String commandDebugInfo;
		if (logger.shouldLog(LogLevel.DEBUG) || logger.shouldLog(LogLevel.TRACE)) {
			commandDebugInfo = "'" + StringUtils.join(command, " ") + "' in dir '" + dir.getAbsolutePath() + "'.";
		} else {
			commandDebugInfo = "";
		}

		final StringBuilder standardOutput = new StringBuilder();
		final StringBuilder errorOutput = new StringBuilder();
		int processExitValue = 0;
		synchronized (lockObject) {
			if (logger.shouldLog(LogLevel.DEBUG)) {
				logger.debug("Executing command " + commandDebugInfo);
			}

			final ProcessAccess process = startProcess(command, envVars, dir);

			final InputStream errorStream = process.getErrorOutput();
			final InputStream stdoutStream = process.getStandardOutput();

			boolean processFinished = false;
			while (!processFinished) {
				if (stdoutStream.available() > 0) {
					readStream(stdoutStream, standardOutput, "STANDARD", commandDebugInfo);
				}
				if (errorStream.available() > 0) {
					readStream(errorStream, errorOutput, "ERROR", commandDebugInfo);
				}
				try {
					processExitValue = process.getProcessExitCode();
					processFinished = true;
					if (logger.shouldLog(LogLevel.TRACE)) {
						logger.trace("Command " + commandDebugInfo + " process exited with code " + processExitValue);
					}
					readStream(stdoutStream, standardOutput, "STANDARD", commandDebugInfo);
					readStream(errorStream, errorOutput, "ERROR", commandDebugInfo);
				} catch (IllegalThreadStateException e) {
					processFinished = false; // superfluous here, but left to indicate the meaning of the code.
				}
			}

			afterProcessFinish();
			if (logger.shouldLog(LogLevel.TRACE)) {
				logger.trace("Command " + commandDebugInfo + " finished.");
			}
		}

		return new ExecResult(standardOutput.toString(), errorOutput.toString(), processExitValue);
	}

	protected void readStream(InputStream stream, StringBuilder destination, String streamName, String commandDebugInfo) throws IOException {
		final List<String> lines = IOUtils.readLines(stream);
		for (String line : lines) {
			destination.append(line).append("\n");
			if (logger.shouldLog(LogLevel.TRACE)) {
				logger.trace("Command " + commandDebugInfo + " " + streamName + " output: " + line);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.gp2srv.service.ExecService#execCommandAsync(x.mvmn.gp2srv.service.ExecServiceImpl.ExecCallback, java.lang.String[], java.lang.String[],
	 * java.io.File)
	 */
	public void execCommandAsync(final ExecCallback callback, final String[] command, final String[] envVars, final File dir) {
		new Thread() {
			@Override
			public void run() {
				try {
					final ExecResult result = execCommandSync(command, envVars, dir);
					if (callback != null) {
						callback.processResult(result);
					}
				} catch (Exception e) {
					if (callback != null) {
						try {
							callback.processError(e);
						} catch (Exception epe) {
							final StringBuilder commandBuilder = new StringBuilder();
							if (command == null) {
								commandBuilder.append("null");
							} else {
								for (String commandPart : command) {
									commandBuilder.append(commandPart).append(" ");
								}
							}
							logger.error("Async call for command " + commandBuilder.toString() + " error processing failed with another error", epe);
							logger.error("Original error", e);
						}
					}
				}
			}
		}.start();
	}

	protected static interface ProcessAccess {
		public InputStream getStandardOutput();

		public InputStream getErrorOutput();

		public int getProcessExitCode();
	}

	protected abstract ProcessAccess startProcess(final String[] command, final String[] envVars, final File dir) throws IOException;

	protected abstract void afterProcessFinish();
}
