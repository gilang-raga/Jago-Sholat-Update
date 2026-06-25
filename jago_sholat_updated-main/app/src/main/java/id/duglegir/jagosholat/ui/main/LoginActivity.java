package id.duglegir.jagosholat.ui.main;

import android.content.Context;
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

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        db = new DatabaseHelper(this);

        // Pastikan ID ini sama dengan di file login_activity.xml Anda
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);

        TextView tvRegister = findViewById(R.id.textViewRegister);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, DaftarActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(LoginActivity.this, "Isi Email dan Password", Toast.LENGTH_SHORT).show();
            } else {
                // --- CEK KE DATABASE DULU ---
                boolean check = db.checkUser(email, pass);

                if(check) {
                    // Jika User Ada di Database

                    // Simpan Sesi (Biar Profil ada namanya)
                    String namaUser = db.getUserName(email);
                    SharedPreferences prefs = getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE);
                    prefs.edit().putString("user_email", email).putString("user_name", namaUser).apply();

                    Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Jika Gagal
                    Toast.makeText(LoginActivity.this, "Email atau Password Salah!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}