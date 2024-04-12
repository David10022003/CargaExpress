package co.edu.unipiloto.cargaexpress.ui.login;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import co.edu.unipiloto.cargaexpress.Ingreso;
import co.edu.unipiloto.cargaexpress.Registro;
import co.edu.unipiloto.cargaexpress.Usuario;
import co.edu.unipiloto.cargaexpress.carga_express;
import co.edu.unipiloto.cargaexpress.databinding.FragmentIngresoBinding;

import co.edu.unipiloto.cargaexpress.R;
import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;

public class IngresoFragment extends Fragment {

    private FragmentIngresoBinding binding;
    private FirebaseFirestore database;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Usuario user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentIngresoBinding.inflate(inflater, container, false);
       return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        database = FirebaseFirestore.getInstance();
        if(!(preferences.getString("user", "").equals("")) && !(preferences.getString("password", "").equals("")))
            generarIngreso(preferences.getString("user", ""), preferences.getString("password", ""));

        final EditText cedula = binding.cedulaIngreso;
        final EditText password = binding.contrasenaIngreso;
        final Button loginButton = binding.buttonLogin;
        final Button registro = binding.buttonCrearCuenta;

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), Registro.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresar();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    public void ingresar() {
        EditText cedula = binding.cedulaIngreso;
        EditText contra = binding.contrasenaIngreso;
        cedula.setError(null);
        contra.setError(null);
        if(cedula.getText().toString().isEmpty()) {
            cedula.setError("Debe ingresar un numero de cedula valido");
            cedula.requestFocus();
            return;
        }
        if(contra.getText().toString().isEmpty()) {
            contra.setError("Debe ingresar una contraseña valida");
            contra.requestFocus();
            return;
        }

        generarIngreso(cedula.getText().toString(), contra.getText().toString());

    }

    private void generarIngreso(String cedula, String password) {
        String texto = "El usuario no se encuentra registrado";
        Query query = database.collection("usuarios").whereEqualTo(FieldPath.documentId(), cedula).whereEqualTo("contra", password);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        user = new Usuario(document.getId(), document.getString("nombre"), document.getString("apellidos"),
                                document.getString("tipoDocumento"), document.getString("email"), document.getString("contra"), document.getString("rol"), document.getString("genero"), document.getString("fechaNacimiento"));
                        acceder();
                    } else {
                        Toast.makeText(requireContext(), "El usuario no se encuentra registrado", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                }
            }
        });
    }

    private void acceder() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_carga_express);
        navController.navigate(R.id.nav_home, bundle);
        ((carga_express) getActivity()).iniciarSesion(user);
        guardarSesion(user.getCedula(), user.getContra());
    }

    private void guardarSesion(String cedula, String password) {
        editor.putString("user", cedula);
        editor.putString("password", password);
        editor.apply();
    }

}