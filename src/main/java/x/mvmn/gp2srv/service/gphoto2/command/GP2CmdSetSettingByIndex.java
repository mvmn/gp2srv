package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.log.api.Logger;

public class GP2CmdSetSettingByIndex extends AbstractGPhoto2Command<Object> {

	protected final String settingKey;
	protected final int valueIndex;
	protected final String[] commandString;

	public GP2CmdSetSettingByIndex(final String settingKey, final int valueIndex, final Logger logger) {
		super(logger);
		this.settingKey = settingKey;
		this.valueIndex = valueIndex;
		this.commandString = new String[] { "--set-config-index", settingKey + "=" + valueIndex };
	}

	public String[] getCommandString() {
		return commandString;
	}

	public String getSettingKey() {
		return settingKey;
	}

	public int getValue() {
		return valueIndex;
	}

	@Override
	protected Object processExecResultInternal(final ExecResult execResult) {
		return null;
	}
}
