package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MisPublicaciones extends AppCompatActivity {
    private String cedula;
    private FirebaseFirestore database;
    private TableLayout tableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_publicaciones);
        Intent intent = getIntent();
        cedula = intent.getStringExtra("documento");
        tableLayout = findViewById(R.id.tabla_publicaciones);
        database = FirebaseFirestore.getInstance();
        mostrarPublicaciones();
    }

    public void backComerciante(View view) {
        finish();
    }

    public void solicitarTransporteView (View view){
        Intent intent = new Intent(this, SolicitudTranporte.class);
        intent.putExtra("documento", cedula);
        startActivity(intent);
    }

    public void mostrarPublicaciones(){
        Query query = database.collection("cargas").whereEqualTo("comerciante", Integer.parseInt(cedula) );
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for(QueryDocumentSnapshot document : querySnapshot) {
                            Carga agregar = new Carga(String.valueOf(document.getId()), document.getString("tipoCarga"),
                                    document.getLong("peso"), document.getString("dimensiones"), document.getString("direccionOrigen"), document.getString("ciudadOrigen"), document.getString("direccionDestino"), document.getString("ciudadDestino"), document.getString("fechaPublicacion"), document.getString("fechaRecogida"), document.getString("horaRecogida"),document.getString("fechaEntrega"), document.getString("especificaciones"), document.getLong("comerciante"), document.getLong("conductor"));
                            TableRow row = new TableRow(MisPublicaciones.this);
                            row.setPadding(10,5,10,5);
                            TextView temp = new TextView(MisPublicaciones.this);
                            temp.setText(agregar.getCodigo());
                            row.addView(temp);
                            temp = new TextView(MisPublicaciones.this);
                            temp.setText(""+agregar.getFechaPublicada());
                            row.addView(temp);
                            if(agregar.getCedulaConductor() == 0){
                                Button conductor = new Button(MisPublicaciones.this);
                                conductor.setText("+");
                                conductor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                row.addView(conductor);
                            }else {
                                temp.setText("" + agregar.getCedulaConductor());
                                row.addView(temp);
                            }
                            tableLayout.addView(row);
                        }
                    } else {
                        Toast.makeText(MisPublicaciones.this, "El usuario no tiene cargas", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}