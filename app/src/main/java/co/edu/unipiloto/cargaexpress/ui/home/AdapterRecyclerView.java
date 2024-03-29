package co.edu.unipiloto.cargaexpress.ui.home;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.edu.unipiloto.cargaexpress.AplicarCarga;
import co.edu.unipiloto.cargaexpress.Carga;
import co.edu.unipiloto.cargaexpress.R;
import co.edu.unipiloto.cargaexpress.carga_express;

public class AdapterRecyclerView extends RecyclerView.Adapter<MyViewHolder>{

    private Context context;
    private List<Carga> dataList;

    private int position;

    public  void setSearchList(List<Carga> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public  AdapterRecyclerView(Context context, List<Carga> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public  MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position) {

        holder.tipoCarga.setText(dataList.get(position).getTipoCarga());
        holder.peso.setText(String.valueOf(dataList.get(position).getPeso()));
        holder.ciudadOrigen.setText(dataList.get(position).getCiudadOrigen());
        holder.ciudadDestino.setText(dataList.get(position).getCiudadDestino());
        holder.fechaRecogida.setText(dataList.get(position).getFechaRecogida());
        this.position = dataList.indexOf(dataList.get(holder.getPosition()).getCodigo());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AplicarCarga.class);
                intent.putExtra("carga", dataList.get(position));
                intent.putExtra("user", carga_express.user);
                startActivity(context, intent, null);
            }
        });
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }



}

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView tipoCarga, peso, ciudadOrigen, ciudadDestino, fechaRecogida;
    CardView cardView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        tipoCarga = itemView.findViewById(R.id.nombre_conductor);
        peso = itemView.findViewById(R.id.textView28);
        ciudadOrigen = itemView.findViewById(R.id.textView32);
        ciudadDestino = itemView.findViewById(R.id.textView31);
        fechaRecogida = itemView.findViewById(R.id.textView34);
        cardView = itemView.findViewById(R.id.card);
    }
}
