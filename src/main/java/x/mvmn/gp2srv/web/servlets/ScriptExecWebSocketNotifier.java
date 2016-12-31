package x.mvmn.gp2srv.web.servlets;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import x.mvmn.gp2srv.scripting.model.ScriptExecution;
import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl.ScriptExecutionObserver;
import x.mvmn.log.api.Logger;

public final class ScriptExecWebSocketNotifier implements ScriptExecutionObserver {

	private static final Gson GSON = new GsonBuilder().create();
	private final Logger logger;
	private final AtomicBoolean dumpVars;

	public ScriptExecWebSocketNotifier(final Logger logger, AtomicBoolean dumpVars) {
		this.logger = logger;
		this.dumpVars = dumpVars;
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
					session.getRemote().sendString(GSON.toJson(toExecutionInfoDTO(execution, "___", eventType, dumpVars.get(), false)));
				} else {
					ScriptExecutionReportingWebSocketServlet.WEB_SOCKET_SESSIONS.remove(session);
				}
			} catch (Exception e) {
				logger.error("WebSocket send failed", e);
			}
		}
	}

	public static Map<String, Object> toExecutionInfoDTO(final ScriptExecution execution, final String keyPrefix, final String eventType,
			final boolean dumpVariables, final boolean dumpAllErrors) {
		Map<String, Object> result = new TreeMap<String, Object>();
		if (dumpVariables) {
			result.putAll(execution.dumpVariables(true));
		}

		result.put(keyPrefix + "eventType", eventType);
		result.put(keyPrefix + "scriptName", execution.getScriptName());
		result.put(keyPrefix + "currentStep", execution.getCurrentStep());
		result.put(keyPrefix + "totalStepsPassed", execution.getTotalStepsPassed());
		result.put(keyPrefix + "loopCount", execution.getLoopCount());
		result.put(keyPrefix + "latestError", execution.getLatestError());
		result.put(keyPrefix + "stopOnError", execution.isStopOnError());
		result.put(keyPrefix + "afterStepDelay", execution.getAfterStepDelay());
		if (dumpAllErrors) {
			result.put(keyPrefix + "errors", execution.getErrors());
		}

		return result;
	}
}