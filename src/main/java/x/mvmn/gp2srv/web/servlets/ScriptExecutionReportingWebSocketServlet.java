package x.mvmn.gp2srv.web.servlets;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl;
import x.mvmn.log.api.Logger;

public class ScriptExecutionReportingWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = -373710150672137029L;

	protected static final ConcurrentHashSet<Session> WEB_SOCKET_SESSIONS = new ConcurrentHashSet<Session>();

	public static void registerSession(Session session) {
		WEB_SOCKET_SESSIONS.add(session);
	}

	public static void deregisterSession(Session session) {
		WEB_SOCKET_SESSIONS.remove(session);
	}

	private final Logger logger;

	public ScriptExecutionReportingWebSocketServlet(ScriptExecutionServiceImpl scriptExecService, Logger logger) {
		this.logger = logger;
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(new WebSocketCreator() {
			public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
				return new ScriptExecutionReportingWebSocket(logger);
			}
		});
	}
}
