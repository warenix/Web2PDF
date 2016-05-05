package org.dyndns.warenix.web2pdf.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by warenix on 3/21/15.
 */
public class Pdf {


    public static enum PageSize {
        A3, A4, LEGAL, LETTER,
    }

    public static enum Orientation {
        PORTRAIT, LANDSCAPE
    }

    public static class ConvertService {
        private String mUrl;
        private PageSize mPageSize;
        private Orientation mOrientation;

        public ConvertService(String url, PageSize pageSize, Orientation oirentation) {
            mUrl = url;
            mPageSize = pageSize;
            mOrientation = oirentation;
        }

        @Override
        public String toString() {
            JSONObject json = new JSONObject();
            if (mPageSize != null) {
                try {
                    json.put("page-size", pageSizeToString(mPageSize));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (mOrientation != null) {
                try {
                    json.put("orientation", orientationToString(mOrientation));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (mUrl != null) {
                try {
                    json.put("url", mUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json.toString();
        }


        public String getUrl() {
            return mUrl;
        }
    }

    public static class ConvertResult implements API.Result {

        public Result result;
        public Exception error;

        @Override
        public Exception getError() {
            return error;
        }

        public static class Result {
            public String url;
            public String pdf_url;
        }

    }

    public static String pageSizeToString(PageSize pageSize) {
        switch (pageSize) {
            case A3:
                return "A3";
            case A4:
                return "A4";
            case LEGAL:
                return "Legal";
            case LETTER:
                return "Letter";
        }
        return "";
    }

    public static String orientationToString(Orientation orientation) {
        switch (orientation) {

            case PORTRAIT:
                return "Portrait";
            case LANDSCAPE:
                return "Landscape";
        }
        return "";
    }
}
