package com.tech.amanah.Constent;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tech.amanah.Utils.DataParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import www.develpoeramit.mapicall.ApiCallBuilder;

public class DrawPollyLine {

    private Context context;
    private LatLng origin;
    private LatLng destination;
    List<List<HashMap<String, String>>> routes = new ArrayList<>();
    ArrayList<LatLng> points = new ArrayList<>();
    private PolylineOptions lineOptions;

    public DrawPollyLine(Context context) {
        this.context = context;
    }

    public static DrawPollyLine get(Context context) {
        return new DrawPollyLine(context);
    }

    public DrawPollyLine setOrigin(LatLng origin) {
        this.origin = origin;
        return this;
    }

    public DrawPollyLine setDestination(LatLng destination) {
        this.destination = destination;
        return this;
    }

    public void execute(onPolyLineResponse listener) {
        String URL = BaseClass.get().getPolyLineUrl(context, origin, destination);
        ApiCallBuilder.build(context)
                .setUrl(URL).execute(new ApiCallBuilder.onResponse() {
            @Override
            public void Success(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    DataParser parser = new DataParser();
                    routes = parser.parse(object);
                    for (int i = 0; i < routes.size(); i++) {
                        points = new ArrayList<>();
//                        lineOptions = new PolylineOptions();
                        List<HashMap<String, String>> path = routes.get(i);
                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);
                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                        }
                        Log.e("SIZE POINT", " True >> " + points.size());
                       /* lineOptions.addAll(points);
                        lineOptions.width(10);
                        lineOptions.color(Color.RED);*/
                        listener.Success(points);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void Failed(String error) {

            }
        });
    }

    public interface onPolyLineResponse {
        void Success(ArrayList<LatLng> latLngs);
    }

}
