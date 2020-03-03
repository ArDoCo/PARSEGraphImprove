package edu.kit.ipd.are.graphimprove.nlp;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.MetaInfServices;

import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceStrategy;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceToPhraseStrategy;
import edu.kit.ipd.parse.luna.data.AbstractPipelineData;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PipelineDataCastException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.pipeline.IPipelineStage;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;


/**
 * 
 * This GraphImprovePipeplineinitialize all {@link edu.kit.ipd.are.graphimprove.nlp.GraphImproveStrategy} and execute these on PipelineData 
 * 
 * @author dominik
 *
 */
@MetaInfServices(IPipelineStage.class)
public class GraphImprovePipeline implements IPipelineStage {
	
	public static final String GRAPH_IMPROVE_PIPELINE = "GraphImprovePipeline";

	List<GraphImproveStrategy> strategies = new ArrayList<GraphImproveStrategy>();

	@Override
	public void init() {
		strategies.add(new SentenceStrategy());
		strategies.add(new PhraseStrategy());
		strategies.add(new SentenceToPhraseStrategy());
	}

	@Override
	public void exec(AbstractPipelineData data) throws PipelineStageException {

		try {
			PrePipelineData prePipelineData = data.asPrePipelineData();
			IGraph graph = prePipelineData.getGraph();
			
			this.strategies.forEach(strategy -> {
				strategy.createNodeTypes(graph);
				strategy.createArcTypes(graph);
				strategy.executeImprovement(graph);			
			});	

		} catch (PipelineDataCastException e) {
			throw new PipelineStageException("Not a PrePiepelineData", e);
		} catch (MissingDataException e) {
			throw new PipelineStageException("Missing data!", e);
		}
	}

	@Override
	public String getID() {
		return GRAPH_IMPROVE_PIPELINE;
	}

}
