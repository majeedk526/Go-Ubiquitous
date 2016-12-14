package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class MyService extends WearableListenerService {

    private static String TAG = MyService.class.getName();

    public MyService() {
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        for(DataEvent de : dataEventBuffer){

            if(de.getType() == DataEvent.TYPE_CHANGED){

                if(de.getDataItem().getUri().getPath().equals("/sunshine/values")){
                    DataMap dataMap = DataMapItem.fromDataItem(de.getDataItem()).getDataMap();

                    int max = dataMap.getInt("max");
                    int min = dataMap.getInt("min");

                    Log.d(TAG, "received max : " + max + " min : " + min);


                }



            }


        }

    }
}
