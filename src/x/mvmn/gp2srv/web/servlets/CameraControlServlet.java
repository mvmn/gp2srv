package x.mvmn.gp2srv.web.servlets;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandService;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class CameraControlServlet extends AbstractErrorHandlingServlet {

	private static final long serialVersionUID = 7389681375772493366L;

	final VelocityContextService velocityContextService;

	public CameraControlServlet(final GPhoto2CommandService gphoto2CommandService, final VelocityContextService velocityContextService,
			final Provider<TemplateEngine> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
		this.velocityContextService = velocityContextService;
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("httprequest", request);
		params.put("httpresponse", response);
		final Context context = velocityContextService.constructContextWithGlobals(params);
		serveTempalteUTF8Safely("camera/index.vm", context, response, logger);
	}
}
