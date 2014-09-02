package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2Command;
import x.mvmn.log.api.Logger;

public abstract class AbstractGPhoto2Command implements GPhoto2Command {
	protected final Logger logger;

	private volatile String rawStandardOutput = "";
	private volatile String rawErrorOutput = "";
	private volatile int exitCode = -1;

	public AbstractGPhoto2Command(final Logger logger) {
		this.logger = logger;
	}

	@Override
	public void submitError(final Throwable error) {
		logger.error("Failed to execute command '" + this.getCommandString() + "'.", error);
	}

	@Override
	public void submitRawStandardOutput(final String standardOutput) {
		this.rawStandardOutput += standardOutput;
	}

	@Override
	public String getRawStandardOutput() {
		return this.rawStandardOutput;
	}

	public void submitRawErrorOutput(final String errorOutput) {
		this.rawErrorOutput += errorOutput;
	}

	public String getRawErrorOutput() {
		return rawErrorOutput;
	}

	@Override
	public void submitExitCode(final int exitCode) {
		this.exitCode = exitCode;
	}

	@Override
	public int getExitCode() {
		return exitCode;
	}
}
