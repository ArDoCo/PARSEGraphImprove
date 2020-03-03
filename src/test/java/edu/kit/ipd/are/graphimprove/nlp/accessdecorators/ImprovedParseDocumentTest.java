package edu.kit.ipd.are.graphimprove.nlp.accessdecorators;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.are.archdoclink.nlp.Annotation;
import edu.kit.ipd.are.archdoclink.nlp.Phrase;
import edu.kit.ipd.are.archdoclink.nlp.PhraseType;
import edu.kit.ipd.are.archdoclink.nlp.Sentence;
import edu.kit.ipd.are.archdoclink.nlp.Word;
import edu.kit.ipd.are.graphimprove.nlp.GraphImprovePipeline;
import edu.kit.ipd.are.graphimprove.nlp.shared.Helper;
import edu.kit.ipd.parse.luna.data.MissingDataException;
import edu.kit.ipd.parse.luna.data.PrePipelineData;
import edu.kit.ipd.parse.luna.graph.IGraph;
import edu.kit.ipd.parse.luna.graph.INode;
import edu.kit.ipd.parse.luna.pipeline.PipelineStageException;

public class ImprovedParseDocumentTest {

    private static final String TEXT1 = "The Assembly_UserManagement <UserManagement> connects the Assembly_Facade <Facade> to the Assembly_UserDBAdapter <UserDBAdapter>. This ensures a nice separation of concerns and reduces the user's risks.";
    private static final String DOCUMENT_ID = "THIS is an ID";

    private static ImprovedParseDocument document;
    private static IGraph graph;

    @BeforeAll
    static void beforeAll() throws PipelineStageException, MissingDataException {

        GraphImprovePipeline gp = new GraphImprovePipeline();
        gp.init();

        PrePipelineData ppd = Helper.initPipeline(TEXT1);

        gp.exec(ppd);
        document = new ImprovedParseDocument(ppd.getGraph(), DOCUMENT_ID);

        graph = ppd.getGraph();

        // Set first token node as named entity
        ppd.getGraph()
           .getNodesOfType(ppd.getGraph()
                              .getNodeType("token"))
           .get(0)
           .setAttributeValue("ner", "not_o");
    }

    @Test
    void text_equals_getText() {
        assertEquals(
                "The Assembly _ UserManagement <UserManagement> connects the Assembly _ Facade <Facade> to the Assembly _ UserDBAdapter <UserDBAdapter>. This ensures a nice separation of concerns and reduces the user's risks.",
                document.getText());
    }

    @Test
    void documentID_equals_getID() {
        assertEquals(DOCUMENT_ID, document.getId());
    }

    @Test
    void text_equals_getSentence() {
        List<? extends Sentence> sentences = document.getSentences();
        assertEquals(2, sentences.size());

        Sentence sentence = sentences.get(0);
        assertThat(sentence, instanceOf(ImprovedParseSentence.class));
    }

    @Test
    void text_equals_getWords() {
        List<? extends Word> words = document.getWords();
        assertEquals(32, words.size());
        assertThat(words.get(0), instanceOf(ImprovedParseWord.class));
    }

    @Test
    void toDebugString() {
        // TODO: BUG Cast everything to string instead of call toString
        // edu.kit.ipd.parse.luna.graphParseGraphshowGraph.showGraph
        assertEquals("", document.toDebugString());
    }

    @Disabled
    @Test
    void toExtensiveDebugString() {
        // TODO: BUG Cast everything to string instead of call toString
        // edu.kit.ipd.parse.luna.graphParseGraphshowGraph.showGraph
        assertEquals("", document.toExtensiveDebugString());
    }

    @Test
    void annotate() {
        Object object = new Object();
        String name = "Test";
        Annotation annotation = new Annotation(name, object);

        document.annotate(annotation);

        List<INode> nodes = graph.getNodesOfType(graph.getNodeType(name));

        assertEquals(1, nodes.size());

        INode node = nodes.get(0);
        assertEquals(object, node.getAttributeValue(name));

    }

    @Test
    void getCoreferences() {
        assertEquals(0, document.getCoreferences()
                                .size());
    }

    @Test
    void getNamedEntityWords() {
        assertEquals(1, document.getNamedEntityWords()
                                .size());
    }

    @Test
    void getNounPhrases() {
        List<? extends Phrase> phrases = document.getNounPhrases();
        assertEquals(8L, phrases.size());
        Map<PhraseType, Long> phrasedByGroup = phrases.stream()
                                                      .collect(Collectors.groupingBy(Phrase::getType,
                                                              Collectors.counting()));

        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.ADJECTIVE_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.ADVERB_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.CONJUNCTION_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.PREPROSITIONAL_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.SUBORDINATING_CONJUNCTION, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.UNDEFINED, -1L));
        assertEquals(Long.valueOf(8L), phrasedByGroup.getOrDefault(PhraseType.NOUN_PHRASE, -1L));
    }

    @Test
    void getPhrasesOfType() {
        List<? extends Phrase> phrases = document.getPhrasesOfType(PhraseType.PREPROSITIONAL_PHRASE);

        assertEquals(2L, phrases.size());
        Map<PhraseType, Long> phrasedByGroup = phrases.stream()
                                                      .collect(Collectors.groupingBy(Phrase::getType,
                                                              Collectors.counting()));

        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.ADJECTIVE_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.ADVERB_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.CONJUNCTION_PHRASE, -1L));
        assertEquals(Long.valueOf(2L), phrasedByGroup.getOrDefault(PhraseType.PREPROSITIONAL_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.SUBORDINATING_CONJUNCTION, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.UNDEFINED, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.NOUN_PHRASE, -1L));
    }

    @Test
    void getPhrases() {
        List<? extends Phrase> phrases = document.getPhrases();

        assertEquals(13L, phrases.size());
        Map<PhraseType, Long> phrasedByGroup = phrases.stream()
                                                      .collect(Collectors.groupingBy(Phrase::getType,
                                                              Collectors.counting()));

        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.ADJECTIVE_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.ADVERB_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.CONJUNCTION_PHRASE, -1L));
        assertEquals(Long.valueOf(2L), phrasedByGroup.getOrDefault(PhraseType.PREPROSITIONAL_PHRASE, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.SUBORDINATING_CONJUNCTION, -1L));
        assertEquals(Long.valueOf(-1L), phrasedByGroup.getOrDefault(PhraseType.UNDEFINED, -1L));
        assertEquals(Long.valueOf(8L), phrasedByGroup.getOrDefault(PhraseType.NOUN_PHRASE, -1L));

    }
}
