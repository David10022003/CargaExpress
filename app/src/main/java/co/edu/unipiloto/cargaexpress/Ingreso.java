package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso);
        database = FirebaseFirestore.getInstance();

    }

    public void registro(View view) {
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }

    public void mainCarga(View view) {
        Intent intent = new Intent(this, carga_express.class);
        startActivity(intent);
    }

    public void ingreso(View view) {
        TextInputEditText cedula = (TextInputEditText) findViewById(R.id.cedulaIngreso);
        TextInputEditText contra = (TextInputEditText) findViewById(R.id.contrasenaIngreso);
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

        generarIngreso(view,this ,cedula.getText().toString(), contra.getText().toString());

    }

    private void generarIngreso(View view, Context context, String cedula, String password) {
        String texto = "El usuario no se encuentra registrado";
        Query query = database.collection("usuarios").whereEqualTo(FieldPath.documentId(), cedula).whereEqualTo("contra", password);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        mainCarga(view);
                    } else {
                        Toast.makeText(context, "El usuario no se encuentra registrado", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
}