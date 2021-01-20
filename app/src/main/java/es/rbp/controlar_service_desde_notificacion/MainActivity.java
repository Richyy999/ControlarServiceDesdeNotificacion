package es.rbp.controlar_service_desde_notificacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.rbp.controlar_service_desde_notificacion.servicios.ServicioContador;

/**
 * Esta activity empieza, pausa o termina el {@link ServicioContador} y muestra el segundo actual del servicio
 * <p>
 * Para conectarse al servicio, hay que implementar la interfaz {@link ServiceConnection} y llamar al método {@link Context#bindService(Intent, ServiceConnection, int)}.
 * Solo puede haber un cliente por servicio, por lo que hay que llamar al método {@link Context#unbindService(ServiceConnection)} cuando se detenga
 * el activity para que cuando se cree otra vez poder conectarse al servicio de nuevo.
 *
 * @author Ricardo Bordería Pi
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection, ServicioContador.Llamada {

    /**
     * TextView que muestra el segundo actual del servicio
     */
    private TextView lblSegundoActual;

    /**
     * Instancia del servicio para acceder a sus métodos
     */
    private ServicioContador servicio;

    /**
     * Intent para identificar el servicio
     */
    private Intent intent;

    /**
     * Estado actual de la cuenta del servicio.
     * <p>
     * Puede ser {@link ServicioContador#ESTADO_DETENIDO}, {@link ServicioContador#ESTADO_PAUSADO} o {@link ServicioContador#ESTADO_CORRIENDO}
     */
    private int estadoServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnEmpezar = findViewById(R.id.btnEmpezar);
        Button btnParar = findViewById(R.id.btnParar);
        Button btnPausar = findViewById(R.id.btnPausar);

        btnEmpezar.setOnClickListener(this);
        btnParar.setOnClickListener(this);
        btnPausar.setOnClickListener(this);

        lblSegundoActual = findViewById(R.id.lblContador);

        intent = new Intent(this, ServicioContador.class);

        estadoServicio = ServicioContador.ESTADO_DETENIDO;
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        Log.d("MAIN ACTIVITY", "STOP");
    }

    @Override
    protected void onDestroy() {
        Log.i("ACTIVITY", "DESTRUIDO");
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnEmpezar)
            empezarServicio();
        else if (id == R.id.btnParar)
            servicio.stop();
        else if (id == R.id.btnPausar)
            servicio.pause();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ServicioContador.LocalBinder binder = (ServicioContador.LocalBinder) service;
        servicio = binder.getServiceInstance();
        servicio.registrarActivity(MainActivity.this);
        actualizarContador(servicio.cargarSegundo());
        Log.i("SERVICIO", "REGISTRADO");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void actualizarContador(int segundoActual) {
        lblSegundoActual.setText(String.valueOf(segundoActual));
    }

    @Override
    public void actualizarEstado(int estado) {
        estadoServicio = estado;
        if (estado == ServicioContador.ESTADO_DETENIDO) {
            lblSegundoActual.setText(String.valueOf(ServicioContador.SEGUNDO_POR_DEFECTO));
        }
    }

    /**
     * Si {@link MainActivity#estadoServicio} es diferente a {@link ServicioContador#ESTADO_CORRIENDO} empieza un {@link ServicioContador}.
     */
    private void empezarServicio() {
        if (estadoServicio != ServicioContador.ESTADO_CORRIENDO)
            startForegroundService(intent);
    }
}
