package co.edu.unipiloto.cargaexpress.ui.home;


import static androidx.core.content.ContextCompat.startForegroundService;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.cargaexpress.AplicarCarga;
import co.edu.unipiloto.cargaexpress.Carga;
import co.edu.unipiloto.cargaexpress.LocationService;
import co.edu.unipiloto.cargaexpress.R;
import co.edu.unipiloto.cargaexpress.Usuario;
import co.edu.unipiloto.cargaexpress.carga_express;
import co.edu.unipiloto.cargaexpress.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static Usuario user;
    private FirebaseFirestore database;

    private static List<Carga> cargas;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        database = FirebaseFirestore.getInstance();
        user = getArguments().getParcelable("user");
        cargar();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void cargar() {
        if (user.getRol().equals("Comerciante")) {
            traerDB("cargas", "comerciante", user.getCedula());

        }
        else if(user.getRol().equals("Propietario de camion")) {
            traerDB("cargas", "", "");
        }
        else if(user.getRol().equals("Conductor")) {
            traerDB("cargas", "conductor", user.getCedula());
        }
    }

    private void traerDB(String tabla, String columna, String fila) {
        Query query;
        cargas = new ArrayList();
        if(!fila.equals(""))
            query = database.collection(tabla).whereEqualTo(columna, Integer.parseInt(fila));
        else
            query = database.collection(tabla);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                          crearLista(querySnapshot);
                          Toast.makeText(HomeFragment.this.requireContext(), "Datos encontrados", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(HomeFragment.this.requireContext(), "Datos no encontrados", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    private void crearLista(QuerySnapshot querySnapshot) {
        RecyclerView recyclerView = binding.recyclerView;
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),1 );
        recyclerView.setLayoutManager(layoutManager);
        llenarLista(querySnapshot);
        AdapterRecyclerView adapterRecyclerView = new AdapterRecyclerView(requireContext(), cargas);
        recyclerView.setAdapter(adapterRecyclerView);

        if(cargas.isEmpty())
            Toast.makeText(requireContext(), "Error al mostrar cargas", Toast.LENGTH_LONG).show();
        else{
            adapterRecyclerView.setSearchList(cargas);
        }

    }

    private void llenarLista(QuerySnapshot query) {
        Carga temp;
        for(DocumentSnapshot result : query.getDocuments()) {
            temp = new Carga(result.getId(), result.getString("tipoCarga"),  result.getLong("peso"),
                    result.getString("dimensiones"), result.getString("direccionOrigen"),
                    result.getString("ciudadOrigen"), result.getString("direccionDestino"),
                    result.getString("ciudadDestino"), result.getString("fechaPublicacion"),
                    result.getString("fechaRecogida"), result.getString("horaRecogida"),
                    result.getString("fechaEntrega"), result.getString("especificaciones"),
                    result.getLong("comerciante"), result.getLong("conductor"), result.getString("estado"),
                    result.getDouble("latitud"), result.getDouble("longitud"));
            if(user.getRol().equals("Conductor"))
                if(temp.getEstado().equals("En viaje") || temp.getEstado().equals("Incidencia") || temp.getEstado().equals("En recorrido alterno"))
                    rastreo(temp);
            cargas.add(temp);

        }
    }


    public static List<Carga> getCargas() {
        return cargas;
    }

    public static void setCargas(Carga carga) {
        for (Carga temp: cargas) {
            if(temp.getCodigo().equals(carga.getCodigo()))
                cargas.set(cargas.indexOf(temp), carga);
        }
    }

    private void rastreo(Carga carga) {
        Intent serviceIntent = new Intent(requireContext(), LocationService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            serviceIntent.putExtra("carga", carga);
            startForegroundService(requireContext(), serviceIntent);
        }
    }
}