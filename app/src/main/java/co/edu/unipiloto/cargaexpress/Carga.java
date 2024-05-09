package co.edu.unipiloto.cargaexpress;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Carga implements Parcelable{
    private double latitud;
    private double longitud;
    private String codigo;
    private String tipoCarga;
    private long peso;
    private String dimensiones;
    private String direccionOrigen;
    private String ciudadOrigen;
    private String direccionDestino;
    private String ciudadDestino;
    private String fechaPublicada;
    private String fechaRecogida;
    private String horaRecogida;
    private String fechaEntrega;
    private String especificaciones;
    private long cedulaComerciante;
    private long cedulaConductor;

    private long incidencias;
    private String estado;

    public Carga() {}

    public Carga(String codigo, String tipoCarga, long peso, String dimensiones, String direccionOrigen,
                 String ciudadOrigen, String direccionDestino, String ciudadDestino, String fechaPublicada,
                 String fechaRecogida, String horaRecogida, String fechaEntrega, String especificaciones, long cedulaComerciante,
                 long cedulaConductor, String estado, double latitud, double longitud, long incidencias) {
        this.codigo = codigo;
        this.tipoCarga = tipoCarga;
        this.peso = peso;
        this.dimensiones = dimensiones;
        this.direccionOrigen = direccionOrigen;
        this.ciudadOrigen = ciudadOrigen;
        this.direccionDestino = direccionDestino;
        this.ciudadDestino = ciudadDestino;
        this.fechaPublicada = fechaPublicada;
        this.fechaRecogida = fechaRecogida;
        this.horaRecogida = horaRecogida;
        this.fechaEntrega = fechaEntrega;
        this.especificaciones = especificaciones;
        this.cedulaComerciante = cedulaComerciante;
        this.cedulaConductor = cedulaConductor;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estado = estado;
        this.incidencias = incidencias;
    }

    public long getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(long incidencias) {
        this.incidencias = incidencias;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTipoCarga() {
        return tipoCarga;
    }

    public void setTipoCarga(String tipoCarga) {
        this.tipoCarga = tipoCarga;
    }

    public long getPeso() {
        return peso;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getDireccionOrigen() {
        return direccionOrigen;
    }

    public void setDireccionOrigen(String direccionOrigen) {
        this.direccionOrigen = direccionOrigen;
    }

    public String getCiudadOrigen() {
        return ciudadOrigen;
    }

    public void setCiudadOrigen(String ciudadOrigen) {
        this.ciudadOrigen = ciudadOrigen;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public String getCiudadDestino() {
        return ciudadDestino;
    }

    public void setCiudadDestino(String ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public String getFechaPublicada() {
        return fechaPublicada;
    }

    public void setFechaPublicada(String fechaPublicada) {
        this.fechaPublicada = fechaPublicada;
    }

    public String getFechaRecogida() {
        return fechaRecogida;
    }

    public void setFechaRecogida(String fechaRecogida) {
        this.fechaRecogida = fechaRecogida;
    }

    public String getHoraRecogida() {
        return horaRecogida;
    }

    public void setHoraRecogida(String horaRecogida) {
        this.horaRecogida = horaRecogida;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEspecificaciones() {
        return especificaciones;
    }

    public void setEspecificaciones(String especificaciones) {
        this.especificaciones = especificaciones;
    }

    public long getCedulaComerciante() {
        return cedulaComerciante;
    }

    public void setCedulaComerciante(long cedulaComerciante) {
        this.cedulaComerciante = cedulaComerciante;
    }

    public void setPeso(long peso) {
        this.peso = peso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getCedulaConductor() {
        return cedulaConductor;
    }

    public void setCedulaConductor(long cedulaConductor) {
        this.cedulaConductor = cedulaConductor;
    }

    protected Carga(Parcel in){
        this.codigo = in.readString();
        this.tipoCarga = in.readString();
        this.peso = in.readLong();
        this.dimensiones = in.readString();
        this.direccionOrigen = in.readString();
        this.ciudadOrigen = in.readString();
        this.direccionDestino = in.readString();
        this.ciudadDestino = in.readString();
        this.fechaPublicada = in.readString();
        this.fechaRecogida = in.readString();
        this.horaRecogida = in.readString();
        this.fechaEntrega = in.readString();
        this.especificaciones = in.readString();
        this.cedulaComerciante = in.readLong();
        this.cedulaConductor = in.readLong();
        this.estado = in.readString();
        this.latitud = in.readDouble();
        this.longitud = in.readDouble();
        this.incidencias = in.readLong();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(codigo);
        dest.writeString(tipoCarga);
        dest.writeLong(peso);
        dest.writeString(dimensiones);
        dest.writeString(direccionOrigen);
        dest.writeString(ciudadOrigen);
        dest.writeString(direccionDestino);
        dest.writeString(ciudadDestino);
        dest.writeString(fechaPublicada);
        dest.writeString(fechaRecogida);
        dest.writeString(horaRecogida);
        dest.writeString(fechaEntrega);
        dest.writeString(especificaciones);
        dest.writeLong(cedulaComerciante);
        dest.writeLong(cedulaConductor);
        dest.writeString(estado);
        dest.writeDouble(latitud);
        dest.writeDouble(longitud);
        dest.writeLong(incidencias);
    }

    public static final Parcelable.Creator<Carga> CREATOR = new Creator<Carga>() {
        @Override
        public Carga createFromParcel(Parcel in) {
            return new Carga(in);
        }

        @Override
        public Carga[] newArray(int size) {
            return new Carga[size];
        }
    };
}
