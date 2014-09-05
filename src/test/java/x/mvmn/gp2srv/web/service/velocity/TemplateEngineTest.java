package x.mvmn.gp2srv.web.service.velocity;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.junit.Test;

import junit.framework.TestCase;

public class TemplateEngineTest extends TestCase {

	@Test
	public void test() {
		final Map<String, String> templatePaths = new HashMap<String, String>();
		templatePaths.put("test.vm", "test.vm");
		final TemplateEngine engine = new TemplateEngine(templatePaths);
		final Map<String, Object> context = new HashMap<String, Object>();
		context.put("testVar", "Yeah, some text here");
		final StringWriter stringWriter = new StringWriter();
		engine.renderTemplate("test.vm", "UTF-8", new VelocityContext(context), stringWriter);
		assertEquals("Test template.\nTest variable: Yeah, some text here\n	Hi there from testGlobalMacro.", stringWriter.toString().trim());
	}
}
