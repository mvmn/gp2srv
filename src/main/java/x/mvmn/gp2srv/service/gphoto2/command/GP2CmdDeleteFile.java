package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.log.api.Logger;

public class GP2CmdDeleteFile extends AbstractGPhoto2Command {
	protected final int sourceFileRef;
	protected final String folder;

	public GP2CmdDeleteFile(final String folder, final int sourceFileRef, final Logger logger) {
		super(logger);
		this.folder = folder;
		this.sourceFileRef = sourceFileRef;
	}

	public String getCommandString() {
		return "delete-file";
	}

	public String[] getCommandParams() {
		return new String[] { String.valueOf(sourceFileRef), "--folder", folder };
	}
}
