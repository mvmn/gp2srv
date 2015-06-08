package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.Arrays;

import org.junit.Test;

import junit.framework.TestCase;

public class GP2CmdCaptureAndDownloadTest extends TestCase {

	@Test
	public void testGetCommandString() {
		assertEquals(Arrays.toString(new String[] { "--capture-image-and-download", "--force-overwrite", "--filename", "capture.jpg" }),
				Arrays.toString(new GP2CmdCaptureAndDownload(null).getCommandString()));
	}
}
