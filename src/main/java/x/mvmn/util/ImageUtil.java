package x.mvmn.util;

import java.awt.image.BufferedImage;

public class ImageUtil {

	public static double calculateAverageBrightness(BufferedImage image) {
		long result = 0;

		int argbs[] = image.getRGB(0, 0, image.getWidth(), image.getHeight(), new int[image.getWidth() * image.getHeight()], 0, image.getWidth());
		for (int i = 0; i < argbs.length; i++) {
			int argb = argbs[i];
			result += argbToRed(argb);
			result += argbToGreen(argb);
			result += argbToBlue(argb);
		}

		// 765 - 255 * 3
		return (result / argbs.length) / 765;
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
