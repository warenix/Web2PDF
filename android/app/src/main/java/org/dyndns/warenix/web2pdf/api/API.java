package org.dyndns.warenix.web2pdf.api;

import java.io.IOException;

/**
 * Created by warenix on 3/21/15.
 */
public class API {
    public static Result makeCall(Request request) throws IOException {
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
