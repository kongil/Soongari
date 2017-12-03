package com.example.puroong.ssuciety.activities.managepic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.puroong.ssuciety.R;
import com.example.puroong.ssuciety.api.ClubActivityAPI;
import com.example.puroong.ssuciety.listeners.AfterImageLoadListener;
import com.example.puroong.ssuciety.listeners.AfterImageUploadListener;
import com.example.puroong.ssuciety.models.ClubActivity;
import com.example.puroong.ssuciety.utils.ImageUtil;

import java.io.IOException;

public class UpdatePic extends AppCompatActivity {
    private Handler handler = new Handler();
    private ImageView iv;
    private Uri ivUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);

        final ClubActivity clubActivity = getIntent().getParcelableExtra("clubActivity");
        final TextView tvname = (TextView) findViewById(R.id.edit_Picname);
        Button btn = (Button) findViewById(R.id.addpic);
        iv = (ImageView) findViewById(R.id.imageView4);

        ImageUtil.getInstance().loadImage(getApplicationContext(), handler, clubActivity.getPictureLink(), new AfterImageLoadListener() {
            @Override
            public void setImage(Bitmap bitmap) {
                ImageUtil.getInstance().setImage(iv, bitmap, true);
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUtil.getInstance().chooseImage(UpdatePic.this);
            }
        });

        tvname.setText(clubActivity.getName());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                String url = ivUrl.toString();

                ImageUtil.getInstance().uploadImage(getApplicationContext(), bitmap, url, new AfterImageUploadListener() {
                    @Override
                    public void afterImageUpload(String downloadUrl) {
                        clubActivity.setPictureLink(downloadUrl);
                        clubActivity.setName(tvname.getText().toString());

                        ClubActivityAPI.getInstance().updateClubActivity(clubActivity.getKey(), clubActivity);

                        startActivity(new Intent(getApplicationContext(), ManagePic.class));
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ImageUtil.PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                ImageUtil.getInstance().setImage(iv, bitmap, true);
                ivUrl = uri;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Failed to get image from gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }
}