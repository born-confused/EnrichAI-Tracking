package product.enrichai.com.enrichaitracking;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WELCOME on 6/15/2018.
 */

public class DrawGeoJsonLineActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "DrawGeoJsonLineActivity";

    public ArrayList<Position> list_of_position;
    public MapView mapView;
    public MapboxMap mapboxMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        list_of_position = new ArrayList<>();
        list_of_position = (ArrayList<Position>) getIntent().getSerializableExtra("PositionListExtra");
        //list_of_position = new TripAdapter().getList();
        //list_of_position = (ArrayList<Position>) getIntent().getBundleExtra("map_list").getSerializable("ARRAYLIST");
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        // Load and Draw the GeoJSON
        new DrawGeoJson(list_of_position).execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class DrawGeoJson extends AsyncTask<Void, Void, List<LatLng>> {
        private ArrayList<Position> datas;

        DrawGeoJson(ArrayList<Position> data) {
            this.datas = data;
        }

        private static final String TAG = "DrawGeoJson";

        @Override
        protected List<LatLng> doInBackground(Void... voids) {

            ArrayList<LatLng> points = new ArrayList<>();

            try {
                for (Position position : datas) {
                    LatLng latLng = new LatLng(position.getLatitude(), position.getLongitude());
                    Log.d("Points ", "Points : " + latLng.toString());
                    points.add(latLng);
                }
            } catch (Exception exception) {
                Log.e(TAG, "Loading GeoJSON: " + exception.toString());
            }
            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> points) {
            super.onPostExecute(points);
            int size = points.size();
            if (points.size() > 0) {
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(points.get(size / 2).getLatitude(), points.get(size / 2).getLongitude())) // Sets the new camera position
                        .zoom(12) // Sets the zoom
                        .build(); // Creates a CameraPosition from the builder
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100);

                mapboxMap.addPolyline(new PolylineOptions()
                        .addAll(points)
                        .color(Color.parseColor("#008BAD"))
                        .width(3));
            }
        }

    }
}
