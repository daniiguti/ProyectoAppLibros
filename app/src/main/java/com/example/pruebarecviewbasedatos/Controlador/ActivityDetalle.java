package com.example.pruebarecviewbasedatos.Controlador;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pruebarecviewbasedatos.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityDetalle extends AppCompatActivity {
    //Variables necesarias
    private TextView titulo;
    private TextView autor;
    private TextView descripcion;
    private TextView anio;
    private TextView numPaginas;
    private ImageView portada;
    private String urlGoogle;

    private Button btComprar;

    private CircularProgressDrawable progressDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        //Inicialización de variables
        titulo = (TextView) findViewById(R.id.etTituloDet);
        autor = (TextView) findViewById(R.id.etAutorDet);
        descripcion = (TextView) findViewById(R.id.etDescripcionDet);
        descripcion.setMovementMethod(new ScrollingMovementMethod());
        anio = (TextView) findViewById(R.id.etAnioDet);
        numPaginas = (TextView) findViewById(R.id.etNumPagDet);

        btComprar = (Button) findViewById(R.id.btEbook);

        portada = (ImageView) findViewById(R.id.imgPortadaDet);

        Intent i = getIntent();
        String id = i.getStringExtra("objetoPulsado");
        //comprobamos que el id es = a no tiene (puesto por nosotros a la hora de añadir un libro), mostramos un toast
        //y finalizamos la actividad
        if(id.equals("no tiene")) {
            finish();
            showToast(R.string.InfoToastDet);
        }
        //si el id es distinto de no tiene, realizamos una busqueda a la api, por su id (ya que la api nos permite)
        //realizar consultas por id
        else{
            buscar("/" + id);
        }

        //Boton hacia atrás en el menu de arriba (action bar)
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Listener que abre un enlace a google
        btComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(urlGoogle);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //Puede ser que el libro no tenga url, por lo que capturamos la excepción y mostramos un toast
                try {
                    startActivity(intent);
                }catch(ActivityNotFoundException ex){
                    showToast(R.string.InfoToastDetError3);
                }
            }
        });

        //Cargamos las preferencias
        loadPreferences();
    }

    //Funcion para enseñar un toast
    public void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    //Para el boton de atras
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Para cargar las preferencias
    public void loadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    //Para realizar las consultas a la api
    public void buscar(String busqueda){
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
                    String auxTitulo = "";
                    String auxAutor = "";
                    String auxDescripcion = "";
                    String auxAnio = "";
                    String auxNumPag = "";
                    String auxImgUrl = "";
                    String auxLinkGoogle = "";

                    JSONObject jsonObject = new JSONObject(s);

                    JSONObject volumenInfo = jsonObject.getJSONObject("volumeInfo");
                    auxTitulo = volumenInfo.getString("title");
                    auxAutor = volumenInfo.getJSONArray("authors").getString(0);
                    auxAnio = volumenInfo.getString("publishedDate");
                    auxNumPag = volumenInfo.getString("pageCount");
                    //Hay libros que no tienen descripcion o fotos o link de google, para que no nos de excepcion
                    try {
                        auxDescripcion = volumenInfo.getString("description");
                    }catch(JSONException e){
                        auxDescripcion = "No descripcion encontrada para este libro";
                    }
                    try {
                        auxImgUrl = volumenInfo.getJSONObject("imageLinks").getString("thumbnail");
                    }catch(JSONException e){
                        auxImgUrl = "NOT FOUND";
                    }
                    try {
                        auxLinkGoogle = volumenInfo.getString("previewLink");
                    }catch(JSONException e){
                        auxLinkGoogle = "NOT FOUND";
                    }

                    //Circulo de carga para que el usuario no piense que se ha quedado la app colgada
                    progressDrawable = new CircularProgressDrawable(getApplicationContext());
                    progressDrawable.setStrokeWidth(10f);
                    progressDrawable.setStyle(CircularProgressDrawable.LARGE);
                    progressDrawable.setCenterRadius(30f);
                    progressDrawable.start();

                    //Metemos la información en nuestros elementos graficos
                    titulo.setText(auxTitulo);
                    autor.setText(auxAutor);
                    descripcion.setText(auxDescripcion);
                    anio.setText(auxAnio);
                    numPaginas.setText(auxNumPag);
                    Glide.with(getApplicationContext())
                            .load(auxImgUrl)
                            .placeholder(progressDrawable)
                            .error(R.drawable.not_found)
                            .into(portada);
                    urlGoogle = auxLinkGoogle;
                }else{
                    showToast(R.string.InfoToastDetError);
                }
            } catch (JSONException e) {
                showToast(R.string.InfoToastDetError2);
            }
        }
    }
}
