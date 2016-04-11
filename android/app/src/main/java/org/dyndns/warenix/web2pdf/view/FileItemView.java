package org.dyndns.warenix.web2pdf.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.dyndns.warenix.web2pdf.R;

/**
 * Created by warenix on 2/21/16.
 */
public class FileItemView extends LinearLayout {
    private final TextView mTitleView;
    private final TextView mDateAddedView;
    private final TextView mDirectoryView;
    private OnClickListener mOnClickListener;

    public FileItemView(Context context) {
        this(context, null);
    }

    public FileItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.file_item_view, this, true);

        mTitleView = (TextView) findViewById(R.id.file_item_title);
        mDateAddedView = (TextView) findViewById(R.id.file_item_date_added);
        mDirectoryView = (TextView) findViewById(R.id.file_item_directory);
    }


    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
        setOnClickListener(l);
    }

    public void setTitle(CharSequence title) {
//        mTitleView.setText(title);
    }

    public void setDateAdded(CharSequence dateAdded) {
        mDateAddedView.setText(dateAdded);
    }

    public void setDirectory(CharSequence directory) {
        mDirectoryView.setText(directory);
    }
}
