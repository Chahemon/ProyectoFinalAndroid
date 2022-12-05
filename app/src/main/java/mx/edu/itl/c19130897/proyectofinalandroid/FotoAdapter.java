package mx.edu.itl.c19130897.proyectofinalandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder> {

    private List<Foto> lista;
    private Context context;
    private int posicion;

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int position) {
        this.posicion = position;
    }

    public FotoAdapter (List<Foto> lista, Context context ) {
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
    public void onBindViewHolder( @NonNull ViewHolder holder, int position ) {
        int actPosition = position;
        Foto bitmap = lista.get( position );
        holder.image.setImageBitmap( bitmap.getFoto() );

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( context, MuestraFotoActivity.class);
                intent.putExtra("uri", getImageUri( context, bitmap.getFoto() ).toString() );

                context.startActivity( intent );
            }
        });

        holder.image.setOnLongClickListener( new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosicion( actPosition );
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgViewFoto);

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    MenuInflater inflater = new MenuInflater(view.getContext());
                    inflater.inflate(R.menu.menu_contextual_foto, contextMenu);
                }
            });
        }
    }

}
