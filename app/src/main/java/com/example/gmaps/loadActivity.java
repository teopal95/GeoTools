package com.example.gmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Strings;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

public class loadActivity extends MainActivity implements OnMapReadyCallback  {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference coordinatesRef = db.collection("Coordinates");

    private NoteAdapter adapter;
    LatLng loc;
    Polygon poly = null;
    List<LatLng> loadList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        setUpRecyclerView();


    }

    private void setUpRecyclerView() {
        Query query = coordinatesRef;
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class).build();

        adapter = new NoteAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                adapter.deleteItem(viewHolder.getAdapterPosition());

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick (DocumentSnapshot documentSnapshot, int position) {
                Note note = documentSnapshot.toObject(Note.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();


                for (String tags : note.getTags()) {


                    String[] latlong = tags.split(",");
                    double latitude = Double.parseDouble(latlong[0]);
                    double longitude = Double.parseDouble(latlong[1]);
                    loc = new LatLng(latitude, longitude);


                    loadList.add(loc);


                  Toast.makeText(loadActivity.this, "ID: " + id + " List: " + loadList, Toast.LENGTH_SHORT).show();


                }

                PolygonOptions polygonOptions = new PolygonOptions().addAll(loadList);
                poly = gMap.addPolygon(polygonOptions);

                loadList.clear();


            }

        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }

    public void openActivityMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}

