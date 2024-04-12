package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AplicarCarga extends AppCompatActivity {

    private Usuario user;
    private static Carga carga;

    ImageButton errorButton = null, locationButton;

    private FirebaseFirestore database;
    private List<Camion> camiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplicar_carga);
        user = getIntent().getParcelableExtra("user");
        carga = getIntent().getParcelableExtra("carga");
        if(carga.getEstado().equals("publicado")) {
            Toast.makeText(AplicarCarga.this, "User "+ user.getCedula()+user.getRol(), Toast.LENGTH_SHORT).show();
            switch (user.getRol()) {
                case "Propietario de camion":
                    ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.btn_postularme);
                    break;
                case "Comerciante":
                    ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.ver_postulaciones);
                    break;
                default:
                    ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setVisibility(View.INVISIBLE);
            }
        } else if( carga.getEstado().equals("Asignado")) {
            if(user.getRol().equals("Conductor"))
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.iniciar_viaje);
            else
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setVisibility(View.INVISIBLE);
        }
        else if (carga.getEstado().equals("En viaje") || carga.getEstado().equals("En recorrido alterno")) {
            if (!user.getRol().equals("Propietario de camion")) {
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.finalizar_viaje);
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setBackgroundColor(getResources().getColor(R.color.red));
                if(user.getRol().equals("Conductor")) {
                    crearBtnIncidencia();
                }
            }
            else
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setVisibility(View.INVISIBLE);
        } else if (carga.getEstado().equals("Incidencia")) {

            if(user.getRol().equals("Conductor")) {
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.continuar_viaje);
                Button btn_recorridoAlterno = (Button) findViewById(R.id.btn_realizarRecorridoAlterno);
                btn_recorridoAlterno.setBackgroundColor(getResources().getColor(R.color.orange));

                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) btn_recorridoAlterno.getLayoutParams();
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                btn_recorridoAlterno.setLayoutParams(params);

                btn_recorridoAlterno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notificarRecorridoAlterno();
                    }
                });


            }
            else
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.ver_incidencia);

        } else if (carga.getEstado().equals("Finalizado")) {
            ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setVisibility(View.INVISIBLE);
        } else if(carga.getEstado().equals("En espera del comerciante")) {
            if(user.getRol().equals("Comerciante")) {
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.finalizar_viaje);
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setBackgroundColor(getResources().getColor(R.color.red));
            } else {
                ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setVisibility(View.INVISIBLE);
            }
        }

        ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detalle_carga();
            }
        });

        database = FirebaseFirestore.getInstance();
        mostrarDatos();
        if(user.getRol().equals("Propietario de camion"))
            buscarCamion(user.getCedula());

        crearBtnUbicacion();
    }

    private void crearBtnIncidencia() {


        errorButton = new ImageButton(this);
        ConstraintLayout constraint = findViewById(R.id.layout);
        errorButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_error_24));
        errorButton.setBackground(getResources().getDrawable(R.drawable.error_button));
        errorButton.setPadding(20, 15, 20, 15);
        errorButton.setId(View.generateViewId());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        params.endToEnd = constraint.getId();
        params.topToTop = constraint.findViewById(R.id.estado).getId();
        params.bottomToBottom = constraint.findViewById(R.id.estado).getId();
        params.setMarginEnd(16);

        errorButton.setLayoutParams(params);

        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificarIncidencia();
            }
        });

        constraint.addView(errorButton);

    }

    private void crearBtnUbicacion() {

        locationButton = new ImageButton(this);
        ConstraintLayout constraint = findViewById(R.id.layout);
        locationButton.setImageDrawable(getResources().getDrawable(R.drawable.baseline_location_on_24));
        locationButton.setBackground(getResources().getDrawable(R.drawable.location_button));
        locationButton.setPadding(20, 15, 20, 15);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );

        params.topToTop = constraint.findViewById(R.id.estado).getId();
        params.bottomToBottom = constraint.findViewById(R.id.estado).getId();
        if (user.getRol().equals("Conductor")) {
            if (carga.getEstado().equals("En viaje") || carga.getEstado().equals("En recorrido alterno"))
                params.endToStart = errorButton.getId();
            else
                params.endToEnd = constraint.getId();
        }
        else
            params.endToEnd = constraint.getId();
        params.setMarginEnd(16);
        locationButton.setLayoutParams(params);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AplicarCarga.this, CargaUbication.class);
                intent.putExtra("carga", carga);
                startActivity(intent);

            }
        });

        constraint.addView(locationButton);

    }

    private void notificarIncidencia() {
        Intent intent = new Intent(this, Incidente.class);
        intent.putExtra("user", user);
        intent.putExtra("carga", carga);
        startActivity(intent);
        finish();
    }

    private void mostrarDatos() {

        ((TextView) findViewById(R.id.tipoCarga)).setText(carga.getTipoCarga());
        ((TextView) findViewById(R.id.textInputLayout4)).setText(String.valueOf(carga.getPeso()));
        String [] dimensiones = carga.getDimensiones().split("X");
        ((TextView) findViewById(R.id.textInputLayout8)).setText(dimensiones[0]);
        ((TextView) findViewById(R.id.textInputLayout6)).setText(dimensiones[1]);
        ((TextView) findViewById(R.id.textInputLayout7)).setText(dimensiones[2]);
        ((TextView) findViewById(R.id.textInputLayout9)).setText(carga.getDireccionOrigen());
        ((TextView) findViewById(R.id.textInputLayout10)).setText(carga.getCiudadOrigen());
        ((TextView) findViewById(R.id.textInputLayout11)).setText(carga.getDireccionDestino());
        ((TextView) findViewById(R.id.textInputLayout12)).setText(carga.getCiudadDestino());
        ((TextView) findViewById(R.id.textInputLayout13)).setText(carga.getFechaRecogida());
        ((TextView) findViewById(R.id.textInputLayout14)).setText(carga.getHoraRecogida());
        ((TextView) findViewById(R.id.textInputLayout15)).setText(carga.getFechaEntrega());
        ((TextView) findViewById(R.id.especificaciones)).setText(carga.getEspecificaciones());
        ((TextView) findViewById(R.id.estado)).setText(carga.getEstado());

    }

    private void detalle_carga() {
        switch (carga.getEstado()) {
            case "publicado":
                if (user.getRol().equals("Propietario de camion")) {
                    showInputDialogPropietario();
                } else if (user.getRol().equals("Comerciante")) {
                    Intent intent = new Intent(this, AsignarConductor.class);
                    intent.putExtra("carga", carga);
                    startActivity(intent);
                }
            break;
            case "Asignado":
                if(user.getRol().equals("Conductor"))
                    showInputDialogConductor();
                break;
            case "En viaje":
                if(!user.getRol().equals("Propietario de camion"))
                    finalizarViaje();
                break;
            case "En recorrido alterno":
                if(!user.getRol().equals("Propietario de camion"))
                    finalizarViaje();
                break;
            case "Incidencia":
                if(user.getRol().equals("Conductor")) {
                    Map<String, Object> usuarioData = new HashMap<>();
                    usuarioData.put("estado", "En viaje");
                    carga.setEstado("En viaje");
                    database.collection("cargas").document(carga.getCodigo()).update(usuarioData);
                    HomeFragment.setCargas(carga);
                    recreate();
                } else {
                    Intent intent = new Intent(this, ver_incidente.class);
                    intent.putExtra("carga", carga);
                    startActivity(intent);
                }
                break;
            case  "En espera del comerciante":
                if(user.getRol().equals("Comerciante"))
                    finalizarViaje();
                break;
        }

    }

    private void finalizarViaje() {
        Map<String, Object> usuarioData = new HashMap<>();
        if(user.getRol().equals("Conductor")) {
            usuarioData.put("estado", "En espera del comerciante");
            carga.setEstado("En espera del comerciante");
        }
        else if(user.getRol().equals("Comerciante")) {
            usuarioData.put("estado", "Finalizado");
            carga.setEstado("Finalizado");
        }
        database.collection("cargas").document(carga.getCodigo()).update(usuarioData);
        HomeFragment.setCargas(carga);
        recreate();
    }

    private void showInputDialogConductor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View alert = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(alert);
        AlertDialog dialog = builder.create();
        Button aceptar = alert.findViewById(R.id.button4);
        Button cancelar = alert.findViewById(R.id.button5);
        TextInputEditText text = alert.findViewById(R.id.aplicar_camion);
        TextView output = alert.findViewById(R.id.textView35);
        text.setVisibility(View.INVISIBLE);
        text.setHeight(0);
        output.setTextSize(18);
        output.setText(getResources().getString(R.string.iniciar_viaje_confirmacion));
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread ntpThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NTPUDPClient client = new NTPUDPClient();
                            client.setDefaultTimeout(3000); // Timeout en milisegundos

                            InetAddress inetAddress = InetAddress.getByName("pool.ntp.org");
                            TimeInfo timeInfo = client.getTime(inetAddress);
                            timeInfo.computeDetails();

                            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            Date date = new Date(returnTime);
                            compararHora(date);
                            client.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Manejar el error
                            Log.e("NTP Time", "Error al obtener la hora actual: " + e.getMessage());
                        }
                    }
                });

                ntpThread.start();
                try {
                    ntpThread.join();
                    ((TextView)findViewById(R.id.estado)).setText(carga.getEstado());
                    HomeFragment.setCargas(carga);
                    dialog.dismiss();
                    Log.d("NTP Time", "El hilo ha finalizado.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("NTP Time", "Error al esperar a que el hilo finalice: " + e.getMessage());
                }
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void compararHora(Date date) {
        boolean aTiempo = true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 20);
        String [] fecha = carga.getFechaRecogida().split("/");
        String [] hora = carga.getHoraRecogida().split(":");
        if(calendar.getTime().getYear()+1900 > Integer.parseInt(fecha[2])) {
            iniciarRecorrido(false);
            return;
        }
        else if(calendar.getTime().getYear()+1900 < Integer.parseInt(fecha[2])) {
            iniciarRecorrido(true);
            return;
        }
        else if ((calendar.getTime().getMonth()+1) > Integer.parseInt(fecha[1])) {
            iniciarRecorrido(false);
            return;
        }
        else if ((calendar.getTime().getMonth()+1) < Integer.parseInt(fecha[1])) {
            iniciarRecorrido(true);
            return;
        }
        else if ((calendar.getTime().getDate()) > Integer.parseInt(fecha[0])) {
            iniciarRecorrido(false);
            return;
        }
        else if ((calendar.getTime().getDate()) < Integer.parseInt(fecha[0])) {
            iniciarRecorrido(true);
            return;
        }
        if (calendar.getTime().getHours() > Integer.parseInt(hora[0])) {
            iniciarRecorrido(false);
            return;
        }
        if (calendar.getTime().getHours() < Integer.parseInt(hora[0])) {
            iniciarRecorrido(true);
            return;
        }
        else if(calendar.getTime().getMinutes() > Integer.parseInt(hora[1])) {
            iniciarRecorrido(false);
            return;
        }
        else if(calendar.getTime().getMinutes() < Integer.parseInt(hora[1])) {
            iniciarRecorrido(true);
            return;
        }
        iniciarRecorrido(true);

    }

    private void iniciarRecorrido(boolean aTiempo) {
        Map<String, Object> usuarioData = new HashMap<>();
        if(aTiempo) {
            usuarioData.put("estado", "En viaje");
            carga.setEstado("En viaje");
        }
        else {
            usuarioData.put("estado", "No recogido");
            carga.setEstado("No recogido");
        }
        database.collection("cargas").document(carga.getCodigo()).update(usuarioData);

    }

    private void showInputDialogPropietario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View alert = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(alert);
        AlertDialog dialog = builder.create();
        Button aceptar = alert.findViewById(R.id.button4);
        Button cancelar = alert.findViewById(R.id.button5);
        TextInputEditText text = alert.findViewById(R.id.aplicar_camion);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = -1;
                dialog.dismiss();
                View view = inflater.inflate(R.layout.aplicar_camion, null);
                Button aceptar = view.findViewById(R.id.button7);
                Button cancelar = view.findViewById(R.id.button8);
                builder.setView(view);
                AlertDialog camion = builder.create();
                for(Camion temp : camiones) {
                    if((temp.getPlaca().replaceAll(" ", "")).equalsIgnoreCase(text.getText().toString().replaceAll(" ", ""))){
                        TextView date = view.findViewById(R.id.nTarjetaPropiedad);
                        date.setText(temp.getTarjetaPropiedad()+"");
                        date = view.findViewById(R.id.placa);
                        date.setText(temp.getPlaca()+"");
                        date = view.findViewById(R.id.modelo);
                        date.setText(temp.getModelo()+"");
                        date = view.findViewById(R.id.capacidad);
                        date.setText(temp.getCapacidad()+"");
                        date = view.findViewById(R.id.conductor);
                        date.setText(temp.getConductor()+"");
                        index = camiones.indexOf(temp);
                        break;
                    }

                }
                if(index == -1)
                    Toast.makeText(AplicarCarga.this, "Usted no tiene este camion", Toast.LENGTH_LONG).show();
                else if(carga.getPeso() > camiones.get(index).getCapacidad())
                    Toast.makeText(AplicarCarga.this, "Este camion no cumple con el peso de la carga"+camiones.get(index).getCapacidad(), Toast.LENGTH_LONG).show();
                else if(camiones.get(index).getConductor() == 0)
                    Toast.makeText(AplicarCarga.this, "Este camion no tiene conductor", Toast.LENGTH_LONG).show();
                else
                    camion.show();
                aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pushPostulacion(((TextView)view.findViewById(R.id.placa)).getText().toString());
                        Toast.makeText(AplicarCarga.this, "Postulacion con exito", Toast.LENGTH_LONG).show();
                        camion.dismiss();
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        camion.dismiss();
                    }
                });

            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void buscarCamion(String cedula) {
        Query query = database.collection("camiones").whereEqualTo("propietario", Long.valueOf(cedula));
        this.camiones = new ArrayList<>();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                       for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                           camiones.add(new Camion(documentSnapshot.getId(),documentSnapshot.getLong("modelo"), documentSnapshot.getLong("nTarjetaPropiedad"), documentSnapshot.getLong("capacidad"),
                           documentSnapshot.getLong("conductor"), documentSnapshot.getLong("propietario") ));
                       }
                    } else {
                        Toast.makeText(AplicarCarga.this, "El camion no se encuentra registrado, no tiene conductor o no cumple con el peso de la carga", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void backDetalleCarga(View view) {
        finish();
    }

    private void pushPostulacion(String placa) {
        Map<String, Object> postulacion = new HashMap<>();
        postulacion.put("carga_id", carga.getCodigo());
        postulacion.put("placa", placa);
        database.collection("aplicaciones").add(postulacion);
    }

    public static Carga getCarga() {
        return carga;
    }

    public static void setCarga(Carga carga) {
        AplicarCarga.carga = carga;
    }
    private void notificarRecorridoAlterno () {
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("estado", "En recorrido alterno");
        carga.setEstado("En recorrido alterno");
        database.collection("cargas").document(carga.getCodigo()).update(usuarioData);
        HomeFragment.setCargas(carga);
        recreate();
    }

}