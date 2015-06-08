package x.mvmn.gp2srv.service.gphoto2.command;

import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.GPhoto2CommandResult;

public class GP2CmdGetThumbnailTest extends MockGPhoto2ExecTest {
	protected static final Pattern RESULT_PATTERN = Pattern.compile("^Saving file as (.+)$");

	@Test
	public void testParseResult() throws Exception {
		final GPhoto2CommandResult<String> result = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdGetThumbnail("/store_00020001/DCIM/100CANON", 2,
				"thumb.jpg", LOGGER));
		TestCase.assertEquals("thumb.jpg", result.getResult());
		TestCase.assertEquals(0, result.getExitCode());
	}
}
