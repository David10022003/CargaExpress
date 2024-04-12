package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SolicitudTranporte extends AppCompatActivity {

    private FirebaseFirestore database;
    private String cedComerciante = "";
    private PlacesClient placesClient;
    private AutoCompleteTextView origen, destino;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_tranporte);
        database = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        cedComerciante = intent.getStringExtra("documento");

        if(!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getAPIKey());
        }
        placesClient = Places.createClient(this);

        TextInputEditText fechaRecogida = (TextInputEditText) findViewById(R.id.fechaRecogida);
        fechaRecogida.setFocusableInTouchMode(false);
        Calendar calendarActual = Calendar.getInstance();
        calendarActual.set(calendarActual.get(Calendar.YEAR), calendarActual.get(Calendar.MONTH), calendarActual.get(Calendar.DAY_OF_MONTH)+1);
        fechaRecogida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager.isAcceptingText())
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                fechaRecogida.setFocusableInTouchMode(false);
                DatePickerDialog datePickerDialog = new DatePickerDialog(SolicitudTranporte.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = String.format("%02d/%02d/%d",dayOfMonth, month+1, year);
                        fechaRecogida.setText(date);
                    }
                }, calendarActual.get(Calendar.YEAR), calendarActual.get(Calendar.MONTH), calendarActual.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendarActual.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        TextInputEditText fechaEntrega = (TextInputEditText) findViewById(R.id.fechaEntrega);
        fechaEntrega.setFocusableInTouchMode(false);
        Calendar calendarPost = Calendar.getInstance();
        calendarPost.set(calendarPost.get(Calendar.YEAR), calendarPost.get(Calendar.MONTH), calendarPost.get(Calendar.DAY_OF_MONTH)+1);
        fechaEntrega.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager.isAcceptingText())
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                fechaEntrega.setFocusableInTouchMode(false);
                if (!fechaRecogida.getText().toString().isEmpty()) {
                    String[] fechaMin = fechaRecogida.getText().toString().split("/");
                    calendarPost.set(Integer.parseInt(fechaMin[2]), Integer.parseInt(fechaMin[1]) - 1, Integer.parseInt(fechaMin[0]));
                }
                DatePickerDialog datePickerDialog = new DatePickerDialog(SolicitudTranporte.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String fecha = String.format("%02d/%02d/%d", dayOfMonth, month+1, year);
                        fechaEntrega.setText(fecha);
                    }
                }, calendarPost.get(Calendar.YEAR), calendarPost.get(Calendar.MONTH), calendarPost.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(calendarPost.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        TextInputEditText horaRecogida = (TextInputEditText) findViewById(R.id.horaRecogida);
        horaRecogida.setFocusableInTouchMode(false);
        Calendar calendar = Calendar.getInstance();

        horaRecogida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager.isAcceptingText())
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                horaRecogida.setFocusableInTouchMode(false);
                TimePickerDialog timePickerDialog = new TimePickerDialog(SolicitudTranporte.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hora = String.format("%02d:%02d", hourOfDay, minute);
                        horaRecogida.setText(hora);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                Calendar minTime = Calendar.getInstance();
                minTime.set(Calendar.HOUR_OF_DAY, 6);
                //timePickerDialog.setMinTime(minTime.getTimeInMillis());
                Calendar maxTime = Calendar.getInstance();
                maxTime.set(Calendar.HOUR_OF_DAY, 21);
                //timePickerDialog.setMaxTime(maxTime.getTimeInMillis());
                timePickerDialog.show();
            }
        });
        origen = findViewById(R.id.direccionOrigen);
        destino = findViewById(R.id.direccionDestino);
        
        initMaps(origen);
        initMaps(destino);
    }

    private void initMaps(AutoCompleteTextView autoCompleteTextView) {
        
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
                    autocompletePlace(s.toString(), autoCompleteTextView);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

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


    private void autocompletePlace(String query, AutoCompleteTextView autoCompleteTextView) {
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SolicitudTranporte.this,
                    android.R.layout.simple_dropdown_item_1line, suggestions);
            autoCompleteTextView.setAdapter(adapter);
        }).addOnFailureListener(exception -> {
            exception.printStackTrace();
        });
    }





    public void publicarSolicitud (View view){
        Spinner tipoCarga = (Spinner) findViewById(R.id.tipoCarga);
        TextInputEditText peso = (TextInputEditText) findViewById(R.id.peso);
        TextInputEditText alto = (TextInputEditText) findViewById(R.id.alto);
        TextInputEditText ancho = (TextInputEditText) findViewById(R.id.ancho);
        TextInputEditText profundidad = (TextInputEditText) findViewById(R.id.profundidad);
        TextInputEditText fechaRecogida = (TextInputEditText) findViewById(R.id.fechaRecogida);
        TextInputEditText horaRecogida = (TextInputEditText) findViewById(R.id.horaRecogida);
        TextInputEditText fechaEntrega = (TextInputEditText) findViewById(R.id.fechaEntrega);
        EditText especificaciones = (EditText) findViewById(R.id.especificaciones);

        peso.setError(null);
        alto.setError(null);
        ancho.setError(null);
        profundidad.setError(null);
        origen.setError(null);
        destino.setError(null);
        fechaRecogida.setError(null);
        horaRecogida.setError(null);
        fechaEntrega.setError(null);
        especificaciones.setError(null);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if(peso.getText().toString().isEmpty()){
            peso.setError("Debe ingresar un peso valido");
            peso.requestFocus();
            return;
        }
        if(alto.getText().toString().isEmpty()){
            alto.setError("Debe ingresar un alto valido");
            alto.requestFocus();
            return;
        }
        if(ancho.getText().toString().isEmpty()){
            ancho.setError("Debe ingresar un ancho valido");
            ancho.requestFocus();
            return;
        }
        if (profundidad.getText().toString().isEmpty()){
            profundidad.setError("Debe ingresar una profundidad valida");
            profundidad.requestFocus();
            return;
        }
        if (origen.getText().toString().isEmpty()){
            origen.setError("Debe ingresar una dirección de origen valida");
            origen.requestFocus();
            return;
        }
        if (destino.getText().toString().isEmpty()){
            destino.setError("Debe ingresar una dirección de destino valida");
            destino.requestFocus();
            return;
        }
        if (fechaRecogida.getText().toString().isEmpty()){
            fechaRecogida.setFocusableInTouchMode(true);
            fechaRecogida.setError("Debe ingresar una fecha de recogida valida");
            fechaRecogida.requestFocus();
            return;
        }
        if (horaRecogida.getText().toString().isEmpty()){
            horaRecogida.setFocusableInTouchMode(true);
            horaRecogida.setError("Debe ingresar una hora de recogida valida");
            horaRecogida.requestFocus();
            return;
        }
        if (fechaEntrega.getText().toString().isEmpty()){
            fechaEntrega.setFocusableInTouchMode(true);
            fechaEntrega.setError("Debe ingresar una fecha de entrega valida");
            fechaEntrega.requestFocus();
            return;
        }

        Calendar dateRecogida = Calendar.getInstance();
        dateRecogida.set(Integer.parseInt(fechaRecogida.getText().toString().substring(6)), Integer.parseInt(fechaRecogida.getText().toString().substring(3,5)), Integer.parseInt(fechaRecogida.getText().toString().substring(0,2)));
        Calendar dateEntrega = Calendar.getInstance();
        dateEntrega.set(Integer.parseInt(fechaEntrega.getText().toString().toString().substring(6)), Integer.parseInt(fechaEntrega.getText().toString().substring(3,5)), Integer.parseInt(fechaEntrega.getText().toString().substring(0,2)));
        if(dateEntrega.before(dateRecogida)){
            fechaEntrega.setFocusableInTouchMode(true);
            fechaEntrega.setError("Debe ingresar una fecha de entrega posterior a la fecha de recogida");
            fechaEntrega.requestFocus();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaPublicacion = dateFormat.format(new Date());
        
        String [] temp = origen.getText().toString().split(",") ;
        String [] temp1 = destino.getText().toString().split(",");
        double [] coorOrigen;
        if((coorOrigen = obtenerCoordenadas(origen.getText().toString())) == null) {
            origen.setError("Debe ingresar una dirección de origen valida");
            origen.requestFocus();
            return;
        }

        if((obtenerCoordenadas(destino.getText().toString())) == null) {
            origen.setError("Debe ingresar una dirección de destino valida");
            origen.requestFocus();
            return;
        }

        crearCargaDocument(tipoCarga.getSelectedItem().toString(), Integer.parseInt(peso.getText().toString()), (alto.getText().toString() +" X "+ ancho.getText().toString() +" X "+ profundidad.getText().toString()),
                temp[0], temp[1]+"-"+temp[2], temp1[0], temp1[1]+"-"+temp1[2], fechaPublicacion, fechaRecogida.getText().toString(), horaRecogida.getText().toString(),
                fechaEntrega.getText().toString(), especificaciones.getText().toString(), Integer.parseInt(cedComerciante), 0,
                coorOrigen[0], coorOrigen[1]);
        misPublicacionesView(view);
    }

    private double[] obtenerCoordenadas(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (p1 != null) {
            // Utiliza las coordenadas obtenidas
            return new double[] { p1.latitude,  p1.longitude};
        }
        return null;
    }

    private void crearCargaDocument (String tipoCarga, int peso, String dimensiones, String direccionOrigen, String ciudadOrigen, String direccionDestino, String ciudadDestino, String fechaPublicada, String fechaRecogida, String horaRecogida, String fechaEntrega, String especificaciones, int cedulaComerciante, int cedulaConductor,
                                     double latitud, double longitud){
        Map<String, Object> cargaData = new HashMap<>();
        cargaData.put("tipoCarga", tipoCarga);
        cargaData.put("peso", peso);
        cargaData.put("dimensiones", dimensiones);
        cargaData.put("direccionOrigen", direccionOrigen);
        cargaData.put("ciudadOrigen", ciudadOrigen);
        cargaData.put("direccionDestino", direccionDestino);
        cargaData.put("ciudadDestino", ciudadDestino);
        cargaData.put("fechaPublicacion", fechaPublicada);
        cargaData.put("fechaRecogida", fechaRecogida);
        cargaData.put("horaRecogida", horaRecogida);
        cargaData.put("fechaEntrega", fechaEntrega);
        cargaData.put("especificaciones", especificaciones);
        cargaData.put("comerciante", cedulaComerciante);
        cargaData.put("conductor", cedulaConductor);
        cargaData.put("latitud", latitud);
        cargaData.put("longitud", longitud);
        cargaData.put("estado", "publicado");

        CollectionReference cargasRef = database.collection("cargas");
        cargasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    if (snapshot != null && !snapshot.isEmpty()){
                        Query query = database.collection("cargas").whereEqualTo("comerciante", Integer.parseInt(cedComerciante));
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null) {
                                        String idCarga = cedComerciante + "-" + (querySnapshot.size() + 1);
                                        database.collection("cargas").document(idCarga).set(cargaData);

                                    }
                                } else
                                    Log.d("SolicitudTransporte", "Error getting documents: ", task.getException());
                            }
                        });
                    } else
                        database.collection("cargas").document(cedComerciante +"-1").set(cargaData);
                } else
                    Log.e("Firestore", "Error al obtener documentos: ", task.getException());
            }
        });
    }

    public void misPublicacionesView (View view) {
        finish();
    }
}
