package com.example.ps5;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocationActivity extends AppCompatActivity {
    private final static int REQUEST_LOCATION_PERMISSION = 5;
    private final static String TAG = "Lokalizacja";

    private TextView locationText;
    private TextView addressText;
    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity_layout);
        Button showLocationButton = findViewById(R.id.show_location_button);
        this.locationText = findViewById(R.id.location_text_view);
        this.addressText = findViewById(R.id.address_text_view);

        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        showLocationButton.setOnClickListener((view)-> getLocation());
        Button showAddressButton = findViewById(R.id.show_address_button);
        showAddressButton.setOnClickListener(v->executeGeocoding());
    }

    private void getLocation(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }else{
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location->{
                if(location != null){
                    lastLocation = location;
                    locationText.setText(getString(R.string.location_text, location.getLatitude(),
                            location.getLongitude(),
                            location.getTime()));
                }else{
                    locationText.setText(R.string.get_localization_text);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String locationGeocoding(Context context, Location location){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        String resultMessage = "";

        try{
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude() ,1);
        } catch (IOException e) {
            resultMessage = context.getString(R.string.service_not_avaible);
            Log.e(TAG, resultMessage, e);
        }

        if(addresses == null || addresses.isEmpty()){
            if(resultMessage.isEmpty()){
                resultMessage = context.getString(R.string.address_no_found);
                Log.e(TAG, resultMessage);
            }
        }else{
            Address address = addresses.get(0);
            List<String> addressParts = new ArrayList<>();

            for(int i=0; i<=address.getMaxAddressLineIndex(); i++){
                addressParts.add(address.getAddressLine(i));
            }
            resultMessage = TextUtils.join("\n", addressParts);
        }

        return resultMessage;
    }

    private void executeGeocoding(){
        if(lastLocation != null){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> returnedAddress = executor.submit(()-> locationGeocoding(getApplicationContext(), lastLocation));
            try{
                String result = returnedAddress.get();
                addressText.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
            }catch (ExecutionException | InterruptedException e){
                Log.e(TAG, e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
        }
    }
}