package co.edu.unipiloto.cargaexpress;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registroCamion extends AppCompatActivity {

    private FirebaseFirestore database;
    private String cedulaPropietario = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_camion);
        database = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        cedulaPropietario = intent.getStringExtra("documento");

    }

    public void registrarCamion (View view) {
        TextInputEditText nTarjetaPropiedad = (TextInputEditText) findViewById(R.id.nTarjetaPropiedad);
        TextInputEditText placa = (TextInputEditText) findViewById(R.id.placa);
        TextInputEditText modelo = (TextInputEditText) findViewById(R.id.modelo);
        TextInputEditText capacidad = (TextInputEditText) findViewById(R.id.capacidad);
        nTarjetaPropiedad.setError(null);
        placa.setError(null);
        modelo.setError(null);
        capacidad.setError(null);
        if(nTarjetaPropiedad.getText().toString().isEmpty()){
            nTarjetaPropiedad.setError("Debe ingresar una tarjeta de propiedad valida");
            nTarjetaPropiedad.requestFocus();
            return;
        }
        if(placa.getText().toString().isEmpty()){
            placa.setError("Debe ingresar una placa valida");
            placa.requestFocus();
            return;
        }
        if(modelo.getText().toString().isEmpty()){
            modelo.setError("Debe ingresar un modelo valido");
            modelo.requestFocus();
            return;
        }
        if (capacidad.getText().toString().isEmpty()){
            capacidad.setError("Debe ingresar una capacidad valida");
            capacidad.requestFocus();
            return;
        }

        crearCamionDocument(Integer.parseInt(nTarjetaPropiedad.getText().toString()), placa.getText().toString(), Integer.parseInt(modelo.getText().toString()),
                Integer.parseInt(capacidad.getText().toString()), 0, Integer.parseInt(cedulaPropietario));
        camionView(view);
    }

    private void crearCamionDocument (int nTarjetaPropiedad, String placa, int modelo, int capacidad, int cedulaConductor, int cedulaPropietario) {
        Map<String, Object> camionData = new HashMap<>();
        camionData.put("nTarjetaPropiedad", nTarjetaPropiedad);
        camionData.put("modelo", modelo);
        camionData.put("capacidad", capacidad);
        camionData.put("conductor", cedulaConductor);
        camionData.put("propietario", cedulaPropietario);
        database.collection("camiones").document(placa).set(camionData);
    }

    public void camionView (View view){
        Intent intent = new Intent(this, MisCamiones.class);
        intent.putExtra("documento", cedulaPropietario);
        startActivity(intent);
    }
}