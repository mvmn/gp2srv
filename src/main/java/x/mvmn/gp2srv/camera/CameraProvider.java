package x.mvmn.gp2srv.camera;

import x.mvmn.jlibgphoto2.api.GP2Camera;

public interface CameraProvider {

	public GP2Camera getCamera();

	public boolean hasCamera();

	public void setCamera(GP2Camera camera);
}
