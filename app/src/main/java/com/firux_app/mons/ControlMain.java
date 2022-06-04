package com.firux_app.mons;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ControlMain extends AppCompatActivity {

    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.bucolor)
    Button fb;
    @BindView(R.id.txtPercentage)
    TextView txtPercentage;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.textLT)
    TextView textLT;
    @BindView(R.id.textLD)
    TextView textLD;
    @BindView(R.id.textANotifi)
    TextView textANotifi;
    @BindView(R.id.textC)
    TextView textC;
    @BindView(R.id.viewLD)
    View viewLD;
    @BindView(R.id.VistaADS)
    RelativeLayout VistaADS;

    private int brightness, brightnessMode = 0;
    Context context = this;
    public static SwitchCompat switchANotifi;
    private Sensor sensorDeLuz = null;
    private SensorManager sensorManager;
    public static ImageView buOnOff, imgLT, imgLD;
    public static SeekBar seekBarOp;
    private static final int MY_CAMERA = 0;
    private static final int MY_GPS = 1;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView adView;
    private int var;
    private CameraManager mCameraManager;
    private String cameraId = null;
    public static RelativeLayout LD,LT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        textLT.setSelected(true);
        textLD.setSelected(true);
        textANotifi.setSelected(true);
        textC.setSelected(true);
        buOnOff = findViewById(R.id.buttonOnOff);
        imgLT = findViewById(R.id.imgLT);
        imgLD = findViewById(R.id.imgLD);
        LD = findViewById(R.id.LD);
        LT = findViewById(R.id.LT);

        seekBarOp = findViewById(R.id.opcidad);
        seekBar.setMax(255);
        seekBar.setKeyProgressIncrement(1);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorDeLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        switchANotifi = findViewById(R.id.switchANotifi);



      /*  //forzar error de Crl
        Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash"); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));*/


      /** coniguracion de vista para encender ya activar led **/
        if (CustomNotificationService.mykeyli){
            int valorcam = getSharedPreferences("ValorCntmoons", MODE_PRIVATE)
                    .getInt("Valorcam", 0);
                if (valorcam == 1){
                    imgLD.setImageResource(R.mipmap.lon);
                }else imgLT.setImageResource(R.mipmap.lon);

                if (valorcam == 0)LD.setEnabled(false);
                if (valorcam == 1)LT.setEnabled(false);
        }else{
            imgLT.setImageResource(R.mipmap.loff);
            imgLD.setImageResource(R.mipmap.loff);
        }

        int progresalfa = getSharedPreferences("ValorCntmoons", MODE_PRIVATE)
                .getInt("Varalfa", 50);
        seekBarOp.setProgress(progresalfa);
        if (CustomNotificationService.mykeyfil){
            seekBarOp.setEnabled(true);
        }else seekBarOp.setEnabled(false);

        if (CustomNotificationService.mykeyonoff) {
            buOnOff.setImageResource(R.mipmap.on);
        } else buOnOff.setImageResource(R.mipmap.offclaro);


        // establecer dispositivo de prueba ADS google
        /*    honor 10 lite - android 10
              sm j5 - android 5.1*/

        /*List<String> testDeviceIds = Arrays.asList("1945182B60858573B05550E1254F43AF",
               "CCF94467116A8075DC128603DC372C28");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);*/


        ConteoShowRes();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                adView = new AdView(context);
                adView.setAdUnitId("ca-app-pub-6179577160528312/2202428187");
                VistaADS.addView(adView);
                loadBanner();
            }
        });


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(MY_GPS);
            }
        }


        /////////////////////////////////////////////////////////////////
        if (CustomNotificationService.color == 0x99FFD600)
            fb.setBackground(getResources().getDrawable(R.drawable.amarillo));
        if (CustomNotificationService.color == 0x99ff0000)
            fb.setBackground(getResources().getDrawable(R.drawable.rojo));
        if (CustomNotificationService.color == 0x9900ff00)
            fb.setBackground(getResources().getDrawable(R.drawable.verde));

        Boolean sw = getSharedPreferences("ValorCntmoons", MODE_PRIVATE)
                .getBoolean("swNoti", true);
        switchANotifi.setChecked(sw);

        try {
            brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        seekBar.setProgress(brightness);
        float perc = (brightness / (float) 255) * 100;
        int valorF = (int) perc;
        txtPercentage.setText(valorF + " %");

        //brillo
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
                try {
                    brightnessMode = Settings.System.getInt(getContentResolver(),
                            Settings.System.SCREEN_BRIGHTNESS_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (PermissionWriteSetting()) {
                    if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        Settings.System.putInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS_MODE,
                                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        Settings.System.putInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, brightness);
                    } else
                        Settings.System.putInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, brightness);
                }
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (PermissionWriteSetting()) {
                    brightness = progress;
                    float perc = (brightness / (float) 255) * 100;
                    int valorF = (int) perc;
                    txtPercentage.setText(valorF + " %");
                }
            }
        });

        //opacidad
        seekBarOp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (PermissionAlertWindow()) {
                    if (CustomNotificationService.mykeyfil) {
                        IrFiltro();
                        IrFiltro();
                    }
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float perc = (i / (float) 255) * 100;
                CustomNotificationService.opacidad = perc / 100 * 2;
                getSharedPreferences("ValorCntmoons", MODE_PRIVATE)
                        .edit()
                        .putInt("Varalfa", i)
                        .apply();
            }
        });

        // verificar si hay cmara frontal
        checkflashD(context);
    }

    private void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
        /*RelativeLayout.LayoutParams lp = (new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);//abajo
        adView.setLayoutParams(lp);
        VistaADS.setLayoutParams(lp);*/
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float hightpixels = outMetrics.heightPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        int adheight = (int) (hightpixels / density);
        return new AdSize(adWidth, adheight / 3);
    }

    private void ConteoShowRes() {
        //importante cambiar el nombre deest share para cada app
        var = getSharedPreferences("ValorCntmoons", MODE_PRIVATE).getInt("Var", 0);
        if (var <= 50) {
            var++;
            getSharedPreferences("ValorCntmoons", MODE_PRIVATE).edit().putInt("Var", var).apply();
            // Toast.makeText(context, String.valueOf(var), Toast.LENGTH_SHORT).show();
            if (var == 25) CalificarApp();
            if (var == 50) CalificarApp();
        }
    }

    private void CalificarApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //debe ser para API > 21
            ReviewManager manager = ReviewManagerFactory.create(context);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful()) {
                        // We can get the ReviewInfo object
                        ReviewInfo reviewInfo = task.getResult();
                        Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                        flow.addOnCompleteListener(task2 -> {
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void checkflashD(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    cameraId = mCameraManager.getCameraIdList()[1];
                    CameraCharacteristics cameraC =
                    mCameraManager.getCameraCharacteristics(cameraId);
                    if (cameraC.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)){
                        LD.setVisibility(View.VISIBLE);
                        viewLD.setVisibility(View.VISIBLE);
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean PermissionAlertWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                Snackbar snackbar = Snackbar.make(activityMain,
                        getResources().getString(R.string.PermisoPantalla), Snackbar.LENGTH_LONG);
                snackbar.setAction(getResources().getString(R.string.Activar), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.getPackageName()));
                        startActivity(myIntent);
                    }
                });
                snackbar.show();
            } else return true;
        } else return true;
        return false;
    }

    private boolean PermissionWriteSetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                Snackbar snackbar = Snackbar.make(activityMain,
                        getResources().getString(R.string.Permisoconfi), Snackbar.LENGTH_LONG);
                snackbar.setAction(getResources().getString(R.string.Activar), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + context.getPackageName()));
                        startActivity(myIntent);
                    }
                });
                snackbar.show();
            } else return true;
        } else return true;
        return false;
    }

    public void IrFiltro() {
        if (PermissionAlertWindow()) {
            Intent intent = new Intent(context, CustomNotificationService.class);
            // prueba de flags para no crear varios servicios
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_CLEAR_TASK|
                    Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
            intent.setAction(CustomNotificationService.ACTION_NOTIFICATION_FILTRO);
            startService(intent);
            CustomNotificationService.contextM = context;

        }
    }

    @OnClick({R.id.buttonfiltro, R.id.buttonOnOff, R.id.modoAutomatico,
            R.id.LD, R.id.LT, R.id.bucolor, R.id.invite, R.id.switchANotifi})

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bucolor:
                final String[] items = getResources().getStringArray(R.array.colores);
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(context)
                        .setIcon(R.drawable.fon)
                        .setTitle(getResources().getString(R.string.selColor))
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    CustomNotificationService.color = 0x66FFD600;
                                    IrFiltro();
                                    IrFiltro();
                                    fb.setBackground(getResources().getDrawable(R.drawable.amarillo));
                                }
                                if (item == 1) {
                                    CustomNotificationService.color = 0x66ff0000;
                                    IrFiltro();
                                    IrFiltro();
                                    fb.setBackground(getResources().getDrawable(R.drawable.rojo));
                                }
                                if (item == 2) {
                                    CustomNotificationService.color = 0x6600ff00;
                                    IrFiltro();
                                    IrFiltro();
                                    fb.setBackground(getResources().getDrawable(R.drawable.verde));
                                }
                            }
                        }).setNeutralButton(getResources().getString(R.string.cancelar), null);
                AlertDialog dialog = builder2.create();
                dialog.show();
                break;

            case R.id.buttonfiltro:
                IrFiltro();
                break;

            case R.id.switchANotifi:
                if (!switchANotifi.isChecked()) {
                    Intent intent = new Intent(context, CustomNotificationService.class);
                    // prueba de flags para no crear varios servicios
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TASK|
                            Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
                    intent.setAction(CustomNotificationService.ACTION_NOTIFICATION_STOP);
                    startService(intent);
                    CustomNotificationService.contextM = context;

                }
                getSharedPreferences("ValorCntmoons", MODE_PRIVATE).
                        edit().putBoolean("swNoti", switchANotifi.isChecked()).apply();
                break;

            case R.id.buttonOnOff:
                if (PermissionAlertWindow()) {
                    Intent intent2 = new Intent(context, CustomNotificationService.class);
                    // prueba de flags para no crear varios servicios
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                            Intent.FLAG_ACTIVITY_CLEAR_TASK|
                            Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
                    intent2.setAction(CustomNotificationService.ACTION_NOTIFICATION_ONOFF);
                    startService(intent2);
                    CustomNotificationService.contextM = context;

                    if (!CustomNotificationService.mykeyonoff) {
                        buOnOff.setImageResource(R.mipmap.on);
                    } else buOnOff.setImageResource(R.mipmap.offclaro);
                }
                break;

            case R.id.modoAutomatico:
                if (sensorDeLuz == null) {
                    Snackbar.make(activityMain, getResources().getString(R.string.NoSensor), Snackbar.LENGTH_LONG).show();
                } else {
                    try {
                        brightnessMode = Settings.System.getInt(getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS_MODE);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (PermissionWriteSetting()) {
                        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
                            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                            Snackbar.make(activityMain, getResources().getString(R.string.MAutoActivado), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
                break;

            case R.id.LD:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(MY_CAMERA);
                    } else IrLiterna(1);
                } else IrLiterna(1);
                break;

            case R.id.LT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(MY_CAMERA);
                    } else IrLiterna(0);
                } else IrLiterna(0);
                break;

            case R.id.invite:
                String deepLink = "https://play.google.com/store/apps/details?id=com.firux_app.mons";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.textshare) + "\n\n" + deepLink);
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.inviteAmigos)));
                break;

        }
    }

    /*private void publicidad() {
        cont_up = getSharedPreferences("PREFERENCEM", MODE_PRIVATE).getInt("cont", 0);
        cont_up++;
        getSharedPreferences("PREFERENCEM", MODE_PRIVATE).edit().putInt("cont",cont_up).apply();
        if (cont_up >=4){
            if (mInterstitialAd.isLoaded()){
                mInterstitialAd.show();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                getSharedPreferences("PREFERENCEM", MODE_PRIVATE).edit().putInt("cont",0).apply();
            }else {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                if (Ads2.isLoaded()){
                    Ads2.show();
                   Ads2.loadAd(new AdRequest.Builder().build());
                    getSharedPreferences("PREFERENCEM", MODE_PRIVATE).edit().putInt("cont",0).apply();
                }
            }

        }
    }*/
    public void IrLiterna(int IDcam) {
        CustomNotificationService.valorCAM = IDcam;
        Intent intent = new Intent(context, CustomNotificationService.class);
        // prueba de flags para no crear varios servicios
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
        intent.setAction(CustomNotificationService.ACTION_NOTIFICATION_LITERNA);
        startService(intent);
        CustomNotificationService.contextM = context;
    }

    public void requestPermissions(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (position) {
                case 0:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        showSnackBar();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA);
                    }
                    break;
                case 1:
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        showSnackBar();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_GPS);
                    }
                    break;
                default:

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); if (requestCode == MY_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(activityMain, getResources().getString(R.string.PermisoOK), Snackbar.LENGTH_LONG).show();
            } else {
                showSnackBar();
            }
        } else if (requestCode == MY_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(activityMain, getResources().getString(R.string.PermisoOK), Snackbar.LENGTH_LONG).show();
            } else {
                showSnackBar();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        }
    }

    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(activityMain,
                getResources().getString(R.string.PermisoRe), Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getString(R.string.ajuste), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                startActivity(intent);
            }
        });
        snackbar.show();
    }
}
