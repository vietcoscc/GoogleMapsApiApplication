package com.example.viet.googlemapsapiapplication;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by viet on 18/08/2017.
 */

public class DirectionRouteAsyncTask extends AsyncTask<Void, Void, ArrayList<LatLng>> {
    private LatLng mSrc;
    private LatLng mDes;
    private Handler mDrawingHandler;

    public DirectionRouteAsyncTask(LatLng src, LatLng des, Handler drawingHandler) {
        this.mSrc = src;
        this.mDes = des;
        this.mDrawingHandler = drawingHandler;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(Void... voids) {
        Direction direction = new Direction();
        Document document = direction.getDocument(mSrc, mDes, Direction.MODE_DRIVING);
        ArrayList<LatLng> arrLatLng = direction.getDirection(document);
        return arrLatLng;
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> latLngs) {
        super.onPostExecute(latLngs);
        Message message = new Message();
        message.obj = latLngs;
        mDrawingHandler.sendMessage(message);
    }
}
