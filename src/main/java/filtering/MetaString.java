package filtering;

public class MetaString {

    private static String TAG = "MetaString";

    private String original;
    private String lower;

    public MetaString(String original) {
        this.original = original;
        this.lower = original.toLowerCase();
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
}
