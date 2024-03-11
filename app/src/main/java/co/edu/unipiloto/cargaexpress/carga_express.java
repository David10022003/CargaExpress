package co.edu.unipiloto.cargaexpress;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.cargaexpress.databinding.ActivityCargaExpressBinding;


public class carga_express extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityCargaExpressBinding binding;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore database;
    public static Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCargaExpressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCargaExpress.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_acount)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_carga_express);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        preferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        database = FirebaseFirestore.getInstance();
        iniciarSesion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.carga_express, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_carga_express);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void iniciarSesion(){
        Intent intent;
        if(getIntent().hasExtra("user")) {
            user = getIntent().getParcelableExtra("user");
            guardarSesion(user.getCedula(), user.getContra());
            iniComponents();
            return;
        }
        if(this.preferences.getString("user", "").equals("") && this.preferences.getString("password", "").equals("")) {
            intent = new Intent(this, Ingreso.class);
            startActivity(intent);
        }
        else{
            acceso(this.preferences.getString("user", ""), this.preferences.getString("password", ""));

        }
    }
    
    public void verElementos(View view) {
        if(user.getRol().equals("Comerciante")){
            Intent intent = new Intent(this, MisPublicaciones.class);
            intent.putExtra("documento", user.getCedula());
            startActivity(intent);
        } else if (user.getRol().equals("Propietario de camion")) {
            Intent intent = new Intent(this, MisCamiones.class);
            intent.putExtra("documento", user.getCedula());
            startActivity(intent);

        }
    }

    private void acceso(String cedula, String password) {
        Query query = database.collection("usuarios").whereEqualTo(FieldPath.documentId(), cedula).whereEqualTo("contra", password);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        user = new Usuario(document.getId(), document.getString("nombre"), document.getString("apellidos"),
                                document.getString("tipoDocumento"), document.getString("email"), document.getString("contra"), document.getString("rol"));
                        iniComponents();

                    }
                    else{
                            Intent intent = new Intent(carga_express.this, Ingreso.class);
                            startActivity(intent);
                    }

                }
                else{
                    Intent intent = new Intent(carga_express.this, Ingreso.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void botonRoles(NavigationView navigation) {
        Button opcional = (Button) navigation.findViewById(R.id.ver);
        if(user.getRol().equals("Conductor")){
            opcional.setVisibility(View.INVISIBLE);
        } else if(user.getRol().equals("Comerciante")) {
            opcional.setText("Mis publicaciones");
        }  if(user.getRol().equals("Propietario de camion")) {
            opcional.setText("Mis camiones");
        }
    }

    private void iniComponents() {
        NavigationView navigation = findViewById(R.id.nav_view);
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        ((TextView)headerView.findViewById(R.id.name)).setText(user.getNombre());
        ((TextView)headerView.findViewById(R.id.rol)).setText(user.getRol());
        Menu menu = navigationView.getMenu();
        botonRoles(navigation);
    }

    private void guardarSesion(String cedula, String password) {
        editor.putString("user", cedula);
        editor.putString("password", password);
        editor.apply();
    }

}