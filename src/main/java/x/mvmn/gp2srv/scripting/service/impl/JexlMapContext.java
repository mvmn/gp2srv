package x.mvmn.gp2srv.scripting.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jexl3.MapContext;

public class JexlMapContext extends MapContext {

	protected final Map<String, Object> vars;

	public JexlMapContext() {
		this(new HashMap<String, Object>());
	}

	protected JexlMapContext(Map<String, Object> vars) {
		super(vars);
		this.vars = vars;
	}

	public Set<String> variableNames() {
		return Collections.unmodifiableSet(vars.keySet());
	}
}