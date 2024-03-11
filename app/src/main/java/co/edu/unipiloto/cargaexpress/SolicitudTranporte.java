package co.edu.unipiloto.cargaexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SolicitudTranporte extends AppCompatActivity {

    private FirebaseFirestore database;
    private String cedComerciante = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_tranporte);
        database = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        cedComerciante = intent.getStringExtra("documento");

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
    }




    public void publicarSolicitud (View view){
        Spinner tipoCarga = (Spinner) findViewById(R.id.tipoCarga);
        TextInputEditText peso = (TextInputEditText) findViewById(R.id.peso);
        TextInputEditText alto = (TextInputEditText) findViewById(R.id.alto);
        TextInputEditText ancho = (TextInputEditText) findViewById(R.id.ancho);
        TextInputEditText profundidad = (TextInputEditText) findViewById(R.id.profundidad);
        TextInputEditText direccionOrigen = (TextInputEditText) findViewById(R.id.direccionOrigen);
        TextInputEditText ciudadOrigen = (TextInputEditText) findViewById(R.id.ciudadOrigen);
        TextInputEditText direccionDestino = (TextInputEditText) findViewById(R.id.direccionDestino);
        TextInputEditText ciudadDestino = (TextInputEditText) findViewById(R.id.ciudadDestino);
        TextInputEditText fechaRecogida = (TextInputEditText) findViewById(R.id.fechaRecogida);
        TextInputEditText horaRecogida = (TextInputEditText) findViewById(R.id.horaRecogida);
        TextInputEditText fechaEntrega = (TextInputEditText) findViewById(R.id.fechaEntrega);
        EditText especificaciones = (EditText) findViewById(R.id.especificaciones);

        peso.setError(null);
        alto.setError(null);
        ancho.setError(null);
        profundidad.setError(null);
        direccionOrigen.setError(null);
        ciudadOrigen.setError(null);
        direccionDestino.setError(null);
        ciudadDestino.setError(null);
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
        if (direccionOrigen.getText().toString().isEmpty()){
            direccionOrigen.setError("Debe ingresar una dirección de origen valida");
            direccionOrigen.requestFocus();
            return;
        }
        if (ciudadOrigen.getText().toString().isEmpty()){
            ciudadOrigen.setError("Debe ingresar una ciudad de origen valida");
            ciudadOrigen.requestFocus();
            return;
        }
        if (direccionDestino.getText().toString().isEmpty()){
            direccionDestino.setError("Debe ingresar una dirección de destino valida");
            direccionDestino.requestFocus();
            return;
        }
        if (ciudadDestino.getText().toString().isEmpty()){
            ciudadDestino.setError("Debe ingresar una ciudad de origen valida");
            ciudadDestino.requestFocus();
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

        crearCargaDocument(tipoCarga.getSelectedItem().toString(), Integer.parseInt(peso.getText().toString()), (alto.getText().toString() +" X "+ ancho.getText().toString() +" X "+ profundidad.getText().toString()),
                direccionOrigen.getText().toString(), ciudadOrigen.getText().toString(), direccionDestino.getText().toString(), ciudadDestino.getText().toString(), fechaPublicacion, fechaRecogida.getText().toString(), horaRecogida.getText().toString(),
                fechaEntrega.getText().toString(), especificaciones.getText().toString(), Integer.parseInt(cedComerciante), 0);
        misPublicacionesView(view);
    }

    private void crearCargaDocument (String tipoCarga, int peso, String dimensiones, String direccionOrigen, String ciudadOrigen, String direccionDestino, String ciudadDestino, String fechaPublicada, String fechaRecogida, String horaRecogida, String fechaEntrega, String especificaciones, int cedulaComerciante, int cedulaConductor){
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
                                        int tam = querySnapshot.size();
                                        database.collection("cargas").document(cedComerciante + "-" + (tam + 1)).set(cargaData);
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
        Intent intent = new Intent(this, MisPublicaciones.class);
        intent.putExtra("documento", cedComerciante);
        startActivity(intent);
    }
}
