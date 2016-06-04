package org.dyndns.warenix.web2pdf;

import android.app.DownloadManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.dyndns.warenix.web2pdf.api.API;
import org.dyndns.warenix.web2pdf.api.Pdf;
import org.dyndns.warenix.web2pdf.api.Pdf.ConvertResult;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Backend service to talk to Web2PDF API
 *
 * @author warenix
 */
public class Web2PDFIntentService extends IntentService {
    public static final String ACTION_WEB2PDF =
            "org.dyndns.warenix.web2pdf.Web2PDFIntentService.ACTION_WEB2PDF";
    private static final String TAG = "Web2PDFIntentService";
    private static final String BUNDLE_ARG =
            "org.dyndns.warenix.web2pdf.Web2PDFIntentService.BUNDLE_ARG";
    /**
     * notification id of conversion error
     */
    private static final int ERROR_NOTIFICATION_ID = 1;
    NotificationManager mNotificationManager;
    static AtomicInteger sNextNotificationId = new AtomicInteger(0);

    public Web2PDFIntentService() {
        super("Web2PDFIntentService");
    }

    /**
     * Helper method to start this service
     *
     * @param context
     * @param arg
     */
    public static void startService(Context context, Web2PDFArgument arg) {
        Bundle extras = new Bundle();
        extras.putParcelable(BUNDLE_ARG, arg);
        Intent intent = new Intent(Web2PDFIntentService.ACTION_WEB2PDF);
        intent.setClass(context, Web2PDFIntentService.class);
        intent.putExtras(extras);
        context.startService(intent);
    }

    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (ACTION_WEB2PDF.equals(action)) {
            handleWeb2PDF(intent);
        }
    }

    /**
     * logic to pass user requirement to api service
     *
     * @param intent
     */
    private void handleWeb2PDF(Intent intent) {
        Bundle extras = intent.getExtras();
        Web2PDFArgument arg = extras.getParcelable(BUNDLE_ARG);
        Pdf.ConvertService service = new Pdf.ConvertService(arg.url, arg.size, arg.orientation);

        try {
            ConvertResult result = (ConvertResult) API.makeCall(service);
            if (result == null) {
                Log.d(TAG, String.format("error convering [%s]occurs: no result", arg.url));
                return;
            }

            if (result.getError() != null) {
                Log.d(TAG, String.format("error convering [%s]occurs:%s", arg.url, result.getError()));
                showNotification(arg.url, result.getError());
                return;
            }
            Log.d(TAG, "pdf_url:" + result.result.pdf_url);
            downloadPDFUsingService(arg, result);
        } catch (IOException e) {
            e.printStackTrace();
            showNotification(arg.url, e);
        }
    }

    private void downloadPDFUsingService(Web2PDFArgument arg, ConvertResult result) {
        String httpsDownloadUrl = result.result.pdf_url.replace("http", "https");

        final Context context = getApplicationContext();
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        File downloadedFile = new File(folder, arg.filename);
        try {
            boolean success = API.downloadFile(httpsDownloadUrl, downloadedFile);
            if (success) {
                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                manager.addCompletedDownload(arg.filename, result.result.url, true, "application/pdf", downloadedFile.getAbsolutePath(), downloadedFile.length(), true);
            } else {
                showNotification(context, "Downloaded failed", httpsDownloadUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showNotification(result.result.pdf_url, e);
        }
    }

    private void showNotification(String url, Exception e) {
        // generate notification
        String notificationText = e == null ? "error" : e.getMessage();
        showNotification(this, url, notificationText);
    }

    public static void showNotification(Context context, String title, String message) {
        Notification notif = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(aBitmap)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .build();
        notif.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        NotificationManager nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        nm.notify(sNextNotificationId.getAndIncrement(), notif);
    }
}
