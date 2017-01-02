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
		int lios = cameraFilePath.lastIndexOf("/");
		return doCalc(cameraService.fileGetContents(cameraFilePath.substring(0, lios), cameraFilePath.substring(lios + 1)));
	}

	public double getForThumb(String cameraFilePath) {
		int lios = cameraFilePath.lastIndexOf("/");
		return doCalc(cameraService.fileGetThumb(cameraFilePath.substring(0, lios), cameraFilePath.substring(lios + 1)));
	}

	public double getForPreview() {
		return doCalc(cameraService.capturePreview());
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
