package co.edu.unipiloto.cargaexpress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        database = FirebaseFirestore.getInstance();
    }

    public void ingreso(View view){
        Intent intent = new Intent(this, Ingreso.class);
        startActivity(intent);
    }

    public void mainCarga(View view) {
        Intent intent = new Intent(this, carga_express.class);
        startActivity(intent);
    }

    public void crearRegistro(View view) {
        TextInputEditText nombre = (TextInputEditText) findViewById(R.id.nombreRegistro);
        TextInputEditText apellidos = (TextInputEditText) findViewById(R.id.apellidosRegistro);
        Spinner tipoDocumento = (Spinner) findViewById(R.id.tipoDocumentoRegistro);
        TextInputEditText cedula = (TextInputEditText) findViewById(R.id.cedulaRegistro);
        TextInputEditText email = (TextInputEditText) findViewById(R.id.correoRegistro);
        TextInputEditText contra = (TextInputEditText) findViewById(R.id.contrasenaRegistro);
        RadioGroup rol = (RadioGroup) findViewById(R.id.rol);
        nombre.setError(null);
        apellidos.setError(null);
        cedula.setError(null);
        email.setError(null);
        contra.setError(null);
        if(nombre.getText().toString().isEmpty()) {
            nombre.setError("Debe ingresar un nombre valido");
            nombre.requestFocus();
            return;
        }
        if(apellidos.getText().toString().isEmpty()) {
            apellidos.setError("Debe ingresar un apellido valido");
            apellidos.requestFocus();
            return;
        }
        if(cedula.getText().toString().isEmpty()) {
            cedula.setError("Debe ingresar un numero cedula valido");
            cedula.requestFocus();
            return;
        }
        if(email.getText().toString().isEmpty()) {
            email.setError("Debe ingresar un correo valido");
            email.requestFocus();
            return;
        }
        if(contra.getText().toString().isEmpty()) {
            contra.setError("Debe ingresar una contraseña valida");
            contra.requestFocus();
            return;
        }
        int id = rol.getCheckedRadioButtonId();
        String rolElegido = ((RadioButton)findViewById(id)).getText().toString();
        createUsuarioDocument(cedula.getText().toString(), nombre.getText().toString(), apellidos.getText().toString(),
                tipoDocumento.getSelectedItem().toString(), email.getText().toString(), contra.getText().toString(), rolElegido);
        mainCarga(view);

    }

    private void createUsuarioDocument(String cedula, String nombre, String apellidos, String tipoDocumento, String email, String contra, String rol) {
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("nombre", nombre);
        usuarioData.put("apellidos", apellidos);
        usuarioData.put("tipoDocumento", tipoDocumento);
        usuarioData.put("email", email);
        usuarioData.put("contra", contra);
        usuarioData.put("rol", rol);
        database.collection("usuarios").document(cedula).set(usuarioData);
    }

}