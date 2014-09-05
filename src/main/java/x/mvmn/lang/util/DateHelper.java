package x.mvmn.lang.util;

import java.util.Calendar;

public class DateHelper {
	public static String getDateSortFriendlyStr() {
		Calendar calendar = Calendar.getInstance();
		StringBuilder dateStrBuilder = new StringBuilder();

		dateStrBuilder.append(String.format("%04d", calendar.get(Calendar.YEAR))).append("-");
		dateStrBuilder.append(String.format("%02d", calendar.get(Calendar.MONTH) + 1)).append("-");
		dateStrBuilder.append(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))).append(" ");

		dateStrBuilder.append(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY))).append(":");
		dateStrBuilder.append(String.format("%02d", calendar.get(Calendar.MINUTE))).append(":");
		dateStrBuilder.append(String.format("%02d", calendar.get(Calendar.SECOND))).append(".");
		dateStrBuilder.append(String.format("%03d", calendar.get(Calendar.MILLISECOND)));

		return dateStrBuilder.toString();
	}
}
