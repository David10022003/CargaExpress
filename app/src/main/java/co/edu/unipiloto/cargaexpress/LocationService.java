package co.edu.unipiloto.cargaexpress;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Build;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;

public class LocationService extends Service {


        private static final int NOTIF_ID = 1;
        private static final String NOTIF_CHANNEL_ID = "LocationServiceChannel";
        private Carga carga;

        private FirebaseFirestore database;

        @Override
        public void onCreate() {
            super.onCreate();
            createNotificationChannel();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            carga = intent.getParcelableExtra("carga");
            database = FirebaseFirestore.getInstance();
            Intent notificationIntent = new Intent(this, carga_express.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

            Notification notification = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                    .setContentTitle("Location Service")
                    .setContentText("Running")
                    .setSmallIcon(R.drawable.icon_carga_express_background)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(NOTIF_ID, notification);

            requestLocationUpdates();

            return START_NOT_STICKY;
        }

        private void requestLocationUpdates() {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(300000); // Intervalo de 5 minutos
            locationRequest.setFastestInterval(300000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
            int permission = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        }

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Map<String, Object> usuarioData = new HashMap<>();
                    usuarioData.put("latitud", latitude);
                    usuarioData.put("longitud", longitude);
                    database.collection("cargas").document(carga.getCodigo()).update(usuarioData);
                    HomeFragment.setCargas(carga);
                }
            }
        };

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel serviceChannel = new NotificationChannel(
                        NOTIF_CHANNEL_ID, "Location Service Channel",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(serviceChannel);
            }
        }

}
