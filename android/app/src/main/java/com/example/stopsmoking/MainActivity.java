package com.example.stopsmoking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stopsmoking.data.UserData;
import com.example.stopsmoking.mainFragment.MeasureFragment;
import com.example.stopsmoking.mainFragment.MeasureLogFragment;
import com.example.stopsmoking.mainFragment.MotivationFragment;
import com.example.stopsmoking.mainFragment.StatusFragment;
import com.example.stopsmoking.mainFragment.SettingFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    UserData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("stopSmoking");
        Intent intentData = getIntent();
        user = (UserData) intentData.getParcelableExtra("OBJECT");
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header_view  = navigationView.getHeaderView(0);
        ImageView nav_headr_icon = (ImageView) nav_header_view.findViewById(R.id.imageView);
        nav_headr_icon.setImageResource(R.drawable.logo_small); //네비게이션 메뉴 상단 아이콘

        TextView nav_header_id_text =(TextView) nav_header_view.findViewById(R.id.welcomText);
        nav_header_id_text.setText(user.getName()+"님 환영합니다.");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showMessage();
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String title = null;


        if (id == R.id.nav_measure) {
            fragment = new MeasureFragment();
            title = "측정";
        } else if (id == R.id.nav_status) {
            fragment = new StatusFragment();
            title = "측정현황";
        } else if (id == R.id.nav_motivation) {
            fragment = new MotivationFragment();
            title = "생각날 때";
        } else if (id == R.id.nav_manage) {
            fragment = new SettingFragment();
            title = "설정";
        } else if (id == R.id.nav_logout) {
            Intent logoutIntent = new Intent(getApplicationContext(), LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logoutIntent);
        }


        if (fragment != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("OBJECT",user);
//            bundle.putString("user_id",user.getName());
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //back 버튼 눌렀을 때 로그아웃 할건지 물어보는 dialog alert
    private void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내");
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
