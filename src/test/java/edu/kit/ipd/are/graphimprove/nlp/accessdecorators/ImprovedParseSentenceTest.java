package edu.kit.ipd.are.graphimprove.nlp.accessdecorators;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ipd.are.archdoclink.nlp.Annotation;
import edu.kit.ipd.are.archdoclink.nlp.Phrase;
import edu.kit.ipd.are.archdoclink.nlp.PhraseType;
import edu.kit.ipd.are.archdoclink.nlp.Sentence;
import edu.kit.ipd.are.archdoclink.nlp.Word;
import edu.kit.ipd.are.graphimprove.nlp.accessdecorators.ImprovedParseDocument;
import edu.kit.ipd.are.graphimprove.nlp.accessdecorators.ImprovedParsePhrase;
import edu.kit.ipd.are.graphimprove.nlp.accessdecorators.ImprovedParseSentence;
import edu.kit.ipd.are.graphimprove.nlp.accessdecorators.ImprovedParseWord;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseEnum;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.shared.MockIArc;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.INode;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.instanceOf;

@ExtendWith(MockitoExtension.class)
public class ImprovedParseSentenceTest {

	@Mock
	ImprovedParseDocument document;

	@Mock
	INode node;

	@InjectMocks
	ImprovedParseSentence sentence;


	@Test
	void getDocument() {
		assertEquals(document, sentence.getDocument());
	}

	@Test
	void getId() {
		when(node.getAttributeValue("sentenceNumber")).thenReturn(10);
		assertEquals("10", sentence.getId());
	}

	@Test
	void getWords() {
		INode word1 = Mockito.mock(INode.class);
		MockIArc arc1 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc1.getType().getName()).thenReturn("word2sentence");
		when(arc1.getSourceNode()).thenReturn(word1);

		INode word2 = Mockito.mock(INode.class);
		MockIArc arc2 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc2.getType().getName()).thenReturn("word2sentence");
		when(arc2.getSourceNode()).thenReturn(word2);

		List<MockIArc> arcs = new ArrayList<MockIArc>();
		arcs.add(arc1);
		arcs.add(arc2);
		List<? extends IArc> test = arcs;

		Mockito.doReturn(test).when(node).getIncomingArcs();
		List<? extends Word> words = sentence.getWords();
		assertEquals(2, words.size());
		assertThat(words.get(0), instanceOf(ImprovedParseWord.class));
		assertThat(words.get(1), instanceOf(ImprovedParseWord.class));
	}

	@Test
	void nextSentence() {
		INode nextSentenceNOde = Mockito.mock(INode.class);
		MockIArc arc1 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc1.getType().getName()).thenReturn("sentence2sentence");
		when(arc1.getTargetNode()).thenReturn(nextSentenceNOde);

		List<MockIArc> arcs = new ArrayList<MockIArc>();
		arcs.add(arc1);
		List<? extends IArc> test = arcs;
		Mockito.doReturn(test).when(node).getOutgoingArcs();

		Sentence nextSentence = sentence.nextSentence().get();
		assertThat(nextSentence, instanceOf(ImprovedParseSentence.class));
	}

	@Test
	void noNextSentence() {
		List<MockIArc> arcs = new ArrayList<MockIArc>();
		List<? extends IArc> test = arcs;
		Mockito.doReturn(test).when(node).getOutgoingArcs();

		assertFalse(sentence.nextSentence().isPresent());
	}

	@Test
	void previousSentence() {
		INode nextSentenceNOde = Mockito.mock(INode.class);
		MockIArc arc1 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc1.getType().getName()).thenReturn("sentence2sentence");
		when(arc1.getSourceNode()).thenReturn(nextSentenceNOde);

		List<MockIArc> arcs = new ArrayList<MockIArc>();
		arcs.add(arc1);
		List<? extends IArc> test = arcs;
		Mockito.doReturn(test).when(node).getIncomingArcs();

		Sentence nextSentence = sentence.previousSentence().get();
		assertThat(nextSentence, instanceOf(ImprovedParseSentence.class));
	}

	@Test
	void noPreviousSentence() {
		List<MockIArc> arcs = new ArrayList<MockIArc>();
		List<? extends IArc> test = arcs;
		Mockito.doReturn(test).when(node).getIncomingArcs();

		assertFalse(sentence.previousSentence().isPresent());
	}

	@Test
	void annotate() {
		Annotation annotation = new Annotation("test", new Object());
		sentence.annotate(annotation);
		verify(document).annotate(annotation);

	}

	@Test
	void getPhrases() {
		List<? extends IArc> test = generatePhraseList();

		Mockito.doReturn(test).when(node).getOutgoingArcs();


		List<? extends Phrase> phrases = sentence.getPhrases();

		assertEquals(2, phrases.size());
		assertThat(phrases.get(0), instanceOf(ImprovedParsePhrase.class));
		assertThat(phrases.get(1), instanceOf(ImprovedParsePhrase.class));
	}

	@Test
	void getPhrasesOfType() {
		List<? extends IArc> test = generatePhraseList();

		Mockito.doReturn(test).when(node).getOutgoingArcs();


		List<? extends Phrase> phrases = sentence.getPhrasesOfType(PhraseType.ADVERB_PHRASE);

		assertEquals(1, phrases.size());
		assertThat(phrases.get(0), instanceOf(ImprovedParsePhrase.class));
	}

	@Test
	void getNountPhrases() {
		List<? extends IArc> test = generatePhraseList();

		Mockito.doReturn(test).when(node).getOutgoingArcs();


		List<? extends Phrase> phrases = sentence.getNounPhrases();

		assertEquals(1, phrases.size());
		assertThat(phrases.get(0), instanceOf(ImprovedParsePhrase.class));
	}

	private List<? extends IArc> generatePhraseList(){
		INode phrase1 = Mockito.mock(INode.class);
		Mockito.lenient().when(phrase1.getAttributeValue(PhraseStrategy.CHUNKIOB_ATTRIBUTE_KEY)).thenReturn(PhraseEnum.ADVERB_PHRASE.getValue());

		MockIArc arc1 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc1.getType().getName()).thenReturn("sentence2phrase");
		when(arc1.getTargetNode()).thenReturn(phrase1);

		INode phrase2 = Mockito.mock(INode.class);
		Mockito.lenient().when(phrase2.getAttributeValue(PhraseStrategy.CHUNKIOB_ATTRIBUTE_KEY)).thenReturn(PhraseEnum.NOUN_PHRASE.getValue());

		MockIArc arc2 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc2.getType().getName()).thenReturn("sentence2phrase");
		when(arc2.getTargetNode()).thenReturn(phrase2);

		List<MockIArc> arcs = new ArrayList<MockIArc>();
		arcs.add(arc1);
		arcs.add(arc2);

		List<? extends IArc> test = arcs;
		return test;
	}



}


