package mc.sample.tasks.mobtech.data;

import android.util.Log;
import mc.sample.tasks.mobtech.data.model.Category;

import java.util.Comparator;

public class CategoryComparator implements Comparator<Category> {
    private final static String LOGTAG = CategoryComparator.class.getSimpleName();

    @Override
    public int compare(Category a, Category b) {
        int aid = 0;
        int bid = 0;
        try {
            aid = Integer.parseInt(a.getId());
            bid = Integer.parseInt(b.getId());
        } catch (NumberFormatException e) {
            Log.e(LOGTAG, "Error on parse event id", e);
        }

        if (aid > bid) {
            return 1;
        } else if (aid < bid) {
            return -1;
        }

        return 0;
    }
}
