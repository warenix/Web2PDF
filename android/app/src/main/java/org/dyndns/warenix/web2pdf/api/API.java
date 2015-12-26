package org.dyndns.warenix.web2pdf.api;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by warenix on 3/21/15.
 */
public class API {
    static final String URL = "http://web2pdf.warenix.app.sailabove.io:5000/pdf/convert";

    public static Result makeCall(Pdf.ConvertService request) throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL);

        StringEntity se = new StringEntity(request.toString());
        se.setContentType("application/json;charset=UTF-8");
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
        httppost.setEntity(se);
        HttpResponse httpresponse = httpclient.execute(httppost);
        String responseText = null;
        try {
            responseText = EntityUtils.toString(httpresponse.getEntity());
            if (responseText != null) {
                try {
                    JSONObject json = new JSONObject(responseText);
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
        } catch (ParseException e) {
            e.printStackTrace();
            Log.i("Parse Exception", e + "");
        }

        return null;

    }

    public interface Request {
    }

    public interface Result {
        public Error getError();
    }

    public interface Error {

    }


}
