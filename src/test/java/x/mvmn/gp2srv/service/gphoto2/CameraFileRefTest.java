package x.mvmn.gp2srv.service.gphoto2;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;

public class CameraFileRefTest {

	@Test
	public void testEquals() {
		final CameraFileRef unit = new CameraFileRef(1, "fn", "fs", "ft");
		TestCase.assertEquals(new CameraFileRef(1, "fn", "fs", "ft"), unit);
		TestCase.assertFalse(unit.equals(new CameraFileRef(2, "fn", "fs", "ft")));
		TestCase.assertFalse(unit.equals(new CameraFileRef(1, "fnx", "fs", "ft")));
		TestCase.assertFalse(unit.equals(new CameraFileRef(1, "fn", "fsx", "ft")));
		TestCase.assertFalse(unit.equals(new CameraFileRef(1, "fn", "fs", "ftx")));
	}
}
