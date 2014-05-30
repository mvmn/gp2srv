package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.lang.util.Provider;

public class StaticsResourcesServlet extends HttpServletWithTemplates {
	private static final long serialVersionUID = 898038473129345743L;

	public static final String STATIC_RESOURCES_CLASSPATH_PREFIX = "/x/mvmn/gp2srv/web/static/";

	final Provider<TemplateEngine> templateEngineProvider;

	public StaticsResourcesServlet(final Provider<TemplateEngine> templateEngineProvider) {
		super(templateEngineProvider);
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
			// Do 500
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void returnNotFound(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		try {
			serveTempalteUTF8("notfound.vm", request, response);
		} catch (IOException e) {
			// TODO: log failure
			e.printStackTrace();
		}
	}
}
