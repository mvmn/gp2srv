package x.mvmn.gp2srv.web.service.velocity;

import org.apache.velocity.context.Context;

import x.mvmn.lang.util.DateHelper;

public class StaticToolsHelper {

	private static final Class<?> TOOLS[] = new Class<?>[] { DateHelper.class };

	public static void populateTools(final Context context) {
		for (final Class<?> toolClass : TOOLS) {
			context.put("tool" + toolClass.getSimpleName(), toolClass);
		}
	}
}
