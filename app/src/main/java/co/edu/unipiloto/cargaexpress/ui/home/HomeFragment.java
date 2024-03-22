package co.edu.unipiloto.cargaexpress.ui.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import co.edu.unipiloto.cargaexpress.R;
import co.edu.unipiloto.cargaexpress.Usuario;
import co.edu.unipiloto.cargaexpress.carga_express;
import co.edu.unipiloto.cargaexpress.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private static Usuario user;
    private FirebaseFirestore database;

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
    }

    private void traerDB(String tabla, String columna, String fila) {
        Query query;
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

    private void crearLista(QuerySnapshot querySnapshot){
        List<DocumentSnapshot> documentList = querySnapshot.getDocuments();
        ConstraintLayout constraintLayout = binding.layout;

        int lastAddedViewId = ConstraintLayout.NO_ID;

        // Iterar sobre los documentos y crear un card para cada uno
        for (int i = 0; i < documentList.size(); i++) {
            DocumentSnapshot document = documentList.get(i);
            View cardView = LayoutInflater.from(requireContext()).inflate(R.layout.card, constraintLayout, false);
            cardView.setId(View.generateViewId());


            TextView tipoCargaTextView = cardView.findViewById(R.id.textView26);
            TextView pesoTextView = cardView.findViewById(R.id.textView28);
            TextView ciudadOrigenTextView = cardView.findViewById(R.id.textView32);
            TextView ciudadDestinoTextView = cardView.findViewById(R.id.textView31);
            TextView fechaRecogidaTextView = cardView.findViewById(R.id.textView34);

            tipoCargaTextView.setText(document.getString("tipoCarga"));
            pesoTextView.setText(String.valueOf(document.getLong("peso")));
            ciudadOrigenTextView.setText(document.getString("ciudadOrigen"));
            ciudadDestinoTextView.setText(document.getString("ciudadDestino"));
            fechaRecogidaTextView.setText(document.getString("fechaRecogida"));

            // Configurar las restricciones del card
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) cardView.getLayoutParams();
            layoutParams.topToBottom = lastAddedViewId != ConstraintLayout.NO_ID ? lastAddedViewId : ConstraintLayout.LayoutParams.MATCH_PARENT;

            // Agregar el card al ConstraintLayout
            constraintLayout.addView(cardView, layoutParams);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Carga carga = new Carga(document.getId(), document.getString("tipoCarga"), document.getLong("peso"), document.getString("dimensiones"), document.getString("direccionOrigen"),
                            document.getString("ciudadOrigen"), document.getString("direccionDestino"), document.getString("ciudadDestino"), document.getString("fechaPublicacion"), document.getString("fechaRecogida"),
                            document.getString("horaRecogida"), document.getString("fechaEntrega"), document.getString("especificaciones"), document.getLong("comerciante"), document.getLong("conductor"));
                    Intent intent = new Intent(requireContext(), AplicarCarga.class);
                    intent.putExtra("user", user);
                    intent.putExtra("carga", carga);
                    startActivity(intent);

                }
            });

            lastAddedViewId = cardView.getId();
        }
    }

}