package com.example.gmaps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonArray;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.data.kml.KmlLayer;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonZShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineZShape;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {



    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference coordinatesRef = db.collection("Coordinates");

    SupportMapFragment supportMapFragment;


    public static GoogleMap gMap;
    private static final int REQUEST_KML = 1;
    private static final int REQUEST_SHP = 1;
    public int holes = 0;

    private static final String AGRO_API_LINK = "http://api.agromonitoring.com/agro/1.0";
    private static final String API_KEY = "242be092da689c49ffbc5765a271b282";

    private RequestQueue mQueue;



    CheckBox checkBox;
    CheckBox checkBoxHoles;


    public String myText;
    TextView countText;
    TextView name;


    String polygonString;
    Polygon polygon = null;
    Polygon holePoly = null;
    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> polygonList = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    List<Marker> holeMarkerList = new ArrayList<>();
    List<LatLng> bigpolygonList = new ArrayList<>();
    List<LatLng> holesList = new ArrayList<>();
    public List<LatLng> holesListPoly = new ArrayList<>();

    public static ArrayList<LatLng> ShapeList = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_SHP) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                try {
                    InputStream in = getContentResolver().openInputStream(uri);
                    MyShapeFileReader mshp = new MyShapeFileReader();
                    mshp.exec(in);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == REQUEST_KML) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();

                String fileContent = readTextFile(uri);
                InputStream is = new ByteArrayInputStream(fileContent.getBytes());

                try {
                    KmlLayer kmlLayer = new KmlLayer(gMap, is, getApplicationContext());
                    kmlLayer.addLayerToMap();

                } catch (IOException | XmlPullParserException e) {
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();

                }

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ex_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.draw: {
                try {
                    PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList);
                    polygon = gMap.addPolygon(polygonOptions);
                    Toast.makeText(MainActivity.this, "Compute Area:" + SphericalUtil.computeArea(latLngList) , Toast.LENGTH_SHORT).show();
                    String sdouble = Double.toString(SphericalUtil.computeArea(latLngList));
                    countText = findViewById(R.id.countText);
                    countText.setText(sdouble);







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

            break;

            case R.id.kml:

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_KML);

                break;

            case R.id.open:

                openActivityLoad();

                break;

            case R.id.shape:
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                startActivityForResult(chooseFile, REQUEST_SHP);

                break;


            case R.id.clear:

                try {

                    latLngList.clear();
                    markerList.clear();
                    polygonList.clear();
                    gMap.clear();
                    polygon.remove();
                    bigpolygonList.clear();
                    holesListPoly.clear();
                    holesList.clear();
                    holeMarkerList.clear();


                } catch (Exception e) {
                    System.out.println("");
                }

                break;

            case R.id.poly:
                PolygonOptions polygonOptions;
                if (holes == 1) {

                    polygonOptions = new PolygonOptions().addAll(latLngList).addHole(holesListPoly);
                    holesListPoly.clear();
                    holes =0;
                }
                else {
                    polygonOptions = new PolygonOptions().addAll(latLngList);
                }
                polygon = gMap.addPolygon(polygonOptions);
                polygon.setClickable(true);

                gMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                   @Override
                   public void onPolygonClick(Polygon polygon) {
                       String epifania = Integer.toString((int)SphericalUtil.computeArea(latLngList));
                       countText = findViewById(R.id.countText);
                       countText.setText(epifania);
                       try {
                           AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                           builder.setTitle("Color");
// add a list
                           String[] colors = {"Red", "Green", "White", "Blue", "Yellow" ,"Holes"};
                           builder.setItems(colors, new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   switch (which) {
                                       case 0:
                                           polygon.setFillColor(Color.RED);
                                           break;
                                       case 1:
                                           polygon.setFillColor(Color.GREEN);
                                           break;
                                       case 2:
                                           polygon.setFillColor(Color.WHITE);
                                           break;
                                       case 3:
                                           polygon.setFillColor(Color.BLUE);
                                           break;
                                       case 4:
                                           polygon.setFillColor(Color.YELLOW);
                                           break;
                                       case 5:
                                           polygon.setClickable(false);
                                           break;



                                   }
                               }
                           });

// create and show the alert dialog
                           AlertDialog dialog = builder.create();
                           dialog.show();
                       }catch (Exception e){

                       }

                       Toast.makeText(MainActivity.this, "Compute Area:" + SphericalUtil.computeArea(latLngList) , Toast.LENGTH_SHORT).show();


                   }
               });
                break;

            case R.id.SaveAs:
                AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
                mydialog.setTitle("Give name to this Polygon:");

                final EditText weightInput = new EditText(MainActivity.this);
                weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
                mydialog.setView(weightInput);

                mydialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myText = weightInput.getText().toString();
                        try {

                            String tagInput = polygonString.replace("[", "").replace("lat/lng:", "").replace("(", "")
                                    .replace(")", "").replace("]", "");

                            String[] tagArray = tagInput.split(",[ ]");
                            List<String> tags = Arrays.asList(tagArray);

                            Note note = new Note(tags);

                            String docName = weightInput.getText().toString();


                            db.collection("Coordinates").document("" + docName).set(note);


                            Toast.makeText(MainActivity.this, "saved", Toast.LENGTH_SHORT).show();

                            createKMLFile();


                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Please, draw a Polygon", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                mydialog.show();


                break;

            case R.id.holes:

                holes = 1;

                PolygonOptions polygonHoles = new PolygonOptions().addAll(holesList);
                holePoly = gMap.addPolygon(polygonHoles);

                holesListPoly = holesList;
               holeMarkerList.clear();
              //  holesList.clear();


                break;

            case R.id.json:

                createJson();

                break;

            case R.id.parse:
                jsonParse jp = new jsonParse();
                JsonArray ja = new JsonArray();

                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);


        checkBox = findViewById(R.id.checkbox);
        checkBoxHoles = findViewById(R.id.checkBoxHoles);
        mQueue = Volley.newRequestQueue(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Assign Variables


        //Initialize SupportMapFragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this::onMapReady);


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
    
    private void postRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.agromonitoring.com/agro/1.0";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(MainActivity.this, "error!!", Toast.LENGTH_SHORT).show();

            }
        });


        requestQueue.add(stringRequest);

    }

    public class jsonParse {

        public JSONArray parseResponse(String data){
            JSONArray jsondata = new JSONArray();
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length() - 1; i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", jsonArray.getJSONObject(i).getString("name"));
                    jsonObject.put("id", jsonArray.getJSONObject(i).getString("id"));
                    jsonObject.put("center", jsonArray.getJSONObject(i).getJSONArray("center"));
                    jsondata.put(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(MainActivity.this, "To ID "+jsondata, Toast.LENGTH_SHORT).show();
            return jsondata;
        }
        public String getId(String name, JSONArray jsonArray){
            String id = null;
            for(int i=0;i<jsonArray.length()-1;i++){
                try {
                    if(name.equals(jsonArray.getJSONObject(i).getString("name"))){
                        id = jsonArray.getJSONObject(i).getString("id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return id;
        }
        public String getImage(String data) {
            String ndvi = null;
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(data);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                JSONObject image = jsonObject.getJSONObject("image");
                ndvi = image.getString("ndvi");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return ndvi;
        }

    }




        public void createKMLFile() {

        String kmlName = myText;

        String kmlInput = polygonString.replace("[", "").replace("lat/lng:", "").replace("(", "")
                .replace(")", "").replace("]", "").replace(", "," ").replaceFirst("^ *", "").replaceAll(" +", " ");


       kmlInput = kmlInput.replaceAll("(-?\\d+[.]\\d+),(-?\\d+[.]\\d+)", "$2,$1");




        String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "<Document>";

        String kmlelement = "<Placemark>"+"<ExtendedData>"+"</ExtendedData>"+"<Polygon>"+"<outerBoundaryIs>"+"<LinearRing>"+"<coordinates>"+kmlInput+"</coordinates>" +
    "</LinearRing>"+"</outerBoundaryIs>"+"</Polygon>"+"</Placemark>";


        String kmlend = "</Document>" +"</kml>";

        ArrayList<String> content = new ArrayList<String>();
        content.add(0, kmlstart);
        content.add(1, kmlelement);
        content.add(2, kmlend);

        String kmltest = content.get(0) + content.get(1) + content.get(2);


        File testexists = new File("/sdcard/download/KML" + "/" + kmlName + ".kml");
        Writer fwriter;

        if (!testexists.exists()) {
            try {
                fwriter = new FileWriter("/sdcard/download/KML" + "/" + kmlName + ".kml");
                fwriter.write(kmltest);
                fwriter.flush();
                fwriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {

            //schleifenvariable
            String filecontent = "";

            ArrayList<String> newoutput = new ArrayList<String>();
            ;

            try {
                BufferedReader in = new BufferedReader(new FileReader(testexists));
                while ((filecontent = in.readLine()) != null)

                    newoutput.add(filecontent);

            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            newoutput.add(2, kmlelement);

            String rewrite = "";
            for (String s : newoutput) {
                rewrite += s;
            }

            try {
                fwriter = new FileWriter("/sdcard/download/KML" + "/" + kmlName + ".kml");
                fwriter.write(rewrite);
                fwriter.flush();
                fwriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void createJson() {


        String jsonstart = "{\n" +
                "   \"name\":\"Polygon Sample\",\n" +
                "   \"geo_json\":{\n" +
                "      \"type\":\"Feature\",\n" +
                "      \"properties\":{\n" +
                "\n" +
                "      },\n" +
                "      \"geometry\":{\n" +
                "         \"type\":\"Polygon\",";

        String jsonelement = "\"coordinates\":[\n" +
                "            [\n" +
                "               [-121.1958,37.6683],\n" +
                "               [-121.1779,37.6687],\n" +
                "               [-121.1773,37.6792],\n" +
                "               [-121.1958,37.6792],\n" +
                "               [-121.1958,37.6683]\n" +
                "            ]\n" +
                "         ]\n" +
                "      }\n" +
                "   }\n" +
                "}";



        ArrayList<String> content = new ArrayList<String>();
        content.add(0, jsonstart);
        content.add(1, jsonelement);

        String jsontest = content.get(0) + content.get(1);
        File testexists = new File("/sdcard/download/KML" + "/" + "test2" + ".geojson");
        Writer fwriter;

        if (!testexists.exists()) {
            try {
                fwriter = new FileWriter("/sdcard/download/KML" + "/" + "test2" + ".geojson");
                fwriter.write(jsontest);
                fwriter.flush();
                fwriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {

            //schleifenvariable
            String filecontent = "";

            ArrayList<String> newoutput = new ArrayList<String>();
            ;

            try {
                BufferedReader in = new BufferedReader(new FileReader(testexists));
                while ((filecontent = in.readLine()) != null)

                    newoutput.add(filecontent);

            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            newoutput.add(2, jsonelement);

            String rewrite = "";
            for (String s : newoutput) {
                rewrite += s;
            }

            try {
                fwriter = new FileWriter("/sdcard/download/KML" + "/" + "test2" + ".geojson");
                fwriter.write(rewrite);
                fwriter.flush();
                fwriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }





    public static void readFromShape(ArrayList<LatLng> result, PointData[] pointsOfPart) {

        ShapeList.clear();

        for (PointData point : pointsOfPart) {
            result.add(new LatLng(point.getY(), point.getX()));
        }
        ShapeList = result;

        PolygonOptions polygonOptions = new PolygonOptions().addAll(ShapeList);
        gMap.addPolygon(polygonOptions);



    }


    public class MyShapeFileReader {
        public ArrayList<LatLng> exec(InputStream is) throws Exception {
            ArrayList<LatLng> result = new ArrayList<>();
            ValidationPreferences prefs = new ValidationPreferences();
            prefs.setMaxNumberOfPointsPerShape(100000);
            ShapeFileReader r = new ShapeFileReader(is, prefs);

            AbstractShape s;
            while ((s = r.next()) != null) {
                switch (s.getShapeType()) {
                    case POLYLINE_M:
                        for (int i = 0; i < ((PolylineMShape) s).getNumberOfParts(); i++) {
                            readFromShape(result, ((PolylineMShape) s).getPointsOfPart(i));
                        }
                        break;
                    case POLYLINE_Z:
                        for (int i = 0; i < ((PolylineZShape) s).getNumberOfParts(); i++) {
                            readFromShape(result, ((PolylineZShape) s).getPointsOfPart(i));
                        }
                        break;
                    case POLYLINE:
                        for (int i = 0; i < ((PolylineShape) s).getNumberOfParts(); i++) {
                            readFromShape(result, ((PolylineShape) s).getPointsOfPart(i));
                        }
                        break;
                    case POLYGON:
                        for (int i = 0; i < ((PolygonShape) s).getNumberOfParts(); i++) {
                            readFromShape(result, ((PolygonShape) s).getPointsOfPart(i));
                        }
                        break;
                    case POLYGON_M:
                        for (int i = 0; i < ((PolygonMShape) s).getNumberOfParts(); i++) {
                            readFromShape(result, ((PolygonMShape) s).getPointsOfPart(i));
                        }
                        break;
                    case POLYGON_Z:
                        for (int i = 0; i < ((PolygonZShape) s).getNumberOfParts(); i++) {
                            readFromShape(result, ((PolygonZShape) s).getPointsOfPart(i));
                        }
                        break;
                }
            }
            return result;
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

        gMap.getUiSettings().setZoomControlsEnabled(true);



        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {


                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude);
                MarkerOptions holeOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                if (checkBoxHoles.isChecked()) {
                    Marker holeMarker = gMap.addMarker(holeOptions);
                    holesList.add(latLng);
                            holeMarkerList.add(holeMarker);

                }
                else


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