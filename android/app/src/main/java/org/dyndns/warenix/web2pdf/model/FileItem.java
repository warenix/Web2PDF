package org.dyndns.warenix.web2pdf.model;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.List;

/**
 * Created by warenix on 1/4/16.
 */
public class FileItem {

    private static final String TAG = FileItem.class.getSimpleName();

    private long mId;
    private String mTitle;
    private long mDateAdded;
    private Uri mData;

    private String mDirectory;

    public static FileItem load(Cursor data) {
        if (data == null) {
            return null;
        }

        FileItem fileItem = new FileItem();
        final int idIndex = data.getColumnIndex(MediaStore.Files.FileColumns._ID);
        final int titleIndex = data.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
        final int dateAddedIndex = data.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED);
        final int dataIndex = data.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        fileItem.mId = data.getLong(idIndex);
        fileItem.mTitle = data.getString(titleIndex);
        fileItem.mDateAdded = data.getLong(dateAddedIndex);
        fileItem.mData = Uri.parse("file://" + data.getString(dataIndex));

        List<String> pathSegmentList = fileItem.mData.getPathSegments();
        if (pathSegmentList.size() >= 2) {
            fileItem.mDirectory = pathSegmentList.get(pathSegmentList.size() - 2);
        }
        return fileItem;
    }

    @Override
    public String toString() {
        return String.format("id[%d] title[%s] dateAdded[%d] data[%s] directory[%s]", mId, mTitle, mDateAdded, mData, mDirectory);
    }

    public String getTitle() {
        return mTitle;
    }

    public long getDateAdded() {
        return mDateAdded;
    }

    public long getId() {
        return mId;
    }

    public Uri getData() {
        return mData;
    }

    public String getDirectory() {
        return mDirectory;
    }
}
