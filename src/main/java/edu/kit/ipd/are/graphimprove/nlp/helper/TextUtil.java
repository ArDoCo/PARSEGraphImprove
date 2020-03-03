package edu.kit.ipd.are.graphimprove.nlp.helper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.kit.ipd.are.archdoclink.nlp.Util;
import edu.kit.ipd.are.archdoclink.nlp.Word;

public class TextUtil {
	
	public static String concat(List<? extends Word> words) {
		return concat(words.stream());
	}
	
	public static String concat(Stream<? extends Word> words) {
		return Util.getTextFromWords(words);
	}

}
