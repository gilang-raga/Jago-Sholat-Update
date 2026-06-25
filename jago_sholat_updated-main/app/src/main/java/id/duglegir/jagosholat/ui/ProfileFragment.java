package id.duglegir.jagosholat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.text.InputType;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import id.duglegir.jagosholat.ui.main.LoginActivity;
import id.duglegir.jagosholat.R;


public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 100;
    private ImageView profileImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profileImage); // TARUH DI AWAL

        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);

        String username = sharedPreferences.getString("username", "Nama Pengguna");
        String email = sharedPreferences.getString("email", "email@domain.com");
        String pass = sharedPreferences.getString("password", "******");

        tvUsername.setText(username);
        tvEmail.setText(email);
        etPassword.setText(pass);

        String savedImage = sharedPreferences.getString("profileImage", null);
        if (savedImage != null) {
            Glide.with(this)
                    .load(Uri.parse(savedImage))
                    .circleCrop()
                    .into(profileImage);
        }

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        ImageView ivTogglePassword = view.findViewById(R.id.ivTogglePassword);
        final boolean[] visible = {false};

        ivTogglePassword.setOnClickListener(v -> {
            if (visible[0]) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
                visible[0] = false;
            } else {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
                visible[0] = true;
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        profileImage = view.findViewById(R.id.profileImage);

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);

            // Simpan ke SharedPreferences
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("profileImage", imageUri.toString());
            editor.apply();
        }
    }

}
