package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.jknack.handlebars.Context;

import x.mvmn.gp2srv.web.service.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public abstract class AbstractErrorHandlingServlet extends HttpServletWithTemplates {

	private static final long serialVersionUID = 8638499002251355635L;

	protected final Logger logger;

	public AbstractErrorHandlingServlet(final Provider<TemplateEngine<Context>> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider);
		this.logger = logger;
	}

	protected void returnForbidden(final HttpServletRequest request, final HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		serveGenericErrorPage(request, response, HttpServletResponse.SC_FORBIDDEN, "forbidden");
	}

	protected void returnInternalError(final HttpServletRequest request, final HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		serveGenericErrorPage(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "internal server error");
	}

	protected void returnNotFound(final HttpServletRequest request, final HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		serveGenericErrorPage(request, response, HttpServletResponse.SC_NOT_FOUND, "not found");
	}

	public void serveGenericErrorPage(final HttpServletRequest request, final HttpServletResponse response, final int errorCode, String errorMessage) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("request", request);
		context.put("response", response);
		try {
			Writer writer = response.getWriter();
			serveGenericErrorPage(context, writer, errorCode, errorMessage);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public void serveGenericErrorPage(final HttpServletRequest request, final Writer writer, final int errorCode, String errorMessage) {
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("request", request);
		serveGenericErrorPage(context, writer, errorCode, errorMessage);
	}

	protected void serveGenericErrorPage(Map<String, Object> context, final Writer writer, final int errorCode, String errorMessage) {
		if (errorMessage == null && errorCode == 404) {
			errorMessage = "not found";
		}
		if (errorMessage == null) {
			errorMessage = "";
		}
		context.put("errorCode", errorCode);
		context.put("errorMessage", errorMessage.split("[\r\n]"));
		try {
			getTemplateEngine().renderTemplate("error", "UTF-8", context, writer);
			writer.flush();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
