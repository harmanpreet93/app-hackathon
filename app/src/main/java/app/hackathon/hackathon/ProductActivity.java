package app.hackathon.hackathon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;

public class ProductActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView name;
    private RatingBar battery,camera,touch, sound,display;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUI();
        setUI();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    /**
     * react to the user tapping the back/up icon in the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initUI() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        name = (TextView) findViewById(R.id.product_name);
        battery = (RatingBar) findViewById(R.id.battery);
        camera = (RatingBar) findViewById(R.id.camera);
        touch = (RatingBar) findViewById(R.id.touch);
        sound = (RatingBar) findViewById(R.id.sound);
        display = (RatingBar) findViewById(R.id.display);
        imageView = (ImageView) findViewById(R.id.image);
    }

    private void setUI() {
        Intent intent = getIntent();
        String mName = intent.getStringExtra("name");
        String mBattery = intent.getStringExtra("battery");
        String mCamera = intent.getStringExtra("camera");
        String mTouch = intent.getStringExtra("touch");
        String mSound = intent.getStringExtra("sound");
        String mDisplay = intent.getStringExtra("display");
        String imagePath = intent.getStringExtra("image_path");

        File imgFile = new  File(imagePath);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            imageView.setImageBitmap(myBitmap);

        }

        collapsingToolbarLayout.setTitle(mName);
//        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
//        getSupportActionBar().setTitle(name);
        name.setText(mName);
        battery.setRating(Float.parseFloat(mBattery)/20);
        camera.setRating(Float.parseFloat(mCamera)/20);
        touch.setRating(Float.parseFloat(mTouch)/20);
        sound.setRating(Float.parseFloat(mSound)/20);
        display.setRating(Float.parseFloat(mDisplay)/20);

    }
}
