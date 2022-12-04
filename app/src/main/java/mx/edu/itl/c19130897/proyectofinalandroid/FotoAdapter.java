package mx.edu.itl.c19130897.proyectofinalandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder> {

    private List<Bitmap> lista;
    private Context context;

    public FotoAdapter ( List<Bitmap> lista, Context context ) {
        this.lista = lista;
        this.context = context;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from( context ).inflate( R.layout.item_foto, parent, false);
        return new ViewHolder ( v );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = lista.get( position );
        holder.image.setImageBitmap(bitmap);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( context, MuestraFotoActivity.class);
                intent.putExtra("uri", getImageUri( context, bitmap).toString() );

                context.startActivity( intent );
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder ( @NonNull View itemView ) {
            super( itemView );
            image = itemView.findViewById( R.id.imgViewFoto );
        }



    }

}
