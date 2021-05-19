package com.example.gamepartners.ui.Activities_Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gamepartners.R;
import com.example.gamepartners.controller.GamePartnerUtills;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends DialogFragment {
     GoogleMap mMap;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    Button selectLocation;
    Address selectedAddress;
    ImageButton searchButton, exit;
    OnLocationPickedListener listener;
    Geocoder geocoder;
    List<Address> addressList = null;
    EditText locationSearch;
    public interface OnLocationPickedListener {
        void onLocationPicked(Address selectedAddress);
    }
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            geocoder = new Geocoder(getContext());
            LatLng latlng = new LatLng(GamePartnerUtills.latitude, GamePartnerUtills.longitude);
            getAddress(latlng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlng);
            markerOptions.title("Selected Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    try {
                        setLocationToClicked(latLng);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mLastLocation = GamePartnerUtills.location;
    }

    public MapsFragment(OnLocationPickedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        locationSearch = getView().findViewById(R.id.searchLocation);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            return;
        }

        selectLocation = getView().findViewById(R.id.selectLocation);
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocation();
            }
        });
        searchButton = getView().findViewById(R.id.searchButton);
        exit = getView().findViewById(R.id.exit);
        exit.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        }));
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation();
            }
        });
    }
    private void exit()
    {
        this.dismiss();
    }
    private void setLocationToClicked(final LatLng latLng) {
        if(mCurrLocationMarker!= null) {
            mCurrLocationMarker.remove();
        }
        getAddress(latLng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Selected Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
    }
    public void selectLocation() {
        if (selectedAddress != null) {
            listener.onLocationPicked(selectedAddress);
            this.dismiss();
        }
    }
    public void searchLocation() {
        String location = locationSearch.getText().toString();
        if (location != null || !location.equals("")) {
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                if(addressList!=null) {
                    selectedAddress = addressList.get(0);
                    LatLng latLng = new LatLng(selectedAddress.getLatitude(), selectedAddress.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Selected Location");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mCurrLocationMarker = mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    Toast.makeText(getContext(), selectedAddress.getCountryName() + " " + selectedAddress.getLongitude(), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void getAddress(final LatLng latlng)
    {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (latlng != null) {
                    try {
                        addressList = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null) {
                        selectedAddress = addressList.get(0);
                    }
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        locationSearch.setText(selectedAddress.getAddressLine(0));
                    }
                });
            }
        }.run();
    }
}