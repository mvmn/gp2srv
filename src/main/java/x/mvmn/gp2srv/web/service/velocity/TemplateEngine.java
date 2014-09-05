package x.mvmn.gp2srv.web.service.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.SystemLogChute;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

import x.mvmn.gp2srv.GPhoto2Server;

public class TemplateEngine {

	public static final String DEFAULT_TEMPLATES_CLASSPATH_PREFIX = "/x/mvmn/gp2srv/web/templates/";

	private final VelocityEngine engine;
	private final String templatesClasspathPrefix;

	public TemplateEngine(Map<String, String> pathToTempalteMapping) {
		this(DEFAULT_TEMPLATES_CLASSPATH_PREFIX, pathToTempalteMapping);
	}

	public TemplateEngine(String templatesClasspathPrefix, Map<String, String> pathToTempalteMapping) {
		this.templatesClasspathPrefix = templatesClasspathPrefix;
		try {
			initStringResourceRepository(pathToTempalteMapping);
			engine = new VelocityEngine(getConfigurationAsProperties());
		} catch (Exception e) {
			throw new RuntimeException("Failed in initialize Template Engine", e);
		}
	}

	private StringResourceRepository initStringResourceRepository(Map<String, String> pathToTempalteMapping) throws IOException {
		StringResourceRepository result = new StringResourceRepositoryImpl();
		StringResourceLoader.setRepository(StringResourceLoader.REPOSITORY_NAME_DEFAULT, result);
		registerTemplate(result, RuntimeConstants.VM_LIBRARY_DEFAULT, RuntimeConstants.VM_LIBRARY_DEFAULT);
		for (Map.Entry<String, String> pathToTempalteMappingItem : pathToTempalteMapping.entrySet()) {
			registerTemplate(result, pathToTempalteMappingItem.getKey(), pathToTempalteMappingItem.getValue());
		}
		return result;
	}

	private void registerTemplate(StringResourceRepository repo, String registeredName, String relativeClasspathRef) throws IOException {
		String templateClasspathRef = templatesClasspathPrefix + relativeClasspathRef.replaceAll("\\.\\./", "");
		InputStream templateBodyInputStream = GPhoto2Server.class.getResourceAsStream(templateClasspathRef);
		String templateBodyString = IOUtils.toString(templateBodyInputStream, "UTF-8");
		repo.putStringResource(registeredName, templateBodyString);
	}

	private Properties getConfigurationAsProperties() {
		Properties result = new Properties();

		result.setProperty(RuntimeConstants.RESOURCE_LOADER, "string");
		result.setProperty(RuntimeConstants.VM_LIBRARY_AUTORELOAD, "false");
		result.setProperty(RuntimeConstants.VM_LIBRARY, RuntimeConstants.VM_LIBRARY_DEFAULT);
		result.setProperty("string.resource.loader.class", StringResourceLoader.class.getName());
		result.setProperty("string.resource.loader.modificationCheckInterval", "0");
		result.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, SystemLogChute.class.getName());

		return result;
	}

	public void renderTemplate(String tempalteName, String encoding, Context context, Writer output) {
		engine.mergeTemplate(tempalteName, encoding, context, output);
	}

	public static void main(String args[]) {
		Map<String, String> templatePaths = new HashMap<String, String>();
		templatePaths.put("test.vm", "test.vm");
		TemplateEngine engine = new TemplateEngine(templatePaths);
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("testVar", "Yeah, some text here");
		StringWriter stringWriter = new StringWriter();
		engine.renderTemplate("test.vm", "UTF-8", new VelocityContext(context), stringWriter);
		System.out.println(stringWriter.toString());
	}
}
