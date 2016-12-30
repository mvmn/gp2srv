package x.mvmn.util;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;

public class ImageUtil {

	public static double calculateAverageBrightness(BufferedImage image) {
		double result = 0;

		int argbs[] = image.getRGB(0, 0, image.getWidth(), image.getHeight(), new int[image.getWidth() * image.getHeight()], 0, image.getWidth());

		final ForkJoinPool pool = new ForkJoinPool();
		int cpuCores = Runtime.getRuntime().availableProcessors();

		BrightnessCalcTask[] tasks = new BrightnessCalcTask[cpuCores];
		int subArraySize = (int) Math.ceil(((double) argbs.length) / cpuCores);
		for (int i = 0; i < cpuCores; i++) {
			tasks[i] = new BrightnessCalcTask(Arrays.copyOfRange(argbs, subArraySize * i, Math.min(subArraySize * (i + 1), argbs.length)));
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

		protected final int argbs[];
		protected volatile double result;

		public BrightnessCalcTask(int argbs[]) {
			this.argbs = argbs;
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
			for (int i = 0; i < argbs.length; i++) {
				int argb = argbs[i];
				result += argbToRed(argb);
				result += argbToGreen(argb);
				result += argbToBlue(argb);
			}
			this.result = (result / argbs.length) / 7.65d;
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
