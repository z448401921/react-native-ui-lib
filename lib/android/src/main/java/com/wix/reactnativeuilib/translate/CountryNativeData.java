package com.wix.reactnativeuilib.translate;

import android.annotation.TargetApi;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class CountryNativeData extends ReactContextBaseJavaModule {
    private static final String REACT_CLASS = "CountryNativeData";

    private List<Locale> countryLocale = new ArrayList<>();
    private Map countryMap = new HashMap<String, String>();
    private Locale languageLocale;
    private Semaphore lock = new Semaphore(1);
    private int counter;


    public CountryNativeData(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return CountryNativeData.REACT_CLASS;
    }

    @TargetApi(21)
    @ReactMethod
    public synchronized void updateLanguage(final String language) throws InterruptedException {
        lock.acquire();
        new Thread(new Runnable() {
            @Override
            public void run() {
                countryMap.clear();
                String[] isoCountryCodes = Locale.getISOCountries();
                for (String country : isoCountryCodes) {
                    countryLocale.add(new Locale.Builder().setRegion(country).build());
                }
                languageLocale = new Locale.Builder().setLanguage(language).build();
                loadCountries();
                lock.release();
            }
        }).start();
    }

    private void loadCountries() {
        for (Locale locale : countryLocale) {
            CountryData countryData;
            String countryName = locale.getDisplayCountry(languageLocale);
            String countryCode = locale.getISO3Country();
            String countryID = locale.getCountry();
            Currency currency = Currency.getInstance(locale);
            if (currency != null) {
                countryData = new CountryData(countryID, countryCode, countryName, null, currency.getDisplayName(), currency.getSymbol());
            } else {
                countryData = new CountryData(countryID, countryCode, countryName, null, null, null);
            }
            countryMap.put(countryID, countryData.getJsonObj());
            counter++;
        }
    }

    // can be slow method.
    @TargetApi(21)
    @ReactMethod
    public synchronized void getCountriesData(Promise promise) throws InterruptedException {
        lock.acquire();
        String countryDataAsString = new JSONObject(countryMap).toString();
        Log.e(this.getName(), "count: " + counter);
        promise.resolve(countryDataAsString);
        lock.release();
    }
}
