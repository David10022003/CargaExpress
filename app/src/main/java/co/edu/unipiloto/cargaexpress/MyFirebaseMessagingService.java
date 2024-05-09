package co.edu.unipiloto.cargaexpress;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("Nuevo Token", token);
        //eliminarTokenAntiguo("Todos", this);
        if (carga_express.user != null){
            //eliminarTokenAntiguo(carga_express.user.getCedula(), this);
            //eliminarTokenAntiguo(carga_express.user.getRol(), this);
            eliminarTokenIndividual(this);
            guardarTokenIndividual(carga_express.user.getCedula(), this);
            guardarToken(carga_express.user.getRol());
            guardarToken("Todos");
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        if (message.getNotification()!=null){
            Log.d("RecibiendoMessage", "Title de la noti: " +message.getNotification().getTitle());
            Log.d("RecibiendoMessage", "Body de la noti: " +message.getNotification().getBody());
        }
        if (message.getData().size() > 0) {
            Log.d("RecibiendoMessage", "Title en data: " + message.getData().get("title"));
            Log.d("RecibiendoMessage", "Body en data: " + message.getData().get("body"));
            Log.d("RecibiendoMessage", "BigText en data: " + message.getData().get("bigText"));
            Log.d("RecibiendoMessage", "Type en data: " + message.getData().get("type"));
            Log.d("RecibiendoMessage", "idCarga en data: " + message.getData().get("idCarga"));
        }
        String title = message.getNotification().getTitle();
        String content = message.getNotification().getBody();
        String bigText = content;

        if (message.getData().size() > 0) {
            bigText = message.getData().get("bigText");
            String type = message.getData().get("type");
            if (type.equalsIgnoreCase("nuevaCarga") || type.equalsIgnoreCase("conductorAsignado") || type.equalsIgnoreCase("incidenciaNueva") || type.equalsIgnoreCase("recorridoAlterno") || type.equalsIgnoreCase("enViaje") || type.equalsIgnoreCase("enViajeLuegoDeIncididencia") || type.equalsIgnoreCase("enEsperaDelComerciante") || type.equalsIgnoreCase("finalizado") || type.equalsIgnoreCase("nuevaAplicacion")) {
                String idCarga = message.getData().get("idCarga");
                String titleData = message.getData().get("titleData");
                buscarCargaNotificacion(this, titleData , bigText, bigText, type, idCarga);
            }
            else if (type.equalsIgnoreCase("conductorAsignadoCamion")) {
                String placa = message.getData().get("placa");
                String titleData = message.getData().get("titleData");
                crearSimpleNotification(this, titleData, bigText, bigText, type, null);
            }
        } else
            crearSimpleNotification(this, title, content, bigText, "", null);

    }

    public void crearSimpleNotification(Context context, String titulo, String texto, String bigText, String type, Object object) {
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
        Intent intent = new Intent();
        if (type.equalsIgnoreCase("nuevaCarga") || type.equalsIgnoreCase("conductorAsignado") || type.equalsIgnoreCase("recorridoAlterno") || type.equalsIgnoreCase("enViaje") || type.equalsIgnoreCase("enViajeLuegoDeIncididencia") || type.equalsIgnoreCase("enEsperaDelComerciante") || type.equalsIgnoreCase("finalizado") || type.equalsIgnoreCase("nuevaAplicacion")) {
            Carga carga = (Carga) object;
            intent = new Intent(context, AplicarCarga.class);
            Usuario user = carga_express.user;
            intent.putExtra("user", user);
            intent.putExtra("carga", carga);
        }
        else if (type.equalsIgnoreCase("incidenciaNueva")) {
            Carga carga = (Carga) object;
            intent = new Intent(context, ver_incidente.class);
            intent.putExtra("carga", carga);
        }
        else if (type.equalsIgnoreCase("conductorAsignadoCamion")){
            Usuario user =carga_express.user;
            intent = new Intent(context, HomeFragment.class);
            intent.putExtra("user", user);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText))
                .setSmallIcon(R.mipmap.logosinfondo)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

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

    private void buscarCargaNotificacion (Context context, String titulo, String texto, String bigText, String type, String idCarga) {

        Query query = FirebaseFirestore.getInstance().collection("cargas").whereEqualTo(FieldPath.documentId(), idCarga);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        Carga carga = new Carga(document.getId(), document.getString("tipoCarga"), document.getLong("peso"), document.getString("dimensiones"), document.getString("direccionOrigen"), document.getString("ciudadOrigen"),
                                document.getString("direccionDestino"), document.getString("ciudadDestino"), document.getString("fechaPublicacion"), document.getString("fechaRecogida"), document.getString("horaRecogida"),
                                document.getString("fechaEntrega"), document.getString("especificaciones"), document.getLong("comerciante"), document.getLong("conductor"), document.getString("estado"),
                                document.getDouble("latitud"), document.getDouble("longitud"), document.getLong("incidencias"));
                        Log.d("buscarCargaNotificacion", "id: " +carga.getCodigo()+ " tipo: " +carga.getTipoCarga()+ " peso: " +carga.getPeso()+ " dimensiones: " +carga.getDimensiones()+ " direccionOrigen: " +carga.getDireccionOrigen()+ " ciudadOrigen: " +carga.getCiudadOrigen()+ " direccionDestino: " +carga.getDireccionDestino()+ " ciudadDestino: " +carga.getCiudadDestino()+ " fechaPublicada: " +carga.getFechaPublicada()+ " fechaRecogida: " +carga.getFechaRecogida()+ " horaRecogida: " +carga.getHoraRecogida()+ " fechaEntrega: " +carga.getFechaEntrega()+ " especif: " + carga.getEspecificaciones()+ " cedComerciante: " + carga.getCedulaComerciante()+ " cedConductor: " + carga.getCedulaConductor());
                        crearSimpleNotification(context, titulo, texto, bigText, type, carga);
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

    public static void guardarTokenIndividual (String idUser, Context context) {
        guardarToken(idUser);
        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult().getToken();
                        Log.d("guardarTokenIndSQLite", "Token FCM: " + token);
                        SQLiteDatabase db = (new SQLiteHelper(context)).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("token", token);
                        db.insert("dispositivo",null,values);
                    } else {
                        Log.w("guardarTokenIndSQLite", "Error al obtener el token FCM", task.getException());
                    }
                });*/
    }

    @SuppressLint("Range")
    private static String obtenerTokenSQLite(Context context){
        SQLiteDatabase db = (new SQLiteHelper(context)).getReadableDatabase();
        String token = null;
        Cursor cursor = db.query("dispositivo", new String[]{"token"}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            token = cursor.getString(cursor.getColumnIndex("token"));
            cursor.close();
        }
        return token;
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

    public static void eliminarTokenIndividual(Context context) {
        if (carga_express.user != null)
            eliminarToken(carga_express.user.getCedula());
        //SQLiteDatabase db = (new SQLiteHelper(context)).getWritableDatabase();
        //db.delete("dispositivo", null, null);
        //db.close();
    }

    private void eliminarTokenAntiguo (String rol, Context context) {
        String topic = rol.split(" ")[0];
        String oldToken = obtenerTokenSQLite(context);
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(topic)
                .addData("action", "unsubscribe")
                .addData("token", oldToken)
                .build());
        Log.d("eliminarTokenAntiguo", "Suscripción anulada del tema: " + topic);
    }
}