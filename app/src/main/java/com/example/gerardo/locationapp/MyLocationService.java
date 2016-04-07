package com.example.gerardo.locationapp;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.OutputStreamWriter;
import java.util.Calendar;


public class MyLocationService extends IntentService implements
        LocationListener {

    private static final String TAG = "com.example.gerardo.locationapp";
    private LocationManager mLocationManager;
    private Criteria criteria;
    private boolean currentlyProcessingLocation;

    public MyLocationService() {
        super("MyLocationService");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "GPS desactivado");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "Proveedor: " + provider);
        switch(status) {
            case 0:
                Log.i(TAG, "Proveedor fuera de servicio");
                break;
            case 1:
                Log.i(TAG, "Proveedor temporalmente no disponible");
                break;
            case 2:
                Log.i(TAG, "Proveedor disponible");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "GPS Activado");
    }

    @Override
    public void onCreate() {
        currentlyProcessingLocation = false;
        super.onCreate();
    }


    //--------------OBTENER LA LOCALIZACIÓN------------------
    /* Se usó el método onStartCommand porque, después de onCreate, es el siguiente método a ejecutar
       en un IntentService.
       1. Primero se valida si NO se está ejecutando un proceso de localización actualmente. La razón
          de esto es que en este ejemplo, al usar intervalos de 10 segundos era probable que obtener
          la localización tomara más tiempo del debido (usar GPS lo hace tardado) y así no se tienen
          dos IntentServices o más ejecutandose al mismo tiempo.
       2. Se da al mLocationManager el servicio de ubicación.
       3. Se crea un nuevo objeto criteria de tipo Criteria, el cual obtendrá el mejor servicio a utilizar
          (GPS, Wi-Fi, Datos) tomando en cuenta: Precisión, consumo de batería, tiempo de respuesta,
          costo monetario)
       4. Se valida si el servicio cuenta con los permisos de ACCESS_FINE_LOCATION
       5. Se solicita una actualización de ubicación utilizando el objeto criteria. En el momento
          que llegue una nueva ubicación, el método OnLocationChanged se activará.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Esperando localización...");
        if (currentlyProcessingLocation == false) {
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            currentlyProcessingLocation = true;

            criteria = new Criteria();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestSingleUpdate(criteria,this,getMainLooper());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        //Se imprime en el Log la Longitud y Latitud del location obtenido
        Log.i(TAG, "Longitud: " + String.valueOf(location.getLongitude()));
        Log.i(TAG, "Latitud: " + String.valueOf(location.getLatitude()));
        writeToFile(String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
    }

    private void writeToFile(String longitud, String latitud) {
        try {
            Calendar calendar = Calendar.getInstance();
            String ubicacion = "Fecha: " + calendar.get(Calendar.YEAR) +
                    "/" + calendar.get(Calendar.MONTH) +
                    "/" + calendar.get(Calendar.DAY_OF_MONTH) +
                    "|Hora: " + calendar.get(Calendar.HOUR_OF_DAY) +
                    ":" + calendar.get(Calendar.MINUTE) +
                    "|Longitud: " + longitud +
                    "|Latitud: " + latitud + "\n";
            OutputStreamWriter outSWMensaje = new OutputStreamWriter(
                    openFileOutput("coordenadas.txt", Context.MODE_APPEND));
            outSWMensaje.write(ubicacion);
            outSWMensaje.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        currentlyProcessingLocation = false;
    }

}
