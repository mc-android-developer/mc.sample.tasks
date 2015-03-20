package mc.sample.tasks.mobtech;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceKeeper {
    private final Typeface mRobotoCondenced;
    private final Typeface mRobotoBoldCondenced;

    public TypefaceKeeper(Context context) {
        mRobotoCondenced = Typeface.createFromAsset(context.getAssets(), "roboto_condenced.ttf");
        mRobotoBoldCondenced = Typeface.createFromAsset(context.getAssets(), "roboto_bold_condenced.ttf");
    }

    public Typeface getRoboto() {
        return mRobotoCondenced;
    }

    public Typeface getRobotoBold() {
        return mRobotoBoldCondenced;
    }
}
