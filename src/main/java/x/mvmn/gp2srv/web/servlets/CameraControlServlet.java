package x.mvmn.gp2srv.web.servlets;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import com.google.gson.Gson;

import x.mvmn.gp2srv.GPhoto2Server;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.gphoto2.jna.Gphoto2Library.CameraEventType;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean;
import x.mvmn.jlibgphoto2.CameraConfigEntryBean.CameraConfigEntryType;
import x.mvmn.jlibgphoto2.CameraFileSystemEntryBean;
import x.mvmn.jlibgphoto2.GP2Camera;
import x.mvmn.jlibgphoto2.GP2CameraFilesHelper;
import x.mvmn.jlibgphoto2.GP2ConfigHelper;
import x.mvmn.jlibgphoto2.exception.GPhotoException;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class CameraControlServlet extends AbstractGP2Servlet {

	private static final long serialVersionUID = 7389681375772493366L;

	public static final String THUMB_FILENAME = "thumb.jpg";

	protected static final Gson GSON = new Gson();

	protected final GP2Camera camera;
	protected final Properties favouredCamConfSettings;
	protected final File imagesFolder;

	public CameraControlServlet(final GP2Camera camera, final Properties favouredCamConfSettings, final VelocityContextService velocityContextService,
			final Provider<TemplateEngine> templateEngineProvider, final File imagesFolder, final Logger logger) {
		super(velocityContextService, templateEngineProvider, logger);
		this.camera = camera;
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
					GP2ConfigHelper.setConfig(camera, updatedConfigEntry);
					if (!skipRedirect) {
						if (page != null && "preview".equals(page)) {
							getConfigAsMap(false);
							redirectLocalSafely(request, response, "/preview");
						} else {
							redirectLocalSafely(request, response, "/allsettings");
						}
					}
				}
			} else if ("/camfilepreview".equals(path)) {
				final String fileName = request.getParameter("name");
				final String filePath = request.getParameter("folder");

				byte[] fileContents = GP2CameraFilesHelper.getCameraFileContents(camera, filePath, fileName);

				response.setContentType("image/jpeg");
				response.getOutputStream().write(fileContents);
				response.flushBuffer();
			} else if ("/deletefile".equals(path)) {
				final String fileName = request.getParameter("name");
				final String filePath = request.getParameter("folder");

				GP2CameraFilesHelper.deleteCameraFile(camera, filePath, fileName);

				redirectLocalSafely(request, response, "/browse");
			} else if ("/capture".equals(path)) {
				camera.capture();
				camera.waitForSpecificEvent(1000, CameraEventType.GP_EVENT_CAPTURE_COMPLETE);
				if (!(request.getParameter("captureOnly") != null && Boolean.parseBoolean(request.getParameter("captureOnly")))) {
					getConfigAsMap(false);
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
			if (requestPath.equals("/") || requestPath.equals("")) {
				serveTempalteUTF8Safely("camera/index.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/automate")) {
				serveTempalteUTF8Safely("camera/automate.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/cameraConfig.json")) {
				final Map<String, CameraConfigEntryBean> cameraConfig = getConfigAsMap(true);
				serveStrContentUTF8("application/json", GSON.toJson(cameraConfig), response);
			} else if (requestPath.equals("/allsettings")) {
				final Map<String, CameraConfigEntryBean> cameraConfig = getConfigAsMap(true);
				velocityContext.put("cameraConfig", cameraConfig);
				serveTempalteUTF8Safely("camera/allsettings.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/browse")) {
				final List<CameraFileSystemEntryBean> fileList = GP2CameraFilesHelper.list(camera, "/", false, true);
				velocityContext.put("filesList", fileList);
				serveTempalteUTF8Safely("camera/browse.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/preview")) {
				getConfigAsMap(true);
				serveTempalteUTF8Safely("camera/preview.vm", velocityContext, response, logger);
			} else {
				returnNotFound(request, response);
			}
		} catch (final GPhotoException e) {
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
				List<CameraConfigEntryBean> config = GP2ConfigHelper.getConfig(camera);
				configAsMap = new HashMap<String, CameraConfigEntryBean>();
				for (final CameraConfigEntryBean configEntry : config) {
					configAsMap.put(configEntry.getPath(), configEntry);
				}
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
