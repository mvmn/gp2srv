package x.mvmn.gp2srv.web.servlets;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.jknack.handlebars.Context;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.web.service.CameraService;
import x.mvmn.gp2srv.web.service.TemplateEngine;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean.CameraConfigEntryType;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;
import x.mvmn.jlibgphoto2.exception.GP2Exception;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class CameraControlServlet extends AbstractGP2Servlet {

	private static final long serialVersionUID = 7389681375772493366L;

	protected final CameraService camera;
	protected final Properties favouredCamConfSettings;

	public CameraControlServlet(final CameraService cameraService, final Properties favouredCamConfSettings,
			final Provider<TemplateEngine<Context>> templateEngineProvider, final Logger logger) {
		super(templateEngineProvider, logger);
		this.camera = cameraService;
		this.favouredCamConfSettings = favouredCamConfSettings;
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		final String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
		try {
			GPhoto2Server.liveViewEnabled.set(false);
			GPhoto2Server.waitWhileLiveViewInProgress(50);
			if ("/favsetting".equals(path)) {
				final String key = request.getParameter("key");
				final String value = request.getParameter("value");
				if (Boolean.valueOf(value.toLowerCase())) {
					favouredCamConfSettings.setProperty(key, Boolean.TRUE.toString());
				} else {
					favouredCamConfSettings.remove(key);
				}
				redirectLocalSafely(request, response, "/allsettings");
			} else if ("/allsettingset".equals(path)) {
				final String type = request.getParameter("type");
				final String key = request.getParameter("key");
				final String value = request.getParameter("value");
				final String page = request.getParameter("page");
				final boolean skipRedirect = request.getParameter("skipRedirect") != null && Boolean.parseBoolean(request.getParameter("skipRedirect"));

				final Map<String, CameraConfigEntryBean> configAsMap = getConfigAsMap(request, true);
				final CameraConfigEntryBean configEntry = configAsMap.get(key);
				CameraConfigEntryBean updatedConfigEntry = null;
				switch (CameraConfigEntryType.valueOf(type).getValueType()) {
					case FLOAT:
						updatedConfigEntry = configEntry.cloneWithNewValue(Float.parseFloat(value));
					break;
					case INT:
						updatedConfigEntry = configEntry.cloneWithNewValue(Integer.parseInt(value));
					break;
					case STRING:
						updatedConfigEntry = configEntry.cloneWithNewValue(value);
					break;
					default:
						returnForbidden(request, response);
					break;
				}
				if (updatedConfigEntry != null) {
					camera.setConfig(updatedConfigEntry);
					getConfigAsMap(request, false);
					if (!skipRedirect) {
						if (page != null && "preview".equals(page)) {
							redirectLocalSafely(request, response, "/preview");
						} else {
							redirectLocalSafely(request, response, "/allsettings");
						}
					}
				}
			} else if ("/camfilepreview".equals(path)) {
				final String fileName = request.getParameter("name");
				final String filePath = request.getParameter("folder");

				byte[] fileContents = camera.fileGetContents(filePath, fileName);

				response.setContentType("image/jpeg");
				response.getOutputStream().write(fileContents);
				response.flushBuffer();
			} else if ("/deletefile".equals(path)) {
				final String fileName = request.getParameter("name");
				final String filePath = request.getParameter("folder");

				camera.fileDelete(filePath, fileName);

				redirectLocalSafely(request, response, "/browse");
			} else if ("/capture".equals(path)) {
				camera.capture();
				camera.waitForSpecificEvent(1000, GP2CameraEventType.CAPTURE_COMPLETE);
				if (!(request.getParameter("captureOnly") != null && Boolean.parseBoolean(request.getParameter("captureOnly")))) {
					getConfigAsMap(request, false);
					redirectLocalSafely(request, response, "/preview");
				}
			}
		} catch (final Exception e) {
			logger.error("Error processing POST to " + path, e);
			serveGenericErrorPage(request, response, -1, e.getMessage());
		} finally {
			GPhoto2Server.liveViewEnabled.set(true);
		}
	}

	protected Map<String, Object> makeVelocityContext(final HttpServletRequest request, final HttpServletResponse response) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("request", request);
		params.put("response", response);
		params.put("currentTimeMillis", System.currentTimeMillis());
		return createContext(request, response, params);
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		processRequestByPath(request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : ""), makeVelocityContext(request, response),
				request, response, logger);
	}

	protected void processRequestByPath(final String requestPath, final Map<String, Object> templateContext, final HttpServletRequest request,
			final HttpServletResponse response, final Logger logger) {
		try {
			if (requestPath.equals("/") || requestPath.equals("")) {
				serveTempalteUTF8Safely("camera/index", templateContext, response, logger);
			} else if (requestPath.equals("/automate")) {
				serveTempalteUTF8Safely("camera/automate", templateContext, response, logger);
			} else if (requestPath.equals("/cameraConfig.json")) {
				final Map<String, CameraConfigEntryBean> cameraConfig = getConfigAsMap(request, true);
				serveWithJsonSerialization(response, cameraConfig);
			} else if (requestPath.equals("/allsettings")) {
				final Map<String, CameraConfigEntryBean> cameraConfig = getConfigAsMap(request, true);
				templateContext.put("cameraConfig", cameraConfig);
				serveTempalteUTF8Safely("camera/allsettings", templateContext, response, logger);
			} else if (requestPath.equals("/browse")) {
				String path = request.getParameter("path");
				if (path == null || path.trim().isEmpty()) {
					path = "/";
				}
				templateContext.put("currentBrowsePath", path);
				final List<CameraFileSystemEntryBean> fileList = camera.filesList(path, true, false, false);
				Collections.sort(fileList);
				templateContext.put("filesList", fileList);
				final List<CameraFileSystemEntryBean> folderList = camera.filesList("/", false, true, true);
				Collections.sort(folderList);
				templateContext.put("folderList", folderList);
				serveTempalteUTF8Safely("camera/browse", templateContext, response, logger);
			} else if (requestPath.equals("/preview")) {
				getConfigAsMap(request, true);
				serveTempalteUTF8Safely("camera/preview", templateContext, response, logger);
			} else {
				returnNotFound(request, response);
			}
		} catch (final GP2Exception e) {
			serveGenericErrorPage(request, response, e.getCode(), e.getMessage());
		} catch (final Exception e) {
			logger.error("Error processing GET to " + requestPath, e);
			serveGenericErrorPage(request, response, -1, e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	protected Map<String, CameraConfigEntryBean> getConfigAsMap(ServletRequest request, final boolean useCache) {
		Map<String, CameraConfigEntryBean> configAsMap = null;
		if (useCache) {
			configAsMap = (Map<String, CameraConfigEntryBean>) request.getServletContext().getAttribute("lastReadCameraConfig");
		}
		if (configAsMap == null) {
			try {
				GPhoto2Server.liveViewEnabled.set(false);
				GPhoto2Server.waitWhileLiveViewInProgress(50);
				configAsMap = new TreeMap<>(camera.getConfig().stream().collect(Collectors.toMap(CameraConfigEntryBean::getPath, Function.identity())));
				request.getServletContext().setAttribute("lastReadCameraConfig", configAsMap);
			} finally {
				GPhoto2Server.liveViewEnabled.set(true);
			}
		}

		return configAsMap;
	}

	protected void redirectLocalSafely(final HttpServletRequest request, final HttpServletResponse response, final String destination) {
		try {
			response.sendRedirect(request.getContextPath() + destination);
		} catch (final Exception e) {
			logger.error(e);
		}
	}
}
