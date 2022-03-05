package com.forex.forex_topup.utils;

import org.json.JSONObject;

public interface ErrorResponseCallback {
    void onErrorResponse(JSONObject response);
}
