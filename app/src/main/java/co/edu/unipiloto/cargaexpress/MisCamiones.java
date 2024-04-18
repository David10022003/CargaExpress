package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import java.util.List;

public class MisCamiones extends AppCompatActivity {

    private String cedula = "";
    private TableLayout tableLayout;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_camiones);
        Intent intent = getIntent();
        cedula = intent.getStringExtra("documento");
        database = FirebaseFirestore.getInstance();
        tableLayout = findViewById(R.id.table_layout);
        misCamiones(MisCamiones.this);

    }

    public void agregarCamion(View view) {
        Intent intent = new Intent(this, registroCamion.class);
        intent.putExtra("documento", cedula);
        startActivity(intent);
    }

    public void back(View view){
        finish();
    }

    private void misCamiones(Context context) {
        Query query = database.collection("camiones").whereEqualTo("propietario", Integer.parseInt(cedula));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for(QueryDocumentSnapshot document : querySnapshot) {
                            Camion agregar = new Camion(String.valueOf(document.getId()), document.getLong("modelo"), document.getLong("nTarjetaPropiedad"),
                                    document.getLong("capacidad"), document.getLong("conductor"), document.getLong("propietario"));
                            TableRow row = new TableRow(context);
                            row.setPadding(10,5,10,5);
                            TextView temp = new TextView(context);
                            temp.setText(agregar.getPlaca());
                            row.addView(temp);
                            temp = new TextView(context);
                            temp.setText(""+agregar.getTarjetaPropiedad());
                            row.addView(temp);
                            temp = new TextView(context);
                            temp.setText(""+agregar.getCapacidad());
                            row.addView(temp);
                            temp = new TextView(context);
                            if(agregar.getConductor() == 0){
                                Button conductor = new Button(context);
                                conductor.setText("+");
                                conductor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        agregarConductor(agregar.getPlaca());
                                    }
                                });
                                row.addView(conductor);
                            }else {
                                temp.setText("" + agregar.getConductor());
                                temp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MisCamiones.this, MostrarConductor.class);
                                        intent.getParcelableExtra("cedula");
                                        startActivity(intent);
                                    }
                                });
                                row.addView(temp);
                            }
                            tableLayout.addView(row);
                        }
                    } else {
                        Toast.makeText(context, "El usuario no se encuentra registrado", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void agregarConductor(String placa) {
        final EditText inputEditText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingrese la cedula del conductor para " + placa+ " :").setView(inputEditText)

                .setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String userInput = inputEditText.getText().toString();
                        buscarConductor(userInput, placa);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Aquí puedes manejar el evento de cancelación
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void buscarConductor(String cedulaBuscar, String placa) {
       Intent intent = new Intent(MisCamiones.this, MostrarConductor.class);
       intent.putExtra("cedula", cedulaBuscar);
       intent.putExtra("placa", placa);
       startActivity(intent);
    }


}