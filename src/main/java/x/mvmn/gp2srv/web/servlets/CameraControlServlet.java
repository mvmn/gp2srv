package x.mvmn.gp2srv.web.servlets;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

import x.mvmn.gp2srv.model.CameraConfigEntry.CameraConfigEntryType;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;
import x.mvmn.gp2srv.service.gphoto2.GPhoto2Command;
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
import x.mvmn.lang.util.Provider;
import x.mvmn.log.api.Logger;

public class CameraControlServlet extends AbstractGP2Servlet {

	private static final long serialVersionUID = 7389681375772493366L;

	protected final GPhoto2CommandService gphoto2CommandService;
	protected volatile GPhoto2Command currentCommand = null;

	public CameraControlServlet(final GPhoto2CommandService gphoto2CommandService, final VelocityContextService velocityContextService,
			final Provider<TemplateEngine> templateEngineProvider, final Logger logger) {
		super(velocityContextService, templateEngineProvider, logger);
		this.gphoto2CommandService = gphoto2CommandService;
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
		final String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
		if ("/allsettingset".equals(path)) {
			final String type = request.getParameter("type");
			final String key = request.getParameter("key");
			final String value = request.getParameter("value");
			final AbstractGPhoto2Command command;
			switch (CameraConfigEntryType.valueOf(type)) {
				case RADIO:
				case MENU:
					command = new GP2CmdSetSettingByIndex(key, Integer.parseInt(value), logger);
				break;
				case TOGGLE:
				case TEXT:
				case DATE:
					command = new GP2CmdSetSetting(key, value, logger);
				break;
				default:
					returnForbidden(request, response);
					command = null;
				break;
			}
			if (command != null) {
				if (!processCommandFailure(gphoto2CommandService.executeCommand(command), makeVelocityContext(request, response), request, response, logger)) {
					redirectSafely(response, request.getContextPath() + "/allsettings", logger);
				}
			}
		} else if ("/refreshpreview".equals(path)) {
			final Integer lastPreviewRetriesCount = (Integer) velocityContextService.getGlobalContext().get("lastPreviewRetriesCount");
			int previewRetriesCount = lastPreviewRetriesCount == null ? 0 : lastPreviewRetriesCount.intValue();
			boolean success = false;
			while (!success && previewRetriesCount < 15) {
				final GP2CmdCapturePreview command = gphoto2CommandService.executeCommand(new GP2CmdCapturePreview(logger, "thumb.jpg", true,
						previewRetriesCount));
				if (command.isResultedWithPossibleTimingFailure()) {
					previewRetriesCount++;
				} else {
					if (command.getRawErrorOutput() != null && command.getRawErrorOutput().trim().length() > 0) {
						processCommandFailure(command, null, request, response, logger);
						break;
					} else {
						success = true;
					}
				}
			}
			if (success) {
				velocityContextService.getGlobalContext().put("lastPreviewRetriesCount", previewRetriesCount);
				redirectSafely(response, request.getContextPath() + "/preview", logger);
			}
		} else if ("/camfilepreview".equals(path)) {
			final int imgRefId = Integer.parseInt(request.getParameter("imgRefId"));
			if (!processCommandFailure(
					gphoto2CommandService.executeCommand(new GP2CmdGetThumbnail(request.getParameter("folder"), imgRefId, "thumb.jpg", logger)),
					makeVelocityContext(request, response), request, response, logger)) {
				redirectSafely(response, request.getContextPath() + "/preview", logger);
			}
		} else if ("/deletefile".equals(path)) {
			final int imgRefId = Integer.parseInt(request.getParameter("imgRefId"));
			if (!processCommandFailure(gphoto2CommandService.executeCommand(new GP2CmdDeleteFile(request.getParameter("folder"), imgRefId, logger)),
					makeVelocityContext(request, response), request, response, logger)) {
				redirectSafely(response, request.getContextPath() + "/browse", logger);
			}
		} else if ("/capture".equals(path)) {
			if (!processCommandFailure(gphoto2CommandService.executeCommand(new GP2CmdCaptureImage(true, logger)), null, request, response, logger)) {
				redirectSafely(response, request.getContextPath() + "/preview", logger);
			}
		} else if ("/captureandrefreshpreview".equals(path)) {
			final GP2CmdCaptureImageAndListFiles cmdCaptureAndList = new GP2CmdCaptureImageAndListFiles(null, logger);
			if (!processCommandFailure(gphoto2CommandService.executeCommand(cmdCaptureAndList), null, request, response, logger)) {
				final String newlyCapturedImagePath = cmdCaptureAndList.getResultFile();
				boolean proceed = true;
				if (newlyCapturedImagePath != null && cmdCaptureAndList.getFilesList() != null && cmdCaptureAndList.getFilesList().getByFolder() != null
						&& cmdCaptureAndList.getFilesList().getByRefId() != null) {
					final int indexOfLastSlash = newlyCapturedImagePath.lastIndexOf("/");
					final String fileName = newlyCapturedImagePath.substring(indexOfLastSlash + 1);
					final String folderPath = newlyCapturedImagePath.substring(0, indexOfLastSlash);
					final Map<String, CameraFileRef> filesInFolder = cmdCaptureAndList.getFilesList().getByFolder().get(folderPath);
					if (filesInFolder != null) {
						final CameraFileRef resultFileRef = filesInFolder.get(fileName);
						if (resultFileRef != null) {
							if (processCommandFailure(
									gphoto2CommandService.executeCommand(new GP2CmdGetThumbnail(folderPath, resultFileRef.getRefId(), "thumb.jpg", logger)),
									null, request, response, logger)) {
								proceed = false;
							}
						}
					}
				}

				if (proceed) {
					redirectSafely(response, request.getContextPath() + "/preview", logger);
				}
			}
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
		if (requestPath.equals("/") || requestPath.equals("")) {
			serveTempalteUTF8Safely("camera/index.vm", velocityContext, response, logger);
		} else if (requestPath.equals("/allsettings")) {
			final GP2CmdGetAllCameraConfigurations cameraConfigsCommand = gphoto2CommandService.executeCommand(new GP2CmdGetAllCameraConfigurations(logger));
			if (!processCommandFailure(cameraConfigsCommand, velocityContext, request, response, logger)) {
				velocityContext.put("cameraConfig", cameraConfigsCommand.getCameraConfig());
				serveTempalteUTF8Safely("camera/allsettings.vm", velocityContext, response, logger);
			}
		} else if (requestPath.equals("/summary")) {
			final GP2CmdSummary cmd = gphoto2CommandService.executeCommand(new GP2CmdSummary(logger));
			populateContextWithGenericCommandResults(cmd, velocityContext);
			serveTempalteUTF8Safely("camera/textcommandresponse.vm", velocityContext, response, logger);
		} else if (requestPath.equals("/browse")) {
			final GP2CmdListFiles listFilesCommand = new GP2CmdListFiles(logger);
			if (!processCommandFailure(gphoto2CommandService.executeCommand(listFilesCommand), velocityContext, request, response, logger)) {
				velocityContext.put("filesList", listFilesCommand.getFilesList());
				serveTempalteUTF8Safely("camera/browse.vm", velocityContext, response, logger);
			}
		} else if (requestPath.equals("/preview")) {
			serveTempalteUTF8Safely("camera/preview.vm", velocityContext, response, logger);
		} else {
			returnNotFound(request, response);
		}
	}

	protected void redirectSafely(final HttpServletResponse response, final String destination, final Logger logger) {
		try {
			response.sendRedirect(destination);
		} catch (final Exception e) {
			logger.error(e);
		}
	}

	protected boolean processCommandFailure(final GPhoto2Command cmd, final Context velocityContext, final HttpServletRequest request,
			final HttpServletResponse response, final Logger logger) {
		final boolean processed;
		if (cmd.getExitCode() != 0) {
			processed = true;
			serveGenericErrorPage(request, response, cmd.getExitCode(),
					((cmd.getRawErrorOutput() != null ? cmd.getRawErrorOutput().trim() : "") + "\n\n" + (cmd.getRawStandardOutput() != null ? cmd
							.getRawStandardOutput().trim() : "")));
		} else {
			processed = false;
		}
		return processed;
	}

	protected void populateContextWithGenericCommandResults(final GPhoto2Command cmd, final Context velocityContext) {
		velocityContext.put("commandResponseStdout", cmd.getRawStandardOutput());
		velocityContext.put("commandResponseStderr", cmd.getRawErrorOutput());
		velocityContext.put("commandExitCode", cmd.getExitCode());
	}
}
