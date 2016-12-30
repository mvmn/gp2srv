package x.mvmn.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

public class ImageUtil {

	public static double calculateAverageBrightness(BufferedImage image) {
		double result = 0;

		int imageType = image.getType();
		if (imageType != BufferedImage.TYPE_INT_ARGB && imageType != BufferedImage.TYPE_INT_RGB && imageType != BufferedImage.TYPE_3BYTE_BGR
				&& imageType != BufferedImage.TYPE_4BYTE_ABGR && imageType != BufferedImage.TYPE_4BYTE_ABGR_PRE && imageType != BufferedImage.TYPE_INT_ARGB_PRE
				&& imageType != BufferedImage.TYPE_INT_BGR) {
			throw new RuntimeException("Unsupported image type: " + image.getType());
		}
		boolean hasAlpha = image.getAlphaRaster() != null;
		int pixelSize = hasAlpha ? 4 : 3;
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

		int cpuCores = Runtime.getRuntime().availableProcessors();
		final ForkJoinPool pool = new ForkJoinPool(cpuCores);

		BrightnessCalcTask[] tasks = new BrightnessCalcTask[cpuCores];
		int subArraySize = (int) Math.ceil(((double) pixels.length) / cpuCores);
		if (subArraySize % pixelSize != 0) {
			subArraySize += pixelSize - subArraySize % pixelSize;
		}
		for (int i = 0; i < cpuCores; i++) {
			tasks[i] = new BrightnessCalcTask(pixels, subArraySize * i, Math.min(subArraySize * (i + 1), pixels.length), pixelSize);
			pool.submit(tasks[i]);
		}
		pool.shutdown();
		while (!pool.isTerminated()) {
			try {
				pool.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}
		}

		for (BrightnessCalcTask task : tasks) {
			result += task.getRawResult();
		}
		result = result / tasks.length;
		return result;
	}

	protected static class BrightnessCalcTask extends ForkJoinTask<Double> {
		private static final long serialVersionUID = -6616059829029123394L;

		protected final byte pixels[];
		protected final int pixelSize;
		protected final int from;
		protected final int to;
		protected volatile double result;

		public BrightnessCalcTask(byte pixels[], int from, int to, int pixelSize) {
			this.pixels = pixels;
			this.pixelSize = pixelSize;
			this.from = from;
			this.to = to;
		}

		@Override
		public Double getRawResult() {
			return result;
		}

		@Override
		protected void setRawResult(Double value) {
		}

		@Override
		protected boolean exec() {
			double result = 0.0d;
			int length = to - from;
			for (int i = from; i < to; i += pixelSize) {
				result += ((int) pixels[pixelSize - 3] & 0xff) + ((int) pixels[pixelSize - 2] & 0xff) + ((int) pixels[pixelSize - 1] & 0xff);
			}
			this.result = (result / (length / pixelSize)) / 7.65d;
			return true;
		}
	}

	public static int argbToAlpha(int argb) {
		return (argb >> 24) & 0xFF;
	}

	public static int argbToRed(int argb) {
		return (argb >> 16) & 0xFF;
	}

	public static int argbToGreen(int argb) {
		return (argb >> 8) & 0xFF;
	}

	public static int argbToBlue(int argb) {
		return argb & 0xFF;
	}

	/*
	 * protected static volatile double currentValue = 0d; public static void main(String args[]) throws Exception { final JFrame frame = new JFrame(); final
	 * JLabel label = new JLabel(); frame.getContentPane().add(label); frame.pack(); frame.setVisible(true);
	 * 
	 * Runnable update = new Runnable() { public void run() { label.setText(String.valueOf(currentValue)); } };
	 * 
	 * Robot r = new Robot(); Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()); for (int i = 0; i < 1000; i++) { currentValue =
	 * calculateAverageBrightness(r.createScreenCapture(screenRect)); SwingUtilities.invokeLater(update); } }
	 */

	/*
	 * public static void main(String args[]) throws Exception { Robot r = new Robot(); Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	 * Rectangle screenRect = new Rectangle(screenSize); for (int k = 0; k < 10; k++) { long st = System.nanoTime(); for (int i = 0; i < 100; i++) {
	 * BufferedImage b = r.createScreenCapture(screenRect); if (k % 2 == 0) { calculateAverageBrightness(b); } } long et = System.nanoTime();
	 * System.out.println(et - st + " " + (k % 2 == 0)); } }
	 */
}
