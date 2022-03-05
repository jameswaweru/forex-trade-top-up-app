package com.forex.forex_topup.utils;

import org.json.JSONObject;

public interface ResponseCallback {
    void onResponse(JSONObject response);
    void onErrorResponse(JSONObject response);
}
