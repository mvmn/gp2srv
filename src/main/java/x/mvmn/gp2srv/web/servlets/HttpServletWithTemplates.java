package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import com.google.gson.Gson;

import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public abstract class HttpServletWithTemplates extends HttpServlet {

	private static final long serialVersionUID = -6614624652923805723L;

	protected static final Gson GSON = new Gson();

	protected final Provider<TemplateEngine> templateEngineProvider;

	public HttpServletWithTemplates(final Provider<TemplateEngine> templateEngineProvider) {
		this.templateEngineProvider = templateEngineProvider;
	}

	public TemplateEngine getTemplateEngine() {
		return this.templateEngineProvider.provide();
	}

	public Context createContext(final HttpServletRequest request, final HttpServletResponse response) {
		Context result = new VelocityContext();
		result.put("request", request);
		result.put("response", response);
		return result;
	}

	public void serveTempalteUTF8Safely(final String tempalteName, final HttpServletRequest request, final HttpServletResponse response, final Logger logger) {
		try {
			serveTempalteUTF8(tempalteName, request, response);
		} catch (Exception e) {
			logger.error("Error rendering template " + tempalteName, e);
		}
	}

	public void serveTempalteUTF8(final String tempalteName, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		serveTempalteUTF8(tempalteName, createContext(request, response), response);
	}

	public void serveTempalteUTF8Safely(final String tempalteName, final Context context, final HttpServletResponse response, final Logger logger) {
		try {
			serveTempalteUTF8(tempalteName, context, response);
		} catch (Exception e) {
			logger.error("Error rendering template " + tempalteName, e);
		}
	}

	public void serveTempalteUTF8(final String tempalteName, final Context context, final HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		Writer writer = response.getWriter();
		getTemplateEngine().renderTemplate(tempalteName, "UTF-8", context, writer);
		writer.flush();
		IOUtils.closeQuietly(writer);
	}

	public void serveJson(final Object result, final HttpServletResponse response) throws IOException {
		serveStrContentUTF8("application/json", GSON.toJson(result), response);
	}

	public void serveStrContentUTF8(final String contentType, final String content, final HttpServletResponse response) throws IOException {
		response.setContentType(contentType);
		response.setCharacterEncoding("UTF-8");
		Writer writer = response.getWriter();
		writer.write(content);
		writer.flush();
		IOUtils.closeQuietly(writer);
	}

	public void serveTempalteSafely(final String tempalteName, final String encoding, final HttpServletRequest request, final HttpServletResponse response,
			final Logger logger) throws IOException {
		try {
			serveTempalte(tempalteName, encoding, request, response);
		} catch (Exception e) {
			logger.error("Error rendering template " + tempalteName, e);
		}
	}

	public void serveTempalte(final String tempalteName, final String encoding, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		serveTempalte(tempalteName, encoding, createContext(request, response), response);
	}

	public void serveTempalte(final String tempalteName, final String encoding, final Context context, final HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		Writer writer = new OutputStreamWriter(response.getOutputStream(), encoding);
		getTemplateEngine().renderTemplate(tempalteName, encoding, context, writer);
		writer.flush();
	}
}
