package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.log.api.Logger;

public class GP2CmdCaptureAndDownload extends AbstractGPhoto2Command<Object> {

	protected static final String[] COMMAND_STRING = new String[] { "--capture-image-and-download", "--force-overwrite", "--filename", "capture.jpg" };

	public GP2CmdCaptureAndDownload(final Logger logger) {
		super(logger);
	}

	public String[] getCommandString() {
		return COMMAND_STRING;
	}

	public Object processExecResultInternal(final ExecResult result) {
		return null;
	}
}
