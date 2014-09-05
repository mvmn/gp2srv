package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.log.api.Logger;

public class GP2CmdCaptureAndDownload extends AbstractGPhoto2Command {

	public GP2CmdCaptureAndDownload(Logger logger) {
		super(logger);
	}

	@Override
	public String getCommandString() {
		return "capture-image-and-download";
	}

	private static final String[] PARAMS = new String[] { "--force-overwrite", "--filename", "capture.jpg" };

	@Override
	public String[] getCommandParams() {
		return PARAMS;
	}
}
