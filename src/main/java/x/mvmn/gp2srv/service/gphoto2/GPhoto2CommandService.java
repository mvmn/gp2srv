package x.mvmn.gp2srv.service.gphoto2;

import x.mvmn.gp2srv.service.ExecService.ExecCallback;
import x.mvmn.gp2srv.service.ExecService.ExecResult;

public class GPhoto2CommandService {

	protected final GPhoto2ExecService gphoto2ExecService;

	public GPhoto2CommandService(final GPhoto2ExecService gphoto2ExecService) {
		this.gphoto2ExecService = gphoto2ExecService;
	}

	public <R> GPhoto2CommandResult<R> executeCommand(final GPhoto2Command<R> command) throws Exception {
		final GPhoto2CommandResult<R> result;
		final ExecResult execResult = gphoto2ExecService.execCommand(command.getCommandString());
		result = command.processExecResult(execResult);
		return result;
	}

	public <R> void executeCommandAsync(final GPhoto2CommandCallback<R, GPhoto2Command<R>> callback, final GPhoto2Command<R> command) {
		gphoto2ExecService.execCommandAsync(new ExecCallback() {
			public void processResult(final ExecResult execResult) {
				callback.processResults(command.processExecResult(execResult));
			}

			public void processError(final Throwable error) {
				callback.processError(error);
			}
		}, command.getCommandString());
	}

	public boolean isProcessRunning() {
		return gphoto2ExecService.isProcessRunning();
	}
}
