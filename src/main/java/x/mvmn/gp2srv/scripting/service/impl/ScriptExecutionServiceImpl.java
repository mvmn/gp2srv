package x.mvmn.gp2srv.scripting.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.gp2srv.scripting.model.ScriptExecution;
import x.mvmn.gp2srv.scripting.model.ScriptStep;
import x.mvmn.log.api.Logger;

public class ScriptExecutionServiceImpl implements Function<ScriptExecution, Void> {

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

	public ScriptExecution execute(final CameraService cameraService, final Logger logger, final String scriptName, final List<ScriptStep> script,
			final ScriptExecutionObserver scriptExecutionObserver) {
		final ScriptExecution scriptExecution = new ScriptExecution(cameraService, logger, scriptName, script, engine, scriptExecutionObserver, this);
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

	public Void apply(ScriptExecution execution) {
		currentExecution.set(null);
		latestFinishedExecution.set(execution);
		return null;
	}
}
