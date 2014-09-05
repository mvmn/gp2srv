package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.log.api.Logger;

public class GP2CmdGetThumbnail extends AbstractGPhoto2Command {

	protected final int sourceFileRef;
	protected final String targetFileName;

	public GP2CmdGetThumbnail(final int sourceFileRef, final String targetFileName, final Logger logger) {
		super(logger);
		this.sourceFileRef = sourceFileRef;
		this.targetFileName = targetFileName;
	}

	public String getCommandString() {
		return "get-thumbnail";
	}

	public String[] getCommandParams() {
		return new String[] { String.valueOf(sourceFileRef), "--force-overwrite", "--filename", targetFileName };
	}
}
