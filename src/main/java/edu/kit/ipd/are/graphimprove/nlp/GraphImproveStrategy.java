package edu.kit.ipd.are.graphimprove.nlp;

import edu.kit.ipd.parse.luna.graph.IGraph;

/**
 * 
 * The GraphImproveStrategy is an interface to implement an improvement strategy the PARSE graph. 
 * 
 * @author dominik
 *
 */
public interface GraphImproveStrategy {
	
	public void createArcTypes(IGraph graph);
	public void createNodeTypes(IGraph graph);
	public void executeImprovement(IGraph graph);

}
