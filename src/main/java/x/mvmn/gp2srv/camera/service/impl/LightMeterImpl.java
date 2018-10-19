package x.mvmn.gp2srv.camera.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import x.mvmn.gp2srv.camera.CameraService;
import x.mvmn.log.api.Logger;
import x.mvmn.util.ImageUtil;

public class LightMeterImpl {

	protected final CameraService cameraService;
	protected final Logger logger;

	public LightMeterImpl(CameraService cameraService, Logger logger) {
		this.cameraService = cameraService;
		this.logger = logger;
	}

	public double getForCameraFile(String cameraFilePath) {
		String file = getFileName(cameraFilePath);
		String folder = getFolder(cameraFilePath);
		try {
			return doCalc(cameraService.fileGetContents(folder, file));
		} catch (Exception e) {
			throw new RuntimeException("Failed to get image brightness for camera file '" + file + "' in folder '" + folder + "'", e);
		}
	}

	public double getForThumb(String cameraFilePath) {
		String file = getFileName(cameraFilePath);
		String folder = getFolder(cameraFilePath);
		try {
			return doCalc(cameraService.fileGetThumb(folder, file));
		} catch (Exception e) {
			throw new RuntimeException("Failed to get image brightness for preview of camera file '" + file + "' in folder '" + folder + "'", e);
		}
	}

	public double getForPreview() {
		try {
			return doCalc(cameraService.capturePreview());
		} catch (Exception e) {
			throw new RuntimeException("Failed to get image brightness for preview image.", e);
		}
	}

	protected String getFileName(String cameraFilePath) {
		int lios = cameraFilePath.lastIndexOf("/");
		return lios > -1 ? cameraFilePath.substring(lios + 1) : cameraFilePath;
	}

	protected String getFolder(String cameraFilePath) {
		int lios = cameraFilePath.lastIndexOf("/");
		if (lios == -1) {
			return "";
		} else if (lios == 0) {
			return "/";
		} else {
			return cameraFilePath.substring(0, lios);
		}
	}

	protected double doCalc(byte[] image) {
		try {
			BufferedImage javaBufferedImage = ImageIO.read(new ByteArrayInputStream(image));
			return ImageUtil.calculateAverageBrightness(javaBufferedImage);
		} catch (IOException e) {
			logger.error("Error obtianing preview average brightness", e);
			return 0;
		}
	}

	public String toString() {
		return "LightMeter";
	}
}
