package x.mvmn.gp2srv.service;

import java.io.File;

public class PathFinderHelper {

	public static File findInPath(String name, boolean tryCurrentDir) {
		File result = null;
		FIND_SEQ: {
			if (tryCurrentDir) {
				File candidate = new File(name);
				if (candidate.exists() && candidate.isFile()) {
					result = candidate;
					break FIND_SEQ;
				}
			}

			final String path = System.getenv("PATH");
			if (path != null && path.trim().length() > 0) {
				final String pathParts[] = path.trim().split(File.pathSeparator);
				for (String pathPart : pathParts) {
					File candidate = new File(pathPart + File.separator + name);
					if (candidate.exists() && candidate.isFile()) {
						result = candidate;
						break FIND_SEQ;
					}
				}
			}
		}
		return result;
	}

	public static void main(String args[]) {
		System.out.println(PathFinderHelper.findInPath("gphoto2", false));
	}
}
