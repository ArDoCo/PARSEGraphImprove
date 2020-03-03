package edu.kit.ipd.are.graphimprove.nlp.accessdecorators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.collections.api.list.MutableList;

import edu.kit.ipd.are.archdoclink.nlp.Annotation;
import edu.kit.ipd.are.archdoclink.nlp.Coreference;
import edu.kit.ipd.are.archdoclink.nlp.Document;
import edu.kit.ipd.are.archdoclink.nlp.NaturalLanguageProcessing;
import edu.kit.ipd.are.archdoclink.nlp.Phrase;
import edu.kit.ipd.are.archdoclink.nlp.Sentence;
import edu.kit.ipd.are.archdoclink.nlp.TextElement;
import edu.kit.ipd.are.archdoclink.nlp.Word;
import edu.kit.ipd.are.archdoclink.nlp.parse.ParseDocument;
import edu.kit.ipd.are.archdoclink.nlp.parse.ParsePhrase;
import edu.kit.ipd.are.archdoclink.nlp.parse.ParseProcessing;
import edu.kit.ipd.are.archdoclink.nlp.parse.ParseSentence;
import edu.kit.ipd.are.archdoclink.nlp.parse.ParseWord;
import edu.kit.ipd.are.graphimprove.nlp.GraphImprovePipeline;
import edu.kit.ipd.indirect.textSNLP.TextSNLP;
import edu.kit.ipd.parse.graphBuilder.GraphBuilder;
import edu.kit.ipd.parse.luna.agent.AbstractAgent;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;
import edu.kit.ipd.parse.luna.tools.StringToHypothesis;

public class ImprovedParseProcessing implements NaturalLanguageProcessing {


    private static Logger logger = Logger.getLogger(ParseProcessing.class);

    private ImprovedParseDocument document;

    private GraphBuilder graphBuilder = null;
    private TextSNLP tnlp = null;

	private GraphImprovePipeline graphImprove;
    // private CorefAnalyzer coref = null;
    // private TextNERTagger nerTagger = null;

    @Override
    public void init(Document document) {
        System.out.println("Here");
        if (!(document instanceof ImprovedParseProcessing)) {
            throw new IllegalArgumentException("Invalid document type. Need ParseDocument");
        }
        System.out.println("Here");
        this.document = (ImprovedParseDocument) document;
        if (logger.isDebugEnabled())
            logger.debug(toDebugString());
    }

    @Override
    public void init(String text, String documentId) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        } else if (documentId == null) {
            documentId = "UNKNOWN";
        }

        PrePipelineData ppd;
        try {
            ppd = initPipeline(text);
            IGraph graph = ppd.getGraph();

            this.document = new ImprovedParseDocument(graph, documentId);
            if (logger.isDebugEnabled())
                logger.debug(toDebugString());
        } catch (PipelineStageException | MissingDataException e) {
            logger.warn(e.getMessage(), e.getCause());
        }
    }

    private PrePipelineData initPipeline(String input) throws PipelineStageException {
        if (graphBuilder == null) {
            graphBuilder = new GraphBuilder();
            graphBuilder.init();
        }
        if (tnlp == null) {
            tnlp = new TextSNLP();
            tnlp.init();
        }
        if (graphImprove == null) {
        	graphImprove = new GraphImprovePipeline();
        	graphImprove.init();
        }

        PrePipelineData ppd = new PrePipelineData();
        ppd.setMainHypothesis(StringToHypothesis.stringToMainHypothesis(input, true));

        tnlp.exec(ppd);
        graphBuilder.exec(ppd);
        graphImprove.exec(ppd);
        return ppd;
    }

    private void execute(AbstractAgent agent, IGraph graph) {
        agent.setGraph(graph);
        try {
            agent.init();
            Method exec = agent.getClass()
                               .getDeclaredMethod("exec");
            exec.setAccessible(true);
            exec.invoke(agent);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.warn(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Document getDocument() {
        return this.document;
    }

    @Override
    public List<ImprovedParseWord> getNamedEntityWords() {
        return document.getNamedEntityWords();
    }

    @Override
    public List<ImprovedParseWord> getWords() {
        return document.getWords();
    }

    @Override
    public Optional<ImprovedParsePhrase> getPhraseOfWord(Word word) {
        for (ImprovedParsePhrase phrase : document.getPhrases()) {
            if (phrase.containsWord(word)) {
                return Optional.of(phrase);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ImprovedParsePhrase> getPhrases() {
        return document.getPhrases();
    }

    @Override
    public List<ImprovedParsePhrase> getNounPhrases() {
        return document.getNounPhrases();
    }

    @Override
    public List<ImprovedParseSentence> getSentences() {
        return document.getSentences();
    }

    @Override
    public List<? extends Coreference> getCoreferences() {
        return document.getCoreferences();
    }

    @Override
    public String toDebugString() {
        return document.toDebugString();
    }

    @Override
    public String toExtensiveDebugString() {
        return document.toExtensiveDebugString();
    }

    @Override
    public void annotate(TextElement textElement, Annotation annotation) {
        if (logger.isDebugEnabled()) {
            String textElementClass = textElement.getClass()
                                                 .getSimpleName();
            String debugString = "Annotating to " + textElementClass + "[" + textElement.toString() + "] the "
                    + annotation.toString();
            logger.debug(debugString);
        }

        textElement.annotate(document, annotation);
    }


}
