package x.mvmn.gp2srv.service.gphoto2;

import x.mvmn.gp2srv.service.ExecService.ExecResult;

public class GPhoto2CommandResult<T> extends ExecResult {

	protected final T value;

	public GPhoto2CommandResult(final T value, final ExecResult execResult) {
		this(value, execResult.getStandardOutput(), execResult.getErrorOutput(), execResult.getExitCode());
	}

	public GPhoto2CommandResult(final T value, String output, String errorOutput, int exitCode) {
		super(output, errorOutput, exitCode);
		this.value = value;
	}

	public T getResult() {
		return value;
	}
}
