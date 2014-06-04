package x.mvmn.gp2srv.service.gphoto2;

import x.mvmn.gp2srv.service.ExecService.ExecCallback;

public class GPhoto2CommandService {

	protected final GPhoto2ExecService gphoto2ExecService;

	public GPhoto2CommandService(final GPhoto2ExecService gphoto2ExecService) {
		this.gphoto2ExecService = gphoto2ExecService;
	}

	public <C extends GPhoto2Command> C executeCommand(final C command) {
		try {
			String output = gphoto2ExecService.execCommand(command.getCommandString(), command.getCommandParams());
			command.submitRawOutput(output);
		} catch (Throwable error) {
			command.submitError(error);
		}
		return command;
	}

	public <C extends GPhoto2Command, CB extends GPhoto2CommandCallback<C>> void executeCommandAsync(final CB callback, final C command) {
		gphoto2ExecService.execCommandAsync(new ExecCallback() {
			@Override
			public void processResult(String processOutput) {
				command.submitRawOutput(processOutput);
				callback.processResults(command);
			}

			@Override
			public void processError(Throwable error) {
				command.submitError(error);
				callback.processResults(command);
			}
		}, command.getCommandString(), command.getCommandParams());
	}
}
