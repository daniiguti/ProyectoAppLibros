package com.example.pruebarecviewbasedatos.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.pruebarecviewbasedatos.Modelo.Libro;
import com.example.pruebarecviewbasedatos.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>{
    //Atributos de nuestra clase
    private List<Libro> listLibros;
    private View.OnLongClickListener longListener;
    private View.OnClickListener smallListener;
    private Context context;

    private CircularProgressDrawable progressDrawable;

    //Constructor
    public RecyclerAdapter(Context auxContext, List<Libro> listVideojuego){
        this.listLibros = listVideojuego;
        this.context = auxContext;
    }

    //Setter de los Listeners
    public void setOnClickListener(View.OnClickListener listener){
        this.smallListener = listener;
    }
    public void setOnLongClickListener(View.OnLongClickListener listener){ this.longListener = listener; }

    //Esto "infla" cada celda del recyclerView con nuestro diseño
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        view.setOnLongClickListener(longListener);
        view.setOnClickListener(smallListener);
        return recyclerHolder;
    }

    //Esto junta cada Libro del arrayList con el diseño de cada celda
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.start();
        Libro libro = listLibros.get(position);
        if(libro.getImagePortada().equals("no tiene")){                 //Comprobamos que tiene imagen, sino la tiene ponemos una
            holder.imgPortada.setImageResource(R.drawable.libro);       //una por defecto nosotros (para cuando añadimos un libro)
        }else{
            Glide.with(context)
                    .load(libro.getImagePortada())
                    .placeholder(progressDrawable)
                    .error(R.drawable.not_found)
                    .into(holder.imgPortada);
        }
        holder.txtTitulo.setText(libro.getTitulo());
        holder.txtAutor.setText(libro.getAutor());
    }

    @Override
    public int getItemCount() {
        return listLibros.size();
    }

    //Enlazamos los elementos del diseño en relacion a nuestra clase
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo;
        TextView txtAutor;
        ImageView imgPortada;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            txtTitulo = (TextView)  itemView.findViewById(R.id.txtTitulo);
            txtAutor = (TextView) itemView.findViewById(R.id.txtAutor);
            imgPortada = (ImageView) itemView.findViewById(R.id.imgPortada);
        }

    }

    //Métodos auxiliares para modificar el array
    //Para borrar de nuestro arrayList
    public void delete(int posicion){
        this.listLibros.remove(posicion);
        this.notifyDataSetChanged();
    }
    //Para insertar en nuestro arrayList
    public void insertar(Libro libro){
        this.listLibros.add(libro);
        this.notifyDataSetChanged();
    }
    //Método para modificar
    public void modificar(int posicion, Libro nuevoLibro){
        this.listLibros.remove(posicion);
        this.listLibros.add(posicion, nuevoLibro);
        this.notifyDataSetChanged();
    }
    //Para borrar nuestro arrayList
    public void clear(){
        listLibros.clear();
    }
    //Para devolver de nuestro arrayList
    public Libro devolverLibro(int posicion){
        return this.listLibros.get(posicion);
    }
}
