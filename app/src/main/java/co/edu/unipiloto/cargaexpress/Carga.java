package co.edu.unipiloto.cargaexpress;

public class Carga {
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

    public Carga() {}

    public Carga(String codigo, String tipoCarga, long peso, String dimensiones, String direccionOrigen, String ciudadOrigen, String direccionDestino, String ciudadDestino, String fechaPublicada, String fechaRecogida, String horaRecogida, String fechaEntrega, String especificaciones, long cedulaComerciante, long cedulaConductor) {
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

    public long getCedulaConductor() {
        return cedulaConductor;
    }

    public void setCedulaConductor(long cedulaConductor) {
        this.cedulaConductor = cedulaConductor;
    }

}
