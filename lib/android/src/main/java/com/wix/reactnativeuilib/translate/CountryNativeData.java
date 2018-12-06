package com.wix.reactnativeuilib.translate;

import android.annotation.TargetApi;
import android.util.Log;
import android.util.Pair;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.wix.reactnativeuilib.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import javax.annotation.Nullable;

public class CountryNativeData extends ReactContextBaseJavaModule {
    private static final String REACT_CLASS = "CountryNativeData";

    private List<Locale> countryLocale = new ArrayList<>();
    private Map countryMap = new HashMap<String, CountryData>();
    private Locale languageLocale;
    private JSONObject dialCodeJson;
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

                if (!countryMap.isEmpty()) {
                    countryMap.clear();
                }
//                long b = System.currentTimeMillis();
                languageLocale = new Locale.Builder().setLanguage(language).build();
                for (String country : Locale.getISOCountries()) {
                    Locale locale = new Locale.Builder().setRegion(country).build();
                    countryLocale.add(locale);
                    initCountryMap(locale);
                }
//                addDialCodeToCountryData();
//                long a = System.currentTimeMillis();

//                Log.e(CountryNativeData.this.getName(), "Time: " + (a - b));
                lock.release();
            }
        }, "country_thread").start();
    }

    private void initCountryMap(Locale locale) {
        String displayCountry = locale.getDisplayCountry(languageLocale);
        String countryID = locale.getCountry();
        CountryData countryData = new CountryData();
        countryData.setName(displayCountry);
        countryMap.put(countryID, countryData);
    }

    @TargetApi(21)
    @ReactMethod
    public void loadCountriesDialCode(Promise promise) throws InterruptedException {
        lock.acquire();
        long b = System.currentTimeMillis();
        dialCodeJson = getJsonFromRaw(R.raw.country_dial_code);
        for (Locale locale : countryLocale) {
            String countryID = locale.getCountry();
            CountryData countryData = (CountryData) countryMap.get(countryID);
            retrieveDialCode(countryID, countryData);
        }
        promise.resolve(parseCountryData());
        long a = System.currentTimeMillis();
        Log.e(CountryNativeData.this.getName() + " DialCode", "Time: " + (a - b));
        lock.release();
    }

    private void retrieveDialCode(String countryID, CountryData countryData) {
        try {
            if (dialCodeJson.get(countryID) != null) {
                countryData.setDialCode(dialCodeJson.get(countryID).toString());
            }
        } catch (JSONException e) {
        }
    }

    @TargetApi(21)
    @ReactMethod
    public void loadCountriesCurrency(Promise promise) throws InterruptedException {
        lock.acquire();
        long b = System.currentTimeMillis();
        for (Locale locale : countryLocale) {
            String countryID = locale.getCountry();
            CountryData countryData = (CountryData) countryMap.get(countryID);
            retrieveCurrency(countryData, locale);
        }
        promise.resolve(parseCountryData());
        long a = System.currentTimeMillis();
        Log.e(CountryNativeData.this.getName() + " Currency", "Time: " + (a - b));
        lock.release();
    }

    private void retrieveCurrency(CountryData countryData, Locale locale) {
        Currency currency = Currency.getInstance(locale);
        if (currency != null) {
            countryData.setCurrencyName(currency.getDisplayName());
            countryData.setCurrencySymbol(currency.getSymbol());
        }
    }

    private String parseCountryData() {
        Map<String, JSONObject> countryMapParsed = new HashMap<>();
        for (Map.Entry<String, CountryData> entry : (Set<Map.Entry<String, CountryData>>) countryMap.entrySet()) {
            countryMapParsed.put(entry.getKey(), entry.getValue().getJsonObj());
        }
        return new JSONObject(countryMapParsed).toString();
    }

//    private void addDialCodeToCountryData() {
//        JSONObject dialCodeJson = getJsonFromRaw(R.raw.country_dial_code);
//        loadCountries(dialCodeJson);
//    }
//
//    private void loadCountries(JSONObject dialCodeJson) {
//        for (Locale locale : countryLocale) {
//            String countryID = locale.getCountry();
//            CountryData countryData = (CountryData) countryMap.get(countryID);
//            try {
//                Currency currency = Currency.getInstance(locale);
//                if (currency != null) {
//                    countryData.setCurrencyName(currency.getDisplayName());
//                    countryData.setCurrencySymbol(currency.getSymbol());
//                }
//                if (dialCodeJson.get(countryID) != null) {
//                    countryData.setDialCode(dialCodeJson.get(countryID).toString());
//                }
//                counter++;
//            } catch (Exception e) {
//            }
//        }
//    }

//    public void getCurrencyLocaleMap() {
//        for (Locale locale : Locale.getAvailableLocales()) {
//            CountryData countryData;
//            String countryName = locale.getDisplayCountry(languageLocale);
//            String countryID = locale.getCountry();
//            try {
//                String countryCode = locale.getISO3Country();
//                Currency currency = Currency.getInstance(locale);
//                Log.e(this.getName(), "currency: " + currency.getSymbol() + ", locale:" + locale.toString());
//                if (currency != null) {
//                    countryData = new CountryData(countryID, countryCode, countryName, null, currency.getDisplayName(), currency.getSymbol());
//                } else {
//                    countryData = new CountryData(countryID, countryCode, countryName, null, null, null);
//                }
//                countryMap.put(countryID, countryData.getJsonObj());
//                counter++;
//            } catch (Exception e) {
//            }
//        }
//    }


    public @Nullable
    JSONObject getJsonFromRaw(int id) {
        String JSONString;
        JSONObject jsonObject = null;
        InputStream is = getReactApplicationContext().getResources().openRawResource(id);
        int sizeOfJSONFile = 0;
        try {
            sizeOfJSONFile = is.available();
            //array that will store all the data
            byte[] bytes = new byte[sizeOfJSONFile];

            //reading data into the array from the file
            is.read(bytes);

            //close the input stream
            is.close();

            JSONString = new String(bytes, "UTF-8");
            jsonObject = new JSONObject(JSONString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

//    // can be slow method.
//    @TargetApi(21)
//    @ReactMethod
//    public synchronized void getCountriesData(Promise promise) throws InterruptedException {
//        long b = System.currentTimeMillis();
//        lock.acquire();
//        String countryDataAsString = new JSONObject(countryMap).toString();
//        Log.e(this.getName(), "count: " + counter);
//        promise.resolve(countryDataAsString);
//        long a = System.currentTimeMillis();
//        Log.e(CountryNativeData.this.getName(), "Time: " + (a - b));
//        lock.release();
//    }
}
