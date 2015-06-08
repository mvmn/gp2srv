package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.log.api.Logger;

public class GP2CmdGetThumbnail extends AbstractGPhoto2Command {

	protected final int sourceFileRef;
	protected final String targetFileName;
	protected final String folder;

	public GP2CmdGetThumbnail(final String folder, final int sourceFileRef, final String targetFileName, final Logger logger) {
		super(logger);
		this.folder = folder;
		this.sourceFileRef = sourceFileRef;
		this.targetFileName = targetFileName;
	}

	public String[] getCommandString() {
		return new String[] { "--get-thumbnail", String.valueOf(sourceFileRef), "--force-overwrite", "--filename", targetFileName, "--folder", folder };
	}
}
