package com.example.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.map.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PointOfInterest;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private int night_mode = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.night_mode:
                switch (night_mode){
                    case 0:
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
                        night_mode = 1;
                        break;
                    case 1:
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_normal));
                        night_mode = 0;
                        break;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng market = new LatLng(10.7721095,106.6982784);
        Marker mMarket = mMap.addMarker(new MarkerOptions().position(market).title("Chợ Bến Thành"));
        mMarket.showInfoWindow();
        LatLng bitexco = new LatLng(10.7719839,106.7022025);
        Marker mBitexco = mMap.addMarker(new MarkerOptions().position(bitexco).title("Bitexco"));
        mBitexco.showInfoWindow();
        LatLng church = new LatLng(10.7797855,106.6990189);
        Marker mChurch = mMap.addMarker(new MarkerOptions().position(church).title("Nhà thờ Đức Bà"));
        mChurch.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(market, 15));
        /*
        LatLng market = new LatLng(10.7721095,106.6982784);
        Marker mMarket = mMap.addMarker(new MarkerOptions().position(market).title("Chợ Bến Thành"));
        mMarket.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Chợ Bến Thành")));
        LatLng bitexco = new LatLng(10.7719839,106.7022025);
        Marker mBitexco = mMap.addMarker(new MarkerOptions().position(bitexco).title("Bitexco"));
        mBitexco.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Bitexco")));
        LatLng church = new LatLng(10.7797855,106.6990189);
        Marker mChurch = mMap.addMarker(new MarkerOptions().position(church).title("Nhà thờ Đức Bà"));
        mChurch.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Nhà thờ Đức Bà")));
         */
        setMapLockClick(googleMap);
        setPoiClick(googleMap);
        enableMyLocation();
        markerListener(googleMap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    private void setMapLockClick(final GoogleMap map){
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull @org.jetbrains.annotations.NotNull LatLng latLng) {
                String snippet = String.format(Locale.getDefault(),
                        "Lat: %1$.5f, Long: %2$.5f",
                        latLng.latitude,
                        latLng.longitude);

                Marker m = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet));
                
                m.showInfoWindow();
            }
        });
    }

    private void setPoiClick(final GoogleMap map){
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(@NonNull @NotNull PointOfInterest pointOfInterest) {
                int click = 0;
                Marker poiMarker = mMap.addMarker(new MarkerOptions()
                        .position(pointOfInterest.latLng)
                        .title(pointOfInterest.name));
                poiMarker.showInfoWindow();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.google.com/search?q=" + poiMarker.getTitle()));
                startActivity(intent);
            }
        });
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void markerListener(final GoogleMap map){
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.google.com/search?q=" + marker.getTitle()));
                startActivity(intent);
                return false;
            }
        });
    }
}