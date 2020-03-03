package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;

import static edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseHelper.*;
import static java.util.Arrays.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import edu.kit.ipd.are.graphimprove.nlp.GraphImproveStrategy;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;

/**
 * The GraphImproveStrategy to improve graph with the type of phrases
 * 
 * @author dominik
 *
 */
public class PhraseStrategy implements GraphImproveStrategy {

	public static final String CHUNKIOB_ATTRIBUTE_KEY = "CHUNK_IOB";

	Map<String, INodeType> nodeTypes = new HashMap<>();
	Map<String, IArcType> arcTypes = new HashMap<>();

	Map<String, Function<INode, Void>> functionMap = new HashMap<>();

	IGraph graph;
	INode phraseNode;

	public PhraseStrategy() {
	}

	@Override
	public void createArcTypes(IGraph graph) {
		asList(PhraseEnum.values()).forEach(chunkIOBName -> {
			arcTypes.put(buildBeginPhraseValue(chunkIOBName),
					graph.createArcType(asBeginnArcTypeName(chunkIOBName)));

			this.functionMap.put(buildBeginPhraseValue(chunkIOBName), (tokenNode) -> {
				if (phraseNode != null) {
					phraseNode = null;
				}

				phraseNode = graph
						.createNode(nodeTypes.get(tokenNode.getAttributeValue(GraphBuilder.CHUNK_IOB_ATTRIBUTE)));
				phraseNode.setAttributeValue(CHUNKIOB_ATTRIBUTE_KEY, chunkIOBName.getValue());
				IArc arc = graph.createArc(tokenNode, phraseNode,
						arcTypes.get(tokenNode.getAttributeValue(GraphBuilder.CHUNK_IOB_ATTRIBUTE)));
				return null;
			});

			arcTypes.put(buildInsidePhraseValue(chunkIOBName),
					graph.createArcType(asInsideArcTypeName(chunkIOBName)));
			this.functionMap.put(buildInsidePhraseValue(chunkIOBName), (tokenNode) -> {
				graph.createArc(tokenNode, phraseNode,
						arcTypes.get(tokenNode.getAttributeValue(GraphBuilder.CHUNK_IOB_ATTRIBUTE)));
				return null;
			});
		});
	}

	@Override
	public void createNodeTypes(IGraph graph) {
		asList(PhraseEnum.values()).forEach(chunkIOBName -> {
			INodeType nodeTyp = graph.createNodeType(chunkIOBName.asNodeTypeName());
			nodeTyp.addAttributeToType("String", CHUNKIOB_ATTRIBUTE_KEY);
			nodeTypes.put(buildBeginPhraseValue(chunkIOBName), nodeTyp);
		});
	}

	@Override
	public void executeImprovement(IGraph graph) {
		List<INode> tokenNodes = getTokenNodes(graph);
		tokenNodes.forEach((tokenNode) -> {
			functionMap.getOrDefault(((String) tokenNode.getAttributeValue(GraphBuilder.CHUNK_IOB_ATTRIBUTE)), (token2) -> {
				if (phraseNode != null) {
					phraseNode = null;
				}
				return null;
			}).apply(tokenNode);
		});
	}

	private List<INode> getTokenNodes(IGraph graph) {
		return graph.getNodesOfType(graph.getNodeType("token"));
	}
	


}
