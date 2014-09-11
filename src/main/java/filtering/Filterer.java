package filtering;

import java.util.*;

public class Filterer {

    private static String TAG = "Filterer";

    private HashSet<String> possibilities = new HashSet<String>();
    private HashMap<String, HashSet<MetaString>> tokenMap = new HashMap<String, HashSet<MetaString>>();
    private HashMap<String, HashSet<String>> matchMap = new HashMap<String, HashSet<String>>();
    private Tree<String> bkTree = new Tree<String>();
    private IDistance distance = new LevenshteinDistance();

    public Filterer(ArrayList<String> possibilitiesArray) {
        possibilities.addAll(possibilitiesArray);

        // no need to tokenize
        // no need to remove stopwords
        // no need to remove punctuation
        // no need to lemmatize

        buildMatchMap();
        buildBKTree();
    }

    public ArrayList<MetaString> search(String query) {
        int distance = 0;
        if (query.length() >= 2) distance = 1;
        return search(query.toLowerCase(), distance);
    }

    private ArrayList<MetaString> search(final String query, int radius) {
        // Get the results from the BK tree
        HashSet<String> bkResults = new HashSet<String>();
        _searchBKTree(bkTree, query, radius, bkResults);
        HashSet<String> filteredResults = filterExcludedDifferences(query, bkResults);

        // Convert to original tokens
        HashSet<String> tokens = new HashSet<String>();
        for (String subString : filteredResults) {
            tokens.addAll(matchMap.get(subString));
        }

        // Convert back to original strings
        HashSet<MetaString> preResults = new HashSet<MetaString>();
        for (String suffix : tokens) {
            preResults.addAll(tokenMap.get(suffix));
        }

        // Re-order
        ArrayList<MetaString> results = new ArrayList<MetaString>(preResults);
        Comparator<MetaString> comparator = new Comparator<MetaString>() {
            @Override
            public int compare(MetaString ms1, MetaString ms2) {
                return distance.distance(query, ms1.getOriginal()) -
                        distance.distance(query, ms2.getOriginal());
            }
        };

        Collections.sort(results, comparator);
        return results;
    }

    private static HashSet<String> filterExcludedDifferences(String query, HashSet<String> bkResults) {
        HashSet<String> filteredResults = new HashSet<String>();
        char firstQueryChar = query.charAt(0);
        for (String bkResult : bkResults) {
            if (bkResult.charAt(0) == firstQueryChar) {
                filteredResults.add(bkResult);
            }
        }
        return filteredResults;
    }

    private void _searchBKTree(Tree<String> tree, String query, int radius, HashSet<String> results) {
        // Get root string and distance to query
        String root = tree.getData();
        int d = distance.distance(root, query);

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

    private void buildMatchMap() {
        buildTokenMap();
        Logger.i(TAG, "Building match map");

        for (String token : tokenMap.keySet()) {
            for (int i = 2; i <= token.length(); i++) {
                String prefix = token.substring(0, i);
                if (!matchMap.containsKey(prefix)) {
                    matchMap.put(prefix, new HashSet<String>());
                }
                matchMap.get(prefix).add(token);
            }
        }
    }

    private void buildTokenMap() {
        Logger.i(TAG, "Building token map");

        for (String s : possibilities) {
            MetaString ms = new MetaString(s);

            // Add tokens
            for (String token : ms.getTokens()) {
                if (!tokenMap.containsKey(token)) {
                    tokenMap.put(token, new HashSet<MetaString>());
                }
                tokenMap.get(token).add(ms);
            }

            // Add tags
            for (String token : ms.getTags()) {
                if (!tokenMap.containsKey(token)) {
                    tokenMap.put(token, new HashSet<MetaString>());
                }
                tokenMap.get(token).add(ms);
            }
        }
    }

    private void buildBKTree() {
        Logger.i(TAG, "Building BK Tree");

        Set<String> items = matchMap.keySet();
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
        int d = distance.distance(tree.getData(), item);
        if (tree.hasEdge(d)) {
            _insertInBKTree(tree.getChild(d), item);
        } else {
            tree.addChild(d, item);
        }
    }

}
