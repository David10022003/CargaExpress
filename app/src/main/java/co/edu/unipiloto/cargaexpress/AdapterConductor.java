package co.edu.unipiloto.cargaexpress;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.unipiloto.cargaexpress.ui.home.HomeFragment;

public class AdapterConductor extends RecyclerView.Adapter<co.edu.unipiloto.cargaexpress.MyAdapterConductor>{

    private Context context;
    private List<Usuario> users;
    private List<Camion> camions;
    private FirebaseFirestore database;
    private Carga carga;

    private int position;

    public  void setSearchList(List<Usuario> users, List<Camion> camions, Carga carga) {
        this.users = users;
        this.carga = carga;
        this.camions = camions;
        notifyDataSetChanged();
    }

    public  AdapterConductor(Context context, List<Usuario> users, List<Camion> camions) {
        this.context = context;
        this.users = users;
        this.camions = camions;
        database = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public co.edu.unipiloto.cargaexpress.MyAdapterConductor onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conductor_card, parent, false);
        return new co.edu.unipiloto.cargaexpress.MyAdapterConductor(view);
    }

    public static class ActivityActual {
        static AsignarConductor asignarConductor;
    }

    public static void finish() {
        ActivityActual.asignarConductor.finish();
    }

    @Override
    public void onBindViewHolder(@NonNull co.edu.unipiloto.cargaexpress.MyAdapterConductor holder, int position) {

        holder.nombre.setText(users.get(position).getNombre());
        holder.apellidos.setText(String.valueOf(users.get(position).getApellidos()));
        holder.placa.setText(camions.get(position).getPlaca());
        holder.capacidad.setText(camions.get(position).getCapacidad()+"");
        holder.modelo.setText(camions.get(position).getModelo()+"");
        this.position = users.indexOf(users.get(holder.getPosition()).getCedula());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.asignar_conductor);
                builder.setMessage(R.string.confirmacion_asignacion_conductor);

                // Botón Aceptar
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String, Object> postulacion = new HashMap<>();
                        postulacion.put("conductor", Long.valueOf(users.get(position).getCedula()));
                        postulacion.put("estado", "Asignado");
                        carga.setEstado("Asignado");
                        database.collection("cargas").document(carga.getCodigo()).update(postulacion);
                        // Acción cuando se hace clic en Aceptar
                        Toast.makeText(context, "Conductor asignado a su carga", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                        HomeFragment.setCargas(carga);
                        Intent intent = new Intent(context, AplicarCarga.class);
                        intent.putExtra("user", carga_express.user);
                        intent.putExtra("carga", carga);
                        context.startActivity(intent);
                        finish();
                    }
                });

                // Botón Cancelar
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                // Mostrar el diálogo
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return users.size();
    }


}

class MyAdapterConductor extends RecyclerView.ViewHolder {
    TextView nombre, apellidos, placa, capacidad, modelo;
    CardView cardView;
    public MyAdapterConductor(@NonNull View itemView) {
        super(itemView);
        nombre = itemView.findViewById(R.id.nombre_conductor);
        apellidos = itemView.findViewById(R.id.apellidos);
        placa = itemView.findViewById(R.id.textView28);
        capacidad = itemView.findViewById(R.id.textView32);
        modelo = itemView.findViewById(R.id.textView31);
        cardView = itemView.findViewById(R.id.card);
    }
}

