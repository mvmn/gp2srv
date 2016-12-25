package x.mvmn.gp2srv.web.servlets;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import com.google.gson.Gson;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.web.CameraService;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean.CameraConfigEntryType;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera.GP2CameraEventType;
import x.mvmn.jlibgphoto2.exception.GP2Exception;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class CameraControlServlet extends AbstractGP2Servlet {

	private static final long serialVersionUID = 7389681375772493366L;

	protected static final Gson GSON = new Gson();

	protected final CameraService camera;
	protected final Properties favouredCamConfSettings;
	protected final File imagesFolder;

	public CameraControlServlet(final CameraService cameraService, final Properties favouredCamConfSettings,
			final VelocityContextService velocityContextService, final Provider<TemplateEngine> templateEngineProvider, final File imagesFolder,
			final Logger logger) {
		super(velocityContextService, templateEngineProvider, logger);
		this.camera = cameraService;
		this.favouredCamConfSettings = favouredCamConfSettings;
		this.imagesFolder = imagesFolder;
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
				final Boolean valueToSet = Boolean.valueOf(value.toLowerCase());
				if (valueToSet) {
					favouredCamConfSettings.setProperty(key, Boolean.TRUE.toString());
				} else {
					favouredCamConfSettings.remove(key);
				}
				serveStrContentUTF8("application/json", GSON.toJson(valueToSet), response);
			} else if ("/allsettingset".equals(path)) {
				final String type = request.getParameter("type");
				final String key = request.getParameter("key");
				final String value = request.getParameter("value");

				final Map<String, CameraConfigEntryBean> configAsMap = getConfigAsMap(true);
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
					getConfigAsMap(false);
				}
				serveStrContentUTF8("application/json", GSON.toJson(updatedConfigEntry), response);
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
					getConfigAsMap(false);
					redirectLocalSafely(request, response, "/preview");
				}
			} else {
				returnNotFound(request, response);
			}
		} catch (final Exception e) {
			logger.error("Error processing POST to " + path, e);
			serveGenericErrorPage(request, response, -1, e.getMessage());
		} finally {
			GPhoto2Server.liveViewEnabled.set(true);
		}
	}

	protected Context makeVelocityContext(final HttpServletRequest request, final HttpServletResponse response) {
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

	protected void processRequestByPath(final String requestPath, final Context velocityContext, final HttpServletRequest request,
			final HttpServletResponse response, final Logger logger) {
		try {
			if (requestPath.equals("/") || requestPath.equals("") || requestPath.equals("/index") || requestPath.equals("/index.html")) {
				serveTempalteUTF8Safely("camera/index.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/automate")) {
				serveTempalteUTF8Safely("camera/automate.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/cameraConfig.json")) {
				final boolean reRead = Boolean.parseBoolean(request.getParameter("reRead"));
				final Map<String, CameraConfigEntryBean> cameraConfig = getConfigAsMap(!reRead);
				serveStrContentUTF8("application/json", GSON.toJson(cameraConfig), response);
			} else if (requestPath.equals("/favedConfigs.json")) {
				serveStrContentUTF8("application/json", GSON.toJson(favouredCamConfSettings), response);
			} else if (requestPath.equals("/browse")) {
				String path = request.getParameter("path");
				if (path == null || path.trim().isEmpty()) {
					path = "/";
				}
				velocityContext.put("currentBrowsePath", path);
				final List<CameraFileSystemEntryBean> fileList = camera.filesList(path, true, false, false);
				Collections.sort(fileList);
				velocityContext.put("filesList", fileList);
				final List<CameraFileSystemEntryBean> folderList = camera.filesList("/", false, true, true);
				Collections.sort(folderList);
				velocityContext.put("folderList", folderList);
				serveTempalteUTF8Safely("camera/browse.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/preview")) {
				getConfigAsMap(true);
				serveTempalteUTF8Safely("camera/preview.vm", velocityContext, response, logger);
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
	protected Map<String, CameraConfigEntryBean> getConfigAsMap(final boolean useCache) {
		Map<String, CameraConfigEntryBean> configAsMap = null;
		if (useCache) {
			configAsMap = (Map<String, CameraConfigEntryBean>) velocityContextService.getGlobalContext().get("lastReadCameraConfig");
		}
		if (configAsMap == null) {
			try {
				GPhoto2Server.liveViewEnabled.set(false);
				GPhoto2Server.waitWhileLiveViewInProgress(50);
				final List<CameraConfigEntryBean> config = camera.getConfig();
				final Map<String, CameraConfigEntryBean> configMap = new TreeMap<String, CameraConfigEntryBean>();
				for (CameraConfigEntryBean configEntry : config) {
					configMap.put(configEntry.getPath(), configEntry);
				}
				configAsMap = configMap;
				velocityContextService.getGlobalContext().put("lastReadCameraConfig", configAsMap);
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
