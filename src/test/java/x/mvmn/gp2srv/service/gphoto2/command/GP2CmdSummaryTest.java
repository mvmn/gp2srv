package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

public class GP2CmdSummaryTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() {
		final GP2CmdSummary cmd = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdSummary(LOGGER));
		TestCase.assertEquals(MOCK_PROPERTIES.get("--summary"), cmd.getRawStandardOutput());
		TestCase.assertEquals(0, cmd.getExitCode());
	}
}
