package co.edu.unipiloto.cargaexpress.ui.dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import co.edu.unipiloto.cargaexpress.Camion;
import co.edu.unipiloto.cargaexpress.MedicionCamion;
import co.edu.unipiloto.cargaexpress.MisCamiones;
import co.edu.unipiloto.cargaexpress.MostrarConductor;
import co.edu.unipiloto.cargaexpress.R;
import co.edu.unipiloto.cargaexpress.Usuario;
import co.edu.unipiloto.cargaexpress.carga_express;
import co.edu.unipiloto.cargaexpress.databinding.FragmentDashboardBinding;

public class Dashboard extends Fragment {

    private List<String> placas;
    private List<MedicionCamion> camiones;
    private FragmentDashboardBinding binding;
    private FirebaseFirestore database;
    private Usuario user;
    private List<String> years;
    private List<String> categorias;
    View rootView;

    public Dashboard() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        database = FirebaseFirestore.getInstance();
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        user = carga_express.user;
        categorias = new ArrayList<>();
        if(user.getRol().equals("Propietario de camion")) {
            placas = new ArrayList<>();
            camiones = new ArrayList<>();
            categorias.add("Cantidad de envios");
            categorias.add("Distancia recorrida");
            categorias.add("Combustible consumido");
            categorias.add("Tiempo de envios");
            categorias.add("Toneladas transportadas");
            obtenerPlacas();
            getAnios();
            adaptadorSpinner(binding.anio, years, "anios");
            adaptadorSpinner(binding.categoria, categorias, "categorias");
            iniciarTabla(binding);
        } else if (user.getRol().equals("Comerciante")) {
            binding.placaElec.setVisibility(View.INVISIBLE);
            categorias.add("Cantidad de publicaciones");
            categorias.add("Publicaciones entregadas");
            categorias.add("Publicaciones no finalizadas");
            categorias.add("Numero de incidencias");
            categorias.add("Publicaciones con incidentes");
            getAnios();
            adaptadorSpinner(binding.anio, years, "anios");
            adaptadorSpinner(binding.categoria, categorias, "categorias");
            iniciarTabla(binding);
        }
        return binding.getRoot();
    }

    private void iniciarTabla(FragmentDashboardBinding binding) {
        Spinner spinner = binding.spinner3;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (user.getRol().equals("Propietario de camion")) {
                    if (camiones != null && !camiones.isEmpty() && binding.placaElec.getSelectedItemPosition() != -1) {
                        String placa = binding.placaElec.getSelectedItem().toString();
                        int mes = spinner.getSelectedItemPosition();
                        int pos = placas.indexOf(placa) - 1;
                        MedicionCamion def = null;
                        for (MedicionCamion temp : camiones) {
                            if (temp.getPlaca().equals(placa.toUpperCase().replaceAll(" ", "") + binding.anio.getSelectedItem().toString()))
                                def = temp;
                        }
                        if (def != null && mes < def.getDatos().size()) {
                            TextView temp = binding.numeroViajes;
                            temp.setText(def.getDatos().get(mes).get(0) + " (unidad)");
                            temp = binding.distanciaRecorrida;
                            temp.setText(def.getDatos().get(mes).get(1) + " (kilometros)");
                            temp = binding.combustible;
                            temp.setText(def.getDatos().get(mes).get(2) + " (galones)");
                            temp = binding.tiempoViaje;
                            temp.setText(def.getDatos().get(mes).get(3) + " (horas)");
                            temp = binding.numeroToneladas;
                            temp.setText(def.getDatos().get(mes).get(4) + " (toneladas)");
                            return;
                        } else {
                            TextView temp = binding.numeroViajes;
                            temp.setText("");
                            temp = binding.distanciaRecorrida;
                            temp.setText("");
                            temp = binding.combustible;
                            temp.setText("");
                            temp = binding.tiempoViaje;
                            temp.setText("");
                            temp = binding.numeroToneladas;
                            temp.setText("");
                            return;
                        }
                    }

                } else if (user.getRol().equals("Comerciante")) {
                    String placa = user.getCedula();
                    int mes = spinner.getSelectedItemPosition();
                    MedicionCamion def = null;
                    for (MedicionCamion temp : camiones) {
                        if (temp.getPlaca().equals(placa+binding.anio.getSelectedItem().toString()))
                            def = temp;
                    }
                    if (def != null && mes < def.getDatos().size()) {
                        TextView temp = binding.numeroViajes;
                        temp.setText(def.getDatos().get(mes).get(0) + " (unidad)");
                        temp = binding.distanciaRecorrida;
                        temp.setText(def.getDatos().get(mes).get(1) + " (unidad)");
                        temp = binding.combustible;
                        temp.setText(def.getDatos().get(mes).get(2) + " (unidad)");
                        temp = binding.tiempoViaje;
                        temp.setText(def.getDatos().get(mes).get(3) + " (unidad)");
                        temp = binding.numeroToneladas;
                        temp.setText(def.getDatos().get(mes).get(4) + " (unidad)");
                        return;
                    } else {
                        TextView temp = binding.numeroViajes;
                        temp.setText("");
                        temp = binding.distanciaRecorrida;
                        temp.setText("");
                        temp = binding.combustible;
                        temp.setText("");
                        temp = binding.tiempoViaje;
                        temp.setText("");
                        temp = binding.numeroToneladas;
                        temp.setText("");
                        return;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void adaptadorSpinner(Spinner spinner, List<String> elementos, String elemento) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>( requireContext(), android.R.layout.simple_spinner_item, elementos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((elemento.equals("placas") || elemento.equals("categorias")) && (!camiones.isEmpty()) && camiones != null) {
                    if(user.getRol().equals("Propietario de camion")) {
                        int indexCam;
                        if (binding.placaElec.getSelectedItemPosition() == -1)
                            indexCam = -1;
                        else
                            indexCam = placas.indexOf(binding.placaElec.getSelectedItem().toString()) - 1;
                        construir(binding.placaElec.getSelectedItem().toString(), binding.categoria.getSelectedItemPosition());
                    } else if(user.getRol().equals("Comerciante")) {
                        construir(user.getCedula(), binding.categoria.getSelectedItemPosition());
                    }

                }
                else if ((user.getRol().equals("Comerciante") || (!placas.isEmpty() && placas != null)) && elemento.equals("anios")) {
                    Toast.makeText(requireContext(), "Entro en anios", Toast.LENGTH_SHORT).show();
                    String elegido = binding.anio.getSelectedItem().toString();
                    obtenerDatos(elegido);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //spinner.setSelection(0);
            }
        });
    }

    private void obtenerDatos(String anio) {

        camiones = new ArrayList<>();
        Query query = null;
        if(user.getRol().equals("Propietario de camion")) {
            List<String> busquedas = new ArrayList<>();
            for (String placa : placas) {
                String busqueda = placa + anio;
                busqueda = busqueda.replaceAll(" ", "").toUpperCase();
                busquedas.add(busqueda);
            }

            query = database.collection("estadisicas").whereIn(FieldPath.documentId(), busquedas);
        } else if (user.getRol().equals("Comerciante"))
            query = database.collection("estadisicas").whereEqualTo(FieldPath.documentId(), user.getCedula()+anio);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String actual="";
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for(QueryDocumentSnapshot document : querySnapshot) {
                            List<List<Long>> datos = new ArrayList<>();
                            actual = document.getId();
                            if(document.contains("enero"))
                                datos.add((List<Long>) document.getData().get("enero"));
                            if(document.contains("febrero"))
                                datos.add((List<Long>) document.getData().get("febrero"));
                            if(document.contains("marzo"))
                                datos.add((List<Long>) document.getData().get("marzo"));
                            if(document.contains("abril"))
                                datos.add((List<Long>) document.getData().get("abril"));
                            if(document.contains("mayo"))
                                datos.add((List<Long>) document.getData().get("mayo"));
                            if(document.contains("junio"))
                                datos.add((List<Long>) document.getData().get("junio"));
                            if(document.contains("julio"))
                                datos.add((List<Long>) document.getData().get("julio"));
                            if(document.contains("agosto"))
                                datos.add((List<Long>) document.getData().get("agosto"));
                            if(document.contains("septiembre"))
                                datos.add((List<Long>) document.getData().get("septiembre"));
                            if(document.contains("octubre"))
                                datos.add((List<Long>) document.getData().get("octubre"));
                            if(document.contains("noviembre"))
                                datos.add((List<Long>) document.getData().get("noviembre"));
                            if(document.contains("diciembre"))
                                datos.add((List<Long>) document.getData().get("diciembre"));
                            camiones.add(new MedicionCamion(actual, anio, datos));
                            if(camiones.size() == 1) {
                                Toast.makeText(requireContext(), "Construido", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(requireContext(), "El usuario no se encuentra registrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void construir(String placa, int categoria) {
        BarChart barChart = binding.barChart;
            String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            MedicionCamion camion = null;
            for (MedicionCamion temp : camiones) {
                if (temp.getPlaca().equals(placa.toUpperCase().replaceAll(" ", "")+binding.anio.getSelectedItem().toString())) {
                    camion = temp;
                    break;
                }
            }
            if(camion == null) {
                barChart.setVisibility(View.INVISIBLE);
                return;
            }
            barChart.removeAllViews();
            List<BarEntry> entries = new ArrayList<>();
            for (int i = 0; i < camion.getDatos().size(); i++) {
                entries.add(new BarEntry(i + 1, camion.getDatos().get(i).get(categoria)));
            }

            BarDataSet barDataSet = new BarDataSet(entries, "Mediciones");
            barDataSet.setColor(Color.rgb(0, 155, 0)); // Color de las barras

            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);

            barChart.getDescription().setEnabled(false); // Deshabilitar la descripción

            // Establecer etiquetas de los meses en el eje X
            XAxis xAxis = barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(meses));
            if(barChart.getVisibility() == View.INVISIBLE)
                barChart.setVisibility(View.VISIBLE);

            barChart.invalidate();
    }

    private void obtenerPlacas() {
        Query query = database.collection("camiones").whereEqualTo("propietario", Integer.parseInt(user.getCedula()));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        for(QueryDocumentSnapshot document : querySnapshot) {
                            placas.add(document.getId());
                        }
                        adaptadorSpinner(binding.placaElec, placas, "placas");
                        obtenerDatos(Calendar.getInstance().get(Calendar.YEAR)+"");
                    } else {
                        Toast.makeText(requireContext(), "El usuario no se encuentra registrado", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d("MisCamiones", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void getAnios() {
        years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int year = currentYear; year >= 2023; year--) {
            years.add(""+year);
        }
    }
}