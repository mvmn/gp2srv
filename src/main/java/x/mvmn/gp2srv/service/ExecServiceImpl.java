package x.mvmn.gp2srv.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import x.mvmn.log.api.Logger;

public class ExecServiceImpl extends AbstractExecService {

	protected volatile Process currentProcess = null;

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
		ProcessAccess result = null;
		if (!isProcessRunning()) {
			final Process process = Runtime.getRuntime().exec(command, envVars, dir);
			currentProcess = process;
			result = new ProcessAccessImpl(process);
		}
		return result;
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

	public static class ProcessAccessImpl implements ProcessAccess {
		protected final Process process;

		public ProcessAccessImpl(final Process process) {
			this.process = process;
		}

		public InputStream getStandardOutput() {
			return process.getInputStream();
		}

		public InputStream getErrorOutput() {
			return process.getErrorStream();
		}

		public int getProcessExitCode() {
			return process.exitValue();
		}
	};
}
