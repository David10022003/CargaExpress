package co.edu.unipiloto.cargaexpress;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.google.firebase.firestore.auth.User;

public class Usuario implements Parcelable {
    private String cedula;
    private String nombre;
    private String apellidos;
    private String tipoDocumento;
    private String email;
    private String contra;
    private String rol;
    private String genero;

    private String fechaNacimiento;

    public Usuario() {}

    public Usuario(String cedula, String nombre, String apellidos, String tipoDocumento, String email, String contra, String rol, String genero, String fechaNacimiento){
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.tipoDocumento = tipoDocumento;
        this.email = email;
        this.contra = contra;
        this.rol = rol;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    protected Usuario(Parcel in){
        cedula = in.readString();
        nombre = in.readString();
        apellidos = in.readString();
        tipoDocumento = in.readString();
        email = in.readString();
        contra = in.readString();
        rol = in.readString();
        genero = in.readString();
        fechaNacimiento = in.readString();

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(cedula);
        dest.writeString(nombre);
        dest.writeString(apellidos);
        dest.writeString(tipoDocumento);
        dest.writeString(email);
        dest.writeString(contra);
        dest.writeString(rol);
        dest.writeString(genero);
        dest.writeString(fechaNacimiento);
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

}
