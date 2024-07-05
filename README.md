"# barcode-scanner" 
## Installation



Declare Maven Central repository in the dependency configuration, then add this library in the dependencies. An example using `build.gradle`:

```groovy
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
	        implementation 'com.github.raolokesh:qrcodescanner:Tag'
	}
```


```gradle.kts

implementation("com.github.raolokesh:qrcodescanner:v1.0.11")

```

Declare in `settings.gradle.kts` 

```settings.gradle.kts

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```


To perform operation, create an intent for the desired operation

```java

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lokesh.barcodescanner.MlkitActivity;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> launcher;
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
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String scannedResult = data.getStringExtra("result");
                            TextView text = findViewById(R.id.text);
                            text.setText(scannedResult);
                            // Handle the scanned result here
                            Log.d("MainActivity", "Scanned Result: " + scannedResult);
                        }
                    }
                }
        );
    }

    public void scanneropen(View view) {
        Intent intent = new Intent(this, MlkitActivity.class);
        launcher.launch(intent);
    }
}
```

in this registerForActivityResult will receive the result of qr Code read by camera 
you can call an intent on button click anywhere in your activity it and define registerForActivityResult
