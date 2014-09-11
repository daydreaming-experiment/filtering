package filtering;

import java.util.*;

public class Filterer {

    private static String TAG = "Filterer";

    private HashSet<String> possibilities = new HashSet<String>();
    private HashMap<String, HashSet<MetaString>> suffixMap = new HashMap<String, HashSet<MetaString>>();
    private HashMap<String, HashSet<String>> subStringMap = new HashMap<String, HashSet<String>>();
    private Tree<String> bkTree = new Tree<String>();
    private LevenshteinDistance levenshtein = new LevenshteinDistance();

    public Filterer(ArrayList<String> possibilitiesArray) {
        possibilities.addAll(possibilitiesArray);

        // no need to tokenize
        // no need to remove stopwords
        // no need to remove punctuation
        // no need to lemmatize

        buildSubStringMap();
        buildBKTree();
    }

    public ArrayList<MetaString> search(String query) {
        return search(query.toLowerCase(), 1);
    }

    private ArrayList<MetaString> search(final String query, int radius) {
        // Get the results in terms of sub-strings
        HashSet<String> subStrings = new HashSet<String>();
        _searchBKTree(bkTree, query, radius, subStrings);

        // Convert to original suffixes
        HashSet<String> suffixes = new HashSet<String>();
        for (String subString : subStrings) {
            suffixes.addAll(subStringMap.get(subString));
        }

        // Convert back to original strings
        HashSet<MetaString> preResults = new HashSet<MetaString>();
        for (String suffix : suffixes) {
            preResults.addAll(suffixMap.get(suffix));
        }

        // Re-order
        ArrayList<MetaString> results = new ArrayList<MetaString>(preResults);
        Comparator<MetaString> comparator = new Comparator<MetaString>() {
            @Override
            public int compare(MetaString ms1, MetaString ms2) {
                return levenshtein.distance(query, ms1.getOriginal()) -
                        levenshtein.distance(query, ms2.getOriginal());
            }
        };

        Collections.sort(results, comparator);
        return results;
    }

    private void _searchBKTree(Tree<String> tree, String query, int radius, HashSet<String> results) {
        // Get root string and distance to query
        String root = tree.getData();
        int d = levenshtein.distance(root, query);

        // Add root if we can
        if (d <= radius) {
            results.add(root);
        }

        // Search all sub-trees at the right distance
        for (Map.Entry<Integer, Tree<String>> child : tree.getChildren().entrySet()) {
            if (child.getKey() <= d + radius) {
                _searchBKTree(child.getValue(), query, radius, results);
            }
        }
    }


    private void buildSubStringMap() {
        buildSuffixMap();
        Logger.i(TAG, "Building sub-string map");

        for (String suffix : suffixMap.keySet()) {
            for (int i = 2; i <= suffix.length(); i++) {
                String sub = suffix.substring(0, i);
                if (!subStringMap.containsKey(sub)) {
                    subStringMap.put(sub, new HashSet<String>());
                }
                subStringMap.get(sub).add(suffix);
            }
        }
    }

    private void buildSuffixMap() {
        Logger.i(TAG, "Building suffix map");
        for (String s : possibilities) {
            MetaString ms = new MetaString(s);
            String lower = ms.getLower();
            int l = lower.length();
            for (int i = 0; i <= l - 2; i++) {
                String sub = lower.substring(i);
                if (!suffixMap.containsKey(sub)) {
                    suffixMap.put(sub, new HashSet<MetaString>());
                }
                suffixMap.get(sub).add(ms);
            }
        }
    }

    private void buildBKTree() {
        Logger.i(TAG, "Building BK Tree");
        Set<String> items = subStringMap.keySet();
        String firstItem = null;
        for (String item : items) {
            if (firstItem == null) {
                firstItem = item;
                bkTree.setData(firstItem);
            } else {
                _insertInBKTree(bkTree, item);
            }
        }
    }

    private void _insertInBKTree(Tree<String> tree, String item) {
        // item is our root
        if (item.equals(tree.getData())) return;

        // Else, insert item in an existing subtree or as a new child
        int d = levenshtein.distance(tree.getData(), item);
        if (tree.hasEdge(d)) {
            _insertInBKTree(tree.getChild(d), item);
        } else {
            tree.addChild(d, item);
        }
    }

}
