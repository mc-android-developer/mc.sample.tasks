package mc.sample.tasks.mobtech.adapter;

import android.content.Context;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import mc.sample.tasks.mobtech.R;
import mc.sample.tasks.mobtech.TypefaceKeeper;
import mc.sample.tasks.mobtech.data.model.Category;
import mc.sample.tasks.mobtech.data.model.CategoryList;

import java.util.HashSet;

public class CategoryAdapter extends BaseExpandableListAdapter {
    private static final String CATEGORY_IMAGE_PREFIX = "cat_";

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final CategoryList[] mCategoryList;
    private final OnClickListener mClickListener;
    private final TypefaceKeeper mTypefaceKeeper;
    private final HashSet<Integer> mExtendedGroups = new HashSet<>();

    public CategoryAdapter(Context context, CategoryList[] categoryList, TypefaceKeeper typefaceKeeper, OnClickListener clickListener) {
        mCategoryList = categoryList;
        mClickListener = clickListener;
        mTypefaceKeeper = typefaceKeeper;
        mInflater = LayoutInflater.from(context);
        mContext = context.getApplicationContext();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCategoryList[groupPosition].getGroups().length;
    }

    @Override
    public Category getChild(int groupPosition, int childPosition) {
        CategoryList categoryList = mCategoryList[groupPosition];
        return categoryList.getGroups()[childPosition];
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        Category category = getChild(groupPosition, childPosition);
        ChildViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.subcategory, parent, false);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setTypeface(mTypefaceKeeper.getRobotoBold());
            ImageView buttonForward = (ImageView) view.findViewById(R.id.button_forward);
            if (mClickListener != null) {
                buttonForward.setOnClickListener(mClickListener);
            }
            holder = new ChildViewHolder(title, buttonForward);
            view.setTag(holder);
        } else {
            holder = (ChildViewHolder) view.getTag();
        }
        holder.title.setText(category.getName());
        holder.button.setTag(category);
        return view;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCategoryList[groupPosition];
    }

    @Override
    public int getGroupCount() {
        return mCategoryList.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
        CategoryList categoryList = mCategoryList[groupPosition];
        GroupViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.category, parent, false);
            TextView subtitle = (TextView) view.findViewById(R.id.subtitle);
            subtitle.setTypeface(mTypefaceKeeper.getRobotoBold());
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setTypeface(mTypefaceKeeper.getRobotoBold());
            ImageView logo = (ImageView) view.findViewById(R.id.image);
            ImageView expander = (ImageView) view.findViewById(R.id.expander);
            holder = new GroupViewHolder(logo, title, subtitle, expander);
            view.setTag(holder);
        } else {
            holder = (GroupViewHolder) view.getTag();
        }
        holder.title.setText(categoryList.getName());
        holder.subtitle.setText(mContext.getString(R.string.subcategories_num) + " " + categoryList.getGroups().length);
        int imageId = mContext.getResources().getIdentifier(CATEGORY_IMAGE_PREFIX + categoryList.getId(), "drawable", mContext.getPackageName());
        if (imageId == 0) {
            imageId = R.drawable.collections_view_as_list;
        }
        holder.logo.setImageDrawable(mContext.getResources().getDrawable(imageId));
        int drawableId = mExtendedGroups.contains(groupPosition) ? R.drawable.navigation_collapse : R.drawable.navigation_expand;
        holder.button.setImageDrawable(mContext.getResources().getDrawable(drawableId));
        return view;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        mExtendedGroups.add(groupPosition);
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        mExtendedGroups.remove(groupPosition);
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return false;
    }
}
