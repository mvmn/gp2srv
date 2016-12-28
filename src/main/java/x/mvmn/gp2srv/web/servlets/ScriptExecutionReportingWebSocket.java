package x.mvmn.gp2srv.web.servlets;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

import x.mvmn.log.api.Logger;
import x.mvmn.log.api.Logger.LogLevel;

public class ScriptExecutionReportingWebSocket implements WebSocketListener {
	private Session outbound;

	private final Logger logger;

	public ScriptExecutionReportingWebSocket(Logger logger) {
		this.logger = logger;
	}

	public void onWebSocketClose(int statusCode, String reason) {
		ScriptExecutionReportingWebSocketServlet.deregisterSession(outbound);
		this.outbound = null;
	}

	public void onWebSocketConnect(Session session) {
		this.outbound = session;
		ScriptExecutionReportingWebSocketServlet.registerSession(session);
	}

	public void onWebSocketError(Throwable cause) {
		logger.log(LogLevel.ERROR, "WebSocket error occurred", cause);
	}

	public void onWebSocketText(String message) {
		// Do nothing
	}

	public void onWebSocketBinary(byte[] payload, int offset, int len) {
		// Do nothing
	}
}