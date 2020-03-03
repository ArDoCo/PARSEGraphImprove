package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.are.archdoclink.nlp.Sentence;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseEnum;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseHelper;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceStrategy;
import edu.kit.ipd.are.graphimprove.nlp.shared.Helper;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;

public class PhraseStrategyTest {

	private static final String NP_TEST_1 = "the assembly context";
	private static final String NP_TEST_2 = "the";
	private static final String NP_TEST_3 = "the assembly context get";
	private PhraseStrategy chnukIOBStratgy;
	private IGraph graph;


	@BeforeEach
	void beforeEach() throws PipelineStageException, MissingDataException {
		this.chnukIOBStratgy = new PhraseStrategy();

	}

	void setupGraph(String input) throws MissingDataException, PipelineStageException {
		this.graph = Helper.initPipeline(input).getGraph();

		this.chnukIOBStratgy.createNodeTypes(this.graph);
		this.chnukIOBStratgy.createArcTypes(this.graph);
		this.chnukIOBStratgy.executeImprovement(this.graph);
	}

	@Test
	void check_noun_phrase() {
		assertEquals("NOUN_PHRASE", PhraseEnum.NOUN_PHRASE.toString());
	}

	@Test
	void test_noun_phrase_with_n_parts() throws MissingDataException, PipelineStageException {

		setupGraph(NP_TEST_1);

		List<INode> nodes = this.graph.getNodesOfType(this.graph.getNodeType(PhraseEnum.NOUN_PHRASE.asNodeTypeName()));

		assertEquals(1, nodes.size());

		INode node = nodes.get(0);
		assertEquals(0, node.getNumberOfOutgoingArcs());

		List<? extends IArc> incommingArcs = node.getIncomingArcs();
		assertEquals(3, incommingArcs.size());

		IArc firstArc = incommingArcs.get(0);
		assertEquals(PhraseHelper.BEGINN_TYPE_PREFIX + PhraseEnum.NOUN_PHRASE.asNodeTypeName(), firstArc.getType().getName());
		assertEquals("the", firstArc.getSourceNode().getAttributeValue("value"));


		IArc secoundArc = incommingArcs.get(1);
		assertEquals(PhraseHelper.INSIDE_TYPE_PREFIX + PhraseEnum.NOUN_PHRASE.asNodeTypeName(), secoundArc.getType().getName());
		assertEquals("assembly", secoundArc.getSourceNode().getAttributeValue("value"));


		IArc thirdArc = incommingArcs.get(2);
		assertEquals(PhraseHelper.INSIDE_TYPE_PREFIX + PhraseEnum.NOUN_PHRASE.asNodeTypeName(), thirdArc.getType().getName());
		assertEquals("context", thirdArc.getSourceNode().getAttributeValue("value"));
	}

	@Test
	void test_noun_phrase_with_one_part() throws MissingDataException, PipelineStageException {
		setupGraph(NP_TEST_2);

		List<INode> nodes = this.graph.getNodesOfType(this.graph.getNodeType(PhraseEnum.NOUN_PHRASE.asNodeTypeName()));

		assertEquals(1, nodes.size());

		INode node = nodes.get(0);
		assertEquals(0, node.getNumberOfOutgoingArcs());

		List<? extends IArc> incommingArcs = node.getIncomingArcs();
		assertEquals(1, incommingArcs.size());

		IArc firstArc = incommingArcs.get(0);
		assertEquals(PhraseHelper.BEGINN_TYPE_PREFIX + PhraseEnum.NOUN_PHRASE.asNodeTypeName(), firstArc.getType().getName());
		assertEquals("the", firstArc.getSourceNode().getAttributeValue("value"));
	}

	@Test
	void test_noun_phrase_with_three_part_one_other() throws MissingDataException, PipelineStageException {
		setupGraph(NP_TEST_3);

		List<INode> nodes = this.graph.getNodesOfType(this.graph.getNodeType(PhraseEnum.NOUN_PHRASE.asNodeTypeName()));

		assertEquals(1, nodes.size());

		INode node = nodes.get(0);
		assertEquals(0, node.getNumberOfOutgoingArcs());

		List<? extends IArc> incommingArcs = node.getIncomingArcs();
		assertEquals(3, incommingArcs.size());

		IArc firstArc = incommingArcs.get(0);
		assertEquals(PhraseHelper.BEGINN_TYPE_PREFIX + PhraseEnum.NOUN_PHRASE.asNodeTypeName(), firstArc.getType().getName());
		assertEquals("the", firstArc.getSourceNode().getAttributeValue("value"));


		IArc secoundArc = incommingArcs.get(1);
		assertEquals(PhraseHelper.INSIDE_TYPE_PREFIX + PhraseEnum.NOUN_PHRASE.asNodeTypeName(), secoundArc.getType().getName());
		assertEquals("assembly", secoundArc.getSourceNode().getAttributeValue("value"));


		IArc thirdArc = incommingArcs.get(2);
		assertEquals(PhraseHelper.INSIDE_TYPE_PREFIX + PhraseEnum.NOUN_PHRASE.asNodeTypeName(), thirdArc.getType().getName());
		assertEquals("context", thirdArc.getSourceNode().getAttributeValue("value"));

		List<INode> verbNodes = this.graph.getNodesOfType(this.graph.getNodeType(PhraseEnum.VERB_PHRASE.asNodeTypeName()));
		assertEquals(1, verbNodes.size());

		INode verbNode = verbNodes.get(0);

		assertEquals(1, verbNode.getIncomingArcs().size());
	}

}
