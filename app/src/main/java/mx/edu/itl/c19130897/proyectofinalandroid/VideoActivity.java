package mx.edu.itl.c19130897.proyectofinalandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private String         ruta;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // Prepara el progress dialog
        progressDialog = new ProgressDialog( this );
        progressDialog.setTitle ( "Reproducción" );
        progressDialog.setMessage ( "Cargando..." );
        progressDialog.setCanceledOnTouchOutside ( false );
        progressDialog.show ();

        videoView = findViewById( R.id.videoView );
        ruta      = getIntent().getStringExtra ("rutaVideo" );

        // Establecer el video que se va a reproducir y agregar los controles de reproducción.
        videoView.setVideoURI ( Uri.parse( ruta ) );
        videoView.setMediaController ( new MediaController( this ) );

        // Al terminar la carga de video (preparado) se inicia la reproducción
        videoView.setOnPreparedListener( new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping( false );
                videoView.requestFocus();
                progressDialog.dismiss();
                videoView.start();
            }
        });

        // Al completar la reproducción del video terminar la actividad
        videoView.setOnCompletionListener ( new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion ( MediaPlayer mediaPlayer ) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState ( Bundle outStage ) {
        super.onSaveInstanceState( outStage );

        outStage.putInt( "position", videoView.getCurrentPosition() );
        videoView.pause();
    }

    @Override
    protected void onRestoreInstanceState ( Bundle savedInstanceState ) {
        super.onRestoreInstanceState( savedInstanceState );

        int position = savedInstanceState.getInt ( "position" );
        videoView.seekTo ( position );
    }

}