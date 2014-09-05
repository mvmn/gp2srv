package x.mvmn.gp2srv.web.servlets;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class StaticsResourcesServlet extends AbstractErrorHandlingServlet {
	private static final long serialVersionUID = 898038473129345743L;

	public static final String STATIC_RESOURCES_CLASSPATH_PREFIX = "/x/mvmn/gp2srv/web/static";

	final Provider<TemplateEngine> templateEngineProvider;

	public StaticsResourcesServlet(final Provider<TemplateEngine> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
		this.templateEngineProvider = templateEngineProvider;
	}

	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		try {
			final String path = request.getPathInfo().replaceAll("\\.\\./", "");
			final InputStream resourceInputStream = GPhoto2Server.class.getResourceAsStream(STATIC_RESOURCES_CLASSPATH_PREFIX + path);
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
