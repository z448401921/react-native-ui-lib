package com.wix.reactnativeuilib.wheelpicker;

import android.view.View;
import android.widget.NumberPicker;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.wix.reactnativeuilib.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by eladbo on 01/04/2018.
 */

public class WheelPickerManager extends SimpleViewManager<WheelPicker> {
    public static final String REACT_CLASS = "WheelPicker";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public WheelPicker createViewInstance(ThemedReactContext context) {
        return new WheelPicker(context); //If your customview has more constructor parameters pass it from here.
    }

    @ReactProp(name = "data")
    public void setData(WheelPicker wheelPicker, @Nullable ReadableArray data) {
        ArrayList dataList;
        if (data != null) {
            dataList = data.toArrayList();
        } else {
            dataList = new ArrayList();
        }
        final String[] arrayString= (String[]) dataList.toArray(new String[0]);

        wheelPicker.setDisplayedValues(null);
        wheelPicker.setWrapSelectorWheel(false);
        wheelPicker.setMinValue(0);
        wheelPicker.setMaxValue(arrayString.length -1);
        wheelPicker.setDisplayedValues( arrayString );
    }

    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "topChange",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onChange")))
                .build();
    }
}
