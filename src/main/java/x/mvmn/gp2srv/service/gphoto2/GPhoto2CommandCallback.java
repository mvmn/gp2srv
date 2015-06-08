package x.mvmn.gp2srv.service.gphoto2;

public interface GPhoto2CommandCallback<T, C extends GPhoto2Command<T>> {
	public void processResults(GPhoto2CommandResult<T> commandExecResult);

	public void processError(Throwable error);
}