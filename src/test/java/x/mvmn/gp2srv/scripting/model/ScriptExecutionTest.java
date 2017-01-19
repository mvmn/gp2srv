package x.mvmn.gp2srv.scripting.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.gp2srv.scripting.model.ScriptExecution.ScriptExecutionFinishListener;
import x.mvmn.gp2srv.scripting.model.ScriptStep.ScriptStepType;
import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl.ScriptExecutionObserver;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.lang.util.ImmutablePair;

public class ScriptExecutionTest {

	@Test
	public void testExecution() {
		List<ImmutablePair<Object, CameraConfigEntryBean>> params = new ArrayList<ImmutablePair<Object, CameraConfigEntryBean>>();
		List<ScriptStep> steps = new ArrayList<ScriptStep>();
		steps.add(createMockStep(ScriptStepType.VAR_SET, true, 1, params, false));
		steps.add(createMockStep(ScriptStepType.STOP, true, null, params, true));
		ScriptExecutionObserver seo = Mockito.mock(ScriptExecutionObserver.class);
		ScriptExecutionFinishListener fl = Mockito.mock(ScriptExecutionFinishListener.class);
		ScriptExecution execution = new ScriptExecution(null, null, "mock", steps, null, seo, fl);
		execution.run();
		Assert.assertEquals(2, params.size());
	}

	protected ScriptStep createMockStep(ScriptStepType type, final boolean conditionEvalResult, final Object expressionEvalResult,
			final Collection<ImmutablePair<Object, CameraConfigEntryBean>> paramsCollector, final boolean stop) {
		ScriptStep result = new ScriptStep(type, null, null, null) {

			@Override
			public boolean evalCondition(JexlEngine engine, JexlContext context) {
				return conditionEvalResult;
			}

			@Override
			public Object evalExpression(JexlEngine engine, JexlContext context, CameraConfigEntryBean configEntryForEval) {
				return expressionEvalResult;
			}

			@Override
			public boolean execute(CameraService cameraService, Object evaluatedValue, JexlEngine engine, JexlContext context,
					CameraConfigEntryBean configEntry) {
				paramsCollector.add(new ImmutablePair<Object, CameraConfigEntryBean>(evaluatedValue, configEntry));
				return stop;
			}
		};

		return result;
	}
}
