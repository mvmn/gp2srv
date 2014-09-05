package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.log.api.Logger;

public class GP2CmdSummary extends AbstractGPhoto2Command {

	public GP2CmdSummary(final Logger logger) {
		super(logger);
	}

	public String getCommandString() {
		return "summary";
	}

	public String[] getCommandParams() {
		return null;
	}
}
