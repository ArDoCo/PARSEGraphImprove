package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;


import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceStrategy;
import edu.kit.ipd.are.graphimprove.nlp.shared.Helper;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class SentenceStrategyTest {


	@Mock
	private IGraph graph;
	@Mock
	private IArcType arcTyp;

    @Captor
    ArgumentCaptor<String> keyCaptor;

	private SentenceStrategy sentenceStrategy;

	@BeforeEach
	void before() throws PipelineStageException {
		this.sentenceStrategy = new SentenceStrategy();
	}

	@Test
	void create_arc_type() {
		when(graph.createArcType(keyCaptor.capture())).thenReturn(arcTyp);

		this.sentenceStrategy.createArcTypes(graph);

		assertTrue(keyCaptor.getAllValues().containsAll(Arrays.asList("sentence2sentence", "word2sentence")));
	}

	@Test
	void create_node_type() {
		when(graph.createNodeType(keyCaptor.capture())).thenReturn(Mockito.mock(INodeType.class));

		this.sentenceStrategy.createNodeTypes(graph);

		assertTrue(keyCaptor.getAllValues().containsAll(Arrays.asList("sentence")));
	}

	@Test
	void create_execute_type() throws MissingDataException, PipelineStageException {
		// Found bug: If the last sentence has no dot, then this is not interpreted as sentence  --> Bug in TextNLP
		IGraph graph = Helper.initPipeline("This is an first sentence. Sentence second this. Test is an third sentence.").getGraph();

		this.sentenceStrategy.createArcTypes(graph);
		this.sentenceStrategy.createNodeTypes(graph);

		this.sentenceStrategy.executeImprovement(graph);

		List<INode> sentences = graph.getNodesOfType(graph.getNodeType(SentenceStrategy.SENTENCE_NODE_TYPE));
		assertEquals(3, sentences.size());

		INode sentence1 = sentences.get(0);
		assertEquals(0, sentence1.getAttributeValue(SentenceStrategy.SENTENCE_NUMBER_ATTRIBUTE));
		List<? extends IArc> wordsFromSentence1 = sentence1.getIncomingArcsOfType(graph.getArcType(SentenceStrategy.WORD_TO_SENCTENCE_ARC_TYPE));
		assertEquals(6, wordsFromSentence1.size());
		assertEquals("This", wordsFromSentence1.get(0).getSourceNode().getAttributeValue("value"));
		assertEquals(".", wordsFromSentence1.get(5).getSourceNode().getAttributeValue("value"));

		INode sentence2 = sentences.get(1);
		assertEquals(1, sentence2.getAttributeValue(SentenceStrategy.SENTENCE_NUMBER_ATTRIBUTE));
		List<? extends IArc> wordsFromSentence2 = sentence2.getIncomingArcsOfType(graph.getArcType(SentenceStrategy.WORD_TO_SENCTENCE_ARC_TYPE));
		assertEquals(4, wordsFromSentence2.size());
		assertEquals("Sentence", wordsFromSentence2.get(0).getSourceNode().getAttributeValue("value"));
		assertEquals(".", wordsFromSentence2.get(3).getSourceNode().getAttributeValue("value"));

		INode sentence3 = sentences.get(2);
		assertEquals(2, sentence3.getAttributeValue(SentenceStrategy.SENTENCE_NUMBER_ATTRIBUTE));
		List<? extends IArc> wordsFromSentence3 = sentence3.getIncomingArcsOfType(graph.getArcType(SentenceStrategy.WORD_TO_SENCTENCE_ARC_TYPE));
		assertEquals(6, wordsFromSentence3.size());
		assertEquals("Test", wordsFromSentence3.get(0).getSourceNode().getAttributeValue("value"));
		assertEquals(".", wordsFromSentence3.get(5).getSourceNode().getAttributeValue("value"));
	}

}
