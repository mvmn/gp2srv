package x.mvmn.gp2srv.web.service.velocity;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.junit.Test;

import junit.framework.TestCase;
import x.mvmn.jlibgphoto2.api.CameraListItemBean;
import x.mvmn.jlibgphoto2.impl.CameraDetectorImpl;
import x.mvmn.jlibgphoto2.impl.GP2PortInfoList;
import x.mvmn.jlibgphoto2.impl.GP2PortInfoList.GP2PortInfo;

public class TemplateEngineTest {

	@Test
	public void test() {
		final Map<String, String> templatePaths = new HashMap<String, String>();
		templatePaths.put("test.vm", "test.vm");
		final TemplateEngine engine = new TemplateEngine(templatePaths);
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("testVar", "Yeah, some text here");
		final StringWriter stringWriter = new StringWriter();
		engine.renderTemplate("test.vm", "UTF-8", new VelocityContext(context), stringWriter);
		TestCase.assertEquals("Test template.\nTest variable: Yeah, some text here\n	Hi there from testGlobalMacro.", stringWriter.toString().trim());
	}

	public static void main(String args[]) {
		List<CameraListItemBean> detectedCameras = new CameraDetectorImpl().detectCameras();
		System.out.println(String.format("Detecred cameras (%s):", detectedCameras.size()));
		for (CameraListItemBean clb : detectedCameras) {
			System.out.println(String.format(" - %s :: %s", clb.getPortName(), clb.getCameraModel()));
		}
		GP2PortInfoList portList = new GP2PortInfoList();
		System.out.println(String.format("Detecred ports (%s):", portList.size()));
		for (GP2PortInfo pinf : portList) {
			System.out.println(String.format(" - %s :: %s [%s]", pinf.getPath(), pinf.getName(), pinf.getTypeName()));
		}
	}
}
