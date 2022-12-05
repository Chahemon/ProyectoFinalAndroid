package mx.edu.itl.c19130897.proyectofinalandroid;

import android.graphics.Bitmap;

public class Foto {

    // Atributos
    private String fichero;
    private String nombre;
    private String descripcion;
    private String etiqueta;
    private Bitmap foto;

    public Foto(String fichero, Bitmap foto) {
        this.fichero = fichero;
        this.foto = foto;
    }

    public String getFichero() {
        return fichero;
    }

    public void setFichero(String fichero) {
        this.fichero = fichero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }
}
