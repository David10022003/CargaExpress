package co.edu.unipiloto.cargaexpress.ui.login;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import co.edu.unipiloto.cargaexpress.Registro;
import co.edu.unipiloto.cargaexpress.SQLiteHelper;
import co.edu.unipiloto.cargaexpress.Usuario;
import co.edu.unipiloto.cargaexpress.carga_express;
import co.edu.unipiloto.cargaexpress.databinding.FragmentIngresoBinding;

import co.edu.unipiloto.cargaexpress.R;

public class IngresoFragment extends Fragment {

    private FragmentIngresoBinding binding;
    private FirebaseFirestore database;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Usuario user;
    private SQLiteHelper dbHelper;

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
        dbHelper = new SQLiteHelper(this.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        database = FirebaseFirestore.getInstance();

        /*preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        if(!(preferences.getString("user", "").equals("")) && !(preferences.getString("password", "").equals("")))
            generarIngreso(preferences.getString("user", ""), preferences.getString("password", ""));
        */

        boolean usuarioIngresado = false;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM usuario", null);
        if (cursor.moveToFirst()) {
            int rowCount = cursor.getInt(0);
            if (rowCount > 0)
                usuarioIngresado = true;
        }

        if (usuarioIngresado){
            cursor = db.query(
                    "usuario",    // La tabla que deseas consultar
                    null,               // Las columnas que deseas recuperar (todas)
                    null,               // No hay cláusula WHERE
                    null,               // No hay valores para la cláusula WHERE
                    null,               // No agrupar las filas
                    null,               // No filtrar por grupos de filas
                    null,               // No ordenar las filas
                    "1"                 // Limitar el resultado a 1 fila
            );

            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String user = cursor.getString(cursor.getColumnIndex("user"));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex("password"));
                generarIngreso(user, password);
       }
    }
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
        //editor.putString("user", cedula);
        //editor.putString("password", password);
        //editor.apply();


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("user", cedula);
        values.put("password", password);
        db.insert("usuario",null,values);
    }

}