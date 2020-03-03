package edu.kit.ipd.are.graphimprove.nlp.accessdecorators;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.factory.Lists;

import edu.kit.ipd.are.archdoclink.nlp.Annotation;
import edu.kit.ipd.are.archdoclink.nlp.Coreference;
import edu.kit.ipd.are.archdoclink.nlp.Document;
import edu.kit.ipd.are.archdoclink.nlp.PhraseType;
import edu.kit.ipd.are.archdoclink.nlp.Util;
import edu.kit.ipd.are.archdoclink.nlp.Word;
import edu.kit.ipd.are.archdoclink.nlp.parse.ParseCoreference;
import edu.kit.ipd.are.graphimprove.nlp.helper.TextUtil;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseHelper;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.SentenceStrategy;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.IArcType;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.graph.INodeType;


/**
 * 
 * ImprovedParseDocument represent entry point of an graph
 * 
 * @author dominik
 *
 */
public class ImprovedParseDocument implements Document {
	

    private static final String CONTEXT_RELATION_ARC_TYPE = "contextRelation";
	
	private IGraph graph;
	private INodeType sentenceNodeType;
	private final String documentId;
	private ZonedDateTime processingDate;

	public ImprovedParseDocument(IGraph graph, String documentId) {
		this.documentId = documentId;
		this.graph = graph;
		this.sentenceNodeType = graph.getNodeType(SentenceStrategy.SENTENCE_NODE_TYPE);
		this.processingDate = ZonedDateTime.now();
		
	}
	

	@Override
	public String getText() {
		
		return TextUtil.concat(this.getWords());
	}

	@Override
	public String getId() {
		return documentId;
	}

    @Override
    public String toDebugString() {
        return "";// graph.showGraph();
    }

    @Override
    public String toExtensiveDebugString() {
        StringBuilder builder = new StringBuilder("\n");
        builder.append(graph.showGraph());
        builder.append("\n");
        builder.append(Util.getINodesInOrder(graph)
                           .makeString("\n"));
        builder.append("\n");
        return builder.toString();
    }

	@Override
	public void annotate(Annotation annotation) {	
        INodeType type;
        if (graph.hasNodeType(annotation.getName())) {
            type = graph.getNodeType(annotation.getName());
        } else {
            type = graph.createNodeType(annotation.getName());
        }
        INode node = graph.createNode(type);

        String typeAsString = annotation.getAnnotationObject()
                                        .getClass()
                                        .getSimpleName();
        String attributeName = annotation.getValueName();
        if (!type.containsAttribute(attributeName, typeAsString)) {
            type.addAttributeToType(typeAsString, attributeName);
        }

        node.setAttributeValue(attributeName, annotation.getAnnotationObject());
	}

	@Override
	public List<? extends Coreference> getCoreferences() {
        List<ParseCoreference> corefList = Lists.mutable.empty();

        IArcType contextRelationType = graph.getArcType(CONTEXT_RELATION_ARC_TYPE);
        List<IArc> arcs = graph.getArcsOfType(contextRelationType);
        for (IArc arc : arcs) {
            String arcAttributeName = arc.getAttributeValue("name")
                                         .toString();
            if (arcAttributeName.equals("anaphoraReferent")) {
                Word sourceWord = new ImprovedParseWord(arc.getSourceNode());
                Word targetWord = new ImprovedParseWord(arc.getTargetNode());
                corefList.add(new ParseCoreference(sourceWord, targetWord));
            }
        }
        return corefList;
	}

	@Override
	public List<ImprovedParseWord> getNamedEntityWords() {
		return this.getWords().stream().filter(Word::isNamedEntity).collect(Collectors.toList());
	}

	@Override
	public List<ImprovedParsePhrase> getPhrasesOfType(PhraseType type) {
		return this.getPhrases().stream().filter(phrase -> phrase.getType().equals(type)).collect(Collectors.toList());
	}
	
	@Override
	public List<ImprovedParsePhrase> getNounPhrases() {
		return getPhrasesOfType(PhraseType.NOUN_PHRASE);
	}

	@Override
	public List<ImprovedParsePhrase> getPhrases() {
		return PhraseHelper.allNodeTypes().stream()
				.map(nodeName -> this.graph.getNodesOfType(graph.getNodeType(nodeName)))
				.flatMap(l -> l.stream()).map(phraseNode -> new ImprovedParsePhrase(phraseNode))
				.collect(Collectors.toList());		
	}

	@Override
	@Deprecated
	public Calendar getProcessingDate() {
		return GregorianCalendar.from(processingDate);
	}
	
	public ZonedDateTime getProcessingDateAsZonedDateTime() {
		return ZonedDateTime.from(processingDate);
	}

	@Override
	public List<ImprovedParseSentence> getSentences() {				
		return this.graph.getNodesOfType(sentenceNodeType).stream().map(sentenceNode -> new ImprovedParseSentence(this, sentenceNode)).collect(Collectors.toList());
	}

	@Override
	public List<ImprovedParseWord> getWords() {
		return this.graph.getNodesOfType(graph.getNodeType("token")).stream().map(wordNode -> new ImprovedParseWord(wordNode)).collect(Collectors.toList());		
	}

}
