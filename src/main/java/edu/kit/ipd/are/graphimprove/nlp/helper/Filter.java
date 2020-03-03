package edu.kit.ipd.are.graphimprove.nlp.helper;

import java.util.Arrays;
import java.util.stream.Stream;

import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.INode;

public abstract class Filter {
	
	
	public static Stream<INode> outgoing(INode current, String... arcTypeNames) {
		return current.getOutgoingArcs().stream().filter((arc) -> Arrays.stream(arcTypeNames).anyMatch(arcTypeName -> arcTypeName.equals(arc.getType().getName()))).map(IArc::getTargetNode);		
	}
	
	public static  Stream<INode> incomming(INode current, String... arcTypeNames) {
		return current.getIncomingArcs().stream().filter((arc) -> Arrays.stream(arcTypeNames).anyMatch(arcTypeName -> arcTypeName.equals(arc.getType().getName()))).map(IArc::getSourceNode);		
	}
	

}
