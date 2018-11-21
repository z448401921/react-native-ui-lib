package com.wix.reactnativeuilib.translate;


import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

public class CountryData{

    private String id;
    private String code;
    private String name;
    private String dialCode;
    private String currencyName;
    private String currencySymbol;

    public CountryData(String id, String code, String name, @Nullable String dialCode, @Nullable String currencyName, @Nullable String currencySymbol) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dialCode = dialCode == null ? "+00" : dialCode;
        this.currencyName = currencyName;
        this.currencySymbol = currencySymbol;
    }

    public JSONObject getJsonObj() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("code", code);
            jsonObject.put("name", name);
            jsonObject.put("dialCode", dialCode);
            jsonObject.put("currencyName", currencyName);
            jsonObject.put("currencySymbol", currencySymbol);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
