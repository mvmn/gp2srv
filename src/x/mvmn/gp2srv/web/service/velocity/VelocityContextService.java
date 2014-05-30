package x.mvmn.gp2srv.web.service.velocity;

import java.util.Map;

import org.apache.velocity.VelocityContext;

public class VelocityContextService {

	private final VelocityContext globalContext;

	public VelocityContextService() {
		this(new VelocityContext());
	}

	public VelocityContextService(final VelocityContext globalContext) {
		this.globalContext = globalContext;
	}

	public VelocityContext getGlobalContext() {
		return this.globalContext;
	}

	public VelocityContext constructContextWithGlobals(final Map<String, Object> parameters) {
		return new VelocityContext(parameters, globalContext);
	}
}
