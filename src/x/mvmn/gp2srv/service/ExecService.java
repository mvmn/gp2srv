package x.mvmn.gp2srv.service;

import java.io.File;
import java.io.IOException;

public interface ExecService {

	public static interface ExecCallback {
		public void processResult(ExecResult execResult);

		public void processError(Throwable error);
	}

	public static class ExecResult {
		private final String output;
		private final String errorOutput;
		private final int exitCode;

		public ExecResult(final String output, final String errorOutput, final int exitCode) {
			super();
			this.output = output;
			this.errorOutput = errorOutput;
			this.exitCode = exitCode;
		}

		public String getStandardOutput() {
			return output;
		}

		public int getExitCode() {
			return exitCode;
		}

		public String getErrorOutput() {
			return errorOutput;
		}
	}

	public ExecResult execCommandSync(String[] command, String[] envVars, File dir) throws IOException;

	public void execCommandAsync(ExecCallback callback, String[] command, String[] envVars, File dir);

	public boolean isProcessRunning();

	public Process getCurrentProcess();

}