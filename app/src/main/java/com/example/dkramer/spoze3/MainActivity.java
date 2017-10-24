package com.example.dkramer.spoze3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.dkramer.spoze3.util.ImportBitmapActivity;

/**
 * Created by dkramer on 10/24/17.
 */

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, ImportBitmapActivity.class);
		startActivity(intent);
	}
}
