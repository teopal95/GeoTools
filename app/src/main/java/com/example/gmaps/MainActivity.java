package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.maps.android.PolyUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList).geodesic(true);
                    polygon = gMap.addPolygon(polygonOptions);

                    bigpolygonList.addAll(latLngList);

                    
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
                //Create Marker Options
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude);
                //Create Marker
                Marker marker = gMap.addMarker(markerOptions);
                //Add latLng and Marker
                boolean contain = PolyUtil.containsLocation(latLng.latitude,latLng.longitude,bigpolygonList,true);
                Toast.makeText(MainActivity.this, "Inside polygon?"+contain, Toast.LENGTH_SHORT).show();

                latLngList.add(latLng);
                markerList.add(marker);


            }
        });


    }


}