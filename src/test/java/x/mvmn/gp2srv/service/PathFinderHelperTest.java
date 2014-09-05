package x.mvmn.gp2srv.service;

import java.io.File;

import org.junit.Test;

import junit.framework.TestCase;

public class PathFinderHelperTest extends TestCase {

	@Test
	public void testFindJava() {
		final File javaFile = PathFinderHelper.findInPath("java", false);
		assertTrue(javaFile.exists());
		String fileName = javaFile.getName();
		if (fileName.indexOf('.') > -1) {
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		assertEquals("java", fileName);
	}
}
