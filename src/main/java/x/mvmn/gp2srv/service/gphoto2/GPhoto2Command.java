package x.mvmn.gp2srv.service.gphoto2;

import x.mvmn.gp2srv.service.ExecService.ExecResult;

public interface GPhoto2Command<T> {
	public String[] getCommandString();

	public GPhoto2CommandResult<T> processExecResult(final ExecResult execResult);
}
