package com.android.plefind.plefindview;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.backendless.Backendless;

public class MainActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Backendless.setUrl( getResources().getString(R.string.SERVER_URL) );
        Backendless.initApp( getApplicationContext(),
                getResources().getString(R.string.APPLICATION_ID),
                getResources().getString(R.string.API_KEY) );


        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "PatientID IS NULL" );
        Backendless.Data.of( "LABdata" ).getObjectCount( queryBuilder,
                new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse(final Integer integer) {
                        //if (integer>0) bottomNav.getMenu().getItem(2).setIcon(R.drawable.ic_notifications_active_orange_24dp);
                        //BottomNavigationItemView itemView = (BottomNavigationItemView) ((BottomNavigationMenuView) bottomNav.getChildAt(0)).getChildAt(2);
                        //View badge = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_main, itemView, false);
                        //itemView.addView(badge);
                        if (integer>0) bottomNav.getOrCreateBadge(bottomNav.getMenu().getItem(2).getItemId()).setBackgroundColor(Color.rgb(253, 147, 70));
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.i("MYAPP", "error - " + backendlessFault.getMessage());
                    }
                } );

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_cloud:
                        selectedFragment = new CloudFragment();
                        break;
                    case R.id.nav_notifications:
                        selectedFragment = new NotificationFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

    }
}
