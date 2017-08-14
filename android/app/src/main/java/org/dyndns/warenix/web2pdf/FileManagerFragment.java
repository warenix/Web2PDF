package org.dyndns.warenix.web2pdf;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import org.dyndns.warenix.web2pdf.model.FileItem;
import org.dyndns.warenix.web2pdf.view.FileManagerAdapter;
import org.dyndns.warenix.web2pdf.view.OnRecyclerItemClickListener;

import java.io.File;

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
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " desc";

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
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {

            @Override
            protected void onLongClick(RecyclerView.ViewHolder vh) {

            }

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Log.d(TAG, "click " + vh.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                FileItem fileItem = mAdapter.getData(vh.getAdapterPosition());

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setDataAndType(fileItem.getData(), "application/pdf");
                } else {
                    File file = new File(fileItem.getData().getPath());
                    Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(fileUri, "application/pdf");
                }
                startActivity(intent);
            }
        });
        return rootView;
    }
}
