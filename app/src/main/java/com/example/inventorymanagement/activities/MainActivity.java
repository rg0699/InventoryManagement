package com.example.inventorymanagement.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.inventorymanagement.R;
import com.example.inventorymanagement.database.InventoryDbHelper;
import com.example.inventorymanagement.fragments.PlaceholderFragment;
import com.example.inventorymanagement.models.User;
import com.example.inventorymanagement.ui.adapters.TabsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_SECTION_TITLE = "section_title";

    static ViewPager viewPager;
    static TabLayout tabLayout;
    private InventoryDbHelper productDbhelper;
    private SQLiteDatabase db;

    public static ArrayList<String> categoriesList;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference mDatabase;
    private ValueEventListener listener;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        storageRef = FirebaseStorage.getInstance().getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                addUserChangeListener(user);
            }
        };

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(mCurrentUser == null){
            navigationView.getMenu().findItem(R.id.nav_delete_account).setVisible(false);
        }

        categoriesList = new ArrayList<>();
        categoriesList.add(getResources().getString(R.string.item_1));
        categoriesList.add(getResources().getString(R.string.item_2));

        productDbhelper = new InventoryDbHelper(getApplicationContext());
        db = productDbhelper.getReadableDatabase();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        if (viewPager != null) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mCurrentUser == null){
                    startActivity(new Intent(getApplicationContext() , RegisterPhoneNumber.class));
                    finish();
                }
                else {
                    Intent i = new Intent(getApplicationContext() , EditActivity.class);
                    i.putExtra("what" , "add");
                    startActivity(i);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {

            return true;
        }else if (id == R.id.action_notifications) {

            return true;
        }else {

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {

        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
        int tmp = 1;
        for(String cat : categoriesList){

            PlaceholderFragment fragment = PlaceholderFragment.newInstance(cat, tmp);
            adapter.addFragment(fragment, cat);
            tmp++;

        }
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_item1) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_item2) {
            viewPager.setCurrentItem(1);
        }else if(id == R.id.nav_share_app){
            shareApplication();
        }
        else if(id == R.id.nav_delete_account){
            deleteAccount();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addUserChangeListener(FirebaseUser user) {
        // User data change listener

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerView = navigationView.getHeaderView(0);
        final TextView navUsername = headerView.findViewById(R.id.nav_user_name);
        final TextView navUserPhone = headerView.findViewById(R.id.nav_user_phone);
        final ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        if (user == null) {
            navUsername.setText("LogIn");
            navUsername.setTextSize(18);
            navUserPhone.setVisibility(View.INVISIBLE);
            headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RegisterPhoneNumber.class));
                    finish();
                }
            });
        }
        else {
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    if (user == null) {
                        Toast.makeText(
                                getApplicationContext(),
                                "User data is null!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                    else {
                        navUsername.setText(user.name);
                        navUserPhone.setText(user.phone);
                        if(user.photo!=null){
                            Glide.with(getApplicationContext()).load(user.photo).into(navUserPhoto);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Failed to read user!"
                                    + " Please try again later",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            };
            mDatabase.child(user.getUid()).addValueEventListener(listener);
        }
    }

    private void deleteAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId=user.getUid();
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                deleteUserData(userId);
                                Toast.makeText(getApplicationContext(), "Your Account is deleted:(", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void deleteUserData(String userId) {
        mDatabase.child(userId).removeValue();
        storageRef.child("profilePictures/" + userId + ".jpg").delete();
    }

    private void shareApplication() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "https://drive.google.com/file/d/1qHlbvBk861-YqqPsI25-pYJMIbmVBbod/view?usp=sharing";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "App link:");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share app via"));
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
        if (listener != null){
            mDatabase.child(mCurrentUser.getUid()).removeEventListener(listener);
        }
    }

}