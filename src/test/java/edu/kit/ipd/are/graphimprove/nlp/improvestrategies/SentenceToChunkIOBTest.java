package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.are.graphimprove.nlp.helper.Filter;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceStrategy;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceToPhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.shared.Helper;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;

public class SentenceToChunkIOBTest {

	private static final String NP_TEST_1 = "This is an house on a street. And this is an other sentence.";
	private SentenceToPhraseStrategy sentenceToChunkIOBStratgy;
	private IGraph graph;


	@BeforeEach
	void beforeEach() throws PipelineStageException, MissingDataException {
		this.sentenceToChunkIOBStratgy = new SentenceToPhraseStrategy();

	}

	void setupGraph(String input) throws MissingDataException, PipelineStageException {
		this.graph = Helper.initPipeline(input).getGraph();
		SentenceStrategy sentenceStrategy = new SentenceStrategy();
		sentenceStrategy.createNodeTypes(graph);
		sentenceStrategy.createArcTypes(graph);
		sentenceStrategy.executeImprovement(graph);

		PhraseStrategy chunkIOBStrategy = new PhraseStrategy();
		chunkIOBStrategy.createNodeTypes(graph);
		chunkIOBStrategy.createArcTypes(graph);
		chunkIOBStrategy.executeImprovement(graph);

		this.sentenceToChunkIOBStratgy.createNodeTypes(this.graph);
		this.sentenceToChunkIOBStratgy.createArcTypes(this.graph);
		this.sentenceToChunkIOBStratgy.executeImprovement(this.graph);
	}


	@Test
	void test_link_sentences_with_phrases() throws MissingDataException, PipelineStageException {

		setupGraph(NP_TEST_1);
		List<INode> tokens = this.graph.getNodesOfType(this.graph.getNodeType("token"));
		tokens.forEach(token -> System.out.println(token));


		List<INode> sentences = this.graph.getNodesOfType(graph.getNodeType(SentenceStrategy.SENTENCE_NODE_TYPE));

		assertEquals(2, sentences.size());

		List<INode> phrases = Filter.outgoing(sentences.get(0), SentenceToPhraseStrategy.SENTENCE2PHRASE).collect(Collectors.toList());

		assertEquals(5, phrases.size());
		phrases.forEach(phrase -> System.out.println(phrase));

		List<INode> phrasesSentence2 = Filter.outgoing(sentences.get(1), SentenceToPhraseStrategy.SENTENCE2PHRASE).collect(Collectors.toList());
		assertEquals(3, phrasesSentence2.size());
	}



}
