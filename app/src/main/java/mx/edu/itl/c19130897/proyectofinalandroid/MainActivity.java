package mx.edu.itl.c19130897.proyectofinalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import mx.edu.itl.c19130897.androlib.util.permisos.PermisoApp;
import mx.edu.itl.c19130897.androlib.util.permisos.ChecadorDePermisos;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Adaptador y RecyclerView para mostrar las imagenes
    public static final int CODIGO_CAPTURA_FOTO = 123;
    static final int REQUEST_VIDEO_CAPTURE = 321;
    private RecyclerView listaFoto;
    private FotoAdapter adapter;

    // Variables para Nombre del archivo
    private String AlbumActual = "Default";
    private String SubAlbum = "";

    // Menu Lateral
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    // Lista de las Fotos que va a mostrar en la aplicación en la caleria
    private List<Foto> fotos = new ArrayList<>();

    // Arreglo de permisos que vamos a pedir en tiempo de ejecucion
    private PermisoApp [] permisosReq = {
            new PermisoApp ( Manifest.permission.READ_EXTERNAL_STORAGE, "Almacenamiento Read", true ),
            new PermisoApp ( Manifest.permission.WRITE_EXTERNAL_STORAGE, "Almacenamiento Write", true ),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar todos parametros que necesitamos para la toolbar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        // Establecer evento onClick al NavigationView
        navigationView.setNavigationItemSelectedListener(this);

        // Asignamos los parametros a la barra toolbar
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        // Pedimos los permisos en tiempo de ejecución con el la libreria ANDROLIB
        ChecadorDePermisos.checarPermisos(this, permisosReq);

        // Sacamos la referencia del Recycler View y le asignamos el Adapter para que muestre las imagenes
        listaFoto = findViewById(R.id.galeria_fotos);
        adapter = new FotoAdapter(fotos, this);
        listaFoto.setLayoutManager(new GridLayoutManager(this, 4));
        listaFoto.setAdapter(adapter);

        // Especificamos las rutas donde estaran las carpetas
        File CarpetaDefault = new File( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM", "Default");
        File CarpetaCasa = new File( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM", "Casa");
        File CarpetaUniversidad = new File( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM", "Universidad");
        File CarpetaViajes = new File( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM", "Viajes");
        File CarpetaFiestas = new File( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM", "Fiestas");
        File CarpetaComidas = new File( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM", "Comidas");

        // Creamos las carpetas si es que no existen
        CarpetaDefault.mkdirs();
        CarpetaCasa.mkdirs();
        CarpetaUniversidad.mkdirs();
        CarpetaViajes.mkdirs();
        CarpetaFiestas.mkdirs();
        CarpetaComidas.mkdirs();

        // Cargamos las fotos
        cargarFotos();

        // Sacamos la referencia del boton y le asignamos el metodo onClick
        FloatingActionButton btn = findViewById( R.id.botonFoto );
        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                hacerFoto();
            }
        });
    }

    public void cargarFotos () {
        // Limpiamos la lista con las fotos para solo mostrar las imagenes que cumplan con el filtro (Album Correcto)
        fotos.clear();

        // Buscamos los archivos que esten en la Carpeta del Album Actual
        File directorio = new File ( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM" + File.separator + AlbumActual );

        // Creamos un arreglo de archivos con las imagenes para poder mostrarlas en el RecyclerView
        File [] ficheros = directorio.listFiles();

        // Validamos que el archivo se llame igual que el album y tenga la extension correcta para mostrarlo
        for ( File f:ficheros ) {
            if ( f.getName().startsWith( AlbumActual ) && f.getName().endsWith( ".jpg" ) ) {
                Bitmap image = BitmapFactory.decodeFile( f.getAbsolutePath() );
                fotos.add( new Foto( f.toString(), image ) );
            }
        }
    }

    private File archivo;
    public void hacerFoto() {
        try {

            // Creamos el intent implicito para que capture la foto
            Intent intent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );

            // Creamos el archivo donde se guardara la imagen y le especificamos que es una foto ( True )
            archivo = crearFichero( true );

            // Otorgamos permisos para evitar conflictos con la aplicación
            Uri foto = FileProvider.getUriForFile( this, "mx.edu.itl.c19130897.proyectofinalandroid.fileprovider", archivo );

            // Pasamos la direccion donde se va a guardar la foto e Iniciamos el Intent
            intent.putExtra( MediaStore.EXTRA_OUTPUT, foto );
            startActivityForResult( intent, CODIGO_CAPTURA_FOTO );

        } catch ( IOException e ) {
            // Si hubo un error, lo notificamos con un Toast
            Toast.makeText(this, "Error:" + e.getMessage() , Toast.LENGTH_LONG ).show();
        }
    }

    public void hacerVideo()
    {
        try {

            // Prodecimiento Igual al de tomar foto, con la diferencia que el Intent Implicito Cambiar por Grabación
            Intent takeVideoIntent = new Intent( MediaStore.ACTION_VIDEO_CAPTURE );
            archivo = crearFichero( false );
            Uri foto = FileProvider.getUriForFile( this, "mx.edu.itl.c19130897.proyectofinalandroid.fileprovider", archivo );
            takeVideoIntent.putExtra( MediaStore.EXTRA_OUTPUT, foto );
            startActivityForResult( takeVideoIntent, REQUEST_VIDEO_CAPTURE );

        } catch ( IOException e ) {
            Toast.makeText(this, "Error:" + e.getMessage() , Toast.LENGTH_LONG ).show();
        }
    }

    public void btnHacerVideo(View view)
    {
        this.hacerVideo();
    }

    public File crearFichero ( Boolean Foto ) throws IOException {

        // Creamos el fichero y el nombre de los archivos
        // Configuramos los parametros que necesitaremos para la el album
        String pre = AlbumActual;
        String subalbum = SubAlbum;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ( "yyyyMMddHHmmss" );
        String strFechaHora = simpleDateFormat.format ( new Date() );

        // Sacamos el directorio donde guardara la el archivo, en este caso la carpeta DCIM y la
        // subcarpeta que creamos en el inicio de la app
        File directorio = new File ( Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "DCIM" + File.separator + AlbumActual );

        // Creamos y regresamos el archivo compuesto por los albums y la fecha, y la direccion donde se guardara
        File fileFoto = File.createTempFile( pre + "_" + subalbum + strFechaHora,  Foto ? ".jpg" : ".mp4" , directorio );

        return fileFoto;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Validamos si se tomo o no la fotografia y la añadimos al recyblerView
        if ( requestCode == CODIGO_CAPTURA_FOTO ) {
            if ( resultCode == RESULT_OK ) {
                // Creamos un Bitmap con la ruta del archivo que creamos y lo agregamos al RecyblerView
                Bitmap image = BitmapFactory.decodeFile( archivo.getAbsolutePath() );
                fotos.add( new Foto( archivo.toString(), image ) );
                // Notificamos los cambios para que se actualice
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );

        // Pedimos los permisos del androlib
        if ( requestCode == ChecadorDePermisos.CODIGO_PEDIR_PERMISOS ) {
            ChecadorDePermisos.verificarPermisosSolicitados ( this, permisosReq, permissions, grantResults );
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Configuramos el Menu desplegable, para que se cierre cuando seleccionemos una opcion
        TextView textView = findViewById( R.id.textView );
        drawerLayout.closeDrawer( GravityCompat.START );

        // Vereficamos cual opcion se eligio, y Realizamos los Cambios Necesarios Dependiendo del album seleccionado
        switch ( item.getItemId() ) {
            case R.id.album_default: AlbumActual = "Default"; SubAlbum = ""; textView.setText( "Album Principal" );
                                    cargarFotos (); adapter.notifyDataSetChanged(); break;
            case R.id.album_casa: AlbumActual = "Casa"; SubAlbum = ""; textView.setText( "Album Casa" );
                                    cargarFotos (); adapter.notifyDataSetChanged(); break;
            case R.id.album_universidad: AlbumActual = "Universidad"; SubAlbum = ""; textView.setText( "Album Universidad" );
                                    cargarFotos (); adapter.notifyDataSetChanged(); break;
            case R.id.album_viajes: AlbumActual = "Viajes"; SubAlbum = ""; textView.setText( "Album Viajes" );
                cargarFotos (); adapter.notifyDataSetChanged(); break;
            case R.id.album_fiestas: AlbumActual = "Fiestas"; SubAlbum = ""; textView.setText( "Album Fiestas" );
                cargarFotos (); adapter.notifyDataSetChanged(); break;
            case R.id.album_comida: AlbumActual = "Comidas"; SubAlbum = ""; textView.setText( "Album Comidas" );
                cargarFotos (); adapter.notifyDataSetChanged(); break;
        }

        return false;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        // Cuando Dejamos Precionada la imagen despliega un menu, si seleccionas "Borrar"
        // Borraremos el archivo del celular

        if ( item.getItemId() == R.id.borrar ) {

            // Obtenemos la posicion de la referencia que vamos a eliminar
            int position = adapter.getPosicion();
            Foto f = fotos.get( position );

            // Eliminamos el la fotos de nuestros archivos
            File file = new File ( f.getFichero() );
            file.delete();

            // Eliminamos las fotos del recycler view y actualizamos los cambios
            fotos.remove( position );
            adapter.notifyDataSetChanged();

        }
        return super.onContextItemSelected(item);
    }

    private String ruta;
    public void btnAcercaDe ( View v ) {
        // Creamos la ruta del video "Acerca De" y lanzamos el video
        ruta = "android.resource://" + this.getPackageName() + "/" + R.raw.acerca_de;
        lanzarVideoActiviy ();
    }

    private void  lanzarVideoActiviy () {
        // Iniciamos el Intent pasandole la ruta del video para que lo reproduzca
        Intent intent = new Intent ( this, VideoActivity.class ) ;
        intent.putExtra ( "rutaVideo", ruta );
        startActivity ( intent );
    }

    // Referencias para el subAlbum
    private View subalbum_layout;
    private EditText edtSubalbum;

    public void btnEditarSubalbum ( View v ) {
        // Obtenemos la referencia del layout a incrustar
        subalbum_layout = this.getLayoutInflater().inflate( R.layout.layout_subalbum, null );
        // Obtener las referencias a los campos EditText
        edtSubalbum = subalbum_layout.findViewById( R.id.edtSubAlbum );

        // Construimos el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( "Nombre Subalbum" )
                .setView( subalbum_layout )
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SubAlbum = edtSubalbum.getText().toString() + "_";
                    }
                })
                .setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable( false )
                .setIcon( R.drawable.edit )
                .create()
                .show();
    }

}