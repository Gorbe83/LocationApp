package com.example.gerardo.locationapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.example.gerardo.locationapp";
    private AlarmManager alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //-----------INICIO DE ALARMA-------------
    /* 1. Se obtiene un Intent de MyAlarmReceiver
    *  2. Se crea un objeto pIntent para obtener el Broadcast de la Alarma, además se obtiene el
    *     REQUEST_CODE ya que se ocupará para detener la alarma después. El FLAG_UPDATE_CURRENT se
    *     asegura de que, si la alarma se dispara muy rápido, los eventos se remplazaran en lugar
    *     de acumularse.
    *  3. Se crea el objeto alarm del tipo AlarmManager para obtener el servicio de la alarma
    *     (del Manifest supongo)
    *  4. Se especifica el tipo de Repetición de la alarma:
    *       -No será exacto (no es necesario y así no se consume tanta batería)
    *       -ELAPSED_REALTIME_WAKEUP inicia la alarma aún si el disp. está en suspensión y después
    *        del tiempo espeficidado porterior al cuando el disp. se inició.
    *       -El segundo valor es el tiempo en que queremos inicie la alarma por PRIMERA VEZ
    *        (en este caso, 1000 milisegundos (1 segundo) después del tiempo actual que lleva
    *        prendido el disp.
    *       -El tercer valor especifica cada cuando se repetirá la alarma (10 segundos), sin embargo
    *        NO será completamente exacto.
    *       -El último valor es el objeto pIntent.
    *
    *  NOTA: Tanto como MyLocationServer como MyAlarmReceiver deben estar dados de altas en el
    *        Manifest. El primero como <service> y el segundo como <receiver>
    * */
    public void StartLocationAlarm(View view) {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent i = new Intent(getApplicationContext(), MyAlarmReceiver.class);

            PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                    i, PendingIntent.FLAG_UPDATE_CURRENT);

            alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000,
                    20000, pIntent);
            Toast.makeText(this, "Alarma iniciada", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Alarma iniciada");
        } else {
            Toast.makeText(this, "El sistema GPS está desactivado. ", Toast.LENGTH_SHORT).show();
        }

    }

    //------------DETENER ALARMA-----------------
    /*
        1. Se crea un objeto Intent el cual obtendrá la clase MyAlarmReceiver
        2. Se crea un PedingIntent como al iniciar la alarma.
        3. Se crea un objeto AlarmManager que obtiene el servicio de alarma
        4. Se cancelará únicamente la alarma cuyo REQUEST_CODE concuerde con la que está actualmente
           habilitada.
     */
    public void StopLocationAlarm(View view) {
        Intent i = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE, i, 0);
        alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);

        Log.i(TAG, "Alarma detenida");
        Toast.makeText(this, "Alarma detenida", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
