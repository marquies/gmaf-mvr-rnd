package de.swa.tools;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeGenerator;
import de.swa.gmaf.GMAF;
import de.swa.gmaf.extensions.defaults.GeneralDictionary;
import de.swa.gmaf.extensions.defaults.Word;
import de.swa.mmfg.MMFG;

public class CollectionReferenceDetection {
	
	public static void main(String[] args) throws Exception {
		// normalize terms
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/diss", "root", "stw476.log");
		Statement stmt = c.createStatement();
		String[] str = new String[] {"bird", "dog", "cat", "car", "train", "clothing", "cloud", "crab", "cucumber", "door", "face", "orange", "plant", "street", "woman", "apple", "banana"};
		String[] files = new String[] {"/Users/stefan_wagenpfeil/Desktop/Team1.xml", "/Users/stefan_wagenpfeil/Desktop/Team3.xml", "/Users/stefan_wagenpfeil/Desktop/Team2.xml"};

		Hashtable<String, Vector<String>> lineHt = new Hashtable<String, Vector<String>>();
		for (String s : str) {
			ResultSet rs = stmt.executeQuery("select image_id from challenge where fulltext_keys like '%" + s + "%';");
			Vector<String> reference_objects = new Vector<String>();
			while (rs.next()) {
				reference_objects.add(rs.getString("image_id"));
			}
//			System.out.println(s + ": " + reference_objects.size());

			for (String f : files) {
				Vector<String> lines = new Vector<String>();
				if (lineHt.get(f) != null) {
					lines = lineHt.get(f);
				}
				else {
					RandomAccessFile rf = new RandomAccessFile(f, "r");
					String line = "";
					String content = "";
					while ((line = rf.readLine()) != null) {
						line = line.toLowerCase();
						if (line.indexOf("<gmaf-data>") >= 0 || line.indexOf("<xs:gmaf-data>") >= 0) {
							lines.add(content);
							content = "";
						}
						content += line;
					}
					lineHt.put(f, lines);
				}

				int true_positive = 0;
				int false_positive = 0;

				for (String l : lines) {
					if (l.indexOf(s) > 0) {
						boolean found = false;
						for (String r : reference_objects) {
							if (l.indexOf(r) >= 0 && !found) {
								true_positive ++;
								found = true;
							}
						}
						if (!found) false_positive ++;
					}
				}
				System.out.println(s + "\t" + reference_objects.size() + "\t" + f + "\t" + true_positive + "\t" + false_positive + "\t" + (reference_objects.size() - true_positive));
//				System.out.println("true_pos : " + true_positive);
//				System.out.println("false_pos: " + false_positive);
//				System.out.println("false_neg: " + (reference_objects.size() - true_positive));
			}
		}
	}
	
	public static void main5(String[] args) throws Exception {
		// normalize terms
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/diss", "root", "stw476.log");
		Statement stmt = c.createStatement();

		ResultSet rs = stmt.executeQuery("select * from challenge;");
		Vector<String> updates = new Vector<String>();

		GeneralDictionary dict = GeneralDictionary.getInstance();

		while (rs.next()) {
			String db_keys = getNormalizedStrings(rs, dict);
			String img = rs.getString("source_img");
			updates.add("update challenge set fulltext_keys='" + db_keys + "' where source_img='" + img + "';");
		}
		for (String s : updates) {
			stmt.executeUpdate(s);
		}		
	}
	
	private static String getNormalizedStrings(ResultSet rs, GeneralDictionary dict) throws Exception {
		String all_keys = rs.getString("google_keys") + "," + rs.getString("annotation_keys") + "," + rs.getString("prio1") + "," + rs.getString("prio2") + "," + rs.getString("annotation_description");
		System.out.println("ALL: " + all_keys);
		Vector<String> new_keys = new Vector<String>();
		all_keys = all_keys.replace(" ", ",");
		all_keys = all_keys.replace(",", " , ");
		all_keys = all_keys.replace("\n", "");
		all_keys = all_keys.replace("root-asset", "");
		String[] str = all_keys.split(",");
		for (String s : str) {
			if (s.equals("null")) continue;
			if (s.indexOf("_") > 0) s = s.substring(0, s.indexOf("_") + 1);
			Vector<Word> words = dict.getWord(s.trim());
			for (Word w : words) {
				String wordStem = w.getWordStem();
				if (wordStem == null) wordStem = s;
				if (!new_keys.contains(wordStem.trim())) new_keys.add(wordStem.trim());
			}
			if (!new_keys.contains(s.trim())) new_keys.add(s.trim());
		}
		String new_keys_string = "";
		for (String s : new_keys) {
			if (s != null && !s.equals("null") && !s.equals(""))
				new_keys_string += s + ",";
		}
		System.out.println("NEW: " + new_keys_string);
		return new_keys_string;
	}
	
	public static void main4(String[] args) throws Exception {
		// normalize terms
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/diss", "root", "stw476.log");
		Statement stmt = c.createStatement();
		
		
//		Image;Keywords (comma separated);Prio-1 Objects;Other Objects;Description;Wikidata;
//		0440.png;funfair, fairground booth, minion, soft toy, lottery;funfair;lottery, fairground booth, soft toy;A fairground booth on a german funfair offers lottery tickets to win different kinds of soft toys.;;

		RandomAccessFile rf = new RandomAccessFile("FP2021_Annotations.csv", "r");
		String line = rf.readLine();
		while ((line = rf.readLine()) != null) {
			try {
				line = line.replace(";", " ; ");
				String[] str = line.split(";");
				String image = str[0];
				String keys = str[1];
				String prio1 = str[2];
				String prio2 = str[3];
				String descr = str[4];
				String wiki = str[5];
				
				String sql = "update challenge set prio1='" + prio1.trim() + "', prio2='" + prio2.trim() + "', annotation_keys='" + keys + "', annotation_description='" + descr + "', wiki='" + wiki + "' where source_img='" + image.trim() + "';";
				stmt.executeUpdate(sql);
				System.out.println(sql);
			}
			catch (Exception x) {
				System.out.println("Error: " + x);
			}
		}
	}
	
	public static void main3(String[] args) throws Exception {
		// normalize terms
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/diss", "root", "stw476.log");
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("select image_id, google_keys_normalized from challenge;");
		Vector<String> updates = new Vector<String>();
		Hashtable<String, Integer> count = new Hashtable<String, Integer>();
		while (rs.next()) {
			String[] str = rs.getString(2).split(",");
			for (String s : str) {
				s = s.trim();
				if (count.containsKey(s)) {
					int ct = count.get(s);
					ct++;
					count.remove(s);
					count.put(s, ct);
				} else
					count.put(s, 1);
			}
		}
		rs.close();

		for (String key : count.keySet()) {
			int ct = count.get(key);
			if (ct > 20)
				System.out.println(key + " : " + ct);
		}
	}

	public static void main2(String[] args) throws Exception {
		// normalize terms
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/diss", "root", "stw476.log");
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery("select image_id, google_keys from challenge;");
		Vector<String> updates = new Vector<String>();
		while (rs.next()) {
			String id = rs.getString(1);
			String keys = rs.getString(2);
			keys = keys.replace("\n", "");
			keys = keys.replace("root-asset,", "");
			String[] str = keys.split(",");
			String normalized = "";
			Vector<String> terms = new Vector<String>();
			for (String s : str) {
				if (s.indexOf("_") > 0)
					s = s.substring(0, s.indexOf("_"));
				if (!terms.contains(s))
					terms.add(s);
			}
			for (String t : terms)
				normalized += t + ", ";
			updates.add(
					"update challenge set google_keys_normalized='" + normalized + "' where image_id='" + id + "';\n");
		}
		rs.close();
		for (String s : updates)
			stmt.execute(s);
	}

	public static void main1(String[] args) throws Exception {
		// detect objects
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost/diss", "root", "stw476.log");
		Statement stmt = c.createStatement();

//		File f = new File("/Users/stefan_wagenpfeil/Library/Mobile Documents/com~apple~CloudDocs/Dissertation/Lehrstuhl/Challenge_2021/Dataset/map_challenge.txt");
//		RandomAccessFile rf = new RandomAccessFile(f, "r");
//		String line = "";
//		while ((line = rf.readLine()) != null) {
//			try {
//				String[] str = line.split(";");
//				String file = str[0].trim();
//				String id = str[1].trim();
//				stmt.execute("replace into challenge(source_img, image_id, google_keys) values('" + file + "', '" + id + "', '');");
//				System.out.println(file + " - " + id);
//			}
//			catch (Exception x) {}
//		}
//		rf.close();

		ResultSet rs = stmt.executeQuery("select image_id from challenge where google_keys = '';");
		Vector<String> ids = new Vector<String>();
		while (rs.next())
			ids.add(rs.getString("image_id"));
		rs.close();

		GMAF gmaf = new GMAF();
		Vector<String> pp = new Vector<String>();
		pp.add("de.swa.gmaf.plugin.googlevision.ObjectDetection");
		gmaf.setProcessingPlugins(pp);
		for (String file : ids) {
			try {
				File fx = new File(
						"/Users/stefan_wagenpfeil/Library/Mobile Documents/com~apple~CloudDocs/Dissertation/Lehrstuhl/Challenge_2021/Dataset/Collection_Challenge/"
								+ file);
				System.out.println(fx.getAbsolutePath());
				System.out.println(fx.exists());

				MMFG mmfg = gmaf.processAsset(fx);
				GraphCode gc = GraphCodeGenerator.generate(mmfg);
				Vector<String> objects = gc.getDictionary();
				String s = "";
				for (String si : objects)
					s += si + ", ";
				s = s.substring(0, s.length() - 2).trim();
				s = s.replace("'", "");
				stmt.execute("update challenge set google_keys='" + s + "' where image_id='" + file + "';");
				System.out.println("processed " + file + ": " + s);
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
	}
}
