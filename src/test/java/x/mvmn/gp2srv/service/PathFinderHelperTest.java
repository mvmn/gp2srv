package x.mvmn.gp2srv.service;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

public class PathFinderHelperTest {

	@Test
	public void testFindJava() {
		final File javaFile = PathFinderHelper.findInPath("java", false);
		TestCase.assertTrue(javaFile.exists());
		String fileName = javaFile.getName();
		if (fileName.indexOf('.') > -1) {
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		}
		TestCase.assertEquals("java", fileName);
	}
}
