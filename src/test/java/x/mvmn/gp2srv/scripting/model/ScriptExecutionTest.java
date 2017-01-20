package x.mvmn.gp2srv.scripting.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.jexl3.JexlEngine;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.gp2srv.scripting.model.ScriptExecution.ScriptExecutionFinishListener;
import x.mvmn.gp2srv.scripting.model.ScriptStep.ScriptStepType;
import x.mvmn.gp2srv.scripting.service.impl.JexlMapContext;
import x.mvmn.gp2srv.scripting.service.impl.ScriptExecutionServiceImpl.ScriptExecutionObserver;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.lang.util.Tuple;

public class ScriptExecutionTest {

	@Test
	public void testExecution() {
		List<Tuple<Object, CameraConfigEntryBean, Map<String, Object>, Void, Void>> params = new ArrayList<Tuple<Object, CameraConfigEntryBean, Map<String, Object>, Void, Void>>();
		List<ScriptStep> steps = new ArrayList<ScriptStep>();
		CameraConfigEntryBean cceb = Mockito.mock(CameraConfigEntryBean.class);
		steps.add(createMockStep(ScriptStepType.VAR_SET, true, 1, null, params, -1));
		steps.add(createMockStep(ScriptStepType.CAMPROP_SET, true, 2, cceb, params, -1));
		steps.add(createMockStep(ScriptStepType.CAPTURE, false, null, null, params, -1));
		steps.add(createMockStep(ScriptStepType.STOP, true, null, null, params, 1));
		ScriptExecutionObserver seo = Mockito.mock(ScriptExecutionObserver.class);
		ScriptExecutionFinishListener fl = Mockito.mock(ScriptExecutionFinishListener.class);
		ScriptExecution execution = new ScriptExecution(null, null, "mock", steps, null, seo, fl);
		execution.run();
		Assert.assertEquals(6, params.size());
		Assert.assertEquals(1, params.get(0).getA());
		Assert.assertEquals(2, params.get(1).getA());
		Assert.assertEquals(cceb, params.get(1).getB());
		Assert.assertEquals(1, params.get(3).getA());
		Assert.assertEquals(2, params.get(4).getA());
		Assert.assertEquals(cceb, params.get(4).getB());
		Assert.assertNull(params.get(2).getA());
		Assert.assertNull(params.get(5).getA());
		Assert.assertEquals(0, params.get(0).getC().get("___currentStep"));
		Assert.assertEquals(1, params.get(1).getC().get("___currentStep"));
		Assert.assertEquals(3, params.get(2).getC().get("___currentStep"));
		Assert.assertEquals(0, params.get(3).getC().get("___currentStep"));
		Assert.assertEquals(1, params.get(4).getC().get("___currentStep"));
		Assert.assertEquals(3, params.get(5).getC().get("___currentStep"));
		for (Tuple<Object, CameraConfigEntryBean, Map<String, Object>, Void, Void> p : params.subList(0, 3)) {
			Assert.assertEquals(0L, p.getC().get("___loopCount"));
			Assert.assertEquals(true, p.getC().get("___evaldCondition"));
		}
		for (Tuple<Object, CameraConfigEntryBean, Map<String, Object>, Void, Void> p : params.subList(3, 6)) {
			Assert.assertEquals(1L, p.getC().get("___loopCount"));
			Assert.assertEquals(true, p.getC().get("___evaldCondition"));
		}
	}

	protected ScriptStep createMockStep(ScriptStepType type, final boolean conditionEvalResult, final Object expressionEvalResult,
			final CameraConfigEntryBean cceb, final Collection<Tuple<Object, CameraConfigEntryBean, Map<String, Object>, Void, Void>> paramsCollector,
			final long stopAtLoop) {
		ScriptStep result = new ScriptStep(type, null, null, null) {

			@Override
			public CameraConfigEntryBean getConfigEntryForEval(CameraService cameraService) {
				return cceb;
			}

			@Override
			public boolean evalCondition(JexlEngine engine, JexlMapContext context) {
				return conditionEvalResult;
			}

			@Override
			public Object evalExpression(JexlEngine engine, JexlMapContext context, CameraConfigEntryBean configEntryForEval) {
				return expressionEvalResult;
			}

			@Override
			public boolean execute(CameraService cameraService, Object evaluatedValue, JexlEngine engine, JexlMapContext context,
					CameraConfigEntryBean configEntry) {
				paramsCollector.add(
						new Tuple<Object, CameraConfigEntryBean, Map<String, Object>, Void, Void>(evaluatedValue, configEntry, context.toMap(), null, null));
				return ((Long) context.get("___loopCount")).longValue() == stopAtLoop;
			}
		};

		return result;
	}
}
