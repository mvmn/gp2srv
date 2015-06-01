package x.mvmn.gp2srv.service.gphoto2.command;

import x.mvmn.log.api.Logger;

public class GP2CmdSetSetting extends AbstractGPhoto2Command {

	protected final String settingKey;
	protected final String settingValue;

	public GP2CmdSetSetting(final String settingKey, final String settingValue, final Logger logger) {
		super(logger);
		this.settingKey = settingKey;
		this.settingValue = settingValue;
	}

	public String getCommandString() {
		return "set-config-value";
	}

	public String[] getCommandParams() {
		return new String[] { settingKey + "=" + settingValue };
	}

	public String getSettingKey() {
		return settingKey;
	}

	public String getSettingValue() {
		return settingValue;
	}

}
