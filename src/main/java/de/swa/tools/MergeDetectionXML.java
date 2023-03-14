package de.swa.tools;

import java.io.File;
import java.io.RandomAccessFile;

import de.swa.mmfg.builder.DetectionExporter;

/** merges detected object XMLs into a single large file **/
public class MergeDetectionXML {
	public static void main(String[] args) {
		int counter = 0;
		if (args.length == 0) {
			System.out.println("usage: java -cp gmaf.jar de.swa.tools.MergeDetectionXML XMLFolder output.xml");
		}
		else {
			System.out.println("GMAF XML Merger:");
			System.out.println("================");

			File f = new File(args[0]);
			File fout = new File(args[1]);
			
			DetectionExporter de = new DetectionExporter();
			String header = de.startFile();
			String footer = de.endFile();
			
			if (f.isDirectory()) {
				try {
					RandomAccessFile rf = new RandomAccessFile(fout, "rw");
					rf.writeBytes(header + "\n");
					for (File fi : f.listFiles()) {
						RandomAccessFile rfi = new RandomAccessFile(fi, "r");
						String content = "";
						String line = "";
						while ((line = rfi.readLine()) != null) {
							content += line;
						}
						try {
							int start = content.indexOf("<gmaf-data>");
							int end = content.lastIndexOf("</gmaf-data>");
							String xml = content.substring(start, end + 12);
							rf.writeBytes(xml + "\n");
							counter ++;
						}
						catch (Exception x) {
							x.printStackTrace();
						}
					}
					rf.writeBytes(footer + "\n");
					rf.close();
				}
				catch (Exception x) {
					x.printStackTrace();
				}
			}
			else System.out.println("Error: " + f.getAbsolutePath() + " not a directory.");
		}
		
		System.out.println("merged " + counter + " files into " + args[1]);
	}
}
