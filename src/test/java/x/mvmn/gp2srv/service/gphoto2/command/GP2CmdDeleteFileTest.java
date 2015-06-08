package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

public class GP2CmdDeleteFileTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() {
		final GP2CmdDeleteFile cmd = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdDeleteFile("/", 1, LOGGER));
		TestCase.assertEquals("", cmd.getRawStandardOutput());
		TestCase.assertEquals(0, cmd.getExitCode());
	}
}
