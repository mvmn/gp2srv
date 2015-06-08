package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.junit.Test;

public class GP2CmdGetThumbnailTest extends MockGPhoto2ExecTest {
	protected static final Pattern RESULT_PATTERN = Pattern.compile("^Saving file as (.+)$");

	@Test
	public void testParseResult() {
		final GP2CmdGetThumbnail cmd = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdGetThumbnail("/store_00020001/DCIM/100CANON", 2, "thumb.jpg",
				LOGGER));
		TestCase.assertEquals("thumb.jpg", cmd.getResultFileName());
		TestCase.assertEquals(0, cmd.getExitCode());
	}

}
