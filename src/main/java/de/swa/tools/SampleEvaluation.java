package de.swa.tools;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.swa.gmaf.extensions.defaults.Word;

/** performs a sample evaluation based on a single xml-file compared to the annotations in a csv file **/
public class SampleEvaluation {
	public static void main(String[] args) {
//		args = new String[] {"/Users/stefan_wagenpfeil/Desktop/output.xml", "/Users/stefan_wagenpfeil/Desktop/annotations.csv"};
		if (args.length != 2) {
			System.out.println("usage: java -cp gmaf.jar de.swa.tools.SampleEvaluation result.xml annotations.csv");
		}
		else {
			System.out.println("GMAF Sample Evaluation:");
			System.out.println("=======================");

			Vector<String> lines = new Vector<String>();
			try {
				RandomAccessFile rf = new RandomAccessFile(args[1], "r");
				String line = "";
				int counter = 0;
				while ((line = rf.readLine()) != null) {
					line = line.replace(" ", ",");
					line = line.replace("\t", ",");
					line = line.replace(";", ",");
					lines.add(line);
					counter ++;
				}
				System.out.println(".... read annotations file, found " + counter + " annotations for images.");
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newDefaultInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(new File(args[0]));
				Element elem = doc.getDocumentElement();
				NodeList nl = elem.getElementsByTagName("gmaf-data");
				for (int i = 0; i < nl.getLength(); i++) {
					Node ni = nl.item(i);
					String fileName = "";
					Vector<String> objects = new Vector<String>();
					for (int j = 0; j < ni.getChildNodes().getLength(); j++) {
						Node nj = ni.getChildNodes().item(j);
						if (nj.getNodeName().indexOf("file") >= 0) {
							fileName = nj.getFirstChild().getNodeValue();
						}
						else if (nj.getNodeName().indexOf("objects") >= 0) {
							NodeList obj = nj.getChildNodes();
							for (int k = 0; k < obj.getLength(); k++) {
								Node obj_k = obj.item(k);
								NodeList descr = obj_k.getChildNodes();
								for (int l = 0; l < descr.getLength(); l++) {
									Node ll = descr.item(l);
									if (ll.getNodeName().indexOf("term") >= 0) {
										String t = ll.getFirstChild().getNodeValue();
										boolean contained = false;
										for (String s : objects) {
											s = s.trim();
											t = t.trim();
											if (s.equals(t)) {
												contained = true;
												System.out.println("Contained " + t);
											}
										}
										if (!contained) objects.add(t);
									}
								}
							}
						}
					}
					
					
					System.out.println("File    : " + fileName);
					line = "";
					line = line.replace("\"", "");
					line = line.replace(",,", ",");
					int objectCount = objects.size();
					int hitcount = 0;
					for (String l : lines) {
						if (l.indexOf(fileName) >= 0) line = l;
					}
					System.out.println("Annotated: " + line);

					for (String s : objects) {
						System.out.print("Detected: " + s);
						if (line.indexOf(s) >= 0) {
							System.out.println(" -> ok");
							hitcount ++;
						}
						else {
							System.out.println(" -> error");
						}
					}
					

					/*
					int objectCount = objects.size();
					int hitCount = 0;
					for (String s : objects) {
						String term = s;
						Vector<Word> words = TermNormalizer.getNormalizedTerm(term);
						for (Word w : words) {
							boolean found = false;
							String[] str = line.split(",");
							for (String si : str) {
								si = si.replace("\"", "").trim();
//								System.out.println("SI: " + si);
								Vector<Word> words2 = TermNormalizer.getNormalizedTerm(si);
								for (Word w2 : words2) {
									if (w.getWord().equals(w2.getWord()) || w.getWordStem().equals(w2.getWordStem())) {
										found = true;
										hitCount ++;
										System.out.println(" " + w.getWord() + "/" + term + " -> " + found);
									}
									else if (w.getWord().indexOf(w2.getWord()) >= 0 || w2.getWord().indexOf(w.getWord()) >= 0) {
										found = true;
										hitCount ++;
									}
								}
							}
							System.out.println(" " + w.getWord() + "/" + term + " -> " + found);
						}
					}
					*/
					System.out.println(" => " + hitcount + " of " + objectCount + " detected.");
					System.out.println("------------------------------------------------------");
				}
			}
			catch (Exception x) {
				x.printStackTrace();
			}
		}		
	}
}
