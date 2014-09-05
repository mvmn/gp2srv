package x.mvmn.gp2srv.web.servlets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public class MimeTypesHelper {

	private static final Map<String, String> EXTENSIONS_TO_MIME_TYPES;

	static {
		Map<String, String> extensionsToMimeTypes = new HashMap<String, String>();
		extensionsToMimeTypes.put("jpg", "image/jpeg");
		extensionsToMimeTypes.put("jpeg", "image/jpeg");
		extensionsToMimeTypes.put("png", "image/png");
		extensionsToMimeTypes.put("gif", "image/gif");

		extensionsToMimeTypes.put("js", "application/javascript");
		extensionsToMimeTypes.put("css", "text/css");
		extensionsToMimeTypes.put("html", "text/html");

		EXTENSIONS_TO_MIME_TYPES = Collections.unmodifiableMap(extensionsToMimeTypes);
	}

	public static String getMimeTypeForExtension(String extension) {
		return EXTENSIONS_TO_MIME_TYPES.get(extension);
	}

	public static void setContentType(HttpServletResponse response, String resourceName) {
		if (resourceName != null) {
			String extension;
			int indexOfDot = resourceName.lastIndexOf('.');
			if (indexOfDot >= 0) {
				extension = resourceName.substring(indexOfDot).trim();
			} else {
				extension = resourceName;
			}
			String mimeType = getMimeTypeForExtension(extension);
			if (mimeType != null) {
				response.setContentType(mimeType);
			}
		}
	}

}
