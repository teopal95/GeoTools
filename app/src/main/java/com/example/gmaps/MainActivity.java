package com.example.gmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference coordinatesRef = db.collection("Coordinates");

    SupportMapFragment supportMapFragment;


    //  FirebaseDatabase rootNote;
    //  DatabaseReference reference;


    private Button btnOpen;
    private XmlSerializer xmlSerializer;


    private static final String FILE_NAME = "example.kml";

    // Initialize Variables

    public static GoogleMap gMap;
    public static int PICK_FILE = 1;


    CheckBox checkBox;
    EditText editText;

    String polygonString;
    Button btDraw, btClear, btnImport;
    Polygon polygon = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> polygonList = new ArrayList<>();
    List<LatLng> tagList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    List<LatLng> bigpolygonList = new ArrayList<>();




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_FILE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String fileContent = readTextFile(uri);
                //   Toast.makeText(this, fileContent, Toast.LENGTH_LONG).show();
                InputStream is = new ByteArrayInputStream(fileContent.getBytes());


                try {
                    KmlLayer kmlLayer = new KmlLayer(gMap, is, getApplicationContext());
                    kmlLayer.addLayerToMap();

                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();

                } catch (XmlPullParserException e) {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();

                }


            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);


        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnImport = findViewById(R.id.btnImport);


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

        btnImport.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE);


        });


        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList);
                    polygon = gMap.addPolygon(polygonOptions);

                    if (checkBox.isChecked()) {
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
                        System.out.println("");
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

    private String readTextFile(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gMap.setMyLocationEnabled(true);




        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude);


                if (bigpolygonList.isEmpty()) {

                    Marker marker = gMap.addMarker(markerOptions);
                    latLngList.add(latLng);
                    markerList.add(marker);
                } else {
                    boolean contain = PolyUtil.containsLocation(latLng.latitude, latLng.longitude, bigpolygonList, true);
                    if (contain == true) {
                        Marker marker = gMap.addMarker(markerOptions);
                        latLngList.add(latLng);
                        markerList.add(marker);
                    } else {

                        Toast.makeText(MainActivity.this, "Please draw inside the polygon", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });


    }


}