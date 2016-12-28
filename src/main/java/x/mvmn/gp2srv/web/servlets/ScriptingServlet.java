package x.mvmn.gp2srv.web.servlets;

import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class ScriptingServlet extends AbstractGP2Servlet {
	private static final long serialVersionUID = -8824710026933050754L;

	public ScriptingServlet(ScriptExecutionServiceImpl scriptExecService, ScriptExecWebSocketNotifier scriptExecWebSocketNotifier,
			VelocityContextService velocityContextService, Provider<TemplateEngine> templateEngineProvider, Logger logger) {
		super(velocityContextService, templateEngineProvider, logger);
	}
}
