package mx.edu.itl.c19130897.proyectofinalandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class MuestraFotoActivity extends AppCompatActivity {

    private ImageView imgvFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muestra_foto);

        imgvFoto = findViewById ( R.id.imgvFoto );

        // Ocultar la barra de acciones
        this.getSupportActionBar().hide();

        // Obtenemos la ubicación de la foto desde el parametro que viene en los Extras
        String strUri = getIntent().getStringExtra( "uri" );
        imgvFoto.setImageURI ( Uri.parse( strUri ) );
    }
}