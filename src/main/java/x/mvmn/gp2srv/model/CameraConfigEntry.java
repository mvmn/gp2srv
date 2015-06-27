package x.mvmn.gp2srv.model;

import java.util.Arrays;

public class CameraConfigEntry {

	public static enum CameraConfigEntryType {
		DATE, MENU, RADIO, TEXT, TOGGLE, RANGE
	}

	protected final String key;
	protected final String label;
	protected final String value;
	protected final String printableValue;
	protected final CameraConfigEntryType type;
	protected final String[] choices;
	protected final String bottom;
	protected final String top;
	protected final String step;

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
		this.bottom = null;
		this.top = null;
		this.step = null;
	}

	public CameraConfigEntry(final String key, final String label, final String value, final String printableValue, final CameraConfigEntryType type,
			final String bottom, final String top, final String step) {
		this.key = key;
		this.label = label;
		this.value = value;
		this.printableValue = printableValue;
		this.type = type;
		this.choices = null;
		this.bottom = bottom;
		this.top = top;
		this.step = step;
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

	public String getBottom() {
		return bottom;
	}

	public String getTop() {
		return top;
	}

	public String getStep() {
		return step;
	}

//	public long[] getRangeOptions() {
//		long[] result = null;
//
//		if (top != null && bottom != null && top >= bottom) {
//			long theStep = this.step != null ? this.step : 1L;
//			int count = (int) ((top - bottom) / theStep + 1);
//			result = new long[count];
//			long val = bottom;
//			for (int i = 0; i < count; i++) {
//				result[i] = val;
//				val += theStep;
//			}
//		}
//
//		return result;
//	}

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
				+ (choices != null ? "[" + choices.length + " choices: " + choicesBuilder.toString() + "] " : "") + (bottom != null ? " bottom=" + bottom : "")
				+ (top != null ? " top=" + top : "") + (step != null ? " step=" + step : "");
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
		result = prime * result + ((top == null) ? 0 : top.hashCode());
		result = prime * result + ((bottom == null) ? 0 : bottom.hashCode());
		result = prime * result + ((step == null) ? 0 : step.hashCode());
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

		if (top == null) {
			if (other.top != null)
				return false;
		} else if (!top.equals(other.top))
			return false;

		if (bottom == null) {
			if (other.bottom != null)
				return false;
		} else if (!bottom.equals(other.bottom))
			return false;

		if (step == null) {
			if (other.step != null)
				return false;
		} else if (!step.equals(other.step))
			return false;

		return true;
	}
}
