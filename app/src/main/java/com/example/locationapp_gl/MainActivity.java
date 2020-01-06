package com.example.locationapp_gl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    TextInputLayout txtInputCode;
    TextInputEditText txtEditCode;
    TextInputLayout txtInputName;
    TextInputEditText txtEditName;
    TextInputLayout txtInputLat;
    TextInputEditText txtEditLat;
    TextInputLayout txtInputLang;
    TextInputEditText txtEditLang;

    Button btnCapture;
    Button btnSave;

    //variables para la localizacion
    LocationManager locationManager; //administrador
    Location location; //localizacion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtInputCode = (TextInputLayout) findViewById(R.id.txtInputCode);
        txtEditCode = (TextInputEditText) findViewById(R.id.txtEditCode);
        txtInputName = (TextInputLayout) findViewById(R.id.txtInputName);
        txtEditName = (TextInputEditText) findViewById(R.id.txtEditName);

        txtInputLat = (TextInputLayout) findViewById(R.id.txtInputLat);
        txtEditLat = (TextInputEditText) findViewById(R.id.txtEditLat);
        txtInputLang = (TextInputLayout) findViewById(R.id.txtInputLang);
        txtEditLang = (TextInputEditText) findViewById(R.id.txtEditLang);

        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnSave = (Button) findViewById(R.id.btnSave);

        btnCapture.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        btnSave.setVisibility(View.GONE);


    }

    private void clean(){

        txtEditCode.setText("");
        txtEditName.setText("");
        txtEditLat.setText("");
        txtEditLang.setText("");
        btnSave.setVisibility(View.GONE);
        btnCapture.setVisibility(View.VISIBLE);

        txtInputCode.setError(null);
        txtInputName.setError(null);
        txtEditCode.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCapture:
               // Toast.makeText(this,"longitud y Latitud",Toast.LENGTH_SHORT).show();
                //solicitar permisos
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 //   // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
                    Toast.makeText(this, "sin permisos", Toast.LENGTH_SHORT).show();
                } else {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    assert locationManager != null;
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //GPS_PROVIDER es para el emulador
                    //NETWORK_PROVIDER es para el telefono
                    if(location != null){
                        txtEditLat.setText(String.valueOf(location.getLatitude()));
                        txtEditLang.setText(String.valueOf(location.getLongitude()));
                        btnSave.setVisibility(View.VISIBLE);
                        btnCapture.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(this, "sin resultados", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btnSave:
              connection db = new connection(this, "bdusuario", null, 1);
                SQLiteDatabase baseDatos = db.getWritableDatabase();
                String code = txtEditCode.getText().toString();
                String name = txtEditName.getText().toString();
                String lat = txtEditLat.getText().toString();
                String lang = txtEditLang.getText().toString();


                if ((!code.isEmpty() && (!name.isEmpty()) && (!lat.isEmpty()) && (!lang.isEmpty()) )) {

                    ContentValues registro = new ContentValues();

                    Cursor fila = baseDatos.rawQuery("SELECT * FROM usuario WHERE codigo = " + code, null);

                    if (fila.getCount() <= 0){
                        registro.put("codigo", code);
                        registro.put("nombre", name);
                        registro.put("latitud", lat);
                        registro.put("longitud", lang);

                        baseDatos.insert("usuario", null, registro);
                        baseDatos.close();
                       clean();
                        Toast.makeText(this, "Se registro un usuario", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "el cóigo ya existe", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    txtInputCode.setError("Ingrese Código.");
                    txtInputName.setError("Ingrese un nombre.");
                    //txtInputLat.setError("Ingrese la latitud.");
                    //txtInputLang.setError("Ingrese la longitud.");
                    Toast.makeText(this, "Hay campos vacios.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
