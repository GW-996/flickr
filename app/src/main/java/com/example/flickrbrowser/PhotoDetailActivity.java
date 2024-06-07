package com.example.flickrbrowser;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.flickrbrowser.databinding.ActivityPhotoDetailBinding;

public class PhotoDetailActivity extends AppCompatActivity {

    private ActivityPhotoDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
    }
}