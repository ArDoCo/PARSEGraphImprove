package edu.kit.ipd.are.graphimprove.nlp.accessdecorators;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ipd.are.archdoclink.nlp.Sentence;
import edu.kit.ipd.are.archdoclink.nlp.Word;
import edu.kit.ipd.are.graphimprove.nlp.accessdecorators.ImprovedParseWord;
import edu.kit.ipd.are.graphimprove.nlp.shared.MockIArc;
import edu.kit.ipd.parse.luna.graph.IArc;
import edu.kit.ipd.parse.luna.graph.INode;

@ExtendWith(MockitoExtension.class)
public class ImprovedParseWordTest {

		@Mock
		INode node;

		@InjectMocks
		ImprovedParseWord word;

		@Test
		void nextWord() {
			INode nextWordNode = Mockito.mock(INode.class);
			MockIArc arc1 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
			when(arc1.getType().getName()).thenReturn("relation");
			when(arc1.getTargetNode()).thenReturn(nextWordNode);

			List<MockIArc> arcs = new ArrayList<MockIArc>();
			arcs.add(arc1);
			List<? extends IArc> test = arcs;
			Mockito.doReturn(test).when(node).getOutgoingArcs();

			Word nextWord = word.nextWord().get();
			assertThat(nextWord, instanceOf(ImprovedParseWord.class));
		}

		@Test
		void noNextWord() {
			List<MockIArc> arcs = new ArrayList<MockIArc>();
			List<? extends IArc> test = arcs;
			Mockito.doReturn(test).when(node).getOutgoingArcs();

			assertFalse(word.nextWord().isPresent());
		}

		@Test
		void previousWord() {
			INode nextWordNode = Mockito.mock(INode.class);
			MockIArc arc1 = Mockito.mock(MockIArc.class, Answers.RETURNS_DEEP_STUBS);
			when(arc1.getType().getName()).thenReturn("relation");
			when(arc1.getSourceNode()).thenReturn(nextWordNode);

			List<MockIArc> arcs = new ArrayList<MockIArc>();
			arcs.add(arc1);
			List<? extends IArc> test = arcs;
			Mockito.doReturn(test).when(node).getIncomingArcs();

			Word nextWord = word.previousWord().get();
			assertThat(nextWord, instanceOf(ImprovedParseWord.class));
		}

		@Test
		void noPreviousWord() {
			List<MockIArc> arcs = new ArrayList<MockIArc>();
			List<? extends IArc> test = arcs;
			Mockito.doReturn(test).when(node).getIncomingArcs();

			assertFalse(word.previousWord().isPresent());
		}


}
