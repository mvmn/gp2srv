package x.mvmn.gp2srv.model;

import java.util.Arrays;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(choices);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((printableValue == null) ? 0 : printableValue.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CameraConfigEntry other = (CameraConfigEntry) obj;
		if (!Arrays.equals(choices, other.choices))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (printableValue == null) {
			if (other.printableValue != null)
				return false;
		} else if (!printableValue.equals(other.printableValue))
			return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
