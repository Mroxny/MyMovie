package com.mroxny.mymovie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class SingleRateActivity extends AppCompatActivity {

    private Rate rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_rate);

        rate = (Rate) getIntent().getSerializableExtra("Rate");

        setInfo();
    }

    public void goBack(View view){onBackPressed();}

    private void setInfo(){
        TextView tvBanner, tvAverage;
        String info1, info2;
        float avg;
        RatingBar picture, plot, cast, audio;



        tvAverage = findViewById(R.id.sr_average);
        tvBanner = findViewById(R.id.sr_banner);
        picture = findViewById(R.id.sr_ratePictures);
        plot = findViewById(R.id.sr_ratePlot);
        cast = findViewById(R.id.sr_rateCast);
        audio = findViewById(R.id.sr_rateAudio);

        setUserProfile();

        info1 = rate.getUserName() + " " + getResources().getString(R.string.tag_rate_info1) + " " + '"' +rate.getMovieTitle()+
                '"'+ " " + getResources().getString(R.string.tag_rate_info2) + ":";
        tvBanner.setText(info1);
        picture.setRating(rate.getRatePictures());
        plot.setRating(rate.getRatePlot());
        cast.setRating(rate.getRateCast());
        audio.setRating(rate.getRateAudio());

        info2 =getResources().getString(R.string.tag_average) +": "+ rate.getAverageRate();
        tvAverage.setText(info2);
    }

    private void setUserProfile(){
        String profilePath = rate.getUserProfile();

        ImageView ivUserProfile;
        CardView imageHolder;

        ivUserProfile = findViewById(R.id.sr_profile);
        imageHolder = findViewById(R.id.sr_imageHolder);

        if(profilePath != null && profilePath.length()>0){
            ImageManager im = new ImageManager();
            im.downloadProfile(profilePath);
            im.setOnDataListener(new ImageManager.DataListener() {
                @Override
                public void onDataLoaded(ArrayList<Bitmap> img) {
                    ivUserProfile.setImageBitmap(img.get(0));
                    imageHolder.setVisibility(View.VISIBLE);
                }
            });
        }
        else imageHolder.setVisibility(View.GONE);
    }
    public void goToMovieList(View view){
        String query = "SELECT m.*, \n" +
                "COUNT(mo1.film_Id) 'Liczba ocen', \n" +
                "IF(AVG(mo1.Ocena) IS NULL, 0, AVG((mo1.OcenaZdjecia+mo1.OcenaFabula+mo1.OcenaAktorzy+mo1.OcenaAudio)/4)) 'Srednia' \n" +
                "FROM filmy m \n" +
                "INNER JOIN oceny mo1 on m.Id_film = mo1.film_Id \n" +
                "INNER JOIN oceny mo2 on m.Id_film = mo2.film_Id \n" +
                "WHERE m.Zatwierdzony = 1 \n" +
                "AND mo2.uzytkownik_Id = "+rate.getUserID()+"\n" +
                "GROUP BY m.Id_film \n" ;

        Intent intent = new Intent(this, MoviesActivity.class);
        intent.putExtra(MoviesActivity.CUSTOM_QUERY_KEY,query);
        intent.putExtra(MoviesActivity.CUSTOM_TAG_KEY,rate.getUserName());

        startActivity(intent);
    }
}