package com.lokesh.qrcodescanner;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;


public class MLKitBarcodeAnalyzer implements ImageAnalysis.Analyzer {

    private final ScanningResultListener listener;
    private boolean isScanning = false;

    public MLKitBarcodeAnalyzer(ScanningResultListener listener) {
        this.listener = listener;
    }

    @ExperimentalGetImage
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        if (imageProxy.getImage() != null && !isScanning) {
            InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build();

            isScanning = true;
            BarcodeScanning.getClient(options)
                    .process(image)
                    .addOnSuccessListener(barcodes -> {
                        // Task completed successfully
                        if (barcodes != null && !barcodes.isEmpty()) {
                            Barcode barcode = barcodes.get(0);
                            if (barcode != null && barcode.getRawValue() != null) {
                                Log.d("Barcode", barcode.getRawValue());
                                listener.onScanned(barcode.getRawValue());
                            }
                        }

                        isScanning = false;
                        imageProxy.close();
                    })
                    .addOnFailureListener(e -> {
                        // Task failed with an exception
                        isScanning = false;
                        imageProxy.close();
                    });
        }
    }

}
