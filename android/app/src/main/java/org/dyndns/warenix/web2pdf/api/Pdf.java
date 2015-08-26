package org.dyndns.warenix.web2pdf.api;

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

    public static class ConvertService implements API.Request {
        private String mUrl;
        private PageSize mPageSize;
        private Orientation mOrientation;

        public ConvertService(String url, PageSize pageSize, Orientation oirentation) {
            mUrl = url;
            mPageSize = pageSize;
            mOrientation = oirentation;
        }
    }

    public static class ConvertResult implements API.Result {

        public Result result;

        @Override
        public API.Error getError() {
            return null;
        }

        public static class Result {
            public String url;
            public String pdf_url;
        }
    }
}
