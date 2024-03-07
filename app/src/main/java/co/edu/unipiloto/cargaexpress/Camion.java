package co.edu.unipiloto.cargaexpress;

public class Camion {

    private String placa;
    private long modelo;
    private long tarjetaPropiedad;
    private long capacidad;
    private long conductor;
    private long propietario;

    public Camion(String placa, long modelo, long tarjetaPropiedad, long capacidad, long conductor, long propietario) {
        this.placa = placa;
        this.modelo = modelo;
        this.tarjetaPropiedad = tarjetaPropiedad;
        this.capacidad = capacidad;
        this.conductor = conductor;
        this.propietario = propietario;
    }

    public Camion (){};

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public long getModelo() {
        return modelo;
    }

    public void setModelo(long modelo) {
        this.modelo = modelo;
    }

    public long getTarjetaPropiedad() {
        return tarjetaPropiedad;
    }

    public void setTarjetaPropiedad(long tarjetaPropiedad) {
        this.tarjetaPropiedad = tarjetaPropiedad;
    }

    public long getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(long capacidad) {
        this.capacidad = capacidad;
    }

    public long getConductor() {
        return conductor;
    }

    public void setConductor(long conductor) {
        this.conductor = conductor;
    }

    public long getPropietario() {
        return propietario;
    }

    public void setPropietario(long propietario) {
        this.propietario = propietario;
    }
}
