package x.mvmn.gp2srv.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import x.mvmn.log.api.Logger;

public class ExecServiceImpl extends AbstractExecService {

	private volatile Process currentProcess = null;

	public ExecServiceImpl(final Logger logger) {
		super(logger);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.gp2srv.service.ExecService#getCurrentProcess()
	 */
	public Process getCurrentProcess() {
		return currentProcess;
	}

	@Override
	protected ProcessAccess startProcess(final String[] command, final String[] envVars, final File dir) throws IOException {
		return new ProcessAccess() {
			private final Process process = Runtime.getRuntime().exec(command, envVars, dir);

			public InputStream getStandardOutput() {
				return process.getInputStream();
			}

			public InputStream getErrorOutput() {
				// TODO Auto-generated method stub
				return process.getErrorStream();
			}

			public int getProcessExitCode() {
				return process.exitValue();
			}
		};
	}

	@Override
	protected void afterProcessFinish() {
		currentProcess = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see x.mvmn.gp2srv.service.ExecService#isProcessRunning()
	 */
	public boolean isProcessRunning() {
		return currentProcess != null;
	}
}
