package co.edu.unipiloto.cargaexpress;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MisCamiones extends AppCompatActivity {

    private String cedula = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_camiones);
        Intent intent = getIntent();
        cedula = intent.getStringExtra("documento");

    }

    public void agregarCamion(View view) {
        Intent intent = new Intent(this, registroCamion.class);
        intent.putExtra("documento", cedula);
        startActivity(intent);
    }
}