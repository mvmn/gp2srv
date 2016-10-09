package x.mvmn.gp2srv.web.service;

import java.io.Writer;
import java.util.Map;

public interface TemplateEngine<T> {

	void renderTemplate(String tempalteName, String encoding, Map<String, Object> context, Writer writer);

	T toContextObject(Map<String, Object> data);

	String renderTemplate(String tempalteName, Map<String, Object> context);

}
