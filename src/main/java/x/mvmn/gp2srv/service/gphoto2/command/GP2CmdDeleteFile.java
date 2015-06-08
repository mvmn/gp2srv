package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.log.api.Logger;

public class GP2CmdDeleteFile extends AbstractGPhoto2Command<Object> {
	protected final int sourceFileRef;
	protected final String folder;

	public GP2CmdDeleteFile(final String folder, final int sourceFileRef, final Logger logger) {
		super(logger);
		this.folder = folder;
		this.sourceFileRef = sourceFileRef;
	}

	public String[] getCommandString() {
		return new String[] { "--delete-file", String.valueOf(sourceFileRef), "--folder", folder };
	}

	@Override
	protected Object processExecResultInternal(final ExecResult execResult) {
		return null;
	}
}
