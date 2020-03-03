package edu.kit.ipd.are.graphimprove.nlp.improvestrategies;


/**
 * Represent the available PhraseTypes
 * 
 * @author dominik
 *
 */
public enum PhraseEnum {
	//https://gist.github.com/Gram21/dde3dc1e5fbb582109e20330578beb98
	NOUN_PHRASE("NP"),
	PREPOSITIONAL_PHRASE("PP"),
	VERB_PHRASE("VP"),
	ADVERB_PHRASE("ADVP"),
	ADJECTIVE_PHRASE("ADJP"),
	SUBORDINATING_CONJUNCTION("SBAR"),
	PARTICLE("PRT"),
	INTERJECTION("INTJ"),
	CONJUNCTION_PHRASE("CONJP"),
	UNLIKE_COODINATED_PHRASE("UCP"),
	LIST_MARKER("LST");
	
	
	
	private String value;
	
	PhraseEnum(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public String asNodeTypeName() {
		return this.toString().toLowerCase();
	}
	
    public static PhraseEnum fromString(String text) {
        for (PhraseEnum b : PhraseEnum.values()) {
            if (b.getValue().equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new RuntimeException("Not found: " + text);
        //return null;
    }

	
	

}
