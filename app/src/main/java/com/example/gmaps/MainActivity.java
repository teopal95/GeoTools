package com.example.gmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {




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
    List<LatLng> bigpolygonList = new ArrayList<>();
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
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList);
                    polygon = gMap.addPolygon(polygonOptions);

                    if (checkBox.isChecked())
                    {
                        bigpolygonList.clear();
                    bigpolygonList.addAll(latLngList);
                    }

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
                    bigpolygonList.clear();

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

            String docName = editText.getText().toString();



            db.collection("Coordinates").document("" + docName).set(note);


            Toast.makeText(this, "Land Saved", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Toast.makeText(this, "Please, draw a Polygon", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);





        //  MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle);
        //  googleMap.setMapStyle(style);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude);


               if (bigpolygonList.isEmpty()){

                   Marker marker = gMap.addMarker(markerOptions);
                   latLngList.add(latLng);
                   markerList.add(marker);
               }else {
                  boolean contain = PolyUtil.containsLocation(latLng.latitude,latLng.longitude,bigpolygonList,true);
                   if (contain==true){
                      Marker marker = gMap.addMarker(markerOptions);
                      latLngList.add(latLng);
                       markerList.add(marker);
                   }
                   else {

                       Toast.makeText(MainActivity.this, "Please draw inside the polygon", Toast.LENGTH_SHORT).show();
                   }
               }

           /*     Marker marker = gMap.addMarker(markerOptions);
                contain = PolyUtil.containsLocation(latLng.latitude,latLng.longitude,bigpolygonList,true);
                Toast.makeText(MainActivity.this, "Inside polygon?"+contain, Toast.LENGTH_SHORT).show();
                latLngList.add(latLng);
                markerList.add(marker); */


            }
        });


    }


}