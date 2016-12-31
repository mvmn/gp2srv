package x.mvmn.gp2srv.scripting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.gp2srv.camera.service.impl.LightMeterImpl;
import x.mvmn.gp2srv.scripting.service.impl.JexlMapContext;
import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl.ScriptExecutionObserver;
import x.mvmn.log.api.Logger;

public class ScriptExecution implements Runnable {

	protected final ScriptStep[] steps;
	protected final List<String> errors = new ArrayList<String>();
	protected volatile String latestError;
	protected final JexlEngine engine;
	protected final JexlMapContext context;
	protected volatile boolean stopRequest = false;
	protected final ScriptExecutionObserver scriptExecutionObserver;
	protected final Function<ScriptExecution, Void> finishListener;
	protected volatile int nextStep = 0;
	protected volatile int currentStep = 0;
	protected volatile long loopCount = 0;
	protected volatile long totalStepsPassed = 0;
	protected final String scriptName;
	protected final CameraService cameraService;

	public ScriptExecution(final CameraService cameraService, final Logger logger, final String scriptName, final List<ScriptStep> steps,
			final JexlEngine engine, final ScriptExecutionObserver scriptExecutionObserver, final Function<ScriptExecution, Void> finishListener) {
		this.steps = steps.toArray(new ScriptStep[steps.size()]);
		this.engine = engine;
		this.context = new JexlMapContext();
		context.set("__lightmeter", new LightMeterImpl(cameraService, logger));
		this.scriptExecutionObserver = scriptExecutionObserver;
		this.finishListener = finishListener;
		this.scriptName = scriptName;
		this.cameraService = cameraService;
	}

	public ScriptStep[] getScriptSteps() {
		return steps;
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

	public synchronized void doStep(int stepNum) {
		ScriptStep currentStepObj = null;
		try {
			currentStepObj = steps[stepNum];
			currentStepObj.execute(cameraService, engine, context);
			totalStepsPassed++;
		} catch (JexlException e) {
			error("Evaluation error on step #" + stepNum + " " + currentStepObj + ": "
					+ ((e.getCause() != null ? e.getCause().getClass().getName() : "") + " " + e.getMessage()).trim());
		} catch (NumberFormatException e) {
			error("Number format error on step #" + stepNum + " " + currentStepObj + ": " + e.getMessage());
		} catch (Exception e) {
			error("Error on step #" + stepNum + " " + currentStepObj + ": " + (e.getClass().getName() + " " + e.getMessage()).trim());

			// TODO: optional
			// requestStop();
		}
	}

	protected void error(String message) {
		latestError = message;
		errors.add(message);
	}

	protected void populateStepData(int stepNumber) {
		context.set("__currentTimeMillis", System.currentTimeMillis());
		context.set("___currentStep", stepNumber);
		context.set("___totalStepsPassed", totalStepsPassed);
		context.set("___loopCount", loopCount);
	}

	protected int getAndAdvanceNextStepNumber() {
		int stepNum = 0;
		if (nextStep >= steps.length) {
			nextStep = 0;
			loopCount++;
		}
		stepNum = nextStep++;
		return stepNum;
	}

	public void requestStop() {
		this.stopRequest = true;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public long getLoopCount() {
		return loopCount;
	}

	public long getTotalStepsPassed() {
		return totalStepsPassed;
	}

	public void run() {
		scriptExecutionObserver.onStart(this);
		while (!stopRequest) {
			int stepNumber = getAndAdvanceNextStepNumber();
			currentStep = stepNumber;
			populateStepData(stepNumber);
			scriptExecutionObserver.preStep(this);
			doStep(stepNumber);
			scriptExecutionObserver.postStep(this);
		}
		finishListener.apply(this);
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