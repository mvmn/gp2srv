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

	public static final String STATIC_RESOURCES_CLASSPATH_PREFIX = "/x/mvmn/gp2srv/web/static/";

	final Provider<TemplateEngine> templateEngineProvider;

	public StaticsResourcesServlet(final Provider<TemplateEngine> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
		this.templateEngineProvider = templateEngineProvider;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			String path = request.getPathInfo().replaceAll("\\.\\./", "");
			InputStream resourceInputStream = GPhoto2Server.class.getResourceAsStream(STATIC_RESOURCES_CLASSPATH_PREFIX + path);
			if (resourceInputStream == null) {
				returnNotFound(request, response);
			} else {
				IOUtils.copy(resourceInputStream, response.getOutputStream());
			}
		} catch (Exception e) {
			returnInternalError(request, response);
		}
	}
}
