package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import x.mvmn.gp2srv.service.gphoto2.model.CameraConfigEntry;
import x.mvmn.gp2srv.service.gphoto2.service.ConfigParser;
import x.mvmn.log.api.Logger;

public class GP2CmdGetAllCameraConfigurations extends AbstractGPhoto2Command {

	private volatile Map<String, CameraConfigEntry> cameraConfig = Collections.emptyMap();

	public GP2CmdGetAllCameraConfigurations(Logger logger) {
		super(logger);
	}

	@Override
	public String getCommandString() {
		return "list-all-config";
	}

	@Override
	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		try {
			final CameraConfigEntry[] config = ConfigParser.parseConfigEntries(standardOutput);
			final Map<String, CameraConfigEntry> cameraConfig = new HashMap<String, CameraConfigEntry>();
			if (config != null) {
				for (final CameraConfigEntry configEntry : config) {
					cameraConfig.put(configEntry.getKey(), configEntry);
				}
			}
			this.cameraConfig = Collections.unmodifiableMap(cameraConfig);
		} catch (Exception e) {
			this.cameraConfig = Collections.emptyMap();
			logger.error("Failed to parse camera config", e);
		}
	}

	@Override
	public String[] getCommandParams() {
		return null;
	}

	public Map<String, CameraConfigEntry> getCameraConfig() {
		return cameraConfig;
	}
}
