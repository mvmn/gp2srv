package x.mvmn.gp2srv.service.gphoto2;

import java.io.File;

import x.mvmn.gp2srv.service.ExecService;
import x.mvmn.gp2srv.service.ExecService.ExecCallback;
import x.mvmn.gp2srv.service.ExecService.ExecResult;

public class GPhoto2ExecService {

	private static final String GP2COMMAND_PREFIX = "--";

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

	public ExecResult execCommand(final String gp2Command, final String... params) throws Exception {
		return execService.execCommandSync(prepareParams(gp2Command, params), null, imagesSubdir);
	}

	public void execCommandAsync(final ExecCallback callback, final String gp2Command, final String... params) {
		execService.execCommandAsync(callback, prepareParams(gp2Command, params), null, imagesSubdir);
	}

	public boolean isProcessRunning() {
		return execService.isProcessRunning();
	}

	public Process getCurrentProcess() {
		return execService.getCurrentProcess();
	}

	protected String[] prepareParams(final String gp2Command, final String... params) {
		String[] execParams = new String[params == null ? 2 : params.length + 2];
		execParams[0] = pathToGphoto2;
		execParams[1] = GP2COMMAND_PREFIX + gp2Command;
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				execParams[i + 2] = params[i];
			}
		}
		return execParams;
	}

}
