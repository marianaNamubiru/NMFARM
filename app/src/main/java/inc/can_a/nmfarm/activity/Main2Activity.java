package inc.can_a.nmfarm.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.app.MyApplication;
import inc.can_a.nmfarm.fragment.GroupsFragment;
import inc.can_a.nmfarm.fragment.HomeFragment;
import inc.can_a.nmfarm.fragment.Q_and_A_Fragment;
import inc.can_a.nmfarm.helper.PagerAdapter;

public class Main2Activity extends AppCompatActivity {


    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    //Dialog for the custom FAB
    private Dialog dialog;
    private final int ADD_FEED = 6;
    private final int ADD_GROUP = 7;
    private final int ADD_PROBLEM = 8;
    private FloatingActionButton fab;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        setupViewPager(mViewPager);

        // Set Tabs inside Toolbar
        tabLayout.setupWithViewPager(mViewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpCustomFABDialog();
            }
        });

    }

    private void updateMenuTitle(){
        MenuItem menuItemLogin = menu.findItem(R.id.action_login);
        if (MyApplication.getInstance().getPrefManager().getUser()!=null){
            menuItemLogin.setTitle("Logout");
        }else{
            menuItemLogin.setTitle("Login");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        this.menu = menu;
        updateMenuTitle();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),MainSearchActivity.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.action_login) {
            if (MyApplication.getInstance().getPrefManager().getUser()!=null){
                MyApplication.getInstance().getPrefManager().clear();
                updateMenuTitle();
            }else {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    // Add Fragments
    private void setupViewPager(final ViewPager viewPager) {
        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        final HomeFragment homeFragment = new HomeFragment();
        final GroupsFragment groupsFragment = new GroupsFragment();
        final Q_and_A_Fragment qandAFragment = new Q_and_A_Fragment();

        pagerAdapter.addFragment(homeFragment, getResources().getString(R.string.tab_text_1));
        pagerAdapter.addFragment(groupsFragment, getResources().getString(R.string.tab_text_2) );
        pagerAdapter.addFragment(qandAFragment, getResources().getString(R.string.tab_text_3));

        //set the limit to 2 so it can keep up to 2 fragments instances
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0){
                    //call the viewpager again
                    pagerAdapter.getFragment(0);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    pagerAdapter.getFragment(0);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setUpCustomFABDialog(){
        dialog = new Dialog(Main2Activity.this);
        // it remove the dialog title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // set the laytout in the dialog
        dialog.setContentView(R.layout.main_floating_layout);
        // initialize the item of the dialog box, whose id is cross
        View crossdialog = dialog.findViewById(R.id.cross);

        View add_feed = dialog.findViewById(R.id.rv_add_feed);
        View add_group = dialog.findViewById(R.id.rv_add_group);
        View add_problem = dialog.findViewById(R.id.rv_add_problem);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams param = window.getAttributes();
        // set the layout at right bottom
        param.gravity = Gravity.BOTTOM | Gravity.END;
        // it dismiss the dialog when click outside the dialog frame
        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onCancel(DialogInterface dialog) {
                fab.setVisibility(View.VISIBLE);
            }
        });

        // it call when click on the item whose id is demo1.
        crossdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // dismiss the dialog
                dialog.dismiss();
                dialog.cancel();
            }
        });
        // it call when click on the item whose id is demo1.
        add_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getPrefManager().getUser()!=null) {
                    Intent in = new Intent(getApplicationContext(),AddFeedActivity.class);
                    startActivity(in);
                }else {
                    Intent in = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivityForResult(in,ADD_FEED);
                }
                // dismiss the dialog
                dialog.dismiss();
                dialog.cancel();
            }
        });

        // it call when click on the item whose id is demo1.
        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getPrefManager().getUser()!=null) {
                    Intent in = new Intent(getApplicationContext(),AddGroupActivity.class);
                    startActivity(in);
                }else {
                    Intent in = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivityForResult(in, ADD_GROUP);
                }
                // dismiss the dialog
                dialog.dismiss();
                dialog.cancel();
            }
        });

        // it call when click on the item whose id is demo1.
        add_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance().getPrefManager().getUser()!=null) {
                    Intent in = new Intent(getApplicationContext(),AddProblemActivity.class);
                    startActivity(in);
                }else {
                    Intent in = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivityForResult(in, ADD_PROBLEM);
                }
                // dismiss the dialog
                dialog.dismiss();
                dialog.cancel();
            }
        });

        // it show the dialog box
        dialog.show();
        fab.setVisibility(View.INVISIBLE);
    }

}
