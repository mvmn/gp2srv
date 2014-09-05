package x.mvmn.gp2srv.service.gphoto2;

public interface GPhoto2CommandCallback<C extends GPhoto2Command> {
	public void processResults(C command);
}