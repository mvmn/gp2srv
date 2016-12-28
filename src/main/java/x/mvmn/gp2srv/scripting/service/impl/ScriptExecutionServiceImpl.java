package x.mvmn.gp2srv.scripting.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.MapContext;

import x.mvmn.gp2srv.scripting.model.ScriptStep;

public class ScriptExecutionServiceImpl {

	protected AtomicReference<ScriptExecution> currentExecution = new AtomicReference<ScriptExecution>();
	protected final JexlEngine engine = new JexlBuilder().create();

	public static interface ScriptExecutionObserver {
		public void onStart(ScriptExecution execution);

		public void preStep(ScriptExecution execution);

		public void postStep(ScriptExecution execution);

		public void onStop(ScriptExecution execution);
	}

	public ScriptExecution execute(List<ScriptStep> script, ScriptExecutionObserver scriptExecutionObserver) {
		final ScriptExecution scriptExecution = new ScriptExecution(script, engine, scriptExecutionObserver);
		if (currentExecution.compareAndSet(null, scriptExecution)) {
			new Thread(scriptExecution);
			return scriptExecution;
		} else {
			return null;
		}
	}

	public ScriptExecution getCurrentExecution() {
		return currentExecution.get();
	}

	public class ScriptExecution implements Runnable {

		protected final ScriptStep[] steps;
		protected final List<String> errors = new ArrayList<String>();
		protected final JexlEngine engine;
		protected final JexlContext context;
		protected int currentStep = 0;
		protected volatile boolean stopRequest = false;
		protected final ScriptExecutionObserver scriptExecutionObserver;

		public ScriptExecution(final List<ScriptStep> steps, final JexlEngine engine, final ScriptExecutionObserver scriptExecutionObserver) {
			this.steps = steps.toArray(new ScriptStep[steps.size()]);
			this.engine = engine;
			this.context = new MapContext();
			this.scriptExecutionObserver = scriptExecutionObserver;
		}

		public synchronized void nextStep() {
			ScriptStep currentStepObj = null;
			try {
				if (currentStep > steps.length) {
					currentStep = 0;
				}
				currentStepObj = steps[currentStep++];

			} catch (Exception e) {
				errors.add("Error on step #" + currentStep + " " + currentStepObj + ": " + (e.getClass().getName() + " " + e.getMessage()).trim());
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
			scriptExecutionObserver.onStop(this);
		}
	}
}
