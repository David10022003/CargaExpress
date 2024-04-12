package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Ingreso extends AppCompatActivity {

    private static final String TAG = "Ingreso";
    private FirebaseFirestore database;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private Usuario user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);
        preferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        database = FirebaseFirestore.getInstance();
    }

    public void registro(View view) {
        Intent intent = new Intent(this, Registro.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }





    private void mainCarga() {
        Intent intent = new Intent(this, carga_express.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    public void ingreso(View view) {
        TextInputEditText cedula = (TextInputEditText) findViewById(R.id.cedulaIngreso);
        TextInputEditText contra = (TextInputEditText) findViewById(R.id.contrasenaIngreso);
        cedula.setError(null);
        contra.setError(null);
        if(cedula.getText().toString().isEmpty()) {
            cedula.setError("Debe ingresar un numero de cedula valido");
            cedula.requestFocus();
            return;
        }
        if(contra.getText().toString().isEmpty()) {
            contra.setError("Debe ingresar una contraseña valida");
            contra.requestFocus();
            return;
        }

        generarIngreso(cedula.getText().toString(), contra.getText().toString());

    }

    private void generarIngreso(String cedula, String password) {
        String texto = "El usuario no se encuentra registrado";
        Query query = database.collection("usuarios").whereEqualTo(FieldPath.documentId(), cedula).whereEqualTo("contra", password);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        user = new Usuario(document.getId(), document.getString("nombre"), document.getString("apellidos"),
                                document.getString("tipoDocumento"), document.getString("email"), document.getString("contra"), document.getString("rol"), querySnapshot.getDocuments().get(0).getString("genero"),
                                document.getString("fechaNacimiento"));

                        mainCarga();
                    } else {
                        Toast.makeText(Ingreso.this, "El usuario no se encuentra registrado", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}