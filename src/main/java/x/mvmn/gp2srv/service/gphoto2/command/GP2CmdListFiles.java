package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.gp2srv.service.gphoto2.FileListParser;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.log.api.Logger;

public class GP2CmdListFiles extends AbstractGPhoto2Command<CameraFileRefsCollected> {

	protected final String[] COMMAND_STR = { "--list-files" };

	public GP2CmdListFiles(final Logger logger) {
		super(logger);
	}

	public String[] getCommandString() {
		return COMMAND_STR;
	}

	@Override
	protected CameraFileRefsCollected processExecResultInternal(final ExecResult execResult) {
		CameraFileRefsCollected filesList = null;
		try {
			filesList = FileListParser.parseList(execResult.getStandardOutput());
		} catch (Exception e) {
			logger.error(e);
		}
		return filesList;
	}
}
