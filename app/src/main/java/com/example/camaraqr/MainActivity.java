package com.example.camaraqr;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;


import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private PreviewView previewView;
    private Button flashButton;
    private Camera camera;
    private boolean isFlashOn = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        // Permiso otorgado, iniciar la cámara
                        startCamera();
                    } else {
                        Toast.makeText(MainActivity.this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        previewView = findViewById(R.id.previewView);
        flashButton = findViewById(R.id.flashButton);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera(); // Iniciar la cámara si ya tiene permiso
        } else {
            // Solicitar permiso de cámara
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }

        // Configura la cámara
        startCamera();

        // Configura el botón de flash
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlash();
            }
        });
    }
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                    Preview preview = new Preview.Builder().build();
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                    camera = cameraProvider.bindToLifecycle(MainActivity.this, cameraSelector, preview);
                } catch (ExecutionException | InterruptedException e) {
                    Toast.makeText(MainActivity.this, "Error al acceder a la cámara", Toast.LENGTH_SHORT).show();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void toggleFlash() {
        if (camera != null) {
            CameraControl cameraControl = camera.getCameraControl();
            if (cameraControl != null) {
                isFlashOn = !isFlashOn; // Alterna el estado manualmente
                cameraControl.enableTorch(isFlashOn); // Cambia el estado del flash
            }
        }
    }

//    private void toggleFlash() {
//        if (camera != null) {
//            CameraControl cameraControl = camera.getCameraControl();
//            if (cameraControl != null) {
//                camera.getCameraInfo().getTorchState().observe(this, torchState -> {
//                    boolean isTorchOn = (torchState == TorchState.ON);
//                    cameraControl.enableTorch(!isTorchOn); // Alterna el estado del flash
//                });
//            }
//        }
//    }



}