package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;

import java.util.List;
import java.util.stream.Collectors;

import edu.kit.ipd.are.graphimprove.nlp.GraphImproveStrategy;
import edu.kit.ipd.are.graphimprove.nlp.helper.Filter;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;

/**
 * The GraphImproveStrategy to improve the graph with arc between phrases and sentence
 *  
 * @author dominik
 *
 */
public class SentenceToPhraseStrategy implements GraphImproveStrategy {

	public static final String SENTENCE2PHRASE = "sentence2phrase";
	private IArcType sentenceToPhraseArcTyp;

	@Override
	public void createArcTypes(IGraph graph) {
		this.sentenceToPhraseArcTyp = graph.createArcType(SENTENCE2PHRASE);
	}

	@Override
	public void createNodeTypes(IGraph graph) {

	}

	@Override
	public void executeImprovement(IGraph graph) {
		List<INodeType> phraseTypes = PhraseHelper.allNodeTypes().stream()
				.map(nodeTypeName -> graph.getNodeType(nodeTypeName))
				.collect(Collectors.toList());
		List<INode> phrases = phraseTypes.stream()
				.map(nodeType -> graph.getNodesOfType(nodeType))
				.flatMap(list -> list.stream())
				.collect(Collectors.toList());
		
		phrases.forEach(phrase -> {
			PhraseEnum type = PhraseEnum.fromString((String) phrase.getAttributeValue(PhraseStrategy.CHUNKIOB_ATTRIBUTE_KEY));
			INode word = Filter.incomming(phrase, PhraseHelper.asBeginnArcTypeName(type)).findFirst().get();
			
			INode sentence = Filter.outgoing(word, SentenceStrategy.WORD_TO_SENCTENCE_ARC_TYPE).findFirst().get();
			graph.createArc(sentence, phrase, this.sentenceToPhraseArcTyp);
		});

	}

}
