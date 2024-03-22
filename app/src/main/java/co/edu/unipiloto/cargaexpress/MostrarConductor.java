package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MostrarConductor extends AppCompatActivity {

    private FirebaseFirestore database;
    private String placa;
    private String cedula;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_conductor);
        database = FirebaseFirestore.getInstance();
        cedula = getIntent().getStringExtra("cedula");
        if(!getIntent().hasExtra("placa")) {
            ((Button) findViewById(R.id.aceptar)).setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.textView23)).setText(getString(R.string.conductor));
            buscarConductor(cedula);
        }
        else {
            placa = getIntent().getStringExtra("placa");
            verificarConductor(cedula);
        }

    }

    public void buscarConductor(String cedulaBuscar) {
        Query query = database.collection("usuarios").whereEqualTo(FieldPath.documentId(), cedulaBuscar).whereEqualTo("rol", "Conductor");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        ((TextView) findViewById(R.id.nombre)).setText(querySnapshot.getDocuments().get(0).getString("nombre"));
                        ((TextView) findViewById(R.id.apellidos)).setText(querySnapshot.getDocuments().get(0).getString("apellidos"));
                        ((TextView) findViewById(R.id.tipoDocumento)).setText(querySnapshot.getDocuments().get(0).getString("tipoDocumento"));
                        ((TextView) findViewById(R.id.numeroDocumento)).setText(querySnapshot.getDocuments().get(0).getId());
                        ((TextView) findViewById(R.id.email)).setText(querySnapshot.getDocuments().get(0).getString("email"));

                    } else {
                        Toast.makeText(MostrarConductor.this, "No se encuentra al conductor: " + cedulaBuscar, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                    finish();
                }
            }
        });
    }

    private void verificarConductor(String cedula) {
        Query query = database.collection("camiones").whereEqualTo("conductor", Integer.parseInt(cedula));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Toast.makeText(MostrarConductor.this, "El conductor ya tiene camion ", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        buscarConductor(cedula);
                        return;
                    }
                } else {
                    return;
                }
            }
        });
    }

    public void aceptar(View view) {

        DocumentReference docRef = database.collection("camiones").document(placa);

        Map<String, Object> updates = new HashMap<>();
        updates.put("conductor", Integer.parseInt(cedula));
        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MostrarConductor.this, "Conductor añadido", Toast.LENGTH_LONG);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MostrarConductor.this, "Error al añadir", Toast.LENGTH_LONG);
                        finish();
                    }
                });

    }

    public void cancelar (View view) {
        finish();
    }
}