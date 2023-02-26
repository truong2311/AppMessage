package com.example.mobilemessagingapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.example.mobilemessagingapp.R;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.language);
        }

        ImageView ivVietNam = findViewById(R.id.ivVietNam);
        ImageView ivEnglish = findViewById(R.id.ivEnglish);

        ivEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locale = new Locale("de", "DE");
                changeLanguage(locale);
            }
        });
        ivVietNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locale = new Locale("vi", "VI");
                changeLanguage(locale);
            }
        });

    }


    public void changeLanguage(Locale locale) {
        //đối tượng lưu thông tin kích thước trình bày
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();

        //đối tượng cấu hình
        Configuration configuration = new Configuration();
        //kiểm tra phiên bản từ api 17
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(locale);
        }else{
            configuration.locale = locale;
        }

        //cài đặt ngôn ngữ
        getBaseContext().getResources().updateConfiguration(configuration, displayMetrics);

        //load lại âctivity
        Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

}