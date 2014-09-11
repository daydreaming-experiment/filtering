package filtering;

import java.util.HashMap;

public abstract class CachingDistance implements IDistance {

    private HashMap<DoubleStringKey,Integer> cache = new HashMap<DoubleStringKey,Integer>();

    public int distance(String s, String t) {
        int d;
        DoubleStringKey st = new DoubleStringKey(s ,t);
        if (!cache.containsKey(st)) {
            cache.put(st, computeDistance(s, t));
        }

        return cache.get(st);
    }

    protected abstract int computeDistance(String s, String t);
}
