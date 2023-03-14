package de.swa.tools;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.UUID;

public class CollectionCreator {
	public static void main(String[] args) throws Exception {
		File[] input = new File("/Users/stefan_wagenpfeil/Library/Mobile Documents/com~apple~CloudDocs/Dissertation/Lehrstuhl/Challenge_2021/Dataset/Collection_Input").listFiles();
		File l = new File("/Users/stefan_wagenpfeil/Library/Mobile Documents/com~apple~CloudDocs/Dissertation/Lehrstuhl/Challenge_2021/Dataset/map.txt");
		RandomAccessFile log = new RandomAccessFile(l, "rw");
		log.seek(l.length());
		log.writeBytes("\n");
		
		for (File fi : input) {
			UUID id = UUID.randomUUID();
			log.writeBytes(fi.getName() + " ; " + id + ".png\n");
			fi.renameTo(new File("/Users/stefan_wagenpfeil/Library/Mobile Documents/com~apple~CloudDocs/Dissertation/Lehrstuhl/Challenge_2021/Dataset/Collection/" + id + ".png"));
		}
	}
}
