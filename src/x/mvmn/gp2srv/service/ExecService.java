package x.mvmn.gp2srv.service;

import java.io.File;
import java.io.IOException;

public interface ExecService {

	public static interface ExecCallback {
		public void processResult(String processOutput);

		public void processError(Throwable error);
	}

	public String execCommandSync(String[] command, String[] envVars, File dir) throws IOException;

	public void execCommandAsync(ExecCallback callback, String[] command, String[] envVars, File dir);

	public boolean isProcessRunning();

	public Process getCurrentProcess();

}