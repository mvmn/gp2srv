package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.lang.util.ImmutablePair;
import x.mvmn.log.api.Logger;

public class GP2CmdSetSetting extends AbstractGPhoto2Command<Object> {

	protected final String settingKey;
	protected final String settingValue;
	protected final String[] commandString;

	public GP2CmdSetSetting(final String settingKey, final String settingValue, final Logger logger) {
		super(logger);
		this.settingKey = settingKey;
		this.settingValue = settingValue;
		this.commandString = new String[] { "--set-config-value", settingKey + "=" + settingValue };
	}

	public String[] getCommandString() {
		return commandString;
	}

	@Override
	protected ImmutablePair<String, String> processExecResultInternal(final ExecResult execResult) {
		return null;
	}

	public String getSettingKey() {
		return settingKey;
	}

	public String getSettingValue() {
		return settingValue;
	}
}
