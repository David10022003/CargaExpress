package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private FirebaseFirestore database;
    private PlacesClient placesClient;
    private ArrayList<String> suggestions;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        database = FirebaseFirestore.getInstance();
        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getAPIKey());
        }
        placesClient = Places.createClient(this);
        initMaps();


        TextInputEditText fechaNacimiento = (TextInputEditText) findViewById(R.id.fechaNacimientoRegistro);
        fechaNacimiento.setFocusableInTouchMode(false);
        Calendar calendarMayoriaEdad = Calendar.getInstance();
        calendarMayoriaEdad.set(calendarMayoriaEdad.get(Calendar.YEAR)-18, calendarMayoriaEdad.get(Calendar.MONTH), calendarMayoriaEdad.get(Calendar.DAY_OF_MONTH));
        fechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager.isAcceptingText())
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                fechaNacimiento.setFocusableInTouchMode(false);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Registro.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        fechaNacimiento.setText(date);
                    }
                }, calendarMayoriaEdad.get(Calendar.YEAR), calendarMayoriaEdad.get(Calendar.MONTH), calendarMayoriaEdad.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(calendarMayoriaEdad.getTimeInMillis());
                datePickerDialog.show();
            }
            });
    }

    private void initMaps() {
        autoCompleteTextView = findViewById(R.id.places_autocomplete_edit_text);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            autoCompleteTextView.setText(selection);
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    autocompletePlace(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    private void searchDirection(){
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.places_autocomplete_edit_text);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(token)
                            .setQuery(s.toString())
                            .build();

                    placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                        ArrayList<String> suggestions = new ArrayList<>();
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            suggestions.add(prediction.getFullText(null).toString());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Registro.this,
                                android.R.layout.simple_dropdown_item_1line, suggestions);
                        autoCompleteTextView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void autocompletePlace(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .setCountry("CO")
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
            ArrayList<String> suggestions = new ArrayList<>();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                suggestions.add(prediction.getFullText(null).toString());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Registro.this,
                    android.R.layout.simple_dropdown_item_1line, suggestions);
            autoCompleteTextView.setAdapter(adapter);
        }).addOnFailureListener(exception -> {
            exception.printStackTrace();
        });
    }

    private String getAPIKey(){
        String apiKey = null;
        try {
            Bundle bundle = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            apiKey = bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            recreate();
        }
        if (apiKey == null) {
            recreate();
        }
        return apiKey;
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
        EditText direccion = (EditText) findViewById(R.id.places_autocomplete_edit_text);
        Spinner genero = (Spinner) findViewById(R.id.spinner2);
        RadioGroup rol = (RadioGroup) findViewById(R.id.rol);
        TextInputEditText fechaNacimiento = (TextInputEditText) findViewById(R.id.fechaNacimientoRegistro);
        TextInputEditText confirmarContrasena = (TextInputEditText) findViewById(R.id.confirmContrasenaRegistro);
        nombre.setError(null);
        apellidos.setError(null);
        cedula.setError(null);
        email.setError(null);
        contra.setError(null);
        fechaNacimiento.setError(null);
        confirmarContrasena.setError(null);
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
        if(genero.getSelectedItemPosition() < 0 ) {
            Toast.makeText(this, "Eliga un genero", Toast.LENGTH_LONG).show();
            return;
        }
        if(fechaNacimiento.getText().toString().isEmpty()) {
            fechaNacimiento.setFocusableInTouchMode(true);
            fechaNacimiento.setError("Debe ingresar una fecha de nacimiento valida");
            fechaNacimiento.requestFocus();
            return;
        }
        if(confirmarContrasena.getText().toString().isEmpty()) {
            confirmarContrasena.setError("Debe ingresar la confirmación de su la contraseña");
            confirmarContrasena.requestFocus();
            return;
        }
        if(!confirmarContrasena.getText().toString().equals(contra.getText().toString())) {
            confirmarContrasena.setError("La confirmación de la contraseña no coincide con la contraseña ingresada");
            confirmarContrasena.requestFocus();
            return;
        }
        if(autoCompleteTextView.getText().toString().isEmpty()) {
            autoCompleteTextView.setError("Debe ingresar una direccion valida");
            autoCompleteTextView.requestFocus();
            return;
        }
        int id = rol.getCheckedRadioButtonId();
        String rolElegido = ((RadioButton)findViewById(id)).getText().toString();
        verificarUsuario(cedula.getText().toString(), nombre.getText().toString(), apellidos.getText().toString(),
                tipoDocumento.getSelectedItem().toString(), email.getText().toString(), contra.getText().toString(), rolElegido,
                genero.getSelectedItem().toString(), autoCompleteTextView.getText().toString(), fechaNacimiento.getText().toString());

    }

    private void verificarUsuario(String cedula, String nombre, String apellidos, String tipoDocumento, String email, String contra, String rol, String genero, String direccion, String fechaNacimiento){
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
                        createUsuarioDocument(cedula, nombre, apellidos, tipoDocumento, email, contra, rol, genero, direccion, fechaNacimiento);
                    }
                } else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void createUsuarioDocument(String cedula, String nombre, String apellidos, String tipoDocumento, String email, String contra, String rol, String genero, String direccion, String fechaNacimiento) {
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("nombre", nombre);
        usuarioData.put("apellidos", apellidos);
        usuarioData.put("tipoDocumento", tipoDocumento);
        usuarioData.put("email", email);
        usuarioData.put("contra", contra);
        usuarioData.put("rol", rol);
        usuarioData.put("genero", genero);
        usuarioData.put("direccion", direccion);
        usuarioData.put("fechaNacimiento", fechaNacimiento);
        database.collection("usuarios").document(cedula).set(usuarioData);
        mainCarga();
    }

}