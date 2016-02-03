package x.mvmn.util.file;

import java.io.File;

public class FilesHelper {
	public static void ensureFileName(File folder, String expectedFilename, String actualFilename) throws Exception {
		if (expectedFilename != null && !expectedFilename.trim().isEmpty() && actualFilename != null && !actualFilename.trim().isEmpty()) {
			if (!expectedFilename.equals(actualFilename)) {
				File sourceFile = new File(folder, actualFilename);
				File targetFile = new File(folder, expectedFilename);
				if (sourceFile.exists()) {
					if (targetFile.exists()) {
						targetFile.delete();
					}
					sourceFile.renameTo(targetFile);
				}
			}
		}
	}
}
