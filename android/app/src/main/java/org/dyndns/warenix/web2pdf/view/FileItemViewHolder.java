package org.dyndns.warenix.web2pdf.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.dyndns.warenix.web2pdf.R;
import org.dyndns.warenix.web2pdf.model.FileItem;

/**
 * Created by warenix on 1/4/16.
 */
public class FileItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView mTitleView;
    private final TextView mDateAddedView;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mFileItem != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setDataAndType(mFileItem.getData(), "application/pdf");
                v.getContext().startActivity(intent);
            }
        }
    };
    private TextView mDirectoryView;
    private FileItem mFileItem;

    public static FileItemViewHolder newInstance(ViewGroup parent) {
        final Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.view_file_item, parent, false);

        FileItemViewHolder fileItemViewHolder = new FileItemViewHolder(v);
        return fileItemViewHolder;
    }

    public FileItemViewHolder(View itemView) {
        super(itemView);

        mTitleView = (TextView) itemView.findViewById(R.id.file_item_title);
        mDateAddedView = (TextView) itemView.findViewById(R.id.file_item_date_added);
        mDirectoryView = (TextView) itemView.findViewById(R.id.file_item_directory);
        itemView.setOnClickListener(mOnClickListener);
    }

    public void bindView(FileItem fileItem) {
        if (fileItem != null) {
            mFileItem = fileItem;
            mTitleView.setText(fileItem.getTitle());
            mDateAddedView.setText(DateUtils.getRelativeTimeSpanString(fileItem.getDateAdded() * 1000));
            mDirectoryView.setText(fileItem.getDirectory());
        }
    }
}
