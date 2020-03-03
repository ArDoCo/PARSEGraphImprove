package edu.kit.ipd.are.graphimprove.nlp.shared;

import java.util.ArrayList;

import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.Pair;

public class MockIArc implements IArc{

	@Override
	public Object getAttributeValue(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeOfAttribute(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getAllAttributeValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Pair<String, Object>> getAllAttributeNamesAndValuesAsPair() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAttributeValue(String varName, Object attributeValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public INode getSourceNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INode getTargetNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IArcType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
