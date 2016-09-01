package x.mvmn.gp2srv.web.servlets;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import com.google.gson.Gson;

import x.mvmn.gp2srv.model.CameraConfigEntry;
import x.mvmn.gp2srv.model.CameraConfigEntry.CameraConfigEntryType;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandService;
import x.mvmn.gp2srv.service.gphoto2.command.AbstractGPhoto2Command;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdCaptureImage;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdCaptureImageAndListFiles;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdCapturePreview;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdDeleteFile;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdGetAllCameraConfigurations;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdGetThumbnail;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdListFiles;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdSetSetting;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdSetSettingByIndex;
import x.mvmn.gp2srv.service.gphoto2.command.GP2CmdSummary;
import x.mvmn.gp2srv.web.service.velocity.TemplateEngine;
import x.mvmn.gp2srv.web.service.velocity.VelocityContextService;
import x.mvmn.lang.util.ImmutablePair;
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;
import x.mvmn.util.file.FilesHelper;

public class CameraControlServlet extends AbstractGP2Servlet {

	private static final long serialVersionUID = 7389681375772493366L;

	public static final String THUMB_FILENAME = "thumb.jpg";

	protected static final Gson GSON = new Gson();

	protected final GPhoto2CommandService gphoto2CommandService;
	protected final Properties favouredCamConfSettings;
	protected final File imagesFolder;

	public CameraControlServlet(final GPhoto2CommandService gphoto2CommandService, final Properties favouredCamConfSettings,
			final VelocityContextService velocityContextService, final Provider<TemplateEngine> templateEngineProvider, final File imagesFolder,
			final Logger logger) {
		super(velocityContextService, templateEngineProvider, logger);
		this.gphoto2CommandService = gphoto2CommandService;
		this.favouredCamConfSettings = favouredCamConfSettings;
		this.imagesFolder = imagesFolder;
	}

	protected GPhoto2CommandResult<String> ensureThumbFileName(GPhoto2CommandResult<String> cmdResult) throws Exception {
		FilesHelper.ensureFileName(imagesFolder, THUMB_FILENAME, cmdResult.getResult());
		return cmdResult;
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		final String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
		try {
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
				final AbstractGPhoto2Command<?> command;
				switch (CameraConfigEntryType.valueOf(type)) {
					case RADIO:
					case MENU:
						command = new GP2CmdSetSettingByIndex(key, Integer.parseInt(value), logger);
					break;
					case TOGGLE:
					case TEXT:
					case DATE:
					case RANGE:
						command = new GP2CmdSetSetting(key, value, logger);
					break;
					default:
						returnForbidden(request, response);
						command = null;
					break;
				}
				if (command != null) {
					if (!processCommandFailure(gphoto2CommandService.executeCommand(command), makeVelocityContext(request, response), request, response,
							logger)) {
						if (!skipRedirect) {
							if (page != null && "preview".equals(page)) {
								updateLastReadConfig();
								redirectLocalSafely(request, response, "/preview");
							} else {
								redirectLocalSafely(request, response, "/allsettings");
							}
						}
					}
				}
			} else if ("/refreshpreview".equals(path)) {
				final Integer lastPreviewRetriesCount = (Integer) velocityContextService.getGlobalContext().get("lastPreviewRetriesCount");
				int previewRetriesCount = lastPreviewRetriesCount == null ? 0 : lastPreviewRetriesCount.intValue();
				boolean success = false;
				while (!success && previewRetriesCount < 15) {
					final GPhoto2CommandResult<String> cmdPreviewResult = gphoto2CommandService
							.executeCommand(new GP2CmdCapturePreview(logger, THUMB_FILENAME, true, previewRetriesCount));
					if (cmdPreviewResult.getResult() != null && cmdPreviewResult.getResult().isEmpty()) {
						previewRetriesCount++;
					} else {
						if (cmdPreviewResult.getErrorOutput() != null && cmdPreviewResult.getErrorOutput().trim().length() > 0) {
							processCommandFailure(cmdPreviewResult, null, request, response, logger);
							break;
						} else {
							ensureThumbFileName(cmdPreviewResult);
							success = true;
						}
					}
				}
				if (success) {
					velocityContextService.getGlobalContext().put("lastPreviewRetriesCount", previewRetriesCount);
					redirectLocalSafely(request, response, "/preview");
				}
			} else if ("/camfilepreview".equals(path)) {
				final int imgRefId = Integer.parseInt(request.getParameter("imgRefId"));
				final GPhoto2CommandResult<String> result = gphoto2CommandService
						.executeCommand(new GP2CmdGetThumbnail(request.getParameter("folder"), imgRefId, THUMB_FILENAME, logger));
				if (!processCommandFailure(result, makeVelocityContext(request, response), request, response, logger)) {
					ensureThumbFileName(result);
					redirectLocalSafely(request, response, "/preview");
				}
			} else if ("/deletefile".equals(path)) {
				final int imgRefId = Integer.parseInt(request.getParameter("imgRefId"));
				if (!processCommandFailure(gphoto2CommandService.executeCommand(new GP2CmdDeleteFile(request.getParameter("folder"), imgRefId, logger)),
						makeVelocityContext(request, response), request, response, logger)) {
					redirectLocalSafely(request, response, "/browse");
				}
			} else if ("/capture".equals(path)) {
				if (!processCommandFailure(gphoto2CommandService.executeCommand(new GP2CmdCaptureImage(true, logger)), null, request, response, logger)) {
					if (!(request.getParameter("captureOnly") != null && Boolean.parseBoolean(request.getParameter("captureOnly")))) {
						updateLastReadConfig();
						redirectLocalSafely(request, response, "/preview");
					}
				}
			} else if ("/captureandrefreshpreview".equals(path)) {
				final GPhoto2CommandResult<ImmutablePair<String, CameraFileRefsCollected>> cmdCaptureAndList = gphoto2CommandService
						.executeCommand(new GP2CmdCaptureImageAndListFiles(null, logger));
				if (!processCommandFailure(cmdCaptureAndList, null, request, response, logger)) {
					final String newlyCapturedImagePath = cmdCaptureAndList.getResult().getA();
					boolean proceed = true;
					if (newlyCapturedImagePath != null && cmdCaptureAndList.getResult().getB() != null
							&& cmdCaptureAndList.getResult().getB().getByFolder() != null && cmdCaptureAndList.getResult().getB().getByRefId() != null) {
						final int indexOfLastSlash = newlyCapturedImagePath.lastIndexOf("/");
						final String fileName = newlyCapturedImagePath.substring(indexOfLastSlash + 1);
						final String folderPath = newlyCapturedImagePath.substring(0, indexOfLastSlash);
						final Map<String, CameraFileRef> filesInFolder = cmdCaptureAndList.getResult().getB().getByFolder().get(folderPath);
						if (filesInFolder != null) {
							final CameraFileRef resultFileRef = filesInFolder.get(fileName);
							if (resultFileRef != null) {
								GPhoto2CommandResult<String> result = gphoto2CommandService
										.executeCommand(new GP2CmdGetThumbnail(folderPath, resultFileRef.getRefId(), THUMB_FILENAME, logger));
								if (processCommandFailure(result, null, request, response, logger)) {
									proceed = false;
								} else {
									ensureThumbFileName(result);
								}
							}
						}
					}

					if (proceed) {
						redirectLocalSafely(request, response, "/preview");
					}
				}
			}
		} catch (final Exception e) {
			logger.error("Error processing POST to " + path, e);
			serveGenericErrorPage(request, response, -1, e.getMessage());
		}
	}

	protected Context makeVelocityContext(final HttpServletRequest request, final HttpServletResponse response) {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("request", request);
		params.put("response", response);
		params.put("currentTimeMillis", System.currentTimeMillis());
		params.put("commandIsBeingExecuted", gphoto2CommandService.isProcessRunning());
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
				final GPhoto2CommandResult<Map<String, CameraConfigEntry>> cameraConfigsCommand = gphoto2CommandService
						.executeCommand(new GP2CmdGetAllCameraConfigurations(logger));
				if (!processCommandFailure(cameraConfigsCommand, velocityContext, request, response, logger)) {
					final Map<String, CameraConfigEntry> cameraConfig = cameraConfigsCommand.getResult();
					velocityContextService.getGlobalContext().put("lastReadCameraConfig", cameraConfig);
					serveStrContentUTF8("application/json", GSON.toJson(cameraConfig), response);
				}
			} else if (requestPath.equals("/allsettings")) {
				final GPhoto2CommandResult<Map<String, CameraConfigEntry>> cameraConfigsCommand = gphoto2CommandService
						.executeCommand(new GP2CmdGetAllCameraConfigurations(logger));
				if (!processCommandFailure(cameraConfigsCommand, velocityContext, request, response, logger)) {
					final Map<String, CameraConfigEntry> cameraConfig = cameraConfigsCommand.getResult();
					velocityContext.put("cameraConfig", cameraConfig);
					velocityContextService.getGlobalContext().put("lastReadCameraConfig", cameraConfig);
					serveTempalteUTF8Safely("camera/allsettings.vm", velocityContext, response, logger);
				}
			} else if (requestPath.equals("/summary")) {
				final GPhoto2CommandResult<String> cmdSummaryResult = gphoto2CommandService.executeCommand(new GP2CmdSummary(logger));
				populateContextWithGenericCommandResults(cmdSummaryResult, velocityContext);
				serveTempalteUTF8Safely("camera/textcommandresponse.vm", velocityContext, response, logger);
			} else if (requestPath.equals("/browse")) {
				final GPhoto2CommandResult<CameraFileRefsCollected> cmdListFilesResult = gphoto2CommandService.executeCommand(new GP2CmdListFiles(logger));
				if (!processCommandFailure(cmdListFilesResult, velocityContext, request, response, logger)) {
					velocityContext.put("filesList", cmdListFilesResult.getResult());
					serveTempalteUTF8Safely("camera/browse.vm", velocityContext, response, logger);
				}
			} else if (requestPath.equals("/preview")) {
				if (velocityContextService.getGlobalContext().get("lastReadCameraConfig") == null) {
					updateLastReadConfig();
				}
				serveTempalteUTF8Safely("camera/preview.vm", velocityContext, response, logger);
			} else {
				returnNotFound(request, response);
			}
		} catch (final Exception e) {
			logger.error("Error processing GET to " + requestPath, e);
			serveGenericErrorPage(request, response, -1, e.getMessage());
		}
	}

	protected void updateLastReadConfig() throws Exception {
		final GPhoto2CommandResult<Map<String, CameraConfigEntry>> cmdGetCameraConfigsResult = gphoto2CommandService
				.executeCommand(new GP2CmdGetAllCameraConfigurations(logger));
		if (cmdGetCameraConfigsResult.getExitCode() == 0) {
			velocityContextService.getGlobalContext().put("lastReadCameraConfig", cmdGetCameraConfigsResult.getResult());
		} else {
			logger.warn("Error reading updated camera config for preview page: " + cmdGetCameraConfigsResult.getErrorOutput());
		}
	}

	protected void redirectLocalSafely(final HttpServletRequest request, final HttpServletResponse response, final String destination) {
		try {
			response.sendRedirect(request.getContextPath() + destination);
		} catch (final Exception e) {
			logger.error(e);
		}
	}

	protected boolean processCommandFailure(final GPhoto2CommandResult<?> cmd, final Context velocityContext, final HttpServletRequest request,
			final HttpServletResponse response, final Logger logger) {
		final boolean processed;
		if (cmd.getExitCode() != 0) {
			processed = true;
			serveGenericErrorPage(request, response, cmd.getExitCode(), ((cmd.getErrorOutput() != null ? cmd.getErrorOutput().trim() : "") + "\n\n"
					+ (cmd.getStandardOutput() != null ? cmd.getStandardOutput().trim() : "")));
		} else {
			processed = false;
		}
		return processed;
	}

	protected void populateContextWithGenericCommandResults(final GPhoto2CommandResult<?> cmd, final Context velocityContext) {
		velocityContext.put("commandResponseStdout", cmd.getStandardOutput());
		velocityContext.put("commandResponseStderr", cmd.getErrorOutput());
		velocityContext.put("commandExitCode", cmd.getExitCode());
	}
}
