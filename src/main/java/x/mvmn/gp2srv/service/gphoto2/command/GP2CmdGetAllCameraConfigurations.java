package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import x.mvmn.gp2srv.model.CameraConfigEntry;
import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.gp2srv.service.gphoto2.ConfigParser;
import x.mvmn.log.api.Logger;

public class GP2CmdGetAllCameraConfigurations extends AbstractGPhoto2Command<Map<String, CameraConfigEntry>> {

	protected final String[] COMMAND_STR = { "--list-all-config" };

	public GP2CmdGetAllCameraConfigurations(final Logger logger) {
		super(logger);
	}

	public String[] getCommandString() {
		return COMMAND_STR;
	}

	@Override
	protected Map<String, CameraConfigEntry> processExecResultInternal(final ExecResult execResult) {
		Map<String, CameraConfigEntry> cameraConfig;
		try {
			final CameraConfigEntry[] config = ConfigParser.parseConfigEntries(execResult.getStandardOutput());
			cameraConfig = new TreeMap<String, CameraConfigEntry>();
			if (config != null) {
				for (final CameraConfigEntry configEntry : config) {
					cameraConfig.put(configEntry.getKey(), configEntry);
				}
			}
			cameraConfig = Collections.unmodifiableMap(cameraConfig);
		} catch (final Exception e) {
			cameraConfig = Collections.emptyMap();
			logger.error("Failed to parse camera config", e);
		}
		return cameraConfig;
	}
}
