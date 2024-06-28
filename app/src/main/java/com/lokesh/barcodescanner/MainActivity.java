package com.lokesh.barcodescanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lokesh.barcodescanner.databinding.ActivityMainBinding;
import com.lokesh.qrcodescanner.MlkitActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int SCAN_REQUEST_CODE = 1001;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String scannedResult = data.getStringExtra("result");
                            binding.result.setText(scannedResult);
                            // Handle the scanned result here
                            Log.d("MainActivity", "Scanned Result: " + scannedResult);
                        }
                    }
                }
        );
    }

    public void qrCodeScanner(View view) {

        Intent intent = new Intent(this, MlkitActivity.class);
        launcher.launch(intent);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
//            if (data != null) {
//                String scannedResult = data.getStringExtra("result");
//                binding.result.setText(scannedResult);
//                // Handle the scanned result here
//                Log.d("MainActivity", "Scanned Result: " + scannedResult);
//            }
//        }
//    }
}