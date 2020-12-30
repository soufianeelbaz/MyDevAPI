package com.ibm.mydev.personaldata.infrasctructure.mydev.api.utils;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class MyDevResponseCodeUtil {

    private static final int STATUS_CODE_200 = 200;
    private static final int STATUS_CODE_400 = 400;
    private static final int STATUS_CODE_401 = 401;
    private static final int STATUS_CODE_404 = 404;
    private static final int STATUS_CODE_429 = 429;
    private static final int STATUS_CODE_500 = 500;
    private static final int STATUS_CODE_503 = 503;
    private static final int STATUS_CODE_504 = 504;

    private static final String STATUS_MESSAGE_400 = "CSOD Bad Request.";
    private static final String STATUS_MESSAGE_401 = "CSOD Unauthorized.";
    private static final String STATUS_MESSAGE_404 = "CSOD Not Found.";
    private static final String STATUS_MESSAGE_429 = "CSOD Too many requests.";
    private static final String STATUS_MESSAGE_50_ = "CSOD Internal Server Error.";

    public static String getCSODHttpStatusMessage(int status, String httpMessage) throws IOException {
        String message = null;
        if (status != STATUS_CODE_200) {
            switch (status) {
                case STATUS_CODE_400:
                    message = STATUS_MESSAGE_400;
                    break;
                case STATUS_CODE_401:
                    message = STATUS_MESSAGE_401;
                    break;
                case STATUS_CODE_404:
                    message = STATUS_MESSAGE_404;
                    break;
                case STATUS_CODE_429:
                    message = STATUS_MESSAGE_429;
                    break;
                case STATUS_CODE_500:
                case STATUS_CODE_503:
                case STATUS_CODE_504:
                    message = STATUS_MESSAGE_50_;
                    break;
                default:
                    break;
            }
        }
        if (message == null) {
            message = httpMessage;
        }
        return message;
    }
}
