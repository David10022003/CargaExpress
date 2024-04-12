package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.unipiloto.cargaexpress.ui.home.AdapterRecyclerView;
import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;

public class AsignarConductor extends AppCompatActivity {

    private List<Usuario> users;
    private List<Camion> camions;
    private static FirebaseFirestore database;
    private Carga carga;

    private Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asignar_conductor);
        users = new ArrayList<>();
        carga = getIntent().getParcelableExtra("carga");
        camions = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
        AdapterConductor.ActivityActual.asignarConductor = this;
        traerDB();
    }

    private void crearLista() {
        RecyclerView recyclerView = findViewById(R.id.lista_conductores);
        GridLayoutManager layoutManager = new GridLayoutManager(this,1 );
        recyclerView.setLayoutManager(layoutManager);
        AdapterConductor adapterRecyclerView = new AdapterConductor(this, users, camions);
        recyclerView.setAdapter(adapterRecyclerView);
        if(users.isEmpty())
            Toast.makeText(this, "Error al mostrar usuarios", Toast.LENGTH_LONG).show();
        else if(camions.isEmpty())
            Toast.makeText(this, "Error al mostrar camiones", Toast.LENGTH_LONG).show();
        else{
            adapterRecyclerView.setSearchList(users, camions, carga);
        }

    }

    private void traerDB() {
        database.collection("aplicaciones")
                .whereEqualTo("carga_id", carga.getCodigo())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> placas = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String placa = document.getString("placa");
                            placas.add(placa);
                        }
                        if(placas.isEmpty()) {
                            return;
                        }
                        database.collection("camiones")
                                .whereIn(FieldPath.documentId(), placas)
                                .get()
                                .addOnCompleteListener(camionesTask -> {
                                    if (camionesTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot camionDocument : camionesTask.getResult()) {
                                            camions.add(new Camion(camionDocument.getId(), camionDocument.getLong("modelo"), camionDocument.getLong("nTarjetaPropiedad"), camionDocument.getLong("capacidad"), camionDocument.getLong("conductor"), camionDocument.getLong("propietario")));
                                            query = database.collection("usuarios").whereEqualTo(FieldPath.documentId(), camions.get(camions.size()-1).getConductor()+"");
                                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        QuerySnapshot querySnapshot = task.getResult();
                                                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                            users.add(new Usuario(querySnapshot.getDocuments().get(0).getId(), querySnapshot.getDocuments().get(0).getString("nombre"), querySnapshot.getDocuments().get(0).getString("apellidos"), querySnapshot.getDocuments().get(0).getString("tipoDocumento"),
                                                                    querySnapshot.getDocuments().get(0).getString("email"), "", querySnapshot.getDocuments().get(0).getString("rol"), querySnapshot.getDocuments().get(0).getString("genero"), querySnapshot.getDocuments().get(0).getString("fechaNacimiento")));
                                                            if(users.size() == camions.size())
                                                                crearLista();
                                                        }
                                                        else{
                                                            Toast.makeText(AsignarConductor.this, "Datos usuario no encontrados", Toast.LENGTH_LONG).show();
                                                        }
                                                    }

                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(this, "No tiene postulaciones a esta publicacion", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                    } else {
                        finish();
                    }
                });
    }

}