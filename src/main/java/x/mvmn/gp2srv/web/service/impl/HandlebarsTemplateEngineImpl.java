package x.mvmn.gp2srv.web.service.impl;

import java.io.Writer;
import java.util.Map;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.web.service.TemplateEngine;

public class HandlebarsTemplateEngineImpl implements TemplateEngine<Context> {

	protected final Handlebars handlebars;

	public HandlebarsTemplateEngineImpl() {
		handlebars = new Handlebars(new ClassPathTemplateLoader(GPhoto2Server.TEMPLATE_RESOURCES_CLASSPATH_PREFIX, ".hbs"));
		for (StringHelpers helper : StringHelpers.values()) {
			handlebars.registerHelper(helper.name(), helper);
		}
	}

	@Override
	public String renderTemplate(String tempalteName, Map<String, Object> context) {
		try {
			return handlebars.compile(tempalteName).apply(toContextObject(context));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void renderTemplate(String tempalteName, String encoding, Map<String, Object> context, Writer writer) {
		try {
			final Context handlebarsContext = toContextObject(context);
			handlebars.compile(tempalteName).apply(handlebarsContext, writer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Context toContextObject(Map<String, Object> data) {
		return Context.newBuilder(data)
				.resolver(JavaBeanValueResolver.INSTANCE, FieldValueResolver.INSTANCE, MapValueResolver.INSTANCE, MethodValueResolver.INSTANCE).build();
	}
}
