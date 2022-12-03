package mx.edu.itl.c19130897.proyectofinalandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mx.edu.itl.c19130897.androlib.util.permisos.PermisoApp;
import mx.edu.itl.c19130897.androlib.util.permisos.ChecadorDePermisos;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int CODIGO_CAPTURA_FOTO = 123;
    private RecyclerView listaFoto;
    private FotoAdapter adapter;
    private String AlbumActual = "Default_";

    // Barra Lateral
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private List<Bitmap> fotos = new ArrayList<>();

    private PermisoApp [] permisosReq = {
            new PermisoApp ( Manifest.permission.READ_EXTERNAL_STORAGE, "Almacenamiento Read", true ),
            new PermisoApp ( Manifest.permission.WRITE_EXTERNAL_STORAGE, "Almacenamiento Write", true ),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        drawerLayout = findViewById( R.id.drawer );
        navigationView = findViewById( R.id.navigationView );

        // Establecer evento onClick al NavigationView
        navigationView.setNavigationItemSelectedListener( this );

        actionBarDrawerToggle = new ActionBarDrawerToggle ( this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer );
        drawerLayout.addDrawerListener( actionBarDrawerToggle );
        actionBarDrawerToggle.setDrawerIndicatorEnabled( true );
        actionBarDrawerToggle.syncState();

        ChecadorDePermisos.checarPermisos( this, permisosReq );
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
        fotos.clear();

        File directorio = this.getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        File [] ficheros = directorio.listFiles();
        for ( File f:ficheros ) {
            if ( f.getName().startsWith( AlbumActual )) {
                Bitmap image = BitmapFactory.decodeFile( f.getAbsolutePath() );
                fotos.add( image );
            }
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
        String pre = AlbumActual;
        File directorio = this.getExternalFilesDir( Environment.DIRECTORY_PICTURES );
        File img = File.createTempFile( pre + UUID.randomUUID().toString(), ".jpg", directorio );
        return img;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == CODIGO_CAPTURA_FOTO ) {
            if ( resultCode == RESULT_OK ) {
                Bitmap image = BitmapFactory.decodeFile( archivo.getAbsolutePath() );
                fotos.add( image );
                adapter.notifyDataSetChanged();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ) {
            case R.id.album_default: AlbumActual = "Default_"; Toast.makeText( this, "Default_", Toast.LENGTH_SHORT ).show();
                                    cargarFotos (); adapter.notifyDataSetChanged(); break;
            case R.id.album_casa: AlbumActual = "Casa_"; Toast.makeText( this, "Casa_", Toast.LENGTH_SHORT ).show();
                                    cargarFotos (); adapter.notifyDataSetChanged(); break;
            case R.id.album_universidad: AlbumActual = "Universidad_"; Toast.makeText( this, "Universidad_", Toast.LENGTH_SHORT ).show();
                                    cargarFotos (); adapter.notifyDataSetChanged(); break;
        }

        return false;
    }
}