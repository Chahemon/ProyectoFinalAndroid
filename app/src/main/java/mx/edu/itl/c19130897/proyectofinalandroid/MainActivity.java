package mx.edu.itl.c19130897.proyectofinalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import mx.edu.itl.c19130897.androlib.util.permisos.PermisoApp;
import mx.edu.itl.c19130897.androlib.util.permisos.ChecadorDePermisos;

public class MainActivity extends AppCompatActivity {

    public static final int CODIGO_CAPTURA_FOTO = 123;
    private Uri uriFoto;
    private RecyclerView listaFoto;
    private FotoAdapter adapter;

    private List<Bitmap> fotos = new ArrayList<>();

    private PermisoApp [] permisosReq = {
            new PermisoApp ( Manifest.permission.READ_EXTERNAL_STORAGE, "Almacenamiento Read", true ),
            new PermisoApp ( Manifest.permission.WRITE_EXTERNAL_STORAGE, "Almacenamiento Write", true ),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChecadorDePermisos.checarPermisos( this, permisosReq );
        /*
        String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

        File file1 = new File ( absolutePath, "foto20221201175614.jpg" );
        Bitmap bitmap1 = null;
        try {
            bitmap1 = BitmapFactory.decodeStream ( new FileInputStream( file1 ) );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        */

        cargarFotos();

        listaFoto = findViewById( R.id.galeria_fotos );
        adapter = new FotoAdapter ( fotos, this );
        listaFoto.setLayoutManager( new GridLayoutManager(this, 4));
        listaFoto.setAdapter( adapter );

        FloatingActionButton btn = findViewById( R.id.botonFoto );
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hacerFoto();
            }
        });
    }

    public void cargarFotos () {
        File directorio = this.getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        File [] ficheros = directorio.listFiles();
        for ( File f:ficheros ) {
            Bitmap image = BitmapFactory.decodeFile( f.getAbsolutePath() );
            fotos.add( image );
        }
    }

    private File archivo;
    public void hacerFoto() {
        try {
            Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );

            archivo = crearFichero();
            Uri foto = FileProvider.getUriForFile( this, "mx.edu.itl.c19130897.proyectofinalandroid.fileprovider", archivo );
            intent.putExtra( MediaStore.EXTRA_OUTPUT, foto );
            startActivityForResult( intent, CODIGO_CAPTURA_FOTO );
        } catch ( IOException e ) {
            Toast.makeText(this, "Error:" + e.getMessage() , Toast.LENGTH_LONG ).show();
        }
    }

    public File crearFichero () throws IOException {
        String pre = "foto_";
        File directorio = this.getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        File img = File.createTempFile( pre + UUID.randomUUID().toString(), ".jpg", directorio );
        return img;
    }

    /*
    public void btnCaptura ( View v ) {
        // Formar el nombre del archivo basado en la fecha y hora para que ese nombre sea unico
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "yyyyMMddHHmmss" );
        String strFechaHora = simpleDateFormat.format ( new Date() );
        String archFoto     = "foto" + strFechaHora + ".jpg";

        File fileFoto = new File ( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator +
                "DCIM" +
                File.separator +
                archFoto );

        // Creamos el URI correspondiente al archivo de destino de la foto. Se usa
        // FileProvider para respaldar las politicas de seguridad.
        uriFoto = FileProvider.getUriForFile ( this,
                                                BuildConfig.APPLICATION_ID + ".provider",
                                                        fileFoto );
        // Creamos el intent que llamara a la app de la camara de fotos y le pasamos el URI
        // del archivo donde se debera guardar la foto si es que se captura una.

        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra ( MediaStore.EXTRA_OUTPUT, uriFoto );
        startActivityForResult ( intent, CODIGO_CAPTURA_FOTO );
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == CODIGO_CAPTURA_FOTO ) {
            if ( resultCode == RESULT_OK ) {
                Bitmap image = BitmapFactory.decodeFile( archivo.getAbsolutePath() );
                fotos.add( image );
                adapter.notifyDataSetChanged();
                /*
                // Abrir el activity para mostrar la foto en pantalla completa.
                // Se enviar como argumentos el URI de la foto como string
                Intent intent = new Intent ( this, MuestraFotoActivity.class );
                intent.putExtra ( "uri", uriFoto.toString() );
                startActivity( intent );
                 */
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        if ( requestCode == ChecadorDePermisos.CODIGO_PEDIR_PERMISOS ) {
            ChecadorDePermisos.verificarPermisosSolicitados ( this, permisosReq, permissions, grantResults );
        }
    }

}