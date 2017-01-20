package x.mvmn.gp2srv.scripting.service.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.jexl3.MapContext;

public class JexlMapContext extends MapContext {

	protected final Map<String, Object> vars;

	public JexlMapContext() {
		this(new TreeMap<String, Object>());
	}

	protected JexlMapContext(Map<String, Object> vars) {
		super(vars);
		this.vars = vars;
	}

	public Set<String> variableNames() {
		return Collections.unmodifiableSet(vars.keySet());
	}

	public Map<String, Object> toMap() {
		return new TreeMap<String, Object>(vars);
	}
}