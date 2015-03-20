package mc.sample.tasks.mobtech.adapter;

import android.widget.ImageView;
import android.widget.TextView;

class GroupViewHolder extends ChildViewHolder {
    public final ImageView logo;
    public final TextView subtitle;

    GroupViewHolder(ImageView logo, TextView title, TextView subtitle, ImageView expander) {
        super(title, expander);
        this.logo = logo;
        this.subtitle = subtitle;
    }
}
