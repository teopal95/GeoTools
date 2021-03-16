package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {




    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference coordinatesRef = db.collection("Coordinates");
    private DocumentReference noteRef = db.collection("Coordinates").document("Marker");



    // Initialize Variables
    GoogleMap gMap;
    Double lat;
    Double lng;

    Button btDraw, btClear;

    Polygon polygon = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FIREBASE

        textViewData = findViewById(R.id.text_view_data);


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

                    latLngList.clear();
                    markerList.clear();

                } catch (Exception e) {
                    System.out.println("");
                }




            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clear all
                if (polygon != null) {

                    latLngList.clear();
                    markerList.clear();
                    gMap.clear();
                    polygon.remove();
                };




            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
        coordinatesRef.limit(3).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setMarkerId(documentSnapshot.getId());

                    String markerId = note.getMarkerId();

                    String latitude = note.getLatitude();
                    String longitude = note.getLongitude();

                    data += "ID: " + markerId + "\nLatitude: "+ latitude + "\n Longitude: " + longitude  + "\n\n";



                }

                textViewData.setText(data);



            }
        });

    }




    public void addNote(View v) {
        String latitude = lat.toString();
        String longitude = lng.toString();

        Note note = new Note(latitude, longitude);


        coordinatesRef.add(note);
    }




    public void loadNotes(View v) {

        coordinatesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String data ="";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                    Note note = documentSnapshot.toObject(Note.class);
                    note.setMarkerId(documentSnapshot.getId());

                    String markerId = note.getMarkerId();

                    String latitude = note.getLatitude();
                    String longitude = note.getLongitude();

                    data += "ID: " + markerId + "\nLatitude: "+ latitude + "\n Longitude: " + longitude  + "\n\n";
                }
                textViewData.setText(data);

            }
        });



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this,R.raw.mapstyle);

        googleMap.setMapStyle(style);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //Create Marker Options
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.latitude+ " : "+ latLng.longitude);
                //Create Marker
                Marker marker = gMap.addMarker(markerOptions);
                //Add latLng and Marker
              
                latLngList.add(latLng);
                markerList.add(marker);
                lat = latLng.latitude;
                lng = latLng.longitude;



            }
        });


    }


}