package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2Command;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;
import x.mvmn.log.api.Logger;

public abstract class AbstractGPhoto2Command<T> implements GPhoto2Command<T> {
	protected final Logger logger;

	public AbstractGPhoto2Command(final Logger logger) {
		this.logger = logger;
	}

	public GPhoto2CommandResult<T> processExecResult(final ExecResult execResult) {
		return new GPhoto2CommandResult<T>(processExecResultInternal(execResult), execResult);
	}

	protected abstract T processExecResultInternal(final ExecResult execResult);
}
