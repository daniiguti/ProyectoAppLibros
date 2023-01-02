package com.example.pruebarecviewbasedatos.Controlador;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.pruebarecviewbasedatos.R;

public class ActivityBuscarLibros extends AppCompatActivity {
    //Variables necesarias
    private String titulo = "";
    private String autor = "";
    private String maxResult = "";
    private String orderBy = "";
    public String comando = "";

    private EditText txtTitulo;
    private EditText txtAutor;

    private RadioGroup rgOrdenarPor;
    private RadioGroup rgBusqueda;

    private Button btAplicar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_libros);
        //Inicializacion de variables
        Intent i = getIntent();

        txtTitulo = (EditText) findViewById(R.id.txtTituloBus);
        txtAutor = (EditText) findViewById(R.id.txtAutorBus);

        rgOrdenarPor = (RadioGroup) findViewById(R.id.rgOrdenarPor);
        rgBusqueda = (RadioGroup) findViewById(R.id.rgBusqueda);

        btAplicar = (Button) findViewById(R.id.btAplicarBus);

        //Listeners de los radioGroup
        //Comprobamos que radioButton se pulso y vamos formando el filtro para la api
        rgOrdenarPor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rgOrdenarPor.findViewById(checkedId);
                int index = rgOrdenarPor.indexOfChild(radioButton);
                switch (index) {
                    case 0: //Boton ordenar por nuevos
                        orderBy = "orderBy=newest";
                        break;
                    case 1: //Boton ordenar por relevancia
                        orderBy = "orderBy=relevance";
                        break;
                }
            }
        });
        rgBusqueda.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = rgBusqueda.findViewById(checkedId);
                int index = rgBusqueda.indexOfChild(radioButton);
                switch (index) {
                    case 0: //Boton busqueda Avanzada
                        maxResult = "maxResults=15";
                        break;
                    case 1: //Boton busqueda Rapida
                        maxResult = "maxResults=5";
                        break;
                }
            }
        });
        //Listener del boton aceptar
        btAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Vamos formando nuestro filtro de busqueda para la api
                titulo = txtTitulo.getText().toString();
                autor = txtAutor.getText().toString();
                comando = "?q=";
                if(titulo.equals("") == false){
                    comando = comando + titulo;
                }

                if(autor.equals("") == false){
                    comando = comando + "+inauthor:" + autor;
                }

                if(orderBy.equals("") == false){
                    comando = comando + "&" + orderBy;
                }

                if(maxResult.equals("") == false){
                    comando = comando + "&" + maxResult;
                }
                else{
                    comando = comando + "&maxResults=7";
                }
                //Al menos obligamos a que introduzca un filtro
                if(titulo.equals("") == false || autor.equals("") == false){
                    //Devolvemos el intent con el comando, para que en el main
                    //(donde tenemos nuestro adapter) añada al arrayList del recAdapter
                    Intent returnIntent = new Intent();

                    returnIntent.putExtra("busqueda", comando);

                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.InfoToastBus, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Añadimos a nuestro menu de arriba una flecha para ir hacia atrás
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Cargamos las preferencias
        loadPreferences();
    }

    //PARA LAS PREFERENCIAS
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

    //Boton de atras
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
