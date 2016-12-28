package x.mvmn.gp2srv.scripting.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;

import x.mvmn.gp2srv.scripting.model.ScriptStep;
import x.mvmn.gp2srv.web.CameraService;
import x.mvmn.log.api.Logger;

public class ScriptExecutionServiceImpl {

	protected AtomicReference<ScriptExecution> currentExecution = new AtomicReference<ScriptExecution>();
	protected AtomicReference<ScriptExecution> latestFinishedExecution = new AtomicReference<ScriptExecution>();
	protected final JexlEngine engine = new JexlBuilder().create();

	public static interface ScriptExecutionObserver {
		public void onStart(ScriptExecution execution);

		public void preStep(ScriptExecution execution);

		public void postStep(ScriptExecution execution);

		public void onStop(ScriptExecution execution);
	}

	public ScriptExecutionServiceImpl(Logger logger) {
	}

	public ScriptExecution execute(final CameraService cameraService, final String scriptName, final List<ScriptStep> script,
			final ScriptExecutionObserver scriptExecutionObserver) {
		final ScriptExecution scriptExecution = new ScriptExecution(cameraService, scriptName, script, engine, scriptExecutionObserver);
		if (currentExecution.compareAndSet(null, scriptExecution)) {
			new Thread(scriptExecution).start();
			return scriptExecution;
		} else {
			return null;
		}
	}

	public ScriptExecution getCurrentExecution() {
		return currentExecution.get();
	}

	public ScriptExecution getLatestFinishedExecution() {
		return latestFinishedExecution.get();
	}

	public class JexlMapContext extends MapContext {

		protected final Map<String, Object> vars;

		public JexlMapContext() {
			this(new HashMap<String, Object>());
		}

		protected JexlMapContext(Map<String, Object> vars) {
			super(vars);
			this.vars = vars;
		}

		public Set<String> variableNames() {
			return Collections.unmodifiableSet(vars.keySet());
		}
	}

	public class ScriptExecution implements Runnable {

		protected final ScriptStep[] steps;
		protected final List<String> errors = new ArrayList<String>();
		protected volatile String latestError;
		protected final JexlEngine engine;
		protected final JexlMapContext context;
		protected volatile boolean stopRequest = false;
		protected final ScriptExecutionObserver scriptExecutionObserver;
		protected volatile int currentStep = 0;
		protected volatile long totalStepsPassed = 0;
		protected final String scriptName;
		protected final CameraService cameraService;

		public ScriptExecution(final CameraService cameraService, final String scriptName, final List<ScriptStep> steps, final JexlEngine engine,
				final ScriptExecutionObserver scriptExecutionObserver) {
			this.steps = steps.toArray(new ScriptStep[steps.size()]);
			this.engine = engine;
			this.context = new JexlMapContext();
			this.scriptExecutionObserver = scriptExecutionObserver;
			this.scriptName = scriptName;
			this.cameraService = cameraService;
		}

		public Set<String> getVariables() {
			return context.variableNames();
		}

		public Object getVariableValue(String variableName) {
			return context.get(variableName);
		}

		public Map<String, Object> dumpVariables(boolean asStrings) {
			Map<String, Object> result = new HashMap<String, Object>();

			for (String varName : getVariables()) {
				Object value = getVariableValue(varName);
				if (asStrings) {
					value = value != null ? value.toString() : null;
				}
				result.put(varName, value);
			}

			return result;
		}

		public synchronized void nextStep() {
			ScriptStep currentStepObj = null;
			int stepNum = 0;
			try {
				if (currentStep >= steps.length) {
					currentStep = 0;
				}
				stepNum = currentStep++;
				currentStepObj = steps[stepNum];
				context.set("__currentTimeMillis", System.currentTimeMillis());
				context.set("__step", stepNum);
				context.set("__totalStepsPassed", totalStepsPassed);
				currentStepObj.execute(cameraService, engine, context);
				totalStepsPassed++;
			} catch (Exception e) {
				latestError = "Error on step #" + stepNum + " " + currentStepObj + ": " + (e.getClass().getName() + " " + e.getMessage()).trim();
				errors.add(latestError);
				requestStop();
			}
		}

		public void requestStop() {
			this.stopRequest = true;
		}

		public int getCurrentStep() {
			return currentStep;
		}

		public void run() {
			scriptExecutionObserver.onStart(this);
			while (!stopRequest) {
				scriptExecutionObserver.preStep(this);
				nextStep();
				scriptExecutionObserver.preStep(this);
			}
			ScriptExecutionServiceImpl.this.currentExecution.set(null);
			ScriptExecutionServiceImpl.this.latestFinishedExecution.set(this);
			scriptExecutionObserver.onStop(this);
		}

		public String getScriptName() {
			return scriptName;
		}

		public List<String> getErrors() {
			return errors;
		}

		public String getLatestError() {
			return latestError;
		}
	}
}
