package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.log.api.Logger;

public class GP2CmdCaptureAndDownload extends AbstractGPhoto2Command {

	protected static final String[] COMMAND_STRING = new String[] { "--capture-image-and-download", "--force-overwrite", "--filename", "capture.jpg" };

	public GP2CmdCaptureAndDownload(Logger logger) {
		super(logger);
	}

	public String[] getCommandString() {
		return COMMAND_STRING;
	}

}
