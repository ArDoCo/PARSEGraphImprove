package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.kit.ipd.are.graphimprove.nlp.GraphImproveStrategy;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;

/**
 * The GraphImproveStrategy to improve the graph with the INodes of INodeType sentence
 *  
 * @author dominik
 *
 */
public class SentenceStrategy implements GraphImproveStrategy{	

	
	
	public static final String SENTENCE_NODE_TYPE = "sentence";
	public static final String SENTENCE_TO_SENCTENCE_ARC_TYPE = "sentence2sentence";
	public static final String WORD_TO_SENCTENCE_ARC_TYPE = "word2sentence";	
	public static final String SENTENCE_NUMBER_ATTRIBUTE = "sentenceNumber";


	private static final int FIRST_SENTENCE_NUMBER = 0;
	
	private static IArcType word2sentenceArcTyp;
	private static IArcType sentence2sentenceArcTyp;
	private static INodeType sentenceNodeType;

	public void createArcTypes(IGraph graph) {
		word2sentenceArcTyp = graph.createArcType(WORD_TO_SENCTENCE_ARC_TYPE);
		sentence2sentenceArcTyp = graph.createArcType(SENTENCE_TO_SENCTENCE_ARC_TYPE);		
	}
	
	public void createNodeTypes(IGraph graph) {
		sentenceNodeType = graph.createNodeType(SENTENCE_NODE_TYPE);
		sentenceNodeType.addAttributeToType("int", SENTENCE_NUMBER_ATTRIBUTE);
	}
	
	public void executeImprovement(IGraph graph) {
		Map<Integer, List<INode>> sentence = this.getTokenGroupedBySentence(graph);
		this.buildSentence(sentence, graph);
		
	}
	

	private Map<Integer, List<INode>> getTokenGroupedBySentence(IGraph graph) {
		return graph.getNodes().stream().collect(
				Collectors.groupingBy((node) -> (int) node.getAttributeValue(GraphBuilder.SENTENCE_NUMBER_ATTRIBUTE)));
	}
	
	private void buildSentence(Map<Integer, List<INode>> sentences, IGraph graph) {
		List<INode> tempSentences = new ArrayList<>();
		sentences.forEach((sentenceNumber, tokenList) -> {
			INode sentence = graph.createNode(sentenceNodeType);
			tempSentences.add(sentence);
			sentence.setAttributeValue(GraphBuilder.SENTENCE_NUMBER_ATTRIBUTE, sentenceNumber);
			tokenList.stream().forEach((token) -> graph.createArc(token, sentence, word2sentenceArcTyp));

			if (sentenceNumber > FIRST_SENTENCE_NUMBER) {
				graph.createArc(tempSentences.get(sentenceNumber - 1), sentence, this.sentence2sentenceArcTyp);
			}
		});
	}

}
