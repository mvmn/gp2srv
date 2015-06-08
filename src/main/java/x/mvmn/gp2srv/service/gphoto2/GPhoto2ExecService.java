package x.mvmn.gp2srv.service.gphoto2;

import java.io.File;

import x.mvmn.gp2srv.service.ExecService;
import x.mvmn.gp2srv.service.ExecService.ExecCallback;
import x.mvmn.gp2srv.service.ExecService.ExecResult;

public class GPhoto2ExecService {
	protected final ExecService execService;

	protected final String pathToGphoto2;

	protected final File workingDir;

	protected final File imagesSubdir;

	public GPhoto2ExecService(final ExecService execService, final String pathToGphoto2, final File workingDir, final File imagesSubdir) {
		this.execService = execService;
		this.pathToGphoto2 = pathToGphoto2;
		this.workingDir = workingDir;
		this.imagesSubdir = imagesSubdir;
	}

	public ExecResult execCommand(final String... gp2Commands) throws Exception {
		return execService.execCommandSync(prepareExecCommand(gp2Commands), null, imagesSubdir);
	}

	public void execCommandAsync(final ExecCallback callback, final String... gp2Commands) {
		execService.execCommandAsync(callback, prepareExecCommand(gp2Commands), null, imagesSubdir);
	}

	public boolean isProcessRunning() {
		return execService.isProcessRunning();
	}

	public Process getCurrentProcess() {
		return execService.getCurrentProcess();
	}

	protected String[] prepareExecCommand(final String[] gp2Commands) {
		String[] execParams = new String[gp2Commands.length + 1];
		execParams[0] = pathToGphoto2;
		for (int i = 0; i < gp2Commands.length; i++) {
			execParams[i + 1] = gp2Commands[i];
		}

		return execParams;
	}
}
