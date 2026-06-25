package id.duglegir.jagosholat.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import id.duglegir.jagosholat.R;
import id.duglegir.jagosholat.model.DatabaseHelper;

public class DaftarActivity extends AppCompatActivity {

    // Variabel Input (Tanpa Confirm Password)
    EditText etNama, etEmail, etPassword;
    Button btnDaftar;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daftar_activity);

        // 1. Panggil Database
        db = new DatabaseHelper(this);

        // 2. Sambungkan ID XML
        // Pastikan ID ini sesuai dengan layout XML Anda
        etNama = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmailReg);
        etPassword = findViewById(R.id.editTextPasswordReg);

        // (Bagian etConfirmPass sudah dihapus)

        btnDaftar = findViewById(R.id.buttonRegister);

        // 3. Logika Tombol Daftar
        btnDaftar.setOnClickListener(v -> {
            String nama = etNama.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            // *** SIMPAN DATA USER ***
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", nama);
            editor.putString("email", email);
            editor.putString("password", pass);
            editor.apply();

            // Validasi Sederhana (Cek apakah kosong)
            if (nama.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(DaftarActivity.this, "Harap isi Nama, Email, dan Password!", Toast.LENGTH_SHORT).show();
            }
            else {
                // --- SIMPAN KE DATABASE ---
                boolean berhasil = db.registerUser(nama, email, pass);

                if (berhasil) {
                    Toast.makeText(DaftarActivity.this, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_SHORT).show();

                    // Pindah ke Halaman Login
                    Intent intent = new Intent(DaftarActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DaftarActivity.this, "Gagal! Email mungkin sudah terdaftar.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Link ke Halaman Login
        TextView tvLoginLink = findViewById(R.id.textViewLoginLink);
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(DaftarActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}