package mx.edu.itl.c19130897.proyectofinalandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FotoAdapter extends RecyclerView.Adapter<FotoAdapter.ViewHolder> {

    private List<Bitmap> lista;
    private Context context;

    public FotoAdapter ( List<Bitmap> lista, Context context ) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from( context ).inflate( R.layout.item_foto, parent, false);
        return new ViewHolder ( v );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bit = lista.get( position );
        holder.image.setImageBitmap(bit);
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
