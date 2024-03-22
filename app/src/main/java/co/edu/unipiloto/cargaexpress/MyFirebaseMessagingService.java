package co.edu.unipiloto.cargaexpress;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.UUID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Nuevo Token", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String title = message.getData().get("title");
        String content = message.getData().get("body").split("##")[0];
        String bigText = message.getData().get("body").split("##")[1];
        String idCarga = message.getData().get("idCarga");
        buscarCargaNotificacion(this, title, content, bigText, idCarga);
    }

    public void crearSimpleNotification(Context context, String titulo, String texto, String bigText, Carga carga) {
        String channelId = "idChannel";
        int id = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "MyChannel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel: "+channelId);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, carga_express.class);
        //Intent intent = new Intent(context, AplicarCarga.class);
        intent.putExtra("carga", carga);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.baseline_work_24)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }

    private void buscarCargaNotificacion (Context context, String titulo, String texto, String bigText, String idCarga) {

        Query query = FirebaseFirestore.getInstance().collection("cargas").whereEqualTo(FieldPath.documentId(), idCarga);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Carga carga = new Carga(document.getId(), document.getString("tipoCarga"), document.getLong("peso"), document.getString("dimensiones"), document.getString("direccionOrigen"), document.getString("ciudadOrigen"), document.getString("direccionDestino"), document.getString("ciudadDestino"), document.getString("fechaPublicacion"), document.getString("fechaRecogida"), document.getString("horaRecogida"), document.getString("fechaEntrega"), document.getString("especificaciones"), document.getLong("comerciante"), document.getLong("conductor"));
                        crearSimpleNotification(context, titulo, texto, bigText, carga);
                    }
                }
                else{
                    Log.e("buscarCargaNotificacion", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public static void guardarToken (String rol) {
        String topic = rol.split(" ")[0];
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("guardarToken", "Suscrito al tema: "+topic);
                        } else {
                            Log.e("guardarToken", "Error al suscribirse al tema: "+topic, task.getException());
                        }
                    }
                });
    }

    public static void eliminarToken (String rol) {
        String topic = rol.split(" ")[0];
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("eliminarToken", "Suscripción anulada del tema: "+topic);
                        } else {
                            Log.e("eliminarToken", "Error al anular la suscripción del tema: "+topic, task.getException());
                        }
                    }
                });
    }

    public static void enviarNotificacionPorTopico(String title, String content, String bigText, String clave, String valor, String topic) {
        RemoteMessage message = new RemoteMessage.Builder(topic + "@fcm.googleapis.com")
                .setMessageId(UUID.randomUUID().toString())
                .addData("title", title)
                .addData("body", content +"##"+ bigText)
                .addData(clave, valor)
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
            Log.d("enviarNotificacionTopic", "Notificación enviada correctamente al tema: " +topic);
            Log.d("prueba", clave+ ": " +valor);
        } catch (Exception e) {
            Log.e("enviarNotificacionTopic","Error al enviar la notificación al tema: " + topic, e);
        }
    }
}