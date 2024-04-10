package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ver_incidente extends AppCompatActivity {

    private FirebaseFirestore database;
    private Carga carga;

    private ImageButton back;

    private List<String> tipos;
    private List<String> descrpciones;
    private List<List<String>> imagenes;
    private int actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_incidente);
        carga = getIntent().getParcelableExtra("carga");
        database = FirebaseFirestore.getInstance();
        back = findViewById(R.id.button);
        tipos = new ArrayList<>();
        descrpciones = new ArrayList<>();
        imagenes = new ArrayList<>();
        actual = -1;
        ImageButton back1 = findViewById(R.id.atras);
        ImageButton forward = findViewById(R.id.forwardButton);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actual-1 <= 0){

                    back1.setEnabled(false);
                }
                if(actual-1 > -1){
                    forward.setEnabled(true);
                    ((TextView)findViewById(R.id.spinner)).setText(tipos.get(actual-1));
                    ((TextView)findViewById(R.id.editTextTextMultiLine)).setText(descrpciones.get(actual-1));
                    convertirBase64ABitmap(imagenes.get(actual-1));
                    actual--;
                }

            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actual+1 >= tipos.size()-1){
                    forward.setEnabled(false);

                }
                if(actual+1 < tipos.size()){
                    back1.setEnabled(true);
                    ((TextView)findViewById(R.id.spinner)).setText(tipos.get(actual+1));
                    ((TextView)findViewById(R.id.editTextTextMultiLine)).setText(descrpciones.get(actual+1));
                    convertirBase64ABitmap(imagenes.get(actual+1));
                    actual++;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llenarCampos();
    }

    private void llenarCampos() {
        Query query = database.collection("incidencias").whereEqualTo("carga_id", carga.getCodigo());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for(DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){

                            tipos.add(documentSnapshot.getString("tipos"));
                            descrpciones.add(documentSnapshot.getString("descripcion"));
                            imagenes.add((List<String>) documentSnapshot.get("imagenes"));
                        }
                        ((TextView)findViewById(R.id.spinner)).setText(tipos.get(0));
                        ((TextView)findViewById(R.id.editTextTextMultiLine)).setText(descrpciones.get(0));
                        convertirBase64ABitmap(imagenes.get(0));
                        actual = 0;
                    } else {
                        Toast.makeText(ver_incidente.this, "El camion no se encuentra registrado, no tiene conductor o no cumple con el peso de la carga", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void convertirBase64ABitmap(List<String> base64Image) {
        // Decodificar la cadena Base64 en un array de bytes
        LinearLayout layout = findViewById(R.id.scroll);
        layout.removeAllViews();
        for (String img: base64Image) {
            byte[] decodedBytes = Base64.decode(img, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            layout.addView(imageView);
        }

    }

}