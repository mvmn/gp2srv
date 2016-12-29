package x.mvmn.gp2srv.camera.service.impl;

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

	public double get() {
		try {
			return ImageUtil.calculateAverageBrightness(ImageIO.read(new ByteArrayInputStream(cameraService.capturePreview())));
		} catch (IOException e) {
			logger.error("Error obtianing preview average brightness", e);
			return 0;
		}
	}
}
