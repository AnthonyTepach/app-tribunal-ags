package com.anthonytepach.app.ags;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.ArrayList;
import java.util.List;

public class CustomScannerActivity extends Activity implements
        DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private  Button button_focus;
    private  Button button_beep;
    private  Button button_vibra;
    private ViewfinderView viewfinderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);


        switchFlashlightButton = (Button)findViewById(R.id.switch_flashlight2);
        button_focus = (Button)findViewById(R.id.button_focus2);
        button_beep = (Button)findViewById(R.id.button_beep2);
        button_vibra = (Button)findViewById(R.id.button_vibra2);


        viewfinderView = (ViewfinderView) findViewById(R.id.zxing_viewfinder_view);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        if (!hasFocusCamera()) {
            button_focus.setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, barcodeScannerView);
        barcodeScannerView.getBarcodeView().getCameraSettings().setAutoFocusEnabled(true);
        barcodeScannerView.decodeSingle(callback);

        //capture.initializeFromIntent(getIntent(), savedInstanceState);
        //capture.decode();

    }
    private void enviaError(View view){
        Intent i = new Intent(this,DocumentError.class);
        startActivity(i);
    }
    private void enviaVista(View view, ArrayList<String> info){
        BeepManager bm=new BeepManager(this);
        Intent i = new Intent(this,MainActivity.class);
        i.putExtra("folio",info.get(0));

        if (getString(R.string.vibra_off).equals(button_vibra.getText()) &&(getString(R.string.beep_off).equals(button_beep.getText()))){
            bm.playBeepSound();
            vibrar();
        }else if(getString(R.string.beep_off).equals(button_beep.getText())){
            bm.playBeepSound();
        }else if(getString(R.string.vibra_off).equals(button_vibra.getText())){
            vibrar();
        }

        startActivity(i);
    }
    private void vibrar(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200L);
        }
    }
    private BarcodeCallback callback = new BarcodeCallback()
    {

        @Override
        public void barcodeResult(BarcodeResult result)
        {
            if (result.getText() != null)
            {
                if (result.getText().contains("http://www.poderjudicialags.gob.mx/Inicio")){
                    if (result.getText().length()==75){
                        Qr qr=new Qr();
                        ArrayList<String> info=new ArrayList<>();
                        info.add(String.valueOf(qr.getFolio(result.getText())));//folio
                        enviaVista(getCurrentFocus(),info);
                    }else{
                        enviaError(getCurrentFocus());

                    }
                }else{
                    enviaError(getCurrentFocus());
                }

            }
        }


        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints)
        {
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
        barcodeScannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
        barcodeScannerView.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        barcodeScannerView.decodeSingle(callback);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }



    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private boolean hasFocusCamera() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
    }
    @Override
    public void onTorchOn() {
        switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }




    public void switchFocus(View view) {
        if (getString(R.string.focus_on_con).equals(button_focus.getText())){
            barcodeScannerView.getBarcodeView().getCameraSettings().setAutoFocusEnabled(true);
            button_focus.setText(R.string.focus_off_con);
            Toast.makeText(this, "Encendido", Toast.LENGTH_SHORT).show();
        }else if (getString(R.string.focus_off_con).equals(button_focus.getText())){
            barcodeScannerView.getBarcodeView().getCameraSettings().setAutoFocusEnabled(false);
            button_focus.setText(R.string.focus_on_con);
            Toast.makeText(this, "Apagado", Toast.LENGTH_SHORT).show();
        }

    }
    public void switchBeep(View view) {
        if (getString(R.string.beep_off).equals(button_beep.getText())){
            button_beep.setText(R.string.beep_on);
            Toast.makeText(this, "Apagado", Toast.LENGTH_SHORT).show();
        }else{
            button_beep.setText(R.string.beep_off);
            Toast.makeText(this, "Encendido", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchVibra(View view) {
        if (getString(R.string.vibra_off).equals(button_vibra.getText())){
            button_vibra.setText(R.string.vibra_on);
            Toast.makeText(this, "Apagado", Toast.LENGTH_SHORT).show();
        }else{
            button_vibra.setText(R.string.vibra_off);
            Toast.makeText(this, "Encendido", Toast.LENGTH_SHORT).show();
        }
    }
    public void switchFlashlight(View view) {
        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())) {
            barcodeScannerView.setTorchOn();
            Toast.makeText(this, "Encendido", Toast.LENGTH_SHORT).show();
        } else {
            barcodeScannerView.setTorchOff();
            Toast.makeText(this, "Apagado", Toast.LENGTH_SHORT).show();
        }
    }
}
