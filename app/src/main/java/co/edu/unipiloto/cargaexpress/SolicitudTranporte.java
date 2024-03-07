package co.edu.unipiloto.cargaexpress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SolicitudTranporte extends AppCompatActivity {

    private FirebaseFirestore database;
    private String cedComerciante = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_tranporte);
        database = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        cedComerciante = intent.getStringExtra("documento");
    }




    public void publicarSolicitud (View view){
        Spinner tipoCarga = (Spinner) findViewById(R.id.tipoCarga);
        TextInputEditText peso = (TextInputEditText) findViewById(R.id.peso);
        TextInputEditText alto = (TextInputEditText) findViewById(R.id.alto);
        TextInputEditText ancho = (TextInputEditText) findViewById(R.id.ancho);
        TextInputEditText profundidad = (TextInputEditText) findViewById(R.id.profundidad);
        TextInputEditText direccionOrigen = (TextInputEditText) findViewById(R.id.direccionOrigen);
        TextInputEditText ciudadOrigen = (TextInputEditText) findViewById(R.id.ciudadOrigen);
        TextInputEditText direccionDestino = (TextInputEditText) findViewById(R.id.direccionDestino);
        TextInputEditText ciudadDestino = (TextInputEditText) findViewById(R.id.ciudadDestino);
        TextInputEditText fechaRecogida = (TextInputEditText) findViewById(R.id.fechaRecogida);
        TextInputEditText horaRecogida = (TextInputEditText) findViewById(R.id.horaRecogida);
        TextInputEditText fechaEntrega = (TextInputEditText) findViewById(R.id.fechaEntrega);
        EditText especificaciones = (EditText) findViewById(R.id.especificaciones);

        if(peso.getText().toString().isEmpty()){
            peso.setError("Debe ingresar un peso valido");
            peso.requestFocus();
        }
        if(alto.getText().toString().isEmpty()){
            alto.setError("Debe ingresar un alto valido");
            alto.requestFocus();
        }
        if(ancho.getText().toString().isEmpty()){
            ancho.setError("Debe ingresar un ancho valido");
            ancho.requestFocus();
        }
        if (profundidad.getText().toString().isEmpty()){
            profundidad.setError("Debe ingresar una profundidad valida");
            profundidad.requestFocus();
        }
        if (direccionOrigen.getText().toString().isEmpty()){
            direccionOrigen.setError("Debe ingresar una dirección de origen valida");
            direccionOrigen.requestFocus();
        }
        if (ciudadOrigen.getText().toString().isEmpty()){
            ciudadOrigen.setError("Debe ingresar una ciudad de origen valida");
            ciudadOrigen.requestFocus();
        }
        if (direccionDestino.getText().toString().isEmpty()){
            direccionDestino.setError("Debe ingresar una dirección de destino valida");
            direccionDestino.requestFocus();
        }
        if (ciudadDestino.getText().toString().isEmpty()){
            ciudadDestino.setError("Debe ingresar una ciudad de origen valida");
            ciudadDestino.requestFocus();
        }
        if (fechaRecogida.getText().toString().isEmpty()){
            fechaRecogida.setError("Debe ingresar una fecha de recogida valida");
            fechaRecogida.requestFocus();
        }
        if (horaRecogida.getText().toString().isEmpty()){
            horaRecogida.setError("Debe ingresar una hora de recogida valida");
            horaRecogida.requestFocus();
        }
        if (fechaEntrega.getText().toString().isEmpty()){
            fechaEntrega.setError("Debe ingresar una fecha de entrega valida");
            fechaEntrega.requestFocus();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaPublicacion = dateFormat.format(new Date());

        crearCargaDocument(tipoCarga.getSelectedItem().toString(), Integer.parseInt(peso.getText().toString()), (alto.getText().toString() +" X "+ ancho.getText().toString() +" X "+ profundidad.getText().toString()),
                direccionOrigen.getText().toString(), ciudadOrigen.getText().toString(), direccionDestino.getText().toString(), ciudadDestino.getText().toString(), fechaPublicacion, fechaRecogida.getText().toString(), horaRecogida.getText().toString(),
                fechaEntrega.getText().toString(), especificaciones.getText().toString(), Integer.parseInt(cedComerciante), 0);
        misPublicacionesView(view);
    }

    private void crearCargaDocument (String tipoCarga, int peso, String dimensiones, String direccionOrigen, String ciudadOrigen, String direccionDestino, String ciudadDestino, String fechaPublicada, String fechaRecogida, String horaRecogida, String fechaEntrega, String especificaciones, int cedulaComerciante, int cedulaConductor){
        Map<String, Object> cargaData = new HashMap<>();
        cargaData.put("tipoCarga", tipoCarga);
        cargaData.put("peso", peso);
        cargaData.put("dimensiones", dimensiones);
        cargaData.put("direccionOrigen", direccionOrigen);
        cargaData.put("ciudadOrigen", ciudadOrigen);
        cargaData.put("direccionDestino", direccionDestino);
        cargaData.put("ciudadDestino", ciudadDestino);
        cargaData.put("fechaPublicacion", fechaPublicada);
        cargaData.put("fechaRecogida", fechaRecogida);
        cargaData.put("horaRecogida", horaRecogida);
        cargaData.put("fechaEntrega", fechaEntrega);
        cargaData.put("especificaciones", especificaciones);
        cargaData.put("comerciante", cedulaComerciante);
        cargaData.put("conductor", cedulaConductor);
        database.collection("cargas").add(cargaData);
    }

    public void misPublicacionesView (View view) {
        Intent intent = new Intent(this, MisPublicaciones.class);
        startActivity(intent);
    }
}
