package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import x.mvmn.gp2srv.model.CameraConfigEntry;
import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.log.api.Logger;

public class GP2CmdGetAllCameraConfigurations extends AbstractGPhoto2Command {

	private volatile Map<String, CameraConfigEntry> cameraConfig = Collections.emptyMap();

	public GP2CmdGetAllCameraConfigurations(Logger logger) {
		super(logger);
	}

	public String getCommandString() {
		return "list-all-config";
	}

	public void submitRawStandardOutput(final String standardOutput) {
		super.submitRawStandardOutput(standardOutput);
		try {
			final CameraConfigEntry[] config = ConfigParser.parseConfigEntries(standardOutput);
			final Map<String, CameraConfigEntry> cameraConfig = new TreeMap<String, CameraConfigEntry>();
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

	public String[] getCommandParams() {
		return null;
	}

	public Map<String, CameraConfigEntry> getCameraConfig() {
		return cameraConfig;
	}
}
