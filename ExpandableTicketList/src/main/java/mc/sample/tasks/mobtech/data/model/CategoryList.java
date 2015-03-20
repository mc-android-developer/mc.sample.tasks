package mc.sample.tasks.mobtech.data.model;

import java.util.Arrays;

public class CategoryList extends Category {
    private final Category[] mGroups;

    public CategoryList(String id, String name, Category[] groups) {
        super(id, name);
        mGroups = groups;
    }

    public Category[] getGroups() {
        return mGroups;
    }

    @Override
    public String toString() {
        return "Group{" +
                "mEvents=" + Arrays.toString(mGroups) +
                "this=" + super.toString() +
                '}';
    }
}
