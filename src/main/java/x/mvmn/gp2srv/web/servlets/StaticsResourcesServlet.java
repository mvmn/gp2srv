package x.mvmn.gp2srv.web.servlets;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.github.jknack.handlebars.Context;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.web.MimeTypesHelper;
import x.mvmn.gp2srv.web.service.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class StaticsResourcesServlet extends AbstractErrorHandlingServlet {
	private static final long serialVersionUID = 898038473129345743L;

	public StaticsResourcesServlet(final Provider<TemplateEngine<Context>> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
	}

	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		if (request.getPathInfo() == null) {
			returnNotFound(request, response);
		} else {
			try {
				final String path = request.getPathInfo().replaceAll("\\.\\./", "");
				final InputStream resourceInputStream = GPhoto2Server.class.getResourceAsStream(GPhoto2Server.STATIC_RESOURCES_CLASSPATH_PREFIX + path);
				if (resourceInputStream == null) {
					returnNotFound(request, response);
				} else {
					MimeTypesHelper.setContentType(response, path);
					IOUtils.copy(resourceInputStream, response.getOutputStream());
				}
			} catch (Exception e) {
				logger.error(e);
				returnInternalError(request, response);
			}
		}
	}
}
