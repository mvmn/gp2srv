package x.mvmn.lang.util;

import java.util.Calendar;

import org.apache.commons.lang.time.FastDateFormat;

public class DateHelper {
	private static final FastDateFormat FDF = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");

	public static String getDateSortFriendlyStr() {
		return FDF.format(Calendar.getInstance());
	}
}
