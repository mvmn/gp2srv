package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2Command;
import x.mvmn.log.api.Logger;

public abstract class AbstractGPhoto2Command implements GPhoto2Command {
	protected final Logger logger;

	private String rawOutput;

	public AbstractGPhoto2Command(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public void submitError(Throwable error) {
		logger.error("Failed to execute command '" + this.getCommandString() + "'.", error);
	}

	@Override
	public void submitRawOutput(String output) {
		this.rawOutput = output;
	}

	public String getRawOutput() {
		return this.rawOutput;
	}
}
