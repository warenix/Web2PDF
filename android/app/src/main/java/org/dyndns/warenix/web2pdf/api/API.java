package org.dyndns.warenix.web2pdf.api;

import android.util.Log;

import org.dyndns.warenix.web2pdf.util.NetworkUtil;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

/**
 * Created by warenix on 3/21/15.
 */
public class API {
    static final String URL = "https://fosshost.warenix.ddnsgeek.com/web2pdf/pdf/convert";
    private static final MediaType CONTENT_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient getOkHttpClient() {
        final PdfServiceProgressListener progressListener = new PdfServiceProgressListener();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        if (chain.request().tag() instanceof Pdf.ConvertService) {
                            Pdf.ConvertService request = (Pdf.ConvertService) chain.request().tag();
                            progressListener.setRequestId(request.getUrl());

                            Response originalResponse = chain.proceed(chain.request());
                            return originalResponse.newBuilder()
                                    .body(new NetworkUtil.ProgressResponseBody(originalResponse.body(), progressListener))
                                    .build();
                        }
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder().build();
                    }
                })
                .readTimeout(1, TimeUnit.HOURS)
                .writeTimeout(1, TimeUnit.HOURS)
                .connectTimeout(1, TimeUnit.HOURS)
                .build();
        return okHttpClient;
    }

    public static Result makeCall(Pdf.ConvertService request) throws IOException {
        RequestBody requestBody = RequestBody.create(CONTENT_TYPE_JSON, request.toString());

        OkHttpClient okHttpClient = getOkHttpClient();
        Request okRequest = new Request.Builder()
                .tag(request)
                .url(URL)
                .post(requestBody)
                .build();


        Response response = okHttpClient.newCall(okRequest).execute();
        String responseText = response.body().string();

        if (responseText != null) {
            try {
                Log.d("response", responseText);
                JSONObject json = new JSONObject(responseText);
                if (json.has("error")) {
                    Pdf.ConvertResult result = new Pdf.ConvertResult();
                    result.error = new Exception(json.getString("error"));
                    return result;
                }
                JSONObject resultJson = json.getJSONObject("result");
                Pdf.ConvertResult result = new Pdf.ConvertResult();
                result.result = new Pdf.ConvertResult.Result();
                result.result.pdf_url = resultJson.getString("pdf_url");
                result.result.url = resultJson.getString("url");
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return null;

    }

    public static boolean downloadFile(String url, File downloadedFile) throws IOException {

        Request okRequest = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient okHttpClient = getOkHttpClient();
        Response response = okHttpClient.newCall(okRequest).execute();

        BufferedSink sink = null;
        try {
            sink = Okio.buffer(Okio.sink(downloadedFile));
            sink.writeAll(response.body().source());
            sink.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public interface Result {
        Exception getError();
    }

    public static class ProgressReport {
        private final String mRequestId;
        private long mBytesRead;
        private long mContentLength;
        private boolean mDone;

        public ProgressReport(String requestId, long bytesRead, long contentLength, boolean done) {
            mRequestId = requestId;
            mBytesRead = bytesRead;
            mContentLength = contentLength;
            mDone = done;
        }

        public long getBytesRead() {
            return mBytesRead;
        }

        public long getContentLength() {
            return mContentLength;
        }

        public boolean isDone() {
            return mDone;
        }

        public float getPercentageDone() {
            if (mContentLength > 0) {
                return (100 * mBytesRead) / mContentLength;
            }
            return 0;
        }
    }

    public static class PdfServiceProgressListener implements NetworkUtil.ProgressListener {
        /**
         * unique identifier for this download
         */
        private String mRequestId;

        public void setRequestId(String requestId) {
            mRequestId = requestId;
        }

        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            System.out.println(bytesRead);
            System.out.println(contentLength);
            System.out.println(done);
            System.out.format("mRequestId[%s] %d%% done\n", mRequestId, (100 * bytesRead) / contentLength);

            EventBus.getDefault().post(new ProgressReport(mRequestId, bytesRead, contentLength, done));
        }
    }
}
