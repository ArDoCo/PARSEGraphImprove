package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Collectors;


/**
 * A helper class to centralize the naming of the INodeTypes and IArcTypes for Phrases 
 * 
 * @author dominik
 *
 */
public abstract class PhraseHelper {
	

	public static final String BEGINN_TYPE_PREFIX = "begin_";
	public static final String INSIDE_TYPE_PREFIX = "inside_";
	
	
	public static String buildBeginPhraseValue(PhraseEnum chunkIob) {
		return "B-" + chunkIob.getValue();
	}
	
	public static String buildInsidePhraseValue(PhraseEnum chunkIob) {
		return "I-" + chunkIob.getValue();
	}
	
	public static String asBeginnArcTypeName(PhraseEnum chunkIob) {
		return BEGINN_TYPE_PREFIX + chunkIob.asNodeTypeName();
	}
	
	public static String asInsideArcTypeName(PhraseEnum chunkIob) {
		return INSIDE_TYPE_PREFIX + chunkIob.asNodeTypeName();
	}
	
	public static List<String> allNodeTypes() {
		return asList(PhraseEnum.values()).stream().map(ChunkIOBEnum -> ChunkIOBEnum.asNodeTypeName()).collect(Collectors.toList());
		
	}
}