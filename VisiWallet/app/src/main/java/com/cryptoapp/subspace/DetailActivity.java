package com.cryptoapp.subspace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.bitcoinj.core.Coin;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        String imgString = intent.getExtras().getString("image");
        Coin balance = (Coin) intent.getExtras().getSerializable("balance");
        Bitmap bmp = BitmapFactory.decodeFile(imgString);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bmp);
        TextView balanceView = findViewById(R.id.balanceView);
        balanceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Switch between currencies
            }
        });
        balanceView.setText(balance.toFriendlyString());



    }
}
