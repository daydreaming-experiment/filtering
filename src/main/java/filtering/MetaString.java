package filtering;

import java.util.ArrayList;
import java.util.Arrays;

public class MetaString {

    private static String TAG = "MetaString";

    private static String TOKEN_SPLIT_REGEX = ",|/| |\\(|\\)";
    private static String DEFINITION_SPLIT_REGEX = "\\|";
    private static String TAG_SPLIT_REGEX = " *, *";
    private static int MIN_TOKEN_TAG_LENGTH = 2;

    private String definition;
    private String original;
    private String lower;
    private ArrayList<String> tokens;
    private ArrayList<String> tags;

    public MetaString(String definition) {
        this.definition = definition.trim();
        parseDefinition();
    }

    private static ArrayList<String> filterMinimumLengthLower(ArrayList<String> strings, int minLength) {
        ArrayList<String> filtered = new ArrayList<String>();
        for (String s : strings) {
            if (s.length() >= minLength) filtered.add(s.toLowerCase().trim());
        }
        return filtered;
    }

    private static ArrayList<String> buildTokens(String s) {
        ArrayList<String> preTokens = new ArrayList<String>(Arrays.asList(s.split(TOKEN_SPLIT_REGEX)));
        return filterMinimumLengthLower(preTokens, MIN_TOKEN_TAG_LENGTH);
    }

    private void parseDefinition() {
        // Get the original sentence
        String[] definitionParts = definition.split(DEFINITION_SPLIT_REGEX);
        original = definitionParts[0].trim();
        lower = original.toLowerCase().trim();
        tokens = buildTokens(lower);

        // Parse tags
        if (definitionParts.length == 1) {
            tags = new ArrayList<String>();
            return;
        } else {
            ArrayList<String> preTags = new ArrayList<String>(Arrays.asList(definitionParts[1].split(TAG_SPLIT_REGEX)));
            tags = filterMinimumLengthLower(preTags, MIN_TOKEN_TAG_LENGTH);
        }
    }

    public String toString() {
        return original;
    }

    public String getOriginal() {
        return original;
    }

    public String getLower() {
        return lower;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
