package org.dyndns.warenix.web2pdf.view;

import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import org.dyndns.warenix.web2pdf.model.FileItem;

/**
 * Created by warenix on 1/4/16.
 */
public class FileManagerAdapter extends RecyclerView.Adapter<FileItemViewHolder> {
    private Cursor mCursor;

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return FileItemViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(FileItemViewHolder holder, int position) {
        if (mCursor == null) {
            return;
        }
        if (mCursor.moveToPosition(position)) {
            FileItem fileItem = FileItem.load(mCursor);
            holder.bindView(fileItem);
        }
    }

    public FileItem getData(int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            FileItem fileItem = FileItem.load(mCursor);
            return fileItem;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }
}
