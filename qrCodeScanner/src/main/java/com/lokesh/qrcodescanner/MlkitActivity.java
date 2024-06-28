package com.lokesh.qrcodescanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.lokesh.qrcodescanner.databinding.ActivityMlkitBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MlkitActivity extends AppCompatActivity {

    private ActivityMlkitBinding binding;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private ExecutorService cameraExecutor;
    private boolean flashEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMlkitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cameraPermission();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (Exception e) {
                    // Handle exception
                    Log.d("Camera", "Failed to bind preview" + e.getMessage());
                }
            }
        }, ContextCompat.getMainExecutor(this));


        binding.overlay.post(new Runnable() {
            @Override
            public void run() {
                binding.overlay.setViewFinder();
            }
        });
    }

    public void bindPreview(ProcessCameraProvider cameraProvider) {

        if (isDestroyed() || isFinishing()) {
            // This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            // java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return;
        }

        if (cameraProvider != null) {
            cameraProvider.unbindAll();

            Preview preview = new Preview.Builder().build();

            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();

            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    .setTargetResolution(new Size(binding.cameraPreview.getWidth(), binding.cameraPreview.getHeight()))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

            OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
                @Override
                public void onOrientationChanged(int orientation) {
                    // Monitors orientation values to determine the target rotation value
                    int rotation;
                    if (orientation >= 45 && orientation <= 134) {
                        rotation = Surface.ROTATION_270;
                    } else if (orientation >= 135 && orientation <= 224) {
                        rotation = Surface.ROTATION_180;
                    } else if (orientation >= 225 && orientation <= 314) {
                        rotation = Surface.ROTATION_90;
                    } else {
                        rotation = Surface.ROTATION_0;
                    }
                    imageAnalysis.setTargetRotation(rotation);
                }
            };
            orientationEventListener.enable();


            ScanningResultListener scanningListener = new ScanningResultListener() {
                @Override
                public void onScanned(String result) {
                    runOnUiThread(() -> {
                        imageAnalysis.clearAnalyzer();
                        if (cameraProvider != null) {
                            cameraProvider.unbindAll();
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("result", result);
                        setResult(RESULT_OK, resultIntent);
                        finish();
//                        ScannerResultDialog.newInstance(
//                                result,
//                                new ScannerResultDialog.DialogDismissListener() {
//                                    @Override
//                                    public void onDismiss() {
//                                        bindPreview(cameraProvider);
//                                    }
//                                }
//                        ).show(getSupportFragmentManager(), ScannerResultDialog.class.getSimpleName());
                    });
                }
            };

            ImageAnalysis.Analyzer analyzer = new MLKitBarcodeAnalyzer(scanningListener);


            imageAnalysis.setAnalyzer(cameraExecutor, analyzer);

            preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider());

            LifecycleOwner lifecycleOwner = this;
            Camera camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview);

            if (camera.getCameraInfo().hasFlashUnit()) {
                binding.ivFlashControl.setVisibility(View.VISIBLE);

                binding.ivFlashControl.setOnClickListener(v -> {
                    camera.getCameraControl().enableTorch(!flashEnabled);
                });

                camera.getCameraInfo().getTorchState().observe(this, torchState -> {
                    if (torchState != null) {
                        if (torchState == TorchState.ON) {
                            flashEnabled = true;
                            binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_on);
                        } else {
                            flashEnabled = false;
                            binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_off);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
    @SuppressLint("MissingPermission")
    private void cameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            int CAMERA_PERMISSION_CODE = 1;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }
}