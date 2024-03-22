package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AplicarCarga extends AppCompatActivity {

    private Usuario user;
    private Carga carga;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplicar_carga);
        user = getIntent().getParcelableExtra("user");
        carga = getIntent().getParcelableExtra("carga");
        if(user.getRol().equals("Propietario de camion"))
            ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.btn_postularme);
        else if (user.getRol().equals("Comerciante"))
            ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setText(R.string.ver_postulaciones);
        ((Button) findViewById(R.id.btn_publicarSolicitud_transporte)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detalle_carga();
            }
        });
        database = FirebaseFirestore.getInstance();
        mostrarDatos();
    }

    private void mostrarDatos() {

        ((TextView) findViewById(R.id.tipoCarga)).setText(carga.getTipoCarga());
        ((TextView) findViewById(R.id.textInputLayout4)).setText(carga.getTipoCarga());
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

    }

    private void detalle_carga() {
        if(user.getRol().equals("Propietario de camion")) {
            showInputDialog();
        }
        else if (user.getRol().equals("Comerciante")) {
            Toast.makeText(this, "Proximamente podra acceder", Toast.LENGTH_LONG);
        }

    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese la placa del camion a postular: ");

        // Campo de entrada para el texto
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                buscarCamion(text);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostrar el diálogo
        builder.show();
    }

    private void buscarCamion(String placa) {
        Query query = database.collection("camiones").whereEqualTo(FieldPath.documentId(), placa);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        if(carga.getPeso() < querySnapshot.getDocuments().get(0).getLong("capacidad") &&  querySnapshot.getDocuments().get(0).getLong("conductor") != 0) {
                            Map<String, Object> camionData = new HashMap<>();
                            camionData.put("carga_id", carga.getCodigo());
                            camionData.put("placa", placa);
                            database.collection("aplicaciones").add(camionData);
                            Toast.makeText(AplicarCarga.this, "Aplicacion realizada", Toast.LENGTH_SHORT).show();
                            finish();
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

}