package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MostrarCamion extends AppCompatActivity {

    long  cedula;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_camion);
        cedula = getIntent().getLongExtra("cedula", 0L);
        database = FirebaseFirestore.getInstance();
        iniciarComponents();
    }

    private void iniciarComponents() {
        Query query = database.collection("camiones").whereEqualTo("conductor", cedula);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        ((TextView) findViewById(R.id.placa_camion)).setText(querySnapshot.getDocuments().get(0).getId());
                        ((TextView) findViewById(R.id.capacidad_camion)).setText(querySnapshot.getDocuments().get(0).getLong("capacidad")+"");
                        ((TextView) findViewById(R.id.conductor_camion)).setText(querySnapshot.getDocuments().get(0).getLong("conductor")+"");
                        ((TextView) findViewById(R.id.modelo_camion)).setText(querySnapshot.getDocuments().get(0).getLong("modelo")+"");
                        ((TextView) findViewById(R.id.propiedad_camion)).setText(querySnapshot.getDocuments().get(0).getLong("nTarjetaPropiedad")+"");

                    } else {
                        Toast.makeText(MostrarCamion.this, "No se encuentra el camion asociado a: " + cedula, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                    finish();
                }
            }
        });
    }
}