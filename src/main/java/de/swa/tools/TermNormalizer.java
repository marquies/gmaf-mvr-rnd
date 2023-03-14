package de.swa.tools;

import java.util.Vector;

import de.swa.gmaf.extensions.defaults.GeneralDictionary;
import de.swa.gmaf.extensions.defaults.Word;

/** returns normalized terms based on word-stems and an english dictionary **/
public class TermNormalizer {
	public static Vector<Word> getNormalizedTerm(String term) {
		GeneralDictionary dict = GeneralDictionary.getInstance();
		Vector<Word> words = dict.getWord(term);
		return words;
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: java -cp gmaf.jar de.swa.tools.TermNormalizer myTestTerm1 myTestTerm2 myTestTerm3 myTestTermX");
		}
		else {
			System.out.println("GMAF Term Normalizer:");
			System.out.println("=====================");
			for (int i = 0; i < args.length; i++) {
				System.out.println("Term: " + args[i]);
				Vector<Word> words = getNormalizedTerm(args[i]);
				for (Word w : words) {
					System.out.println(" -> " + w.getWord() + ", Type: " + w.getType() + ", Wordstem: " + w.getWordStem());
				}
			}
		}
	}
}
