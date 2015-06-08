package x.mvmn.gp2srv.service.gphoto2.command;

import junit.framework.TestCase;

import org.junit.Test;

import x.mvmn.gp2srv.model.CameraConfigEntry;
import x.mvmn.gp2srv.model.CameraConfigEntry.CameraConfigEntryType;

public class GP2CmdGetAllCameraConfigurationsTest extends MockGPhoto2ExecTest {
	@Test
	public void testParseResult() {
		final GP2CmdGetAllCameraConfigurations cmd = MOCK_GPHOTO2_COMMAND_SERVICE.executeCommand(new GP2CmdGetAllCameraConfigurations(LOGGER));
		TestCase.assertEquals(0, cmd.getExitCode());
		TestCase.assertEquals(55, cmd.getCameraConfig().size());

		TestCase.assertEquals(new CameraConfigEntry("/main/actions/autofocusdrive", "Drive Canon DSLR Autofocus", "0", CameraConfigEntryType.TOGGLE), cmd
				.getCameraConfig().get("/main/actions/autofocusdrive"));
		TestCase.assertEquals(new CameraConfigEntry("/main/actions/eosremoterelease", "Canon EOS Remote Release", "None", CameraConfigEntryType.RADIO,
				new String[] { "None", "Press Half", "Press Full", "Release Half", "Release Full" }),
				cmd.getCameraConfig().get("/main/actions/eosremoterelease"));
		TestCase.assertEquals(new CameraConfigEntry("/main/actions/eosviewfinder", "Canon EOS Viewfinder", "2", CameraConfigEntryType.TOGGLE), cmd
				.getCameraConfig().get("/main/actions/eosviewfinder"));
		TestCase.assertEquals(new CameraConfigEntry("/main/actions/eoszoom", "Canon EOS Zoom", "0", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/actions/eoszoom"));
		TestCase.assertEquals(new CameraConfigEntry("/main/actions/eoszoomposition", "Canon EOS Zoom Position", "0,0", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/actions/eoszoomposition"));
		TestCase.assertEquals(new CameraConfigEntry("/main/actions/manualfocusdrive", "Drive Canon DSLR Manual focus", "None", CameraConfigEntryType.RADIO,
				new String[] { "Near 1", "Near 2", "Near 3", "None", "Far 1", "Far 2", "Far 3" }), cmd.getCameraConfig().get("/main/actions/manualfocusdrive"));
		TestCase.assertEquals(
				new CameraConfigEntry("/main/actions/syncdatetime", "Synchronize camera date and time with PC", "0", CameraConfigEntryType.TOGGLE), cmd
						.getCameraConfig().get("/main/actions/syncdatetime"));
		TestCase.assertEquals(new CameraConfigEntry("/main/actions/uilock", "UI Lock", "2", CameraConfigEntryType.TOGGLE),
				cmd.getCameraConfig().get("/main/actions/uilock"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/aeb", "Auto Exposure Bracketing", "off", CameraConfigEntryType.RADIO, new String[] {
				"off", "+/- 1/3", "+/- 2/3", "+/- 1", "+/- 1 1/3", "+/- 1 2/3", "+/- 2" }), cmd.getCameraConfig().get("/main/capturesettings/aeb"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/aperture", "Aperture", "3.5", CameraConfigEntryType.RADIO, new String[] { "3.5",
				"4", "4.5", "5", "5.6", "6.3", "7.1", "8", "9", "10", "11", "13", "14", "16", "18", "20", "22" }),
				cmd.getCameraConfig().get("/main/capturesettings/aperture"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/autoexposuremode", "Canon Auto Exposure Mode", "Manual",
				CameraConfigEntryType.RADIO, new String[] { "P", "TV", "AV", "Manual", "Bulb", "A_DEP", "DEP", "Custom", "Lock", "Green", "Night Portrait",
						"Sports", "Portrait", "Landscape", "Closeup", "Flash Off" }), cmd.getCameraConfig().get("/main/capturesettings/autoexposuremode"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/bracketmode", "Bracket Mode", "0", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/capturesettings/bracketmode"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/drivemode", "Drive Mode", "Continuous", CameraConfigEntryType.RADIO, new String[] {
				"Single", "Continuous", "Timer 10 sec", "Timer 2 sec", "Unknown value 0007" }), cmd.getCameraConfig().get("/main/capturesettings/drivemode"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/focusmode", "Focus Mode", "Unknown value 0003", CameraConfigEntryType.RADIO,
				new String[] { "One Shot", "AI Focus", "AI Servo", "Unknown value 0003" }), cmd.getCameraConfig().get("/main/capturesettings/focusmode"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/meteringmode", "Metering Mode", "Evaluative", CameraConfigEntryType.RADIO,
				new String[] { "Evaluative", "Partial", "Spot", "Center-weighted average" }), cmd.getCameraConfig().get("/main/capturesettings/meteringmode"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/picturestyle", "Picture Style", "User defined 1", CameraConfigEntryType.RADIO,
				new String[] { "Standard", "Portrait", "Landscape", "Neutral", "Faithful", "Monochrome", "Unknown value 0087", "User defined 1",
						"User defined 2", "User defined 3" }), cmd.getCameraConfig().get("/main/capturesettings/picturestyle"));
		TestCase.assertEquals(new CameraConfigEntry("/main/capturesettings/shutterspeed", "Shutter Speed", "1", CameraConfigEntryType.RADIO, new String[] {
				"bulb", "30", "25", "20", "15", "13", "10", "8", "6", "5", "4", "3.2", "2.5", "2", "1.6", "1.3", "1", "0.8", "0.6", "0.5", "0.4", "0.3", "1/4",
				"1/5", "1/6", "1/8", "1/10", "1/13", "1/15", "1/20", "1/25", "1/30", "1/40", "1/50", "1/60", "1/80", "1/100", "1/125", "1/160", "1/200",
				"1/250", "1/320", "1/400", "1/500", "1/640", "1/800", "1/1000", "1/1250", "1/1600", "1/2000", "1/2500", "1/3200", "1/4000" }), cmd
				.getCameraConfig().get("/main/capturesettings/shutterspeed"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/colorspace", "Color Space", "sRGB", CameraConfigEntryType.RADIO, new String[] { "sRGB",
				"AdobeRGB" }), cmd.getCameraConfig().get("/main/imgsettings/colorspace"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/imageformat", "Image Format", "Large Fine JPEG", CameraConfigEntryType.RADIO,
				new String[] { "Large Fine JPEG", "Large Normal JPEG", "Medium Fine JPEG", "Medium Normal JPEG", "Small Fine JPEG", "Small Normal JPEG",
						"Smaller JPEG", "Tiny JPEG", "RAW + Large Fine JPEG", "RAW" }), cmd.getCameraConfig().get("/main/imgsettings/imageformat"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/imageformatsd", "Image Format SD", "Large Fine JPEG", CameraConfigEntryType.RADIO,
				new String[] { "Large Fine JPEG", "Large Normal JPEG", "Medium Fine JPEG", "Medium Normal JPEG", "Small Fine JPEG", "Small Normal JPEG",
						"Smaller JPEG", "Tiny JPEG", "RAW + Large Fine JPEG", "RAW" }), cmd.getCameraConfig().get("/main/imgsettings/imageformatsd"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/iso", "ISO Speed", "3200", CameraConfigEntryType.RADIO, new String[] { "Auto", "100",
				"200", "400", "800", "1600", "3200", "6400", "12800" }), cmd.getCameraConfig().get("/main/imgsettings/iso"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/whitebalance", "WhiteBalance", "Auto", CameraConfigEntryType.RADIO, new String[] {
				"Auto", "Daylight", "Shadow", "Cloudy", "Tungsten", "Fluorescent", "Flash", "Manual" }),
				cmd.getCameraConfig().get("/main/imgsettings/whitebalance"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/whitebalanceadjusta", "WhiteBalance Adjust A", "0", CameraConfigEntryType.RADIO,
				new String[] { "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" }), cmd.getCameraConfig()
				.get("/main/imgsettings/whitebalanceadjusta"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/whitebalanceadjustb", "WhiteBalance Adjust B", "0", CameraConfigEntryType.RADIO,
				new String[] { "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" }), cmd.getCameraConfig()
				.get("/main/imgsettings/whitebalanceadjustb"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/whitebalancexa", "WhiteBalance X A", "0", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/imgsettings/whitebalancexa"));
		TestCase.assertEquals(new CameraConfigEntry("/main/imgsettings/whitebalancexb", "WhiteBalance X B", "0", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/imgsettings/whitebalancexb"));
		TestCase.assertEquals(new CameraConfigEntry("/main/other/5001", "Battery Level", "100", CameraConfigEntryType.MENU, new String[] { "100", "0", "75",
				"0", "50" }), cmd.getCameraConfig().get("/main/other/5001"));
		TestCase.assertEquals(new CameraConfigEntry("/main/other/d303", "PTP Property 0xd303", "1", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/other/d303"));
		TestCase.assertEquals(new CameraConfigEntry("/main/other/d402", "PTP Property 0xd402", "Canon EOS 600D", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/other/d402"));
		TestCase.assertEquals(new CameraConfigEntry("/main/other/d406", "PTP Property 0xd406", "Unknown Initiator", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/other/d406"));
		TestCase.assertEquals(new CameraConfigEntry("/main/other/d407", "PTP Property 0xd407", "1", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/other/d407"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/artist", "Artist", "Saur", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/settings/artist"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/autopoweroff", "Auto Power Off", "0", CameraConfigEntryType.TEXT), cmd.getCameraConfig()
				.get("/main/settings/autopoweroff"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/capture", "Capture", "0", CameraConfigEntryType.TOGGLE),
				cmd.getCameraConfig().get("/main/settings/capture"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/capturetarget", "Capture Target", "Memory card", CameraConfigEntryType.RADIO, new String[] {
				"Internal RAM", "Memory card" }), cmd.getCameraConfig().get("/main/settings/capturetarget"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/copyright", "Copyright", "", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/settings/copyright"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/customfuncex", "Custom Functions Ex", "10,c189,d105,3,", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/settings/customfuncex"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/datetime", "Camera Date and Time", "1409600309", "Mon Sep  1 22:38:29 2014",
				CameraConfigEntryType.DATE), cmd.getCameraConfig().get("/main/settings/datetime"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/evfmode", "EVF Mode", "1", CameraConfigEntryType.RADIO, new String[] { "0", "1" }), cmd
				.getCameraConfig().get("/main/settings/evfmode"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/movierecord", "Movie Recording", "0", CameraConfigEntryType.TEXT), cmd.getCameraConfig()
				.get("/main/settings/movierecord"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/output", "Camera Output", "Unknown value 0000", CameraConfigEntryType.RADIO, new String[] {
				"TFT", "PC", "TFT + PC", "Unknown value 0004", "Unknown value 0005", "Unknown value 0006", "Unknown value 0007", "Unknown value 0000" }), cmd
				.getCameraConfig().get("/main/settings/output"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/ownername", "Owner Name", "", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/settings/ownername"));
		TestCase.assertEquals(new CameraConfigEntry("/main/settings/reviewtime", "Quick Review Time", "4 seconds", CameraConfigEntryType.RADIO, new String[] {
				"None", "2 seconds", "4 seconds", "8 seconds", "Hold" }), cmd.getCameraConfig().get("/main/settings/reviewtime"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/availableshots", "Available Shots", "1934", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/availableshots"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/batterylevel", "Battery Level", "100%", CameraConfigEntryType.TEXT), cmd.getCameraConfig()
				.get("/main/status/batterylevel"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/cameramodel", "Camera Model", "Canon EOS 600D", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/cameramodel"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/deviceversion", "Device Version", "3-1.0.2", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/deviceversion"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/eosserialnumber", "Serial Number", "228076076384", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/eosserialnumber"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/lensname", "Lens Name", "EF-S18-135mm f/3.5-5.6 IS", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/lensname"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/manufacturer", "Camera Manufacturer", "Canon Inc.", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/manufacturer"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/model", "Camera Model", "2147484294", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/status/model"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/ptpversion", "PTP Version", "256", CameraConfigEntryType.TEXT),
				cmd.getCameraConfig().get("/main/status/ptpversion"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/serialnumber", "Serial Number", "e31a91f3389c47dba584133793106db7",
				CameraConfigEntryType.TEXT), cmd.getCameraConfig().get("/main/status/serialnumber"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/shuttercounter", "Shutter Counter", "10206", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/shuttercounter"));
		TestCase.assertEquals(new CameraConfigEntry("/main/status/vendorextension", "Vendor Extension", "None", CameraConfigEntryType.TEXT), cmd
				.getCameraConfig().get("/main/status/vendorextension"));
	}
}
