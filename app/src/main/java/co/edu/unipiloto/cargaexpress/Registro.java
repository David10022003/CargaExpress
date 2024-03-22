package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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
        finish();
    }

    public void mainCarga() {
        finish();
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
        verificarUsuario(cedula.getText().toString(), nombre.getText().toString(), apellidos.getText().toString(),
                tipoDocumento.getSelectedItem().toString(), email.getText().toString(), contra.getText().toString(), rolElegido);

    }

    private void verificarUsuario(String cedula, String nombre, String apellidos, String tipoDocumento, String email, String contra, String rol){
        Query query = database.collection("usuarios").whereEqualTo(FieldPath.documentId(), cedula);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Toast.makeText(Registro.this, "El usuario ya esta registrado: ", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        createUsuarioDocument(cedula, nombre, apellidos, tipoDocumento, email, contra, rol);
                    }
                } else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
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
        mainCarga();
    }

}