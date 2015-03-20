package mc.sample.tasks.ngen;

import java.util.*;

class HistoryKeeper {
    private static final List<Integer> sHistory = new LinkedList<>();

    public static void add(int num) {
        sHistory.add(num);
    }

    public static void clear() {
        sHistory.clear();
    }

    public static Iterable<Integer> iterator() {
        return Collections.unmodifiableList(sHistory);
    }
}
