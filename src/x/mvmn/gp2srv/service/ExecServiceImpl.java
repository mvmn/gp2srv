package x.mvmn.gp2srv.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import x.mvmn.log.PrintStreamLogger;
import x.mvmn.log.api.Logger;

public class ExecServiceImpl implements ExecService {

	private volatile boolean processRunning = false;
	private volatile Process currentProcess = null;

	private final Object lockObject = new Object();

	private final Logger logger;

	public ExecServiceImpl(Logger logger) {
		this.logger = logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.gp2srv.service.ExecService#execCommandSync(java.lang.String[], java.lang.String[], java.io.File)
	 */
	public String execCommandSync(final String[] command, final String[] envVars, final File dir) throws IOException {
		String result;
		synchronized (lockObject) {
			currentProcess = Runtime.getRuntime().exec(command, envVars, dir);
			processRunning = true;

			List<String> resultLines = IOUtils.readLines(currentProcess.getInputStream());
			processRunning = false;
			currentProcess = null;
			StringBuilder resultBuilder = new StringBuilder();
			for (String line : resultLines) {
				resultBuilder.append(line).append("\n");
			}
			result = resultBuilder.toString();
		}
		return result;
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
					final String result = execCommandSync(command, envVars, dir);
					if (callback != null) {
						callback.processResult(result);
					}
				} catch (Exception e) {
					if (callback != null) {
						try {
							callback.processError(e);
						} catch (Exception epe) {
							StringBuilder commandBuilder = new StringBuilder();
							if (command == null) {
								commandBuilder.append("null");
							} else {
								for (String commandPart : command) {
									commandBuilder.append(commandPart).append(" ");
								}
							}
							logger.error("Async call for command " + commandBuilder.toString() + " error processing failed with another error", epe);
						}
					}
				}
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.gp2srv.service.ExecService#isProcessRunning()
	 */
	public boolean isProcessRunning() {
		return processRunning;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.gp2srv.service.ExecService#getCurrentProcess()
	 */
	public Process getCurrentProcess() {
		return currentProcess;
	}

	public static void main(String[] args) throws Exception {
		ExecServiceImpl svc = new ExecServiceImpl(new PrintStreamLogger(System.out));
		System.out.println(svc.execCommandSync(new String[] { "ls" }, null, null));
		svc.execCommandAsync(null, new String[] { "sleep", "5" }, null, null);
		Thread.sleep(1);
		System.out.println(svc.execCommandSync(new String[] { "/opt/local/bin/gphoto2", "--summary" }, null, null));
	}
}
