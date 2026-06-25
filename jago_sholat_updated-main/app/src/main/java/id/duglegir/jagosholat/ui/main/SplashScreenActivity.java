package id.duglegir.jagosholat.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.ui.main.MainActivity;

// PENTING: Tambahkan import ini agar LoginActivity dikenali
import id.duglegir.jagosholat.ui.main.LoginActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Contoh durasi 2 detik
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // --- BAGIAN INI YANG DIUBAH ---

                // Awalnya mungkin: MainActivity.class
                // Ubah menjadi: LoginActivity.class
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);

                // finish() agar saat di tombol back tidak kembali ke splash screen
                finish();
            }
        }, 2000);
    }
}