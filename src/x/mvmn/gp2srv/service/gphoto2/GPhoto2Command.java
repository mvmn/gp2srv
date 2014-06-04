package x.mvmn.gp2srv.service.gphoto2;

public interface GPhoto2Command {
	public String getCommandString();

	public String[] getCommandParams();

	public void submitRawOutput(String output);

	public void submitError(Throwable error);

	public String getRawOutput();
}