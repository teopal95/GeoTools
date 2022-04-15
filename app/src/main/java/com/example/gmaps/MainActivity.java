package com.example.gmaps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import com.example.gmaps.ndviGet.NdviGet;
import com.example.gmaps.post.Geo_Json;
import com.example.gmaps.post.Geometry;
import com.example.gmaps.post.Post;
import com.example.gmaps.post.Properties;
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
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.data.kml.KmlLayer;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonZShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineMShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolylineZShape;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
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
    public static final String EXTRA_TEXT = "com.example.gmaps.EXTRA_TEXT";

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    public static String content;


    CheckBox checkBox;
    CheckBox checkBoxHoles;


    public String myText;
    public String json;
    public String textImage;
    public List<JSONObject> jsonObjectList = new ArrayList<>();
    TextView countText;
    TextView name;


    String polygonString;
    Polygon polygon = null;
    Polygon holePoly = null;
    public List<LatLng> latLngList = new ArrayList<>();
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
                    Toast.makeText(MainActivity.this, "Compute Area:" + SphericalUtil.computeArea(latLngList), Toast.LENGTH_SHORT).show();
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
                    holes = 0;
                } else {
                    polygonOptions = new PolygonOptions().addAll(latLngList);
                }
                polygon = gMap.addPolygon(polygonOptions);
                polygon.setClickable(true);

                gMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
                    @Override
                    public void onPolygonClick(Polygon polygon) {
                        String epifania = Integer.toString((int) SphericalUtil.computeArea(latLngList));
                        countText = findViewById(R.id.countText);
                        countText.setText(epifania);
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Color");
// add a list
                            String[] colors = {"Red", "Green", "White", "Blue", "Yellow", "Holes"};
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
                        } catch (Exception e) {

                        }

                        Toast.makeText(MainActivity.this, "Compute Area:" + SphericalUtil.computeArea(latLngList), Toast.LENGTH_SHORT).show();


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
                try {
                    createJson();
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.parse:

                openNdvi();


                break;

            case R.id.ndvi:
                createPost();
                // getPosts();
               // getImage();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    public void createPost() {
        HashMap<String,String> headerMap = new HashMap<>();
        headerMap.put("Content-Type","application/json");


        Properties properties = new Properties();
        Geometry geometry = new Geometry("Polygon",latLngList);
        Geo_Json geo_json = new Geo_Json("Feature", properties ,geometry);
        Post post = new Post("sample",geo_json);


        Call<Post> call = jsonPlaceHolderApi.createPost(headerMap,post,"acf65f9a08bada4884544c8116252667");

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Toast.makeText(MainActivity.this, "Εζ ποστ ιζ εζ ποστ " + response.code(), Toast.LENGTH_SHORT).show();

                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "ok" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                String name = response.body().getName();
                Geo_Json geoJson = response.body().getGeo_json();
                Geometry geometry1 = response.body().getGeo_json().getGeometry();



            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

                Toast.makeText(MainActivity.this, "Error " + t, Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.agromonitoring.com/agro/1.0/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


        checkBox = findViewById(R.id.checkbox);
        checkBoxHoles = findViewById(R.id.checkBoxHoles);


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

    private void getPosts() {
        Call<List<Post>> call = jsonPlaceHolderApi.getPosts("application/json; charset=utf-8", "242be092da689c49ffbc5765a271b282");

        call.enqueue(new Callback<List<Post>>() {

            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "ok" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Post> posts = response.body();

                for (Post post : posts) {


                    content = post.getGeo_json().getGeometry().getCoordinates().toString();


                    Toast.makeText(MainActivity.this, "Polygon " + "\n" + content, Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error " + t, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void openNdvi() {

        String text = textImage;

        Intent intent = new Intent(this, NDVI.class);
        intent.putExtra(EXTRA_TEXT, text);
        startActivity(intent);


    }


    public void getImage() {

        Call<List<NdviGet>> call = jsonPlaceHolderApi.getImages(1647769302, 1647855702, "62374ee3f03cc7dc4271b7d7", "242be092da689c49ffbc5765a271b282");
        call.enqueue(new Callback<List<NdviGet>>() {
            @Override
            public void onResponse(Call<List<NdviGet>> call, Response<List<NdviGet>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "ok" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                List<NdviGet> posts = response.body();
                String content = "";


                for (NdviGet post : posts) {




                    content += "ndvi: " + post.getImage().getDswi() + "\n";


                    Toast.makeText(MainActivity.this, "" + "\n" + content, Toast.LENGTH_SHORT).show();

                    textImage = content;



                }

            }

            @Override
            public void onFailure(Call<List<NdviGet>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error image", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    public void createKMLFile() {

        String kmlName = myText;

        String kmlInput = polygonString.replace("[", "").replace("lat/lng:", "").replace("(", "")
                .replace(")", "").replace("]", "").replace(", ", " ").replaceFirst("^ *", "").replaceAll(" +", " ");


        kmlInput = kmlInput.replaceAll("(-?\\d+[.]\\d+),(-?\\d+[.]\\d+)", "$2,$1");


        String kmlstart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                "<Document>";

        String kmlelement = "<Placemark>" + "<ExtendedData>" + "</ExtendedData>" + "<Polygon>" + "<outerBoundaryIs>" + "<LinearRing>" + "<coordinates>" + kmlInput + "</coordinates>" +
                "</LinearRing>" + "</outerBoundaryIs>" + "</Polygon>" + "</Placemark>";


        String kmlend = "</Document>" + "</kml>";

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


    public void createJson() throws JSONException {

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
        json = jsontest;


        File testexists = new File("/sdcard/download/KML" + "/" + "test55" + ".geojson");
        Writer fwriter;

        if (!testexists.exists()) {
            try {
                fwriter = new FileWriter("/sdcard/download/KML" + "/" + "test55" + ".geojson");
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
                fwriter = new FileWriter("/sdcard/download/KML" + "/" + "test55" + ".geojson");
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

                } else if (bigpolygonList.isEmpty()) {

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