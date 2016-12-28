package x.mvmn.gp2srv.web.servlets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl.ScriptExecution;
import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl.ScriptExecutionObserver;
import x.mvmn.log.api.Logger;

public final class ScriptExecWebSocketNotifier implements ScriptExecutionObserver {

	private static final Gson GSON = new GsonBuilder().create();
	private final Logger logger;

	public ScriptExecWebSocketNotifier(final Logger logger) {
		this.logger = logger;
	}

	public void preStep(ScriptExecution execution) {
		try {
			send(execution, "preStep");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void postStep(ScriptExecution execution) {
		try {
			send(execution, "postStep");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void onStop(ScriptExecution execution) {
		try {
			send(execution, "onStop");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void onStart(ScriptExecution execution) {
		try {
			send(execution, "onStart");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	protected void send(ScriptExecution execution, String eventType) {
		for (Session session : ScriptExecutionReportingWebSocketServlet.WEB_SOCKET_SESSIONS) {
			try {
				if (session.isOpen()) {
					session.getRemote().sendString(toJson(execution, eventType));
				} else {
					ScriptExecutionReportingWebSocketServlet.WEB_SOCKET_SESSIONS.remove(session);
				}
			} catch (Exception e) {
				logger.error("WebSocket send failed", e);
			}
		}
	}

	protected String toJson(ScriptExecution execution, String eventType) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("stepNumber", execution.getCurrentStep());
		result.put("eventType", eventType);
		return GSON.toJson(result);
	}
}