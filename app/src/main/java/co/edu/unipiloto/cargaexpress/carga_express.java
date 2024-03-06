package co.edu.unipiloto.cargaexpress;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

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

    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCargaExpressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCargaExpress.toolbar);
        binding.appBarCargaExpress.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        NavigationView navigation = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ((TextView)headerView.findViewById(R.id.name)).setText(user.getNombre());
        ((TextView)headerView.findViewById(R.id.rol)).setText(user.getRol());
        Menu menu = navigationView.getMenu();

        Button opcional = (Button) navigation.findViewById(R.id.ver);
        if(user.getRol().equals("Conductor")){
            opcional.setVisibility(View.INVISIBLE);
        } else if(user.getRol().equals("Comerciante")) {
            opcional.setText("Mis publicaciones");
        }  if(user.getRol().equals("Propietario de camion")) {
            opcional.setText("Mis camiones");
        }
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
    
    public void verElementos(View view) {
        if(user.getRol().equals("Comerciante")){
            Intent intent = new Intent(this, MisPublicaciones.class);
        } else if (user.getRol().equals("Propietario de camion")) {
            Intent intent = new Intent(this, MisCamiones.class);

        }
    }
}