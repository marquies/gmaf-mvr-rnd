package de.swa.fuh.audio;

import java.io.File;

import de.swa.fuh.audio.plugin.AudioPlugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.builder.FeatureVectorBuilder;
import de.swa.mmfg.builder.XMLEncodeDecode;

public class Test {
	public static void main(String[] args) {
		File f1 = new File("collection/sample.mp3");
		
		AudioPlugin ap = new AudioPlugin();
		MMFG mmfg = new MMFG();
		ap.process(null, f1, null, mmfg);
		String result = FeatureVectorBuilder.flatten(mmfg, new XMLEncodeDecode());
		System.out.println(result);
	}
}
