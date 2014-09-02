package x.mvmn.gp2srv.service.gphoto2;

public interface GPhoto2Command {
	public String getCommandString();

	public String[] getCommandParams();

	public void submitRawStandardOutput(String standardOutput);

	public void submitRawErrorOutput(String errorOutput);

	public void submitError(Throwable error);

	public void submitExitCode(int exitCode);

	public String getRawStandardOutput();

	public String getRawErrorOutput();

	public int getExitCode();
}
