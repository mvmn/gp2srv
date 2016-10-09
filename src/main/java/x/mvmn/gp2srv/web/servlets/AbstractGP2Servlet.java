package x.mvmn.gp2srv.web.servlets;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.jknack.handlebars.Context;

import x.mvmn.gp2srv.web.service.TemplateEngine;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

/**
 * @author Mykola Makhin This type of servlets adds shared global context to template engine + inherits from {@link AbstractErrorHandlingServlet} error handling
 *         with error pages rendered from properly templates + {@link AbstractErrorHandlingServlet} itself inherits from {@link HttpServletWithTemplates} a
 *         template engine + methods for outputting render of templates.<br/>
 *         <br/>
 *         Thus we get templating in general, default error handing with proper templates and global context in templates.
 */
public class AbstractGP2Servlet extends AbstractErrorHandlingServlet {
	private static final long serialVersionUID = 7210482012835862732L;

	public AbstractGP2Servlet(final Provider<TemplateEngine<Context>> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
	}

	@Override
	public Map<String, Object> createContext(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("request", request);
		result.put("response", response);
		return result;
	}

	public Map<String, Object> createContext(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> values) {
		Map<String, Object> result = new HashMap<String, Object>(values);
		result.put("request", request);
		result.put("response", response);
		return result;
	}
}
