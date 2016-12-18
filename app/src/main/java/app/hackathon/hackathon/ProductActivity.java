package app.hackathon.hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView name,screen,reviews;
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
        screen = (TextView) findViewById(R.id.screen);
        reviews = (TextView) findViewById(R.id.reviews);
        imageView = (ImageView) findViewById(R.id.event_image);
    }

    private void setUI() {
        Intent intent = getIntent();
        String mName = intent.getStringExtra("name");
        String mScreen = intent.getStringExtra("screen");
        String mReviews = intent.getStringExtra("reviews");
        String mRecommendations = intent.getStringExtra("recommendations");

        collapsingToolbarLayout.setTitle(mName);
//        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
//        getSupportActionBar().setTitle(name);
        name.setText(mName);
        screen.setText(mScreen);
        reviews.setText(mReviews);
//        mRecommendations.setText(mRecommendations);

        // loading image using Glide library
//        Glide.with(this)
//                .load(url)
//                .error(R.drawable.toppr_app_icon)
//                .into(imageView);

    }
}
