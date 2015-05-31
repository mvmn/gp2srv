package x.mvmn.gp2srv.web.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import x.mvmn.gp2srv.web.MimeTypesHelper;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class ImagesServlet extends AbstractErrorHandlingServlet {

	private static final long serialVersionUID = -2923068775778250752L;

	// private final File imagesFolder;
	private final String imagesFolderCanonicalPath;

	public ImagesServlet(final Provider<TemplateEngine> templateEngineProvider, final File imagesFolder, Logger logger) throws IOException {
		super(templateEngineProvider, logger);

		if (!imagesFolder.exists()) {
			throw new RuntimeException("Images folder does not exist");
		}
		if (!imagesFolder.isDirectory()) {
			throw new RuntimeException("Images folder path doesn't point to a folder (must be a file)");
		}
		// this.imagesFolder = imagesFolder;
		imagesFolderCanonicalPath = imagesFolder.getCanonicalPath();
	}

	protected File processRequest(HttpServletRequest request, HttpServletResponse response) {
		File result;
		try {
			String path = request.getPathInfo();
			File targetFile = new File(imagesFolderCanonicalPath + path);
			String targetFileCanonicalPath = targetFile.getCanonicalPath();
			if (!targetFileCanonicalPath.startsWith(imagesFolderCanonicalPath)) {
				result = null;
				returnForbidden(request, response);
			} else if (!targetFile.exists()) {
				result = null;
				returnNotFound(request, response);
			} else {
				result = targetFile;
				response.setStatus(HttpServletResponse.SC_OK);
				MimeTypesHelper.setContentType(response, path);
			}
		} catch (Exception e) {
			result = null;
			logger.error(e);
			returnInternalError(request, response);
		}
		return result;
	}

	@Override
	public void doHead(final HttpServletRequest request, final HttpServletResponse response) {
		File file = processRequest(request, response);
		if (file != null && file.length() < Integer.MAX_VALUE) {
			response.setContentLength((int) file.length());
			// response.setContentLengthLong(file.length());
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		File result = processRequest(request, response);
		if (result != null) {
			try {
				serveFile(result, request, response);
			} catch (Exception e) {
				logger.error(e);
				returnInternalError(request, response);
			}
		}
	}

	private void serveFile(File result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		IOUtils.copy(new FileInputStream(result), response.getOutputStream());
	}
}
