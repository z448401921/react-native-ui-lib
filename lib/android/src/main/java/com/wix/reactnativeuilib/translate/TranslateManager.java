package com.wix.reactnativeuilib.translate;

import android.annotation.TargetApi;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TranslateManager extends ReactContextBaseJavaModule {
    private static final String REACT_CLASS = "Translate";

    public TranslateManager(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return TranslateManager.REACT_CLASS;
    }

    @TargetApi(21)
    @ReactMethod
    public void getCountryByLocale(String language, Callback callback) {
        Map countryMap = new HashMap<String, String>();

        String[] isoCountryCodes = Locale.getISOCountries();
        for(String country: isoCountryCodes) {
            Locale countryLocale = new Locale.Builder().setRegion(country).build();
            Locale languageLocale = new Locale.Builder().setLanguage(language).build();

            String countryName = countryLocale.getDisplayCountry(languageLocale);

            countryMap.put(country, countryName);
        }

        callback.invoke(countryMap);
    }
}
