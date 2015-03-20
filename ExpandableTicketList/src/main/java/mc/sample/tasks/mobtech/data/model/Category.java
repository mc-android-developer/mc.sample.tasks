package mc.sample.tasks.mobtech.data.model;

public class Category {
    private final String mId;
    private final String mName;

    public Category(String id, String name) {
        super();
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return "Event{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                '}';
    }
}
