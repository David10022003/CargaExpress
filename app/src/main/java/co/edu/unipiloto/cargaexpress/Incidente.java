package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;

public class Incidente extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private List<ImageView> imageView;
    private Button selectImageButton;

    private Button enviar;

    private Usuario usuario;
    private Carga carga;
    private ImageButton back;
    List<String> img;
     private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidente);
        database = FirebaseFirestore.getInstance();
        usuario = getIntent().getParcelableExtra("user");
        carga = getIntent().getParcelableExtra("carga");

        imageView = new ArrayList();
        selectImageButton = findViewById(R.id.selectImageButton);
        enviar = findViewById(R.id.publicar_incidencia);
        selectImageButton.setOnClickListener(v -> openFileChooser());
        img = new ArrayList();
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar();
            }
        });

        back = findViewById(R.id.button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            ImageView temp = new ImageView(this);
            temp.setId(View.generateViewId());
            LinearLayout constraint = findViewById(R.id.scroll);
            temp.setImageURI(imageUri);
            if (imageView.size() < 3) {
                imageView.add(temp);
                img.add(comprimirImagen(imageUri));
            } else {
                constraint.removeView(findViewById(imageView.get(imageView.size() -1).getId()));
                imageView.set(imageView.size() - 1, temp);
                img.set(img.size() -1 ,comprimirImagen(imageUri));

            }
            constraint.addView(temp);


        }
    }

    private void enviar() {
        Spinner spinner = findViewById(R.id.spinner);
        TextView descripcion = findViewById(R.id.editTextTextMultiLine);
        if(spinner.getSelectedItemPosition() == -1) {
            Toast.makeText(this, "Escoja un tipo de incidente", Toast.LENGTH_LONG).show();
            return;
        }
        if(descripcion.getText().toString().isEmpty()) {
            descripcion.setError("Llenar este campo");
            return;
        }
        if(imageView == null) {
            Toast.makeText(this, "Subir imagenes del incidente", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("carga_id", carga.getCodigo());
        usuarioData.put("descripcion", descripcion.getText().toString());
        usuarioData.put("tipos", spinner.getSelectedItem().toString());
        usuarioData.put("imagenes", img);
        database.collection("incidencias").document().set(usuarioData);
        carga.setEstado("Incidencia");
        carga.setIncidencias(carga.getIncidencias()+1);
        HomeFragment.setCargas(carga);
        usuarioData = new HashMap<>();
        usuarioData.put("estado", "Incidencia");
        usuarioData.put("incidencias", carga.getIncidencias());
        database.collection("cargas").document(carga.getCodigo()).update(usuarioData);
        HomeFragment.setCargas(carga);
        actualizarComerciante();
        finish();
        Intent intent = new Intent(this, AplicarCarga.class);
        intent.putExtra("user", usuario);
        intent.putExtra("carga", carga);
        startActivity(intent);

    }

    private void actualizarComerciante() {
        DocumentReference docRef = database.collection("estadisicas").document(carga.getCedulaComerciante()+""+ Calendar.getInstance().get(Calendar.YEAR));
        int month = Calendar.getInstance().get(Calendar.MONTH);
        String[] monthNames = {
                "enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};
        database.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(docRef);
                Log.d("AplicarCarga", "Transacción completada con éxito entrando en if");
                final List<Long> arregloActual = (List<Long>) snapshot.get(monthNames[month]);
                arregloActual.set(3, arregloActual.get(3) +1);
                Map<String, Object> updateData = new HashMap<>();
                updateData.put(monthNames[month], arregloActual);

                Log.d("AplicarCarga", "Transacción completada con éxito actualizar estadisticas exists" + arregloActual.size());
                transaction.update(docRef, monthNames[month], arregloActual);

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AplicarCarga", "Transacción completada con éxito");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("AplicarCarga", "Error en la transacción", e);
            }
        });
    }

    private String comprimirImagen(Uri imageUri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }

    }

}