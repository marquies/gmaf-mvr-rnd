package de.swa.fuh.audio.plugin;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AudioPlugin implements GMAF_Plugin {
	public boolean canProcess(String extension) {
		if (extension.endsWith("mp3")) return true;
		return false;
	}

	public void process(URL url, File f, byte[] bytes, MMFG mmfg) {
		try {
			Node title = new Node("Title", f.getName(), mmfg);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Vector<Node> detected = new Vector<Node>();
	public Vector<Node> getDetectedNodes() {
		return detected;
	}

	public boolean isGeneralPlugin() {
		return false;
	}


	public boolean providesRecoursiveData() {
		return false;
	}
}
