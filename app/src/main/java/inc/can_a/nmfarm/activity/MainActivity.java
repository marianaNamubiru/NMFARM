package inc.can_a.nmfarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.app.MyApplication;
import inc.can_a.nmfarm.fragment.HomeFragment;
import inc.can_a.nmfarm.fragment.Q_and_A_Fragment;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    HomeFragment homeFragment = new HomeFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,homeFragment)
                            .commit();
                    return true;
                case R.id.navigation_dashboard:
                    Q_and_A_Fragment topicsFrament = new Q_and_A_Fragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,topicsFrament)
                            .commit();
                    return true;
            }
            return false;
        }
    };
    private Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,homeFragment)
                .commit();

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer);

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            VectorDrawableCompat indicator = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            assert indicator != null;
            indicator.setTint(ResourcesCompat.getColor(getResources(),R.color.white,getTheme()));
            supportActionBar.setHomeAsUpIndicator(indicator);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        drawerToggle();

        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        final Intent in;
                        switch (menuItem.getItemId()) {
                            case R.id.btn_login:
                                if (MyApplication.getInstance().getPrefManager().getUser() != null) {
                                    MyApplication.getInstance().getPrefManager().clear();
                                    menuItem.setTitle("Login");
                                }else {
                                    //here it means ur not logged in
                                    in = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(in);
                                }
                                break;
//                            case R.id.settings:
//                                in = new Intent(getApplicationContext(),MainSearchActivity.class);
//                                startActivity(in);
//                                break;
////
//                            case R.id.about:
//                                in = new Intent(getApplicationContext(),AboutUs.class);
//                                startActivity(in);
//                                break;
                        }
                        // Closing drawer on item click
                        drawer.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            //case R.id.action_settings:
            //Intent intent_settings = new Intent(getApplicationContext(),ActivitySetting.class);
            ///startActivity(intent_settings);
            //    break;
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                checkNavigationItems();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Called whenever we call invalidateOptionsMenu()
     * and this happens every time the drawer is opened or closed
     * cause i added a drawer open /close listener up there*/
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        checkNavigationItems();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    private void drawerToggle(){

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawer.addDrawerListener(mDrawerToggle);
    }

    private void checkNavigationItems(){
        Menu menu = navigationView.getMenu();
        View navheaderview = navigationView.getHeaderView(0);
        //if (AppController.getInstance().getPrefManager().isLoggedIn()) {
        //todo put the login staff
        if (MyApplication.getInstance().getPrefManager().getUser() != null) {

            MenuItem btn_login = menu.findItem(R.id.btn_login);
            // set new title to the MenuItem
            btn_login.setTitle("Logout");

            // get menu from navigationView
            //Find Navigation Drawer header view

            TextView user_prof = navheaderview.findViewById(R.id.public_user);
            TextView sign_in = navheaderview.findViewById(R.id.sign_in);
            user_prof.setVisibility(View.VISIBLE);

            String fn = "FN dummy test";
            String ln = "LN dummy test";

            String fnl = fn+" "+ln;
            user_prof.setText(MyApplication.getInstance().getPrefManager().getUser().getName());
            sign_in.setText(R.string.welcome);

        }else {

            MenuItem btn_login = menu.findItem(R.id.btn_login);
            // set new title to the MenuItem
            btn_login.setTitle("Login");

            //Find Navigation Drawer header view
            TextView user_prof = navheaderview.findViewById(R.id.public_user);
            user_prof.setVisibility(View.GONE);

            TextView sign_in = navheaderview.findViewById(R.id.sign_in);
            sign_in.setText(R.string.sign_in);
            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            });


        }

    }

}
