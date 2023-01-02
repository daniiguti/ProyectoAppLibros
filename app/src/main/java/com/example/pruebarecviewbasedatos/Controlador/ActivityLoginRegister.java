package com.example.pruebarecviewbasedatos.Controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.captaindroid.tvg.Tvg;
import com.example.pruebarecviewbasedatos.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import java.io.File;

public class ActivityLoginRegister extends AppCompatActivity {
    //Variables necesarias
    private EditText txtContrasenia;
    private EditText txtUsuario;
    private Button btLogin;
    private Button btSignup;
    private Button btVerPass;
    private CheckBox cbRecordarDatos;
    private TextView tvInicioSesion;

    private ComprobarLoginRegister clr;
    private File ruta;
    private GuardarUser gu;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().hide();

        //Inicialización de variables
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtContrasenia = (EditText) findViewById(R.id.txtContrasenia);
        btLogin = (Button) findViewById(R.id.btLogin);
        btSignup = (Button) findViewById(R.id.btSignup);
        btVerPass = (Button) findViewById(R.id.btVerPass);
        cbRecordarDatos = (CheckBox) findViewById(R.id.cbRecordarDatos);
        tvInicioSesion = (TextView) findViewById(R.id.etInfoLog);

        ruta = getExternalFilesDir(null);
        gu = GuardarUser.getGuardarUser(ruta);

        clr = new ComprobarLoginRegister(this);

        //Listener del boton login
        //Cuando se pulse realizara una consulta a la bdd comprobando si el usuario y la contraseña existen
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = txtUsuario.getText().toString();
                String pass = txtContrasenia.getText().toString();
                if (user.equals("") == false && pass.equals("") == false) {
                    if (clr.consultar(user, pass) == true) {
                        showToast(R.string.InfoToastLog1);
                        //Metodo para que cuando se pulse el boton de login
                        //comprobar si esta checked el combo box o no, para guardar la contraseña
                        if (cbRecordarDatos.isChecked()) {
                            gu.guardarFichero(user, pass);
                            savePrefs("guardarCambios", true);
                        } else {
                            gu.borrarFichero();
                            savePrefs("guardarCambios", false);
                        }
                        Intent i = new Intent(ActivityLoginRegister.this, ActivityMain.class);
                        startActivity(i);
                        finish();
                    } else {
                        showToast(R.string.InfoToastLog2);
                    }
                }
                else{
                    showToast(R.string.InfoToastLog5);
                }
            }
        });

        //Listener del boton signup
        //Cuando se pulse realizara una consulta a la bdd comprobando si el usuario existe
        //si existe dará un mensaje de error diciendo que ya existe el usuario.
        //sino existe lo dará de alta.
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = txtUsuario.getText().toString();
                String pass = txtContrasenia.getText().toString();
                if(user.equals("") == false && pass.equals("") == false) {
                    long codError = clr.insertar(user, pass);
                    if (codError == -1) {
                        showToast(R.string.InfoToastLog3);
                    } else {
                        showToast(R.string.InfoToastLog4);
                    }
                }
                else{
                    showToast(R.string.InfoToastLog5);
                }
            }
        });

        //Listener del boton ver contraseña
        //Cuando se activa la muestra, cuando lo desactiva la esconde
        btVerPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch ( event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        txtContrasenia.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        txtContrasenia.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });
        //Para cuando se pincha el checkBox (ya que puede pincharlo y despues no darle a inicar sesion)
        cbRecordarDatos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String user = txtUsuario.getText().toString();
                String pass = txtContrasenia.getText().toString();
                if (user.equals("") == false && pass.equals("") == false) {
                    if (cbRecordarDatos.isChecked()) {
                        gu.guardarFichero(user, pass);
                        savePrefs("guardarCambios", true);
                    } else {
                        gu.borrarFichero();
                        savePrefs("guardarCambios", false);
                    }
                }
                else{
                    cbRecordarDatos.setChecked(false);
                    showToast(R.string.InfoToastLog5);
                }
            }
        });

        //Cambio de color gradiente text view (con una biblioteca externa)
        Tvg.change(tvInicioSesion, new int[]{
                Color.parseColor("#F97C3C"),
                Color.parseColor("#FDB54E"),
                Color.parseColor("#64B678"),
                Color.parseColor("#478AEA"),
                Color.parseColor("#8446CC"),
        });

        //Para cargar las preferencias
        loadPreferences();
    }

    //Metodo para guardar las preferencias de Recordar Contraseña
    public void savePrefs(String key, Boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    //Método para cargar las preferencias
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

        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean data = sharedPreferences2.getBoolean("guardarCambios", false);
        if(data == true){
            gu.leerFichero();
            String user = gu.devolverUser();
            String pass = gu.devolverPass();
            txtUsuario.setText(user);
            txtContrasenia.setText(pass);
            cbRecordarDatos.setChecked(true);
        }
    }

    //Método para mostrar los Toasts
    public void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }
}
