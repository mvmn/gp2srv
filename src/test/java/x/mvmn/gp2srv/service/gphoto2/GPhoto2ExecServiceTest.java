package x.mvmn.gp2srv.service.gphoto2;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.service.ExecService;
import x.mvmn.gp2srv.service.ExecService.ExecResult;
import x.mvmn.gp2srv.service.ExecService.ExecCallback;

public class GPhoto2ExecServiceTest extends TestCase {

	GPhoto2ExecService unit;
	final ExecService mockExecService = new ExecService() {
		public boolean isProcessRunning() {
			return true;
		}

		public Process getCurrentProcess() {
			return null;
		}

		public ExecResult execCommandSync(String[] command, String[] envVars, File dir) throws IOException {
			assertEquals("/fake/imgdir", dir.getAbsolutePath());
			final String[] expectedCommand = new String[] { "/path/to/gphoto2", "--fakeCommand", "fakeParam1", "fakeParam2" };
			assertEquals(expectedCommand.length, command.length);
			for (int i = 0; i < expectedCommand.length; i++) {
				assertEquals(expectedCommand[i], command[i]);
			}
			assertTrue(unit.isProcessRunning());
			assertNull(unit.getCurrentProcess());
			return new ExecResult("Unit passed", "", 123);
		}

		public void execCommandAsync(ExecCallback callback, String[] command, String[] envVars, File dir) {
		}
	};

	@Test
	public void testExecCommand() throws Exception {
		unit = new GPhoto2ExecService(mockExecService, "/path/to/gphoto2", null, new File("/fake/imgdir"));
		final ExecResult result = unit.execCommand("fakeCommand", new String[] { "fakeParam1", "fakeParam2" });

		assertEquals("Unit passed", result.getStandardOutput());
		assertEquals("", result.getErrorOutput());
		assertEquals(123, result.getExitCode());
	}

	@Test
	public void testExecCommandAsync() throws Exception {
		unit = new GPhoto2ExecService(mockExecService, "/path/to/gphoto2", null, new File("/fake/imgdir"));

		unit.execCommandAsync(new ExecCallback() {
			public void processResult(final ExecResult execResult) {
				assertEquals("Unit passed", execResult.getStandardOutput());
				assertEquals("", execResult.getErrorOutput());
				assertEquals(123, execResult.getExitCode());
			}

			public void processError(final Throwable error) {
				throw new RuntimeException(error);
			}
		}, "fakeCommand", "fakeParam1", "fakeParam2");

	}
}
