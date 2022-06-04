package com.firux_app.mons;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


public class CustomNotificationService extends Service {

    public static final String ACTION_NOTIFICATION_LITERNA = "action_notification_linterna";
    public static final String ACTION_NOTIFICATION_ONOFF = "action_notification_onoff";
    public static final String ACTION_NOTIFICATION_FILTRO = "action_notification_filtro";
    public static final String ACTION_NOTIFICATION_STOP = "action_notification_stop";

    public static boolean mykeyli = false,mykeyonoff = false,mykeyfil = false;
    public static int valorCAM = 0;
    public static int color = 0x66FFD600;
    public static float opacidad = 0.50f;
    public static RelativeLayout oViewF,oViewI;
    public static WindowManager.LayoutParams paramsF,paramsI;
    public static WindowManager wmF,wmI;
    private Context context = this;
    public static Context contextM;
    private Camera objCamera;
    private Camera.Parameters parametrosCamara;
    private static boolean keyLin = false;
    private int cont_up ;
    private CameraManager mCameraManager = null;
    public static String cameraId = null;
    private CameraCharacteristics cameraC;

    @Override
    public IBinder onBind(Intent intent) {return null;}
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleIntent(Intent intent ) {
        if( intent != null && intent.getAction() != null ) {

           /* cont_up = getSharedPreferences("PREFERENCEM", MODE_PRIVATE).getInt("cont", 0);
            if (cont_up<=1){
                mInterstitialAd = new InterstitialAd(this);
                mInterstitialAd.setAdUnitId("ca-app-pub-6179577160528312/8109360985");
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                Ads2 = new InterstitialAd(this);
                Ads2.setAdUnitId("ca-app-pub-6179577160528312/2761092654");
                Ads2.loadAd(new AdRequest.Builder().build());
            }*/

            Boolean sw = getSharedPreferences("ValorCntmoons", MODE_PRIVATE)
                    .getBoolean("swNoti", true);

            if( intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_LITERNA) ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, getResources().getString(R.string.Permisocam) , Toast.LENGTH_SHORT).show();
                    }else{
                        mykeyli = !mykeyli;
                        Usar_linterna(valorCAM);
                        if (sw)showNotification(mykeyli,mykeyonoff,mykeyfil);
                    }
                }else{
                    mykeyli = !mykeyli;
                    Usar_linterna(valorCAM);
                    if (sw)showNotification(mykeyli,mykeyonoff,mykeyfil);
                }

            } else if( intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_ONOFF) ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(context)) {
                        Toast.makeText(context, getResources().getString(R.string.PermisoPantalla) , Toast.LENGTH_SHORT).show();
                    }else{
                        mykeyonoff = !mykeyonoff;
                        if (sw)showNotification(mykeyli,mykeyonoff,mykeyfil);
                        UsarRetroiluminacion();
                    }
                }else {
                    mykeyonoff = !mykeyonoff;
                    if (sw)showNotification(mykeyli,mykeyonoff,mykeyfil);
                    UsarRetroiluminacion();
                }

            } else if( intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_FILTRO) ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(context)) {
                        Toast.makeText(context,  getResources().getString(R.string.PermisoPantalla), Toast.LENGTH_SHORT).show();
                    }else {
                        mykeyfil = !mykeyfil;
                        if (sw)showNotification(mykeyli,mykeyonoff,mykeyfil);
                        UsarFiltro();
                    }
                }else {
                    mykeyfil = !mykeyfil;
                    if (sw)showNotification(mykeyli,mykeyonoff,mykeyfil);
                    UsarFiltro();
                }

            } else if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_STOP))
                stopForeground(true);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {handleIntent( intent );
        return super.onStartCommand(intent, flags, startId);
    }
    private void showNotification( boolean key1,boolean key2,boolean key3) {

        Intent intent = new Intent(this, ControlMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,intent,PendingIntent.FLAG_CANCEL_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String NOTIFICATION_CHANNEL_ID = "com.firux_app.mons";
            String channelName = "My Background Service";

            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,NotificationManager.IMPORTANCE_LOW);
            mChannel.enableLights(true);
            mChannel.setShowBadge(true);//muestra punto cuando tiene notificacion activas
            mChannel.setImportance(NotificationManager.IMPORTANCE_LOW);
            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            } else {
                stopSelf();
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setCustomContentView(getExpandedView(key1,key2,key3))
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setAutoCancel(false)
                    .setNotificationSilent()
                    .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setShowWhen(true)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(2, notification);
        }else {
            NotificationCompat.Builder builder =
                    (NotificationCompat.Builder)
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.ic_stat_name)
                                    .setCustomContentView(getExpandedView(key1,key2,key3))
                                    .setPriority(BIND_IMPORTANT)
                                    .setContentIntent(pendingIntent)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            startForeground(1, builder.build());
        }
    }
    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        return "snap map channel";
    }

    private RemoteViews getExpandedView(boolean key1,boolean key2,boolean key3) {
        RemoteViews customView = new RemoteViews( getPackageName(), R.layout.layout_notifi );

        if( !key1 ) {
            customView.setImageViewResource( R.id.BNlit,R.mipmap.loff );
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!((ControlMain) contextM).isFinishing()){
                    if (cameraId != null){
                        if (Integer.parseInt(cameraId) == 1){
                            ControlMain.imgLD.setImageResource(R.mipmap.loff);
                        }else ControlMain.imgLT.setImageResource(R.mipmap.loff);
                    }
                }
            }else {
                if(!((ControlMain) contextM).isFinishing()){
                    if (valorCAM== 1){
                        ControlMain.imgLD.setImageResource(R.mipmap.loff);
                    }else ControlMain.imgLT.setImageResource(R.mipmap.loff);
                }
            }
        }else {
            if (keyLin){
                customView.setImageViewResource( R.id.BNlit, R.mipmap.lon );
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (cameraId != null){
                        if(!((ControlMain) contextM).isFinishing()){
                            if (Integer.parseInt(cameraId) == 1){
                                ControlMain.imgLD.setImageResource(R.mipmap.lon);
                            }else ControlMain.imgLT.setImageResource(R.mipmap.lon);
                        }
                    }
                }else {
                    if(!((ControlMain) contextM).isFinishing()){
                        if (valorCAM== 1){
                            ControlMain.imgLD.setImageResource(R.mipmap.lon);
                        }else ControlMain.imgLT.setImageResource(R.mipmap.lon);
                    }
                }
            }else {
                customView.setImageViewResource( R.id.BNlit,R.mipmap.loff );
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (cameraId != null){
                        if(!((ControlMain) contextM).isFinishing()){
                            if (Integer.parseInt(cameraId) == 1){
                                ControlMain.imgLD.setImageResource(R.mipmap.loff);
                            }else ControlMain.imgLT.setImageResource(R.mipmap.loff);
                        }
                    }

                }else{
                    if(!((ControlMain) contextM).isFinishing()){
                        if (valorCAM== 1){
                            ControlMain.imgLD.setImageResource(R.mipmap.loff);
                        }else ControlMain.imgLT.setImageResource(R.mipmap.loff);
                    }
                }
            }
        }

        if( !key2 ){
            customView.setImageViewResource( R.id.BNpower, R.mipmap.off );
            if(!((ControlMain) contextM).isFinishing())
                ControlMain.buOnOff.setImageResource(R.mipmap.offclaro);
        } else {
            customView.setImageViewResource( R.id.BNpower, R.mipmap.on );
            if(!((ControlMain) contextM).isFinishing())
                ControlMain.buOnOff.setImageResource(R.mipmap.on);
        }

        if( !key3 ){
            customView.setImageViewResource( R.id.BNfiltro, R.mipmap.foff );
            if(!((ControlMain) contextM).isFinishing())
                ControlMain.seekBarOp.setEnabled(false);
        } else{
            customView.setImageViewResource( R.id.BNfiltro, R.mipmap.fon);
            if(!((ControlMain) contextM).isFinishing())
                ControlMain.seekBarOp.setEnabled(true);
        }

        Intent intent = new Intent( getApplicationContext(), CustomNotificationService.class );
        // prueba de flags para no crear varios servicios
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);

        intent.setAction(ACTION_NOTIFICATION_LITERNA);
        PendingIntent pendingIntent = PendingIntent.getService( getApplicationContext(),
                1, intent, 0);
        customView.setOnClickPendingIntent( R.id.BNlit, pendingIntent );

        intent.setAction(ACTION_NOTIFICATION_ONOFF);
        pendingIntent = PendingIntent.getService( getApplicationContext(),
                1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.BNpower, pendingIntent );

        intent.setAction(ACTION_NOTIFICATION_FILTRO);
        pendingIntent = PendingIntent.getService( getApplicationContext(),
                1, intent, 0 );
        customView.setOnClickPendingIntent( R.id.BNfiltro, pendingIntent );
        return customView;
    }

   /* private void publicidad() {
        cont_up = getSharedPreferences("PREFERENCEM", MODE_PRIVATE).getInt("cont", 0);
        cont_up++;
        getSharedPreferences("PREFERENCEM", MODE_PRIVATE).edit().putInt("cont",cont_up).apply();
        if (cont_up >=4){
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                getSharedPreferences("PREFERENCEM", MODE_PRIVATE).edit().putInt("cont",0).apply();
            } else {
                if (Ads2 != null && Ads2.isLoaded()){
                    Ads2.show();
                    Ads2.loadAd(new AdRequest.Builder().build());
                    getSharedPreferences("PREFERENCEM", MODE_PRIVATE).edit().putInt("cont",0).apply();
                }
            }
        }
    }*/

    public  void Usar_linterna(int valor){
        getSharedPreferences("ValorCntmoons", MODE_PRIVATE)
                .edit()
                .putInt("Valorcam", valor)
                .apply();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                cameraId = mCameraManager.getCameraIdList()[valor];
                cameraC = mCameraManager.getCameraCharacteristics(cameraId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cameraC.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                if (mykeyli){
                    //encender
                    try {
                      //  cameraId = mCameraManager.getCameraIdList()[valor];
                        mCameraManager.setTorchMode(cameraId, true);
                        mykeyli = true;
                        keyLin = true;

                        if(!((ControlMain) contextM).isFinishing()){
                            if (valor == 0)ControlMain.LD.setEnabled(false);
                            if (valor == 1)ControlMain.LT.setEnabled(false);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }else{
                    //apagar
                    try {
                      //  cameraId = mCameraManager.getCameraIdList()[valor];
                        mCameraManager.setTorchMode(cameraId, false);
                        mykeyli = false;
                        keyLin = false;

                        if(!((ControlMain) contextM).isFinishing()){
                            if (valor == 0)ControlMain.LD.setEnabled(true);
                            if (valor == 1)ControlMain.LT.setEnabled(true);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            } else  {
                mykeyli = false;
                Toast.makeText(context,getResources().getString(R.string.NoFlash),Toast.LENGTH_LONG).show();
            }
        }else {
                // camara para menos de la version 21 lollipop
            // se debe agergar systemfeature para poder usar la camara

            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                try {
                    // alista la camara para su uso
                    if (objCamera!=null)objCamera.release();
                    objCamera = Camera.open(valor);
                    parametrosCamara = objCamera.getParameters();
                }catch (Exception e){
                    if (objCamera!=null)objCamera.release();
                }
                if (mykeyli) {
                    //encender
                    try {
                        parametrosCamara.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        objCamera.setParameters(parametrosCamara);
                        objCamera.startPreview();
                        mykeyli = true;
                        keyLin = true;
                        if(!((ControlMain) contextM).isFinishing()){
                            if (valor == 0)ControlMain.LD.setEnabled(false);
                            if (valor == 1)ControlMain.LT.setEnabled(false);
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                        objCamera.release();
                    }
                } else {
                    //apagar
                    try {
                        parametrosCamara.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        objCamera.setParameters(parametrosCamara);
                        objCamera.stopPreview();
                        objCamera.release();
                        mykeyli = false;
                        keyLin = false;
                        if(!((ControlMain) contextM).isFinishing()){
                            if (valor == 0)ControlMain.LD.setEnabled(true);
                            if (valor == 1)ControlMain.LT.setEnabled(true);
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                        objCamera.release();
                    }
                }
            }else {
                if (valor==1){
                    if (objCamera!=null)objCamera.release();
                    Toast.makeText(context,getResources()
                            .getString(R.string.NoFfrontal),Toast.LENGTH_LONG).show();
                }else if (valor==0){
                    if (objCamera!=null)objCamera.release();
                    Toast.makeText(context,getResources()
                            .getString(R.string.NoFlash),Toast.LENGTH_LONG).show();
                }mykeyli = false;
            }
        }
    }

    public  void UsarFiltro(){
        if (mykeyfil){
            try {
                oViewF = new RelativeLayout(this);
                oViewF.setBackgroundColor(color);
                oViewF.setAlpha(opacidad);
            }catch (Exception e){

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                paramsF = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }else {
                paramsF = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }

            wmF = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT < 21) {
                paramsF.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                        WindowManager.LayoutParams.FLAG_FULLSCREEN|
                     //   WindowManager.LayoutParams.FLAG_SECURE|
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                DisplayMetrics metrics = new DisplayMetrics();
                wmF.getDefaultDisplay().getMetrics(metrics);
                int alto = metrics.heightPixels;
                paramsF.height = alto+300;
            }else {
                //apartir de version 21

             //   paramsF.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
                paramsF.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                         WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                DisplayMetrics metrics = new DisplayMetrics();
                wmF.getDefaultDisplay().getMetrics(metrics);
                int alto = metrics.heightPixels;
                paramsF.height = alto+300;
            }
            wmF.addView(oViewF, paramsF);
        }else {
            if(oViewF !=null){
                wmF = (WindowManager) getSystemService(WINDOW_SERVICE);
                wmF.removeView(oViewF);
            }
        }
    }

    public  void UsarRetroiluminacion(){
        if (mykeyonoff){
            oViewI = new RelativeLayout(this);
            oViewI.setBackgroundColor(0xbb000000);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                paramsI = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }else {
                paramsI = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }

            wmI = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT < 21) {
                paramsI.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                 WindowManager.LayoutParams.FLAG_FULLSCREEN|
                 WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                DisplayMetrics metrics = new DisplayMetrics();
                wmI.getDefaultDisplay().getMetrics(metrics);
                int alto = metrics.heightPixels;
                paramsI.height = alto+300;
            }else {
                //apartir de version 21 LOLLIPOP
                paramsI.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                DisplayMetrics metrics = new DisplayMetrics();
                wmI.getDefaultDisplay().getMetrics(metrics);
                int alto = metrics.heightPixels;
                paramsI.height = alto+300;
            }
            wmI.addView(oViewI, paramsI);
        }else{
            if(oViewI !=null){
                wmI = (WindowManager) getSystemService(WINDOW_SERVICE);
                wmI.removeView(oViewI);
            }
        }
    }
}