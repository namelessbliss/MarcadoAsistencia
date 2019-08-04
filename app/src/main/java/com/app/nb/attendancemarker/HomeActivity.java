package com.app.nb.attendancemarker;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fabStart);

        fab.setOnClickListener(this);

        Drawable drawable = getResources().getDrawable(R.drawable.ic_mark);

        TapTargetView.showFor(this, TapTarget.forView(fab, "Bienvenido al marcador", "Click aqui para iniciar")
                        // Opciones opcionales
                        .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(android.R.color.white)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(android.R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.colorAccent)  // Specify the color of the description text
                        .textColor(R.color.colorPrimaryDark)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(android.R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .icon(drawable)                     // Specify a custom drawable to draw as the target
                        .targetRadius(40),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        startScanner();
                    }
                });
    }


    @Override
    public void onClick(View view) {
        startScanner();
    }

    private void startScanner() {
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(this, "Iniciando Scanner", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
