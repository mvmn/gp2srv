package x.mvmn.gp2srv.service.gphoto2;

import x.mvmn.gp2srv.service.ExecService.ExecCallback;
import x.mvmn.gp2srv.service.ExecService.ExecResult;

public class GPhoto2CommandService {

	protected final GPhoto2ExecService gphoto2ExecService;

	public GPhoto2CommandService(final GPhoto2ExecService gphoto2ExecService) {
		this.gphoto2ExecService = gphoto2ExecService;
	}

	public <C extends GPhoto2Command> C executeCommand(final C command) {
		try {
			final ExecResult execResult = gphoto2ExecService.execCommand(command.getCommandString(), command.getCommandParams());
			command.submitRawStandardOutput(execResult.getStandardOutput());
			command.submitRawErrorOutput(execResult.getErrorOutput());
			command.submitExitCode(execResult.getExitCode());
		} catch (Throwable error) {
			command.submitError(error);
		}
		return command;
	}

	public <C extends GPhoto2Command, CB extends GPhoto2CommandCallback<C>> void executeCommandAsync(final CB callback, final C command) {
		gphoto2ExecService.execCommandAsync(new ExecCallback() {
			public void processResult(final ExecResult execResult) {
				command.submitRawStandardOutput(execResult.getStandardOutput());
				command.submitRawErrorOutput(execResult.getErrorOutput());
				command.submitExitCode(execResult.getExitCode());
				callback.processResults(command);
			}

			public void processError(final Throwable error) {
				command.submitError(error);
				callback.processResults(command);
			}
		}, command.getCommandString(), command.getCommandParams());
	}

	public boolean isProcessRunning() {
		return gphoto2ExecService.isProcessRunning();
	}
}
