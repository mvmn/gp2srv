package x.mvmn.gp2srv.service.gphoto2.model;

public class CameraConfigEntry {

	public static enum CameraConfigEntryType {
		DATE, MENU, RADIO, TEXT, TOGGLE
	}

	protected final String key;
	protected final String label;
	protected final String value;
	protected final String printableValue;
	protected final CameraConfigEntryType type;
	protected final String[] choices;

	public CameraConfigEntry(final String key, final String label, final String value, final CameraConfigEntryType type) {
		this(key, label, value, null, type, null);
	}

	public CameraConfigEntry(final String key, final String label, final String value, final String printableValue, final CameraConfigEntryType type) {
		this(key, label, value, printableValue, type, null);
	}

	public CameraConfigEntry(final String key, final String label, final String value, final CameraConfigEntryType type, final String[] choices) {
		this(key, label, value, null, type, choices);
	}

	public CameraConfigEntry(final String key, final String label, final String value, final String printableValue, final CameraConfigEntryType type,
			final String[] choices) {
		this.key = key;
		this.label = label;
		this.value = value;
		this.printableValue = printableValue;
		this.type = type;
		this.choices = choices;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public String getPrintableValue() {
		return printableValue;
	}

	public CameraConfigEntryType getType() {
		return type;
	}

	public String[] getChoices() {
		return choices;
	}

	public String toString() {
		final StringBuilder choicesBuilder;
		if (choices != null) {
			choicesBuilder = new StringBuilder();
			for (String choice : choices) {
				choicesBuilder.append(choice).append("; ");
			}
		} else {
			choicesBuilder = null;
		}
		return String.format("CameraConfigEntry: [%s] %s - %s = %s ", type.name(), key, label, value)
				+ (printableValue != null ? "(" + printableValue + ") " : "")
				+ (choices != null ? "[" + choices.length + " choices: " + choicesBuilder.toString() + "] " : "");
	}
}
