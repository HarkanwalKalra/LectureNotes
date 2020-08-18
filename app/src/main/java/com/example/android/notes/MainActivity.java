package com.example.android.notes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, UploadsFragment.OnFragmentInteractionListener, DownloadsFragment.OnFragmentInteractionListener {

    private BottomNavigationView bottomNavBar;
    private FragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setIDs();
        createToolbar();
        createBottomNavBar();
    }


    @Override
    public void onBackPressed() {
        Fragment frag = manager.findFragmentById(R.id.frame_container);

        if (frag instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            manager.beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
            bottomNavBar.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void createBottomNavBar(){

        manager = getSupportFragmentManager();
        bottomNavBar = findViewById(R.id.bottomNavBar);
        bottomNavBar.setSelectedItemId(R.id.navigation_home);
        manager.beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.navigation_home:
                                manager.beginTransaction().replace(R.id.frame_container, new HomeFragment(),"Home").commit();
                                return true;
                            case R.id.navigation_uploads:
                        manager.beginTransaction().replace(R.id.frame_container, new UploadsFragment()).commit();
                        return true;
                   /* case R.id.navigation_downloads:
                        manager.beginTransaction().replace(R.id.frame_container, new DownloadsFragment()).commit();
                        return true;
                    case R.id.navigation_settings:
                        return true;*/
                }
                return false;
            }
        });
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            toolbar.inflateMenu(R.menu.toolbar_menu);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.toolbar_search) {

        } else if (item.getItemId() == R.id.toolbar_profile) {

        } else*/

        /*if (item.getItemId() == R.id.toolbar_profile) {
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void setIDs() {

        FirebaseApp.initializeApp(this);

        bottomNavBar = findViewById(R.id.bottomNavBar);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
