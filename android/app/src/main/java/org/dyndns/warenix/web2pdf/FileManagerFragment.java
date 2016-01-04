package org.dyndns.warenix.web2pdf;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import org.dyndns.warenix.web2pdf.model.FileItem;
import org.dyndns.warenix.web2pdf.view.FileManagerAdapter;

/**
 * Created by warenix on 1/4/16.
 */
public class FileManagerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID_GET_ALL_PDF = 1;
    private static final String TAG = FileManagerFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private FileManagerAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().restartLoader(LOADER_ID_GET_ALL_PDF, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // only pdf
        String[] projection = null;
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] selectionArgsPdf = new String[]{mimeType};
        String sortOrder =  MediaStore.Files.FileColumns.DATE_ADDED + " desc";

        Uri uri = MediaStore.Files.getContentUri("external");
        return new CursorLoader(getContext(), uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, String.format("found %d files on device", data == null ? 0 : data.getCount()));
        data.moveToPosition(-1);
        FileItem fileItem = null;
        while (data.moveToNext()) {
            fileItem = FileItem.load(data);
            Log.d(TAG, fileItem.toString());
        }
        data.moveToPosition(-1);
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public static FileManagerFragment newInstance() {
        FileManagerFragment f = new FileManagerFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_manager, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new FileManagerAdapter();
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }
}
