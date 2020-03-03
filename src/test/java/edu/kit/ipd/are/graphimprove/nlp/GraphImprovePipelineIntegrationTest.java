package edu.kit.ipd.are.graphimprove.nlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import edu.kit.ipd.are.graphimprove.nlp.GraphImprovePipeline;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseEnum;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseHelper;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceStrategy;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceToPhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.shared.Helper;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;

@Ignore
public class GraphImprovePipelineIntegrationTest {

	private static final String TEXT1 = "The Assembly_UserManagement <UserManagement> connects the Assembly_Facade <Facade> to the Assembly_UserDBAdapter <UserDBAdapter>. This ensures a nice separation of concerns and reduces the user's risks.";
	private static final String NESTED_PHRASE = "Donald Trump has the temperament of a bully, the ego of a dictator, and the patience of a tyrant";
	private PrePipelineData preProcessPipeling;
	private GraphImprovePipeline gip;

	@Before
	public void before() throws Exception {
		preProcessPipeling = Helper.initPipeline(TEXT1);

		gip = new GraphImprovePipeline();
    }


	@Test
	public void testInit() throws PipelineStageException, MissingDataException {
        gip.init();
		gip.exec(preProcessPipeling);

		IGraph graph = preProcessPipeling.getGraph();


		List<INode> sentences = graph.getNodesOfType(graph.getNodeType(SentenceStrategy.SENTENCE_NODE_TYPE));

		assertEquals(2, sentences.size());

		INode sentence1 = sentences.get(0);
		INode sentence2 = sentences.get(1);
		assertEquals(6, sentence1.getOutgoingArcs().size());

		assertEquals(sentence2, sentence1.getOutgoingArcs().get(0).getTargetNode());
		assertEquals(18, sentence1.getIncomingArcs().size());

		assertEquals(8, graph.getNodesOfType(graph.getNodeType("token")).stream().filter((node) -> ((String)node.getAttributeValue(GraphBuilder.CHUNK_IOB_ATTRIBUTE)).equals("B-NP")).count());
		graph.getNodesOfType(graph.getNodeType(PhraseEnum.NOUN_PHRASE.asNodeTypeName())).forEach(node -> System.out.println(node.getIncomingArcs().size()));
	}

	@Test
	public void checkEncapsulation() throws PipelineStageException, MissingDataException {

		preProcessPipeling = Helper.initPipeline(NESTED_PHRASE);
		IGraph graph = preProcessPipeling.getGraph();

        gip.init();
		gip.exec(preProcessPipeling);

		Arrays.asList(PhraseEnum.values()).forEach((chunkIob -> {
			List<INode> nodes = graph.getNodesOfType(graph.getNodeType(chunkIob.asNodeTypeName()));
			nodes.forEach((node) -> {
				assertEquals(0, node.getNumberOfOutgoingArcs()); // No arcs to other nodes;
				int beginnerArcsSize = node.getIncomingArcsOfType(graph.getArcType(PhraseHelper.BEGINN_TYPE_PREFIX + chunkIob.asNodeTypeName())).size();
				int insideArcsSize = node.getIncomingArcsOfType(graph.getArcType(PhraseHelper.INSIDE_TYPE_PREFIX + chunkIob.asNodeTypeName())).size();
				int sentence2phraseArcsSize = node.getIncomingArcsOfType(graph.getArcType(SentenceToPhraseStrategy.SENTENCE2PHRASE)).size();

				assertEquals(1, beginnerArcsSize); // Only on beginner arc
				assertTrue(0 <= insideArcsSize); // Minimum 0 Inside nodes
				assertEquals(1, sentence2phraseArcsSize); // Exactly one sentence to phrase arc
				assertTrue(0 == (node.getIncomingArcs().size() - beginnerArcsSize - insideArcsSize - sentence2phraseArcsSize)); // No other incomming arcs
			});
		}));
	}

}
