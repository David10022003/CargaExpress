package co.edu.unipiloto.cargaexpress;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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

import java.util.ArrayList;
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
    private static final int REQUEST_CODE = 100;


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
        solicitarPermisoNotificaciones();

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
        recreate();
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

    /*private void solicitarPermisoNotificaciones () {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 o superior, usar POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);


            }
        }
        else {
            // Android 12 o inferior, usar permiso alternativo
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NOTIFICATION_POLICY}, REQUEST_CODE);
        }
    }*/

    private void solicitarPermisoNotificaciones() {
        List<String> permissionsNeeded = new ArrayList<>();

        // Agregar permisos de ubicación si no se han concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        // Agregar permisos específicos de la versión de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 o superior, usar POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Android 12 o inferior, usar permiso alternativo
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
            }
        }

        // Si hay permisos que necesitan ser solicitados, pedirlos todos juntos
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            boolean permisoSolicitado = preferences.getBoolean("permisoNotificacionMostrado", false);
            if (grantResults.length == 0 || (grantResults[0] == PackageManager.PERMISSION_DENIED && !permisoSolicitado)) {
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View alert = inflater.inflate(R.layout.alert_dialog, null);
        builder.setView(alert);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();

        Button continuar = alert.findViewById(R.id.button4);
        continuar.setText(getResources().getString(R.string.continuar));
        continuar.setBackgroundColor(getResources().getColor(R.color.light_gray));
        continuar.setTextColor(getResources().getColor(R.color.blue_color));
        continuar.setTextSize(14);

        Button configuracion = alert.findViewById(R.id.button5);
        configuracion.setText(getResources().getString(R.string.ir_a_configuracion));
        configuracion.setTextSize(14);

        TextView titulo = alert.findViewById(R.id.textView35);
        titulo.setText("Carga Express no podrá enviarte notificaciones");
        titulo.setTextSize(18);

        TextView descripcion = new TextView(this);
        TextInputEditText editText = alert.findViewById(R.id.aplicar_camion);
        ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
        ViewGroup parentView = (ViewGroup) editText.getParent();
        int index = parentView.indexOfChild(editText);
        parentView.removeView(editText);
        parentView.addView(descripcion, index, layoutParams);
        descripcion.setText(getResources().getString(R.string.descripcion_permiso));
        descripcion.setTextSize(14);

        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                editor.putBoolean("permisoNotificacionMostrado", true);
                editor.apply();
            }
        });
        dialog.show();
    }
}