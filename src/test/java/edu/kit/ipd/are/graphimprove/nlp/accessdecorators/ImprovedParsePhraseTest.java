package edu.kit.ipd.are.graphimprove.nlp.accessdecorators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;


import edu.kit.ipd.are.archdoclink.nlp.Annotation;
import edu.kit.ipd.are.archdoclink.nlp.Document;
import edu.kit.ipd.are.archdoclink.nlp.PhraseType;
import edu.kit.ipd.are.archdoclink.nlp.Word;
import edu.kit.ipd.are.graphimprove.nlp.accessdecorators.ImprovedParsePhrase;
import edu.kit.ipd.are.graphimprove.nlp.accessdecorators.ImprovedParseWord;
import edu.kit.ipd.are.graphimprove.nlp.improvestrategies.PhraseStrategy;
import edu.kit.ipd.are.graphimprove.nlp.shared.MockIArc;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.INode;

@ExtendWith(MockitoExtension.class)
public class ImprovedParsePhraseTest {

	@Mock
	INode phraseSentence;

	@InjectMocks
	ImprovedParsePhrase phrase;

	@Test
	void getText() {
		List<? extends IArc> test = generateWordListMock();
		Mockito.doReturn(test).when(phraseSentence).getIncomingArcs();

		assertEquals("Das from", phrase.getText());
	}

	@Test
	void annotate() {
		Document document = Mockito.mock(Document.class);
		Annotation annotation = new Annotation("test", new Object());

		phrase.annotate(document, annotation);

		verify(document).annotate(annotation);
	}

	@Test
	void containsWord() {
		List<? extends IArc> test = generateWordListMock();
		Mockito.doReturn(test).when(phraseSentence).getIncomingArcs();

		Word word = new ImprovedParseWord(test.get(0).getSourceNode());

		assertTrue(phrase.containsWord(word));
	}

	@Test
	void notContainsWord() {
		List<? extends IArc> test = generateWordListMock();
		Mockito.doReturn(test).when(phraseSentence).getIncomingArcs();

		Word word = new ImprovedParseWord(Mockito.mock(INode.class));

		assertFalse(phrase.containsWord(word));
	}

	@Test
	void getTextWithoutLeadingStopwords() {
		List<? extends IArc> test = generateWordListMock();
		Mockito.doReturn(test).when(phraseSentence).getIncomingArcs();

		assertEquals("Das", phrase.getTextWithoutLeadingStopwords());
	}

	@Test
	void getType() {
		when(phraseSentence.getAttributeValue(PhraseStrategy.CHUNKIOB_ATTRIBUTE_KEY)).thenReturn("NP");

		assertEquals(PhraseType.NOUN_PHRASE,  phrase.getType());
	}

	@Test
	void getWords() {
		List<? extends IArc> test = generateWordListMock();
		Mockito.doReturn(test).when(phraseSentence).getIncomingArcs();
		assertEquals(2, phrase.getWords().size());
	}

	private List<? extends IArc> generateWordListMock() {
		when(phraseSentence.getAttributeValue(PhraseStrategy.CHUNKIOB_ATTRIBUTE_KEY)).thenReturn("NP");
		INode word1 = Mockito.mock(INode.class);
		Mockito.lenient().when(word1.getAttributeValue(or(Mockito.eq("value"), Mockito.eq("pos")))).thenReturn("Das");
		MockIArc arc1 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc1.getType().getName()).thenReturn("begin_noun_phrase");
		when(arc1.getSourceNode()).thenReturn(word1);

		INode word2 = Mockito.mock(INode.class);
		Mockito.lenient().when(word2.getAttributeValue(or(Mockito.eq("value"), Mockito.eq("pos")))).thenReturn("from");
		MockIArc arc2 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
		when(arc2.getType().getName()).thenReturn("inside_noun_phrase");
		when(arc2.getSourceNode()).thenReturn(word2);

		List<MockIArc> arcs = new ArrayList<MockIArc>();
		arcs.add(arc1);
		arcs.add(arc2);

		List<? extends IArc> test = arcs;
		return test;
	}




}
