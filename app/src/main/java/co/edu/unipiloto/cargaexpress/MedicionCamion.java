package co.edu.unipiloto.cargaexpress;

import java.util.ArrayList;
import java.util.List;

public class MedicionCamion {
    private String placa;
    private String año;
    private List<List<Long>> datos;

    public MedicionCamion(String placa, String año, List<List<Long>> datos) {
        this.placa = placa;
        this.año = año;
        this.datos = datos;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public List<List<Long>> getDatos() {
        return datos;
    }

    public void setDatos(List<List<Long>> datos) {
        this.datos = datos;
    }
}
