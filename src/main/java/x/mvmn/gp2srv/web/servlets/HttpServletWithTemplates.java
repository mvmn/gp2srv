package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.github.jknack.handlebars.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import x.mvmn.gp2srv.web.service.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public abstract class HttpServletWithTemplates extends HttpServlet {

	private static final long serialVersionUID = -6614624652923805723L;
	protected final Provider<TemplateEngine<Context>> templateEngineProvider;
	protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public HttpServletWithTemplates(final Provider<TemplateEngine<Context>> templateEngineProvider) {
		this.templateEngineProvider = templateEngineProvider;
	}

	public TemplateEngine<Context> getTemplateEngine() {
		return this.templateEngineProvider.provide();
	}

	public Map<String, Object> createContext(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> result = new HashMap<>();
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

	public void serveTempalteUTF8Safely(final String tempalteName, final Map<String, Object> context, final HttpServletResponse response, final Logger logger) {
		try {
			serveTempalteUTF8(tempalteName, context, response);
		} catch (Exception e) {
			logger.error("Error rendering template " + tempalteName, e);
		}
	}

	public void serveTempalteUTF8(final String tempalteName, final Map<String, Object> context, final HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		Writer writer = response.getWriter();
		getTemplateEngine().renderTemplate(tempalteName, "UTF-8", context, writer);
		writer.flush();
		IOUtils.closeQuietly(writer);
	}

	public void serveStrContentUTF8(final String contentType, final String content, final HttpServletResponse response) throws IOException {
		response.setContentType(contentType);
		response.setCharacterEncoding("UTF-8");
		Writer writer = response.getWriter();
		writer.write(content);
		writer.flush();
		IOUtils.closeQuietly(writer);
	}

	public void serveWithJsonSerialization(final HttpServletResponse response, final Object object) throws IOException {
		serveStrContentUTF8("application/json", gson.toJson(object), response);
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

	public void serveTempalte(final String tempalteName, final String encoding, final Map<String, Object> context, final HttpServletResponse response)
			throws IOException {
		response.setContentType("text/html");
		Writer writer = new OutputStreamWriter(response.getOutputStream(), encoding);
		getTemplateEngine().renderTemplate(tempalteName, encoding, context, writer);
		writer.flush();
	}
}
