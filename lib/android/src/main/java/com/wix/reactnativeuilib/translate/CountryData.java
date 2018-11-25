package com.wix.reactnativeuilib.translate;


import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nullable;

public class CountryData{

    private String name;
    private String dialCode;
    private String currencyName;
    private String currencySymbol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(@Nullable String dialCode) {
        this.dialCode = dialCode == null ? "+00" : dialCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(@Nullable String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(@Nullable String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public JSONObject getJsonObj() {
        JSONObject jsonObject = new JSONObject();
        try {
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
