package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.gphoto2.service.FileListParser;
import x.mvmn.gp2srv.service.gphoto2.service.FileListParser.CameraFileRefsCollected;
import x.mvmn.log.api.Logger;

public class GP2CmdListFiles extends AbstractGPhoto2Command {

	protected CameraFileRefsCollected filesList = null;

	public GP2CmdListFiles(final Logger logger) {
		super(logger);
	}

	@Override
	public String getCommandString() {
		return "list-files";
	}

	@Override
	public String[] getCommandParams() {
		return null;
	}

	@Override
	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		try {
			filesList = FileListParser.parseList(standardOutput);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public CameraFileRefsCollected getFilesList() {
		return filesList;
	}
}
