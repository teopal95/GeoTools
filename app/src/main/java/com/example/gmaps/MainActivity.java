package com.example.gmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {


    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference coordinatesRef = db.collection("Coordinates");


    private Button btnOpen;

    // Initialize Variables

    public static GoogleMap gMap;

    CheckBox checkBox;
    EditText editText;

    String polygonString;
    Button btDraw, btClear;
    Polygon polygon = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> polygonList = new ArrayList<>();
    List<LatLng> tagList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    LatLng location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnOpen = (Button) findViewById(R.id.btnOpen);
        checkBox = findViewById(R.id.checkbox);
        editText = findViewById(R.id.editText);

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityLoad();
            }
        });


        //Assign Variables
        btDraw = findViewById(R.id.bt_draw);
        btClear = findViewById(R.id.bt_clear);

        //Initialize SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this::onMapReady);


        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).geodesic(true);
                    polygon = gMap.addPolygon(polygonOptions);

                    polygonList.addAll(latLngList);
                    polygonString = polygonList.toString();

                    latLngList.clear();
                    markerList.clear();


                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Select your Land", Toast.LENGTH_SHORT).show();

                }

                polygonList.clear();


            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear all
                try {

                    latLngList.clear();
                    markerList.clear();
                    polygonList.clear();
                    gMap.clear();
                    polygon.remove();

                } catch (Exception e) {


                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void openActivityLoad() {
        Intent intent = new Intent(this, loadActivity.class);
        startActivity(intent);

    }


    public void saveNote(View v) {

        try {


            String tagInput = polygonString.replace("[", "").replace("lat/lng:", "").replace("(", "")
                    .replace(")", "").replace("]", "");
            ;
            String[] tagArray = tagInput.split(",[ ]");
            List<String> tags = Arrays.asList(tagArray);

            Note note = new Note(tags);

            String name = editText.getText().toString();


            db.collection("Coordinates").document("" + name).set(note);


            Toast.makeText(this, "Land Saved", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(this, "Please, draw a Polygon", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        try{
            GeoJsonLayer Layer = new GeoJsonLayer(gMap, R.raw.map, this);
            GeoJsonPointStyle pointStyle = Layer.getDefaultPointStyle();
            Layer.addLayerToMap();
        }
        catch (Exception e){
            System.out.println("");

        }

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Create Marker Options
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude);
                //Create Marker
                Marker marker = gMap.addMarker(markerOptions);
                //Add latLng and Marker

                latLngList.add(latLng);
                markerList.add(marker);


            }
        });


    }


}