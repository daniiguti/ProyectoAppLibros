package com.example.pruebarecviewbasedatos.Controlador;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.pruebarecviewbasedatos.Adapter.RecyclerAdapter;
import com.example.pruebarecviewbasedatos.Modelo.Libro;
import com.example.pruebarecviewbasedatos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity{
    //Variable estática para startActivityForResult
    public static final int RQ_CODE = 1;

    //Variables necesarias
    private ActionMode actionMode;
    private RecyclerView recyclerView;
    private RecyclerAdapter recAdapter;
    private int posicionPulsada;
    private TextView info;
    private ArrayList<Libro> libros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializamos las variables
        Intent i = getIntent();
        info = (TextView) findViewById(R.id.etIntoMain);
        recyclerView = (RecyclerView) findViewById(R.id.recView);

        recAdapter = new RecyclerAdapter(this, libros);

        //Llamos al metodo getRecomendaciones, este se encarga de mostrar al principio
        //una serie de libros recomendados por mi
        getRecomendaciones();

        //Listener del recAdapter, para cuando pulsemos se vaya a la vista Maestro-Detalle
        //le pasamos el id del libro pulsado
        recAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posicionPulsada = recyclerView.getChildAdapterPosition(view);
                Libro aux = recAdapter.devolverLibro(posicionPulsada);
                Intent i = new Intent(ActivityMain.this, ActivityDetalle.class);
                i.putExtra("objetoPulsado", aux.getId());
                startActivity(i);
            }
        });
        //Listener del recAdapterm para cuando dejemos pulsado se nos habra un menú de acción
        recAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                posicionPulsada = recyclerView.getChildAdapterPosition(view);
                //Hay que pasarle la interfaz implementada mas abajo
                actionMode = startActionMode(actionModeCallback);
                view.setSelected(true);
                return true;
            }
        });

        //Layout para que salga en forma de lista el recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Le ponemos al recyclerView el adapter que contiene el arraylist
        recyclerView.setAdapter(recAdapter);
    }

    //On resume para cargar las preferencias, necesario aqui para que se apliquen nada mas cambiarse
    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    //MENÚ SIMPLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.btBuscarMenu:
                Intent i = new Intent(ActivityMain.this, ActivityBuscarLibros.class);
                startActivityForResult(i, RQ_CODE);
                break;
            case R.id.btPreferenciasMenu:
                Intent i2 = new Intent(ActivityMain.this, ActivityPreferences.class);
                startActivity(i2);
                break;
            case R.id.btCerrarSesion:
                Intent i3 = new Intent(ActivityMain.this, ActivityLoginRegister.class);
                startActivity(i3);
                //Para cerrar todas las actividades anteriores a esta
                //puesto que si le da para atrás esto no serviria de nada
                finishAffinity();
                break;
        }
        return true;
    }

    //Interfaz para el MENU DE ACCION
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_accion, menu);
            return true;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.btEliminar:
                    AlertDialog alertDialog = createAlertDialogEliminar();
                    alertDialog.show();
                    mode.finish();
                    break;
                case R.id.btAnadir:
                    showAlertDialog(-1);
                    mode.finish();
                    break;
                case R.id.btModificar:
                    showAlertDialog(posicionPulsada);
                    mode.finish();
                    break;
            }
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    //Funcion para enseñar un toast
    private void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    //Funcion para cambiar la etiqueta RECOMENDACIONES A RESULTADO BUSQUEDA
    private void cambiarInfo(){
        info.setText(R.string.etInfoMain2);
    }

    //Funcion para cargar las preferencias
    private void loadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ActivityMain.this);
        String colorFondo = sharedPreferences.getString("preferences_tema","Light");
        switch (colorFondo){
            case "Light":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Night":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    //Metodo para la primera vez que se cargue la lista
    //esto son una serie de libros que YO he escogido, (se puede buscar por el id, pero el tratamiento
    // del json, seria distinto, puesto que ya nos devolveria un objeto y no un array, habria que hacer dos metodos distintos para el
    // tratamiento)
    private void getRecomendaciones(){
        new taskConnections().execute("GET", "?q=El%20Senor%20de%20Los%20Anillos%201%20(Movie%20Ed):%20La%20Comunidad%20del%20Anillo&maxResults=1"); //El señor de los anillos
        new taskConnections().execute("GET", "?q=Harry%20Potter+inauthor:J.K.Rowling&maxResults=1"); //Harry Potter
        new taskConnections().execute("GET", "?q=El%20Principito+inauthor:Antoine%20De%20Saint-exupery&maxResults=1"); //El principito
        new taskConnections().execute("GET", "?q=Don%20Quijote%20De%20La%20Mancha+inauthor:Miguel%20De%20Cervantes&maxResults=1"); //Don Quijote de la mancha
        new taskConnections().execute("GET", "?q=La%20Biblia%20Epica+inauthor:Tyndale&maxResults=1"); //La biblia
    }
    private void buscar(String busqueda){
        new taskConnections().execute("GET", busqueda);
    }
    private class taskConnections extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            switch (strings[0]){
                case "GET":
                    result = ConnectToGoogleApi.getRequest(strings[1]);
                    break;
            }
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            try {
                if(s != null){
                    JSONObject jsonObject = new JSONObject(s);

                    JSONArray jsonArray = jsonObject.getJSONArray("items");

                    String id = "";
                    String titulo = "";
                    String autor = "";
                    String imgUrl = "";
                    for(int i=0; i<jsonArray.length(); i++){
                        id = jsonArray.getJSONObject(i).getString("id");

                        JSONObject volumenInfo = jsonArray.getJSONObject(i).getJSONObject("volumeInfo");
                        titulo = volumenInfo.getString("title");

                        autor = volumenInfo.getJSONArray("authors").getString(0);

                        try {
                            imgUrl = volumenInfo.getJSONObject("imageLinks").getString("thumbnail");
                        }catch(JSONException e){
                            imgUrl = "NOT FOUND";
                        }

                        Libro aux = new Libro(id, titulo, autor, imgUrl);
                        recAdapter.insertar(aux);
                    }

                }else{
                    showToast(R.string.InfoToastMainError);
                }
            } catch (JSONException e){

            }
        }
    }

    //Método que me devuelve el comando/filtro del ActivityBuscarLibros para realizar la busqueda en la api
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RQ_CODE:
                if(resultCode==RESULT_OK){
                    String busqueda = data.getStringExtra("busqueda");
                    //Para realizar las consultas
                    if(busqueda != null){
                        cambiarInfo();
                        recAdapter.clear();
                        //para que en caso de que el usuario meta en buscar por ej: ñasdflapsdf
                        //la app no se quede pillada
                        recyclerView.getRecycledViewPool().clear();
                        recAdapter.notifyDataSetChanged();
                        buscar(busqueda);
                    }
                }
                break;
        }

    }

    //Método para crear un alert Dialog para insertar o modificar un libro
    AlertDialog alertDialog;
    private void showAlertDialog(int posicionLibro){
        alertDialog = createAlertDialogInsert(posicionLibro);

        alertDialog.show();
    }
    public AlertDialog createAlertDialogInsert(int posicionLibro){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);

        builder.setTitle(R.string.adAnadirTitulo);

        View view = getLayoutInflater().inflate(R.layout.layout_custom_dialog, null);
        EditText txtTitulo, txtAutor;
        txtTitulo = view.findViewById(R.id.txtTitulo2);
        txtAutor = view.findViewById(R.id.txtAutor2);

        Button aceptar = view.findViewById(R.id.btAceptar2);
        Button cancelar = view.findViewById(R.id.btCancelar2);
        //Para comprobar si pulso el boton de añadir o modificar, si pulso el boton
        //de modificar le ponemos el texto
        if(posicionLibro >= 0){
            builder.setTitle(R.string.adModificarTitulo);
            Libro auxLibro  = recAdapter.devolverLibro(posicionLibro);
            txtTitulo.setText(auxLibro.getTitulo());
            txtAutor.setText(auxLibro.getAutor());
        }

        //si pulsa el boton aceptar, añadimos el libro a nuestro arraylist del recAdapter
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = txtTitulo.getText().toString();
                String autor = txtAutor.getText().toString();

                if(titulo.equals("") || autor.equals("")){
                    showToast(R.string.adAnadirInfo);
                }else{
                    Libro aux = new Libro("no tiene", titulo, autor,"no tiene");
                    if(posicionLibro >= 0){
                        Libro auxLibro  = recAdapter.devolverLibro(posicionLibro);
                        aux.setId(auxLibro.getId());
                        aux.setImagePortada(auxLibro.getImagePortada());
                        recAdapter.modificar(posicionLibro, aux);
                    }else{
                        recAdapter.insertar(aux);
                    }
                    alertDialog.dismiss();
                    showToast(R.string.adAnadirInfo2);
                }
            }
        });
        //si pulsa cancelar no hacemos nada
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }
    public AlertDialog createAlertDialogEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);

        String msg = getResources().getString(R.string.adEliminar2);
        builder.setMessage(Html.fromHtml("<font color='#79828D'>" + msg + "</font>"));
        builder.setTitle(R.string.adEliminar1);

        builder.setPositiveButton(R.string.adEliminarSi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recAdapter.delete(posicionPulsada);
                Toast.makeText(ActivityMain.this, R.string.adEliminarInfo, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.adEliminarNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ActivityMain.this, R.string.adEliminarInfo2, Toast.LENGTH_SHORT).show();
            }
        });

        return builder.create();
    }
}