package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;

import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public abstract class AbstractErrorHandlingServlet extends HttpServletWithTemplates {

	private static final long serialVersionUID = 8638499002251355635L;

	protected final Logger logger;

	public AbstractErrorHandlingServlet(Provider<TemplateEngine> templateEngineProvider, Logger logger) {
		super(templateEngineProvider);
		this.logger = logger;
	}

	protected void returnForbidden(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		serveTempalteUTF8Safely("forbidden.vm", request, response, logger);
	}

	protected void returnInternalError(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		serveTempalteUTF8Safely("internalerror.vm", request, response, logger);
	}

	protected void returnNotFound(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		serveTempalteUTF8Safely("notfound.vm", request, response, logger);
	}

	public void serveGenericErrorPage(HttpServletRequest request, Writer writer, int errorCode, String errorMessage) {
		VelocityContext context = new VelocityContext();
		if (errorMessage == null && errorCode == 404) {
			errorMessage = "not found";
		}
		context.put("request", request);
		context.put("errorCode", errorCode);
		context.put("errorMessage", errorMessage);
		try {
			getTemplateEngine().renderTemplate("error.vm", "UTF-8", context, writer);
			writer.flush();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
