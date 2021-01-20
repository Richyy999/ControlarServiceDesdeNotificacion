package es.rbp.controlar_service_desde_notificacion.servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Ricardo Bordería Pi
 * <p>
 * {@link BroadcastReceiver} que recibe y envía las instrucciones procedentes de los botones de la notificación
 */
public class ServicioAccionNotificacion extends BroadcastReceiver {

    /**
     * Código de request del broadcast
     */
    public static final int REQUEST_CODE = 0;

    /**
     * Filtro para el {@link android.content.IntentFilter} para identificar las instrucciones enviadas desde esta clase
     */
    public static final String FILTRO_INTENT = "mensaje";

    /**
     * Extra para identificar la acción enviada en el intent
     */
    public static final String ACCION_EXTRA = "accion_extra";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(FILTRO_INTENT).putExtra(ACCION_EXTRA, intent.getAction()));
    }
}
