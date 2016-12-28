package x.mvmn.gp2srv.web.servlets;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import x.mvmn.gp2srv.scripting.model.ScriptStep;
import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl;
import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl.ScriptExecution;
import x.mvmn.gp2srv.scripting.service.impl.ScriptsManagementServiceImpl;
import x.mvmn.gp2srv.web.CameraService;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class ScriptingServlet extends AbstractGP2Servlet {
	private static final long serialVersionUID = -8824710026933050754L;
	protected static final Gson GSON = new GsonBuilder().create();

	protected final ScriptsManagementServiceImpl scriptManagementService;
	protected final ScriptExecutionServiceImpl scriptExecService;
	protected final ScriptExecWebSocketNotifier scriptExecWebSocketNotifier;
	protected final CameraService cameraService;
	protected final AtomicBoolean scriptDumpVars;

	public ScriptingServlet(final CameraService cameraService, ScriptsManagementServiceImpl scriptManagementService,
			ScriptExecutionServiceImpl scriptExecService, ScriptExecWebSocketNotifier scriptExecWebSocketNotifier, AtomicBoolean scriptDumpVars,
			VelocityContextService velocityContextService, Provider<TemplateEngine> templateEngineProvider, Logger logger) {
		super(velocityContextService, templateEngineProvider, logger);
		this.cameraService = cameraService;
		this.scriptManagementService = scriptManagementService;
		this.scriptExecService = scriptExecService;
		this.scriptExecWebSocketNotifier = scriptExecWebSocketNotifier;
		this.scriptDumpVars = scriptDumpVars;
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		final String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
		try {
			if ("/scripts/list".equals(path)) {
				serveJson(scriptManagementService.listScriptFiles(), response);
			} else if ("/scripts/get".equals(path)) {
				serveJson(scriptManagementService.load(request.getParameter("name")), response);
			} else if ("/scripts/exec/current".equals(path)) {
				ScriptExecution currentExecution = scriptExecService.getCurrentExecution();
				Map<String, Object> result = new HashMap<String, Object>();
				if (currentExecution != null) {
					if (scriptDumpVars.get()) {
						result.putAll(currentExecution.dumpVariables(true));
					}
					result.put("__scriptName", currentExecution.getScriptName());
					result.put("__currentStep", currentExecution.getCurrentStep());
					result.put("__latestError", currentExecution.getLatestError());
					result.put("__errors", currentExecution.getErrors());
				}
				serveJson(result, response);
			} else if ("/scripts/exec/finished".equals(path)) {
				ScriptExecution finishedExecution = scriptExecService.getLatestFinishedExecution();
				Map<String, Object> result = new HashMap<String, Object>();
				if (finishedExecution != null) {
					if (scriptDumpVars.get()) {
						result.putAll(finishedExecution.dumpVariables(true));
					}
					result.put("__scriptName", finishedExecution.getScriptName());
					result.put("__currentStep", finishedExecution.getCurrentStep());
					result.put("__latestError", finishedExecution.getLatestError());
					result.put("__errors", finishedExecution.getErrors());
				}
				serveJson(result, response);
			}
		} catch (final Exception e) {
			logger.error("Error processing GET to " + path, e);
			serveGenericErrorPage(request, response, -1, e.getMessage());
		}
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		final String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
		try {
			if ("/scripts/put".equals(path)) {
				String scriptName = scriptManagementService.normalizeScriptName(request.getParameter("name"));

				ScriptStep[] script = GSON.fromJson(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8), ScriptStep[].class);
				serveJson(scriptManagementService.save(scriptName, Arrays.asList(script)), response);
			} else if ("/scripts/delete".equals(path)) {
				String scriptName = scriptManagementService.normalizeScriptName(request.getParameter("name"));
				serveJson(scriptManagementService.delete(scriptName), response);
			} else if ("/scripts/exec/dumpvars".equals(path)) {
				this.scriptDumpVars.set(Boolean.valueOf(request.getParameter("enable")));
			} else if ("/scripts/exec/stop".equals(path)) {
				boolean result = false;
				ScriptExecution currentExecution = scriptExecService.getCurrentExecution();
				if (currentExecution != null) {
					currentExecution.requestStop();
					result = true;
				}
				serveJson(result, response);
			} else if ("/scripts/exec/start".equals(path)) {
				ScriptExecution execution = null;
				final String scriptName = scriptManagementService.normalizeScriptName(request.getParameter("name"));
				List<ScriptStep> scriptContent = scriptManagementService.load(scriptName);
				String result;
				if (scriptContent != null) {
					execution = scriptExecService.execute(cameraService, scriptName, scriptContent, scriptExecWebSocketNotifier);
					if (execution != null) {
						result = "Script has been run";
					} else {
						result = "Another execution already in progress";
					}
				} else {
					result = "Script not found for name " + scriptName;
				}
				serveJson(result, response);
			}
		} catch (final Exception e) {
			logger.error("Error processing POST to " + path, e);
			serveGenericErrorPage(request, response, -1, e.getMessage());
		}
	}
}
