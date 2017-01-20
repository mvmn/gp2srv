package x.mvmn.gp2srv.scripting.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlInfo;
import org.apache.commons.jexl3.JexlScript;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.gp2srv.mock.service.impl.MockCameraServiceImpl;
import x.mvmn.gp2srv.scripting.model.ScriptStep.ScriptStepType;
import x.mvmn.gp2srv.scripting.service.impl.JexlMapContext;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean.CameraConfigEntryType;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;

public class ScriptStepTest {

	MockCameraServiceImpl mockCameraService = new MockCameraServiceImpl();
	JexlMapContext jexlContext = new JexlMapContext();

	@Before
	public void cleanup() {
		jexlContext.clear();
	}

	@Test
	public void testEval() {
		Assert.assertTrue(new ScriptStep(ScriptStepType.STOP, null, null, "true").evalCondition(new JexlBuilder().create(), jexlContext));
		Assert.assertFalse(new ScriptStep(ScriptStepType.STOP, null, null, "false").evalCondition(new JexlBuilder().create(), jexlContext));
		CameraService cameraService = Mockito.mock(CameraService.class);
		CameraConfigEntryBean cceb = new CameraConfigEntryBean(0, "prop", "prop", "", CameraConfigEntryType.TEXT, null, null, "val", null, null);
		Map<String, CameraConfigEntryBean> ccebMap = new HashMap<String, CameraConfigEntryBean>();
		ccebMap.put("prop", cceb);
		Mockito.when(cameraService.getConfigAsMap()).thenReturn(ccebMap);
		ScriptStep unit = new ScriptStep(ScriptStepType.CAMPROP_SET, "prop", "2+2", "true");
		Assert.assertEquals(cceb, unit.getConfigEntryForEval(cameraService));
		jexlContext.clear();
		Assert.assertEquals(4, unit.evalExpression(new JexlBuilder().create(), jexlContext, cceb));
		Assert.assertEquals(cceb, jexlContext.get("__camprop"));
	}

	@Test
	public void testWaitForCamEvent() {
		{
			CameraService mockCameraService = Mockito.mock(CameraService.class);
			ArgumentCaptor<GP2CameraEventType> captor = ArgumentCaptor.forClass(GP2CameraEventType.class);
			Mockito.when(mockCameraService.waitForSpecificEvent(Mockito.eq(1000), captor.capture())).thenReturn(null);
			ScriptStep scriptStep = new ScriptStep(ScriptStepType.CAMEVENT_WAIT, "" + GP2CameraEventType.CAPTURE_COMPLETE.getCode(), null, null);
			Assert.assertFalse(scriptStep.execute(mockCameraService, "1000", null, null, null));
			Assert.assertEquals(GP2CameraEventType.CAPTURE_COMPLETE, captor.getValue());
		}
		{
			CameraService mockCameraService = Mockito.mock(CameraService.class);
			final AtomicBoolean calledWith1000 = new AtomicBoolean(false);
			Mockito.when(mockCameraService.waitForEvent(Mockito.eq(1000))).then(new Answer<GP2CameraEventType>() {
				public GP2CameraEventType answer(InvocationOnMock invocation) throws Throwable {
					calledWith1000.set(true);
					return null;
				}
			});
			ScriptStep scriptStep = new ScriptStep(ScriptStepType.CAMEVENT_WAIT, null, null, null);
			Assert.assertFalse(scriptStep.execute(mockCameraService, "1000", null, null, null));
			Assert.assertTrue(calledWith1000.get());
		}
	}

	@Test
	public void testSetCamProperty() {
		CameraService mockCameraService = Mockito.mock(CameraService.class);

		ScriptStep scriptStep = new ScriptStep(ScriptStepType.CAMPROP_SET, null, null, null);
		ArgumentCaptor<CameraConfigEntryBean> captor = ArgumentCaptor.forClass(CameraConfigEntryBean.class);
		Mockito.when(mockCameraService.setConfig(captor.capture())).thenReturn(mockCameraService);
		CameraConfigEntryBean cceb = new CameraConfigEntryBean(123, "path", "label", "info", CameraConfigEntryType.TEXT, null, null, "oldVal", null, null);
		Assert.assertFalse(scriptStep.execute(mockCameraService, "newVal", null, null, cceb));
		Assert.assertEquals(1, captor.getAllValues().size());
		Assert.assertEquals(cceb.cloneWithNewValue("newVal"), captor.getValue());
	}

	@Test
	public void testCapture() {
		ScriptStep scriptStep = new ScriptStep(ScriptStepType.CAPTURE, null, null, null);

		CameraService mockCameraService = Mockito.mock(CameraService.class);
		Mockito.when(mockCameraService.capture()).thenReturn(new CameraFileSystemEntryBean("name", "/path", false));
		Assert.assertFalse(scriptStep.execute(mockCameraService, null, null, jexlContext, null));
		Mockito.verify(mockCameraService).capture();
		Assert.assertEquals("/path/name", jexlContext.get("__capturedFile"));
	}

	@Test
	public void testSetVar() {
		ScriptStep scriptStep = new ScriptStep(ScriptStepType.VAR_SET, "varx", null, null);
		Assert.assertFalse(scriptStep.execute(null, 12345, null, jexlContext, null));
		Object varx = jexlContext.get("varx");
		Assert.assertNotNull(varx);
		Assert.assertEquals(12345, varx);
	}

	@Test
	public void testStop() {
		ScriptStep scriptStep = new ScriptStep(ScriptStepType.STOP, null, null, null);
		Assert.assertTrue(scriptStep.execute(null, null, null, null, null));
	}

	@Test
	public void testDelay() {
		ScriptStep scriptStep = new ScriptStep(ScriptStepType.DELAY, null, null, null);
		long t1 = System.currentTimeMillis();
		Assert.assertFalse(scriptStep.execute(null, "1000", null, null, null));
		long t2 = System.currentTimeMillis();
		Assert.assertTrue(t2 - t1 >= 1000);
	}

	@Test
	public void testExecScript() {
		ScriptStep scriptStep = new ScriptStep(ScriptStepType.EXEC_SCRIPT, null, "x=2+2", null);
		JexlEngine mockEngine = Mockito.mock(JexlEngine.class);
		JexlScript jexlScript = Mockito.mock(JexlScript.class);
		Mockito.when(mockEngine.createScript(Mockito.any(JexlInfo.class), Mockito.anyString(), Mockito.any(String[].class))).thenReturn(jexlScript);
		Assert.assertFalse(scriptStep.execute(null, 12345, mockEngine, jexlContext, null));
		Mockito.verify(mockEngine).createScript(null, "x=2+2", null);
		Mockito.verify(jexlScript).execute(jexlContext);
	}
}
