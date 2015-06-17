package x.mvmn.gp2srv.service.gphoto2;

import java.util.LinkedList;
import java.util.List;

import x.mvmn.gp2srv.model.CameraConfigEntry;
import x.mvmn.gp2srv.model.CameraConfigEntry.CameraConfigEntryType;

public class ConfigParser {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator") != null && System.getProperty("line.separator").length() > 0 ? System
			.getProperty("line.separator") : "\n";
	private static final String SEPARATOR = ": ";

	public static CameraConfigEntry parseConfigEntry(final String configAsText) throws Exception {
		final CameraConfigEntry[] results = parseConfigEntries(configAsText);

		if (results != null && results.length == 1) {
			return results[0];
		} else {
			throw new Exception("Error parsing config entries: " + results != null ? "expected one entry but got " + results.length : " results are null.");
		}
	}

	public static CameraConfigEntry[] parseConfigEntries(final String configAsText) throws Exception {
		final List<CameraConfigEntry> results = new LinkedList<CameraConfigEntry>();

		final String[] lines = configAsText.split(LINE_SEPARATOR);

		CameraConfigEntryBuilder builder = new CameraConfigEntryBuilder();

		for (int i = 0; i < lines.length; i++) {
			final String line = lines[i];
			if (line.trim().length() > 0) {
				if (!line.contains(SEPARATOR)) {
					if (builder.key != null) {
						results.add(builder.build());
						builder = new CameraConfigEntryBuilder();
						builder.key = line;
					} else {
						builder.key = line;
					}
				} else {
					if (builder.key == null) {
						throw new Exception(String.format("Unexpected line #%s: %s", i, line));
					} else {
						final int separatorIndex = line.indexOf(SEPARATOR);
						final String configType = line.substring(0, separatorIndex);
						final String configValue = line.substring(separatorIndex + SEPARATOR.length());
						setSetting(builder, configType, configValue);
					}
				}
			}
		}
		if (builder.key != null) {
			results.add(builder.build());
		}

		return results.toArray(new CameraConfigEntry[results.size()]);
	}

	protected static void setSetting(final CameraConfigEntryBuilder builder, final String configType, final String configValue) throws Exception {
		if (configType.equals("Type")) {
			try {
				builder.type = CameraConfigEntryType.valueOf(configValue.trim());
			} catch (Exception e) {
				throw new Exception("Unable to parse type value " + configValue);
			}
		} else if (configType.equals("Choice")) {
			builder.choices.add(configValue.trim().substring(configValue.indexOf(" ") + 1));
		} else if (configType.equals("Current")) {
			builder.value = configValue;
		} else if (configType.equals("Top")) {
			try {
				builder.top = Long.parseLong(configValue.trim());
			} catch (final Exception e) {
				// TODO: Proper logging
				System.err.println("Failed to parse Top value: " + configValue);
			}
		} else if (configType.equals("Bottom")) {
			try {
				builder.bottom = Long.parseLong(configValue.trim());
			} catch (final Exception e) {
				// TODO: Proper logging
				System.err.println("Failed to parse Bottom value: " + configValue);
			}
		} else if (configType.equals("Step")) {
			try {
				builder.step = Long.parseLong(configValue.trim());
			} catch (final Exception e) {
				// TODO: Proper logging
				System.err.println("Failed to parse Step value: " + configValue);
			}
		} else if (configType.equals("Label")) {
			builder.label = configValue;
		} else if (configType.equals("Printable")) {
			builder.printableValue = configValue;
		}
	}

	protected static class CameraConfigEntryBuilder {
		public String key = null;
		public String label = null;
		public String value = null;
		public String printableValue = null;
		public CameraConfigEntryType type = null;
		public final List<String> choices = new LinkedList<String>();
		public Long bottom;
		public Long top;
		public Long step;

		public CameraConfigEntry build() {
			return type.equals(CameraConfigEntryType.RANGE) ? new CameraConfigEntry(key, label, value, printableValue, type, bottom, top, step)
					: new CameraConfigEntry(key, label, value, printableValue, type, choices.size() > 0 ? choices.toArray(new String[choices.size()]) : null);
		}
	}
}
