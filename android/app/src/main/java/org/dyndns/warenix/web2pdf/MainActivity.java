package org.dyndns.warenix.web2pdf;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import org.dyndns.warenix.web2pdf.api.API;
import org.dyndns.warenix.web2pdf.api.Pdf.Orientation;
import org.dyndns.warenix.web2pdf.api.Pdf.PageSize;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

/**
 * The main activity accepts user configurations to convert a url to pdf. The pdf file is downloaded
 * by either: {@link DownloadManager} when the system verions is {@link VERSION_CODES#GINGERBREAD}
 * or above other wise, the generated pdf url will be opened by default browser
 *
 * @author warenix
 */
@TargetApi(VERSION_CODES.GINGERBREAD)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * This pdf extension will be checked against user input on {@link #mFilename}. This will be
     * appended if needed.
     */
    private static final String PDF_EXT = ".pdf";
    private static final String PREF_NAME = "web2pdfSharedPrefs";
    /**
     * stored the selected page size view id, {@link #sizeIDs}
     */
    private static final String PREF_KEY_SIZE = "size";
    /**
     * stored the selected orientation view id, {@link #orientationIDs}
     */
    private static final String PREF_KEY_ORIENTATION = "orientation";
    private int[] sizeIDs = {R.id.a4, R.id.a3, R.id.legal, R.id.letter};
    private int[] orientationIDs = {R.id.portrait, R.id.landscape};
    private HorizontalScrollView mSizeHorizontalScrollView;
    private SizeOnClickListener mSizeOnClickListener;
    private HorizontalScrollView mOrientationHorizontalScrollView;
    private OrientationOnClickListener mOrientationOnClickListener;
    private EditText mUrl;
    private EditText mFilename;
    /**
     * user selected page size
     */
    private int mSize;
    /**
     * user selected orientation
     */
    private int mOrientation;
    private SharedPreferences mPrefs;
    private int PADDING = 16;
    private View mContentFrame;
    private Menu mMenu;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // restore default option
        mPrefs = getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        mSize = mPrefs.getInt(PREF_KEY_SIZE, R.id.a4);
        mOrientation = mPrefs.getInt(PREF_KEY_ORIENTATION, R.id.portrait);

        mUrl = (EditText) findViewById(R.id.url);
        mFilename = (EditText) findViewById(R.id.filename);
        if (VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
            // filename is useful when we use download manager
            mFilename.setVisibility(View.GONE);
            findViewById(R.id.filenameLabel).setVisibility(View.GONE);
        }

        mSizeHorizontalScrollView = (HorizontalScrollView) findViewById(R.id.size_scroller);
        mSizeOnClickListener = new SizeOnClickListener();
        for (int id : sizeIDs) {
            findViewById(id).setOnClickListener(mSizeOnClickListener);
        }

        mOrientationHorizontalScrollView =
                (HorizontalScrollView) findViewById(R.id.orientation_scroller);
        mOrientationOnClickListener = new OrientationOnClickListener();
        for (int id : orientationIDs) {
            findViewById(id).setOnClickListener(mOrientationOnClickListener);
        }

        // if a url is shared from other apps, like system browser
        String url = getURLFromIntent();
        if (url != null) {
            mUrl.setText(url);
            // extract the domain as pdf filename
            Uri uri = Uri.parse(url);
            mFilename.setText(uri.getAuthority() + PDF_EXT);
        }
        mSizeOnClickListener.setSelect(mSize);
        mOrientationOnClickListener.setSelect(mOrientation);

        mContentFrame = findViewById(R.id.content_frame);
        if (savedInstanceState != null) {
            boolean isFileManagerShown = isFileManagerShown();
            mContentFrame.setVisibility(isFileManagerShown() ? View.VISIBLE : View.INVISIBLE);
        }
    }


    public void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt(PREF_KEY_SIZE, mSize);
        ed.putInt(PREF_KEY_ORIENTATION, mOrientation);
        ed.commit();

        EventBus.getDefault().unregister(this);
    }

    public void onSizeSelected(int id) {
        mSize = id;
    }

    private PageSize convertViewIDToSize(int id) {
        PageSize value = PageSize.A4;
        switch (id) {
            case R.id.a3: {
                value = PageSize.A3;
                break;
            }
            case R.id.a4: {
                value = PageSize.A4;
                break;
            }
            case R.id.legal: {
                value = PageSize.LEGAL;
                break;
            }
            case R.id.letter: {
                value = PageSize.LETTER;
                break;
            }
        }
        return value;
    }

    public void onOrientationSelected(int id) {
        mOrientation = id;
    }

    private Orientation convertViewIDToOrientation(int id) {
        Orientation value = Orientation.PORTRAIT;
        switch (id) {
            case R.id.landscape: {
                value = Orientation.LANDSCAPE;
                break;
            }
            case R.id.portrait: {
                value = Orientation.PORTRAIT;
                break;
            }
        }
        return value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mMenu = menu;
        updateToolBarMenus(isFileManagerShown());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_convert: {
                convertNow();
                return true;
            }
            case R.id.action_file_manager: {
                mContentFrame.setVisibility(View.VISIBLE);
                showFileManager();
                updateToolBarMenus(true);
                return true;
            }
            case android.R.id.home:
            case R.id.home:
            case R.id.homeAsUp: {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFileManager() {
        FileManagerFragment f = FileManagerFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, f, FileManagerFragment.class.getSimpleName())
                .addToBackStack(FileManagerFragment.class.getSimpleName())
                .commit();
    }

    private boolean isFileManagerShown() {
        // assumed there's only one FileManagerFragment in the backstack
        return getSupportFragmentManager().getBackStackEntryCount() > 0;
    }

    private void updateToolBarMenus(boolean isFileManagerShown) {
        if (isFileManagerShown) {
            mMenu.findItem(R.id.action_convert).setVisible(false);
            mMenu.findItem(R.id.action_file_manager).setVisible(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            mMenu.findItem(R.id.action_convert).setVisible(true);
            mMenu.findItem(R.id.action_file_manager).setVisible(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

    }

    /**
     * action involked when user press the convert action button
     */
    private void convertNow() {
        // do validations on url and filename fields
        String url = mUrl.getText().toString().trim();
        if (url.equals("")) {
            mUrl.setError("Please input a url");
            return;
        } else {
            mUrl.setError(null);
        }

        String filename = null;
        if (mFilename != null && mFilename.getVisibility() == View.VISIBLE) {
            filename = mFilename.getText().toString().trim();
            if (filename.equals("")) {
                mFilename.setError("Please input a desire filename");
                return;
            } else {
                mFilename.setError(null);
            }
            // smartly append pdf extension if needed
            boolean withPDFExt = filename.toLowerCase(Locale.US).endsWith(PDF_EXT);
            if (!withPDFExt) {
                filename = filename.concat(PDF_EXT);
                mFilename.setText(filename);
            }
        }

        // Log.d(TAG, "url:" + url);
        // Log.d(TAG, "size:" + mSize);
        // Log.d(TAG, "orientation:" + mOrientation);
        // Log.d(TAG, "filename:" + filename);

        Web2PDFArgument arg = new Web2PDFArgument();
        arg.url = url;
        arg.size = convertViewIDToSize(mSize);
        arg.orientation = convertViewIDToOrientation(mOrientation);
        arg.filename = filename;

        Web2PDFIntentService.startService(this, arg);
        Toast.makeText(this, "Converting now...", Toast.LENGTH_SHORT).show();
    }

    /**
     * extract shared url from intent
     *
     * @return
     */
    protected String getURLFromIntent() {
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            return bundle.getString(Intent.EXTRA_TEXT);
        }
        return null;
    }

    /**
     * Set a view selected when it hasn't been selected, meanwhile deselect previously selected one.
     *
     * @author warenix
     */
    private class SingleSelectOnClickListener implements OnClickListener {
        /**
         * currently selected view id
         */
        protected int mSelectedViewID = -1;

        @Override
        public void onClick(View view) {
            int viewID = view.getId();
            if (mSelectedViewID == viewID) {
                return;
            }
            setSelect(viewID);
        }

        /**
         * logic to set a view selected
         *
         * @param viewID a view is being selected
         */
        public void setSelect(int viewID) {
            View view = null;
            if (mSelectedViewID != -1) {
                view = findViewById(mSelectedViewID);
                if (view != null) {
                    view.setSelected(false);
                }
            }
            mSelectedViewID = viewID;
            view = findViewById(mSelectedViewID);
            if (view != null) {
                view.setSelected(true);
            }
        }
    }

    private class SizeOnClickListener extends SingleSelectOnClickListener {

        @Override
        public void onClick(View view) {
            super.onClick(view);
            mSizeHorizontalScrollView.scrollTo(view.getLeft() - PADDING, 0);
        }

        @Override
        public void setSelect(int viewID) {
            super.setSelect(viewID);
            onSizeSelected(viewID);
        }
    }

    private class OrientationOnClickListener extends SingleSelectOnClickListener {

        @Override
        public void onClick(View view) {
            super.onClick(view);

            mOrientationHorizontalScrollView.scrollTo(view.getLeft() - PADDING, 0);
        }

        @Override
        public void setSelect(int viewID) {
            super.setSelect(viewID);
            onOrientationSelected(viewID);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }


    Toast mToast;

    @Subscribe
    public void onEventMainThread(API.ProgressReport progressReport) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), String.format("downloaed [%.2f]%%", progressReport.getPercentageDone()),
                Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            mContentFrame.setVisibility(View.INVISIBLE);
            updateToolBarMenus(false);
        }
        super.onBackPressed();
    }

}
