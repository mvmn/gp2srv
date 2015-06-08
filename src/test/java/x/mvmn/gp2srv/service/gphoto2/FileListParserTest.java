package x.mvmn.gp2srv.service.gphoto2;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.util.Map;

import org.junit.Test;

import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRef;
import x.mvmn.gp2srv.service.gphoto2.FileListParser.CameraFileRefsCollected;

public class FileListParserTest {

	@Test
	public void testParsing() throws Exception {
		// TODO: make unit test out of this
		final String dummyData = "There is no file in folder '/'.\n" + "There is no file in folder '/store_00020001'.\n"
				+ "There is no file in folder '/store_00020001/DCIM'.\n" + "There are 6 files in folder '/store_00020001/DCIM/100CANON'.\n"
				+ "#1     IMG_0781.JPG               rd   921 KB image/jpeg\n" + "#2     IMG_0782.JPG               rd   984 KB image/jpeg\n"
				+ "#3     IMG_0787.JPG               rd  2873 KB image/jpeg\n" + "#4     IMG_0792.JPG               rd  4888 KB image/jpeg\n"
				+ "#5     IMG_0793.JPG               rd  4893 KB image/jpeg\n" + "#6     IMG_0794.JPG               rd  4900 KB image/jpeg\n"
				+ "There is no file in folder '/store_00020001/MISC'.\n";

		final CameraFileRefsCollected results = FileListParser.parseList(dummyData);

		assertEquals(0, results.getByFolder().get("/store_00020001").size());
		assertEquals(0, results.getByFolder().get("/store_00020001/DCIM").size());
		assertEquals(0, results.getByFolder().get("/store_00020001/MISC").size());
		assertEquals(0, results.getByFolder().get("/").size());
		assertEquals(6, results.getByFolder().get("/store_00020001/DCIM/100CANON").size());

		final Map<String, CameraFileRef> inFolderResults = results.getByFolder().get("/store_00020001/DCIM/100CANON");
		assertTrue(inFolderResults.get("IMG_0793.JPG").equals(new CameraFileRef(5, "IMG_0793.JPG", "4893 KB", "image/jpeg")));
		assertTrue(inFolderResults.get("IMG_0781.JPG").equals(new CameraFileRef(1, "IMG_0781.JPG", "921 KB", "image/jpeg")));
		assertTrue(inFolderResults.get("IMG_0782.JPG").equals(new CameraFileRef(2, "IMG_0782.JPG", "984 KB", "image/jpeg")));
		assertTrue(inFolderResults.get("IMG_0794.JPG").equals(new CameraFileRef(6, "IMG_0794.JPG", "4900 KB", "image/jpeg")));
		assertTrue(inFolderResults.get("IMG_0787.JPG").equals(new CameraFileRef(3, "IMG_0787.JPG", "2873 KB", "image/jpeg")));
		assertTrue(inFolderResults.get("IMG_0792.JPG").equals(new CameraFileRef(4, "IMG_0792.JPG", "4888 KB", "image/jpeg")));
	}
}
