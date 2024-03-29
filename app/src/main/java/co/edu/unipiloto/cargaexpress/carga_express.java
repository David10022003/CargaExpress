package co.edu.unipiloto.cargaexpress;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import co.edu.unipiloto.cargaexpress.databinding.ActivityCargaExpressBinding;
import co.edu.unipiloto.cargaexpress.ui.acount.AcountFragment;
import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;
import co.edu.unipiloto.cargaexpress.ui.login.IngresoFragment;


public class carga_express extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityCargaExpressBinding binding;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore database;
    public static Usuario user;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCargaExpressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarCargaExpress.toolbar);
        toolbar = binding.appBarCargaExpress.toolbar;
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
              R.id.nav_home)
            .setOpenableLayout(drawer)
              .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_carga_express);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        preferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = preferences.edit();
        database = FirebaseFirestore.getInstance();
        FirebaseApp.initializeApp(this);

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
    boolean userLoaded;
    public void iniciarSesion(Usuario user){
        this.user = user;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_carga_express);
        iniComponents();
        MyFirebaseMessagingService.guardarTokenIndividual(this.user.getCedula());
        MyFirebaseMessagingService.guardarToken(this.user.getRol());
        MyFirebaseMessagingService.guardarToken("Todos");
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

    public void salir(View view){
        MyFirebaseMessagingService.eliminarToken(user.getRol());
        MyFirebaseMessagingService.eliminarToken("Todos");
        user = null;
        editor.remove("user");
        editor.remove("password");
        editor.apply();
        DrawerLayout drawer = binding.drawerLayout;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_carga_express);
        navController.popBackStack();
        navController.navigate(R.id.nav_ingreso);
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

}