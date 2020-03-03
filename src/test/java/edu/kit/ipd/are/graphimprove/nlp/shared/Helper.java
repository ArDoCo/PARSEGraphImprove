package edu.kit.ipd.are.graphimprove.nlp.shared;

import edu.kit.ipd.indirect.textSNLP.TextSNLP;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;

public class Helper {
	
	public static PrePipelineData initPipeline(String input) throws PipelineStageException {
		GraphBuilder graphBuilder = new GraphBuilder();
        graphBuilder.init();
        
        TextSNLP tnlp = new TextSNLP();
        tnlp.init();

        PrePipelineData ppd = new PrePipelineData();
        ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, true));

        tnlp.exec(ppd);
        graphBuilder.exec(ppd);
        return ppd;
 }


}
