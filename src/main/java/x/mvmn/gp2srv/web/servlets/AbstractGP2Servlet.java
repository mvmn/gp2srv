package x.mvmn.gp2srv.web.servlets;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

/**
 * @author Mykola Makhin This type of servlets adds shared global context to template engine + inherits from {@link AbstractErrorHandlingServlet} error handling
 *         with error pages rendered from properly templates + {@link AbstractErrorHandlingServlet} itself inherits from {@link HttpServletWithTemplates} a
 *         template engine + methods for outputting render of templates.<br/>
 * <br/>
 *         Thus we get templating in general, default error handing with proper templates and global context in templates.
 */
public class AbstractGP2Servlet extends AbstractErrorHandlingServlet {
	private static final long serialVersionUID = 7210482012835862732L;

	protected VelocityContextService velocityContextService;

	public AbstractGP2Servlet(final VelocityContextService velocityContextService, final Provider<TemplateEngine> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
		this.velocityContextService = velocityContextService;
	}

	@Override
	public Context createContext(final HttpServletRequest request, final HttpServletResponse response) {
		Context result = new VelocityContext(velocityContextService.getGlobalContext());
		result.put("request", request);
		result.put("response", response);
		return result;
	}

	public Context createContext(final HttpServletRequest request, final HttpServletResponse response, final Map<String, Object> values) {
		Context result = new VelocityContext(values, velocityContextService.getGlobalContext());
		result.put("request", request);
		result.put("response", response);
		return result;
	}
}
