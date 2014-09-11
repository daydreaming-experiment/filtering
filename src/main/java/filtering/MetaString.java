package filtering;

import java.util.ArrayList;
import java.util.Arrays;

public class MetaString {

    private static String TAG = "MetaString";

    private static String SPLIT_REGEX = ",|/| |\\(|\\)";
    private static int MIN_TOKEN_LENGTH = 2;

    private String original;
    private String lower;
    private ArrayList<String> lowerTokens;

    public MetaString(String original) {
        this.original = original;
        this.lower = original.toLowerCase();
        this.lowerTokens = buildLowerTokens();
    }

    private ArrayList<String> buildLowerTokens() {
        ArrayList<String> preTokens = new ArrayList<String>(Arrays.asList(lower.split(SPLIT_REGEX)));
        ArrayList<String> tokens = new ArrayList<String>();
        for (String preToken : preTokens) {
            if (preToken.length() >= MIN_TOKEN_LENGTH) tokens.add(preToken);
        }
        return tokens;
    }

    public String toString() {
        return original + "|" + lower;
    }

    public String getOriginal() {
        return original;
    }

    public String getLower() {
        return lower;
    }

    public ArrayList<String> getLowerTokens() {
        return lowerTokens;
    }
}
