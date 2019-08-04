package com.app.nb.attendancemarker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1; //Idenificador para el permiso de la camara

    private ZXingScannerView mScannerView;

    private int currentApiVersion;

    private String messageCameraAccepted;
    private String messageCameraDenied;
    private String alertDialogScannerTitle;
    private String alertDialogScannerPositiveButton;
    private String alertDialogScannerNegativeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_scanner);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        // Variable con la version actual
        currentApiVersion = Build.VERSION.SDK_INT;
        // Valida la version
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            if (!checkPermission()) { // Si el permiso de camra no ha sido concedido
                requestPermission();
            }
        }
        getStringResources();

    }

    private void getStringResources() {
        messageCameraAccepted = getString(R.string.message_camera_accepted);
        messageCameraDenied = getString(R.string.message_camera_denied);
        alertDialogScannerTitle = getString(R.string.alert_dialog_scanner_title);
        alertDialogScannerPositiveButton = getString(R.string.alert_dialog_scanner_positive_button);
        alertDialogScannerNegativeButton = getString(R.string.alert_dialog_scanner_negative_button);
    }

    /**
     * Valida que el permiso de la camara haya sido concedido
     *
     * @return true si el permiso ha sido concedido
     */
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Requiere el permiso de la camara
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {
                    boolean permissionAcepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (permissionAcepted)
                        showMessage(messageCameraAccepted);
                    else {
                        showMessage(messageCameraDenied);
                        onBackPressed();
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                        REQUEST_CAMERA);
                                            }
                                        });
                            }
                        }*/
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentApiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) { //Si el permiso de la camara ha sido concedido
                if (mScannerView == null) {// Si la vista del scanner no ha sido inicializada
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

/*    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(ScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }*/

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Maneja el resultado del scaneado QR
     *
     * @param rawResult
     */
    @Override
    public void handleResult(Result rawResult) {
        Log.d("QRCodeScanner", rawResult.getText());
        Log.d("QRCodeScanner", rawResult.getBarcodeFormat().toString());
        showDialog(rawResult.getText());
    }

    private void showDialog(String result) {
        AlertDialog alertDialog = createAlertDialog(result);
        alertDialog.show();
    }

    private AlertDialog createAlertDialog(final String result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(alertDialogScannerTitle);
        builder.setPositiveButton(alertDialogScannerPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mScannerView.resumeCameraPreview(ScannerActivity.this); //Regresa al vista scanner
            }
        });
        builder.setNeutralButton(alertDialogScannerNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result)); //Muestra en el navegador
                startActivity(browserIntent);
            }
        });
        builder.setMessage(result);
        return builder.create();
    }
}
