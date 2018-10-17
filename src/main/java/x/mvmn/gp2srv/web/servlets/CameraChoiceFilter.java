package x.mvmn.gp2srv.web.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import x.mvmn.gp2srv.camera.CameraProvider;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.jlibgphoto2.GP2AutodetectCameraHelper;
import x.mvmn.jlibgphoto2.GP2Camera;
import x.mvmn.jlibgphoto2.GP2Context;
import x.mvmn.jlibgphoto2.GP2PortInfoList;
import x.mvmn.jlibgphoto2.GP2PortInfoList.GP2PortInfo;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class CameraChoiceFilter extends AbstractGP2Servlet implements Filter {
	private static final long serialVersionUID = 1827967172388376853L;

	private final CameraProvider camProvider;

	public CameraChoiceFilter(final CameraProvider camProvider, final VelocityContextService velocityContextService,
			final Provider<TemplateEngine> templateEngineProvider, final Logger logger) {
		super(velocityContextService, templateEngineProvider, logger);
		this.camProvider = camProvider;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			if (httpRequest.getRequestURI().startsWith("/static") || camProvider.hasCamera()) {
				chain.doFilter(request, response);
			} else {
				synchronized (camProvider) {
					if (!camProvider.hasCamera()) {
						String cameraPortParam = httpRequest.getParameter("cameraPort");
						if (cameraPortParam != null) {
							// Set camera
							GP2PortInfoList portList = new GP2PortInfoList();
							GP2PortInfo gp2PortInfo = portList.getByPath(cameraPortParam);
							if (gp2PortInfo != null) {
								camProvider.setCamera(new GP2Camera(gp2PortInfo));
								httpResponse.sendRedirect("/");
							} else {
								httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							}
						} else {
							// Show camera choice
							Map<String, Object> tempalteModel = new HashMap<String, Object>();
							tempalteModel.put("cameras", GP2AutodetectCameraHelper.autodetectCameras(new GP2Context()));
							serveTempalteUTF8Safely("camera/choice.vm", createContext(httpRequest, httpResponse, tempalteModel), httpResponse, logger);
						}
					}
				}
			}
		}
	}

	public void destroy() {
	}
}
