package co.edu.unipiloto.cargaexpress;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.cargaexpress.databinding.ActivityCargaUbicationBinding;

public class CargaUbication extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityCargaUbicationBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private Carga carga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCargaUbicationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        carga = getIntent().getParcelableExtra("carga");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.setMyLocationEnabled(true);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            String estado = carga.getEstado();
                            String rol = carga_express.user.getRol();
                            if (!estado.equals("En espera del comerciante") || !estado.equals("Finalizado")){
                                mostrarRuta();
                            }
                            if (!rol.equals("Conductor")) {
                                marcadorUbicacionCarga();
                            }

                        } else {
                            // Si no se puede obtener la ubicación actual, muestra un mensaje de advertencia
                            Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Si no se tienen los permisos, solicítalos al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void marcadorUbicacionCarga () {
        LatLng ubication = new LatLng(carga.getLatitud(), carga.getLongitud());
        MarkerOptions marker = new MarkerOptions()
                .position(ubication)
                .title("Ubicacion carga")
                .snippet(carga.getCodigo());
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubication, 15));
    }

    private void mostrarRuta () {
        String origen = carga.getDireccionOrigen()+","+carga.getCiudadOrigen();
        String destino = carga.getDireccionDestino()+","+carga.getCiudadDestino();

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyClz_VQvzrSHIpLESzMFzyvaeE_JjmC6LI")
                .build();

        new Thread(() -> {
            try {
                DirectionsResult result = DirectionsApi.newRequest(context)
                        .origin(origen)
                        .destination(destino)
                        .await();

                runOnUiThread(() -> {
                    if (result.routes != null && result.routes.length > 0) {
                        DirectionsRoute route = result.routes[0];
                        EncodedPolyline polyline = route.overviewPolyline;
                        if (polyline != null) {
                            List<LatLng> points = new ArrayList<>();
                            for (com.google.maps.model.LatLng latLng : polyline.decodePath()) {
                                points.add(new LatLng(latLng.lat, latLng.lng));
                            }
                            mMap.addPolyline(new PolylineOptions().addAll(points).color(Color.BLUE));
                            mMap.addMarker(new MarkerOptions().position(points.get(0)).title("Origen").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            mMap.addMarker(new MarkerOptions().position(points.get(points.size() - 1)).title("Destino").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 10));
                        }
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(CargaUbication.this, "Error mostrarRuta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}