package mc.sample.tasks.treto.adapter;

import android.widget.ProgressBar;

import uk.co.senab.photoview.PhotoView;

class ViewHolder {
    public final PhotoView photoView;
    public final ProgressBar progressBar;

    public ViewHolder(PhotoView photoView, ProgressBar progressBar) {
        this.photoView = photoView;
        this.progressBar = progressBar;
    }
}
