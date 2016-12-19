package com.example.android.sunshine.app;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.sunshine.app.data.WeatherContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

/**
 * Created by Majeed on 13-12-2016.
 */

public class SendMessaageService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    private static final String TAG = "WearTest";

    public static String WEAR_ACTION = "com.example.android.sunshine.app.SEND_TO_WEARABLE";

    public SendMessaageService() {
        super(SendMessaageService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "in sendMessage Service");



            mGoogleApiClient = new GoogleApiClient.Builder(SendMessaageService.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApiIfAvailable(Wearable.API)
                    .build();

            mGoogleApiClient.connect();

    }

    private Asset createAssetFromBitmap(Bitmap bitmap){



        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());

    }

    private void sendMessage(int id, String max, String min){

            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/wear-data");

            putDataMapRequest.getDataMap().putString("max", max);
            putDataMapRequest.getDataMap().putString("min", min);

            Bitmap bitmap = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(getResources(),Utility.getArtResourceForWeatherCondition(id))
                    ,100,100,true );
            putDataMapRequest.getDataMap().putAsset("image", createAssetFromBitmap(bitmap));
            putDataMapRequest.getDataMap().putInt("id", id);
            putDataMapRequest.getDataMap().putLong("time", System.currentTimeMillis());

            PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient,putDataRequest)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                            if(dataItemResult.getStatus().isSuccess()){
                                Log.d(TAG, "weather data stored successfully");
                            }
                            else {
                                Log.d(TAG, "could not store weather data.");
                            }
                        }
                    });
        }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        String location = Utility.getPreferredLocation(this);
        Uri uri = WeatherContract.WeatherEntry
                .buildWeatherLocationWithDate(location, System.currentTimeMillis());

        Cursor c = getContentResolver().query(
                uri,
                new String[]{WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
                }, null, null, null);


        if (c.moveToFirst()) {
            int id = c.getInt(c.getColumnIndex(
                    WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
            String max = Utility.formatTemperature(this, c.getDouble(
                    c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)));
            String min = Utility.formatTemperature(this, c.getDouble(
                    c.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)));

            sendMessage(id, max,min);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection suspended " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed : " + connectionResult.getErrorMessage() + " " + connectionResult.getErrorCode());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       //mGoogleApiClient.disconnect();
    }
}
