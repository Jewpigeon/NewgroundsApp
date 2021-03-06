package jewpigeon.apps.newgrounds;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.ncapdevi.fragnav.FragNavController;
import com.ncapdevi.fragnav.FragNavTransactionOptions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import jewpigeon.apps.newgrounds.ContentFragments.ArtPortal;
import jewpigeon.apps.newgrounds.ContentFragments.AudioPortal;
import jewpigeon.apps.newgrounds.ContentFragments.CommunityPortal;
import jewpigeon.apps.newgrounds.ContentFragments.FeaturedPortal;
import jewpigeon.apps.newgrounds.ContentFragments.GamesPortal;
import jewpigeon.apps.newgrounds.ContentFragments.MoviesPortal;
import jewpigeon.apps.newgrounds.Fundamental.NG_Activity;
import jewpigeon.apps.newgrounds.Fundamental.NG_Bus;
import jewpigeon.apps.newgrounds.Fundamental.NG_Events;
import jewpigeon.apps.newgrounds.Fundamental.NG_PreferencesData;
import jewpigeon.apps.newgrounds.GenericLayouts.GenericUserFragment;
import jewpigeon.apps.newgrounds.Views.ProfileWidget;
import jewpigeon.apps.newgrounds.Views.ProfileWidgetData.ProfileIconClickListener;
import jewpigeon.apps.newgrounds.Views.ProfileWidgetData.SliderItemClickListener;


public class ContentActivity extends NG_Activity implements
        FragNavController.RootFragmentListener, FragNavController.TransactionListener,

        BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar ContentToolbar;
    private ImageButton HomeButton;
    private ImageButton SearchButton;
    private MaterialTextView LoginButton;
    private SearchView ContentSearch;
    private AppBarLayout contentAppBarLayout;
    private ProfileWidget ProfileMenu;
    private ConstraintLayout ProfileMenuHeader;

    private BottomNavigationView ContentBottomBar;
    private HideBottomViewOnScrollBehavior contentBottomBarBehaviour;

    private NavigationView ContentLeftMenu;
    private NavigationView ProfileActionMenu;

    private DrawerLayout ContentDrawerLayout;
    private ActionBarDrawerToggle ContentDrawerToggle;

    private FragNavController ContentFragmentsController;
    private ArrayList<Fragment> ContentFragmentsList = new ArrayList<Fragment>(
            Arrays.asList(
                    MoviesPortal.newInstance(),
                    AudioPortal.newInstance(),
                    ArtPortal.newInstance(),
                    GamesPortal.newInstance(),
                    CommunityPortal.newInstance(),
                    FeaturedPortal.newInstance()
            ));

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        establishViews();
        if (ContentFragmentsController == null) {
            ContentFragmentsController = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.content_container)
                    .rootFragments(ContentFragmentsList)
                    .rootFragmentListener(this, ContentFragmentsList.size())
                    .transactionListener(this)
                    .selectedTabIndex(FragNavController.TAB6)
                    .defaultTransactionOptions(
                            FragNavTransactionOptions.newBuilder()
                                    .transition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .build())
                    .build();

            super.setUpController(ContentFragmentsController);
        }

        setUpListeners();
        NG_Bus.get().register(this);


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        NG_Bus.get().unregister(this);
    }

    private void establishViews() {
        NG_PreferencesData preferences = getPreferencesFromStore();

        ContentToolbar = findViewById(R.id.content_toolbar);
        contentAppBarLayout = findViewById(R.id.content_appbarlayout);


        ContentBottomBar = findViewById(R.id.content_bottombar);
        contentBottomBarBehaviour = (HideBottomViewOnScrollBehavior) ((CoordinatorLayout.LayoutParams) ContentBottomBar.getLayoutParams()).getBehavior();
        ContentBottomBar.getMenu().setGroupCheckable(0, false, false);

        ContentDrawerLayout = findViewById(R.id.content_drawerlayout);
        ContentLeftMenu = findViewById(R.id.content_left_menu);

        HomeButton = ContentToolbar.findViewById(R.id.NG_appbar_home);
        SearchButton = ContentToolbar.findViewById(R.id.NG_appbar_search);
        LoginButton = ContentToolbar.findViewById(R.id.NG_LoginOrSignUp);
        ProfileMenu = ContentToolbar.findViewById(R.id.NG_ProfileWidget);
        ProfileActionMenu = findViewById(R.id.content_profile_menu);
        ProfileMenuHeader = (ConstraintLayout) ProfileActionMenu.getHeaderView(0);



        if(preferences != null && preferences.isUserLogged()){
            LoginButton.setVisibility(View.GONE);
            ProfileMenu.setVisibility(View.VISIBLE);
            ProfileActionMenu.setVisibility(View.VISIBLE);

        }

        ContentSearch = findViewById(R.id.content_search);
        EditText searchEditText = (EditText) ContentSearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        ImageView close_button = ContentSearch.findViewById(androidx.appcompat.R.id.search_close_btn);
        close_button.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));


        setSupportActionBar(ContentToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ng_menu_icon);


        ContentDrawerToggle = new ActionBarDrawerToggle(this, ContentDrawerLayout, ContentToolbar, 0, 0){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                contentAppBarLayout.setExpanded(true, true);
            }
        };
        ContentDrawerLayout.addDrawerListener(ContentDrawerToggle);

        ContentDrawerToggle.setDrawerIndicatorEnabled(false);
        ContentDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   checkDrawer();
            }
        });
        ContentDrawerToggle.syncState();

        ContentDrawerLayout.setScrimColor(getColor(android.R.color.transparent));
        ContentDrawerLayout.setDrawerElevation(0);

        ContentDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);


    }




    private void setUpListeners() {
        ContentBottomBar.setOnNavigationItemSelectedListener(this);
        ContentLeftMenu.setNavigationItemSelectedListener(this);
        ProfileActionMenu.setNavigationItemSelectedListener(this);

        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(getController().getCurrentFrag() instanceof FeaturedPortal)){
                    getController().switchTab(FragNavController.TAB6);
                }
                ContentBottomBar.getMenu().setGroupCheckable(0, false, false);
                if(!(getController().getStack(FragNavController.TAB6).get(0) instanceof FeaturedPortal)){
                    getController().popFragments(getController().getStack(FragNavController.TAB6).size());
                }
                getController().clearStack();
            }
        });

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContentSearch.getVisibility() == View.GONE) {
                    ContentSearch.setVisibility(View.VISIBLE);
                    ContentSearch.setIconified(false);
                } else {
                    ContentSearch.setIconified(true);
                    ContentSearch.setVisibility(View.GONE);

                }

            }
        });
        ContentSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ContentSearch.setVisibility(View.GONE);
                return false;
            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(ContentActivity.this, PassportActivity.class));
            }

        });

        ProfileMenu.setMenuClickListener(new SliderItemClickListener() {
            @Override
            public void onItemClick(View view) {
                switch (view.getId()){
                    case R.id.profileWidget_Messages:{

                        break;
                    }


                    case R.id.profileWidget_Upload:{

                        break;
                    }

                    case R.id.profileWidget_Notification:{

                        break;
                    }

                }
            }
        });

        ProfileMenu.setOnProfileIconClickListener(new ProfileIconClickListener() {
            @Override
            public void onClick(View view) {
                if(ContentDrawerLayout.isDrawerOpen(ProfileActionMenu)) ContentDrawerLayout.closeDrawer(ProfileActionMenu);
                else ContentDrawerLayout.openDrawer(ProfileActionMenu);
            }
        });

        ProfileMenuHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getController().pushFragment(GenericUserFragment.newInstance());
                ContentDrawerLayout.closeDrawer(ProfileActionMenu);

            }
        });


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ContentDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
        ContentDrawerToggle.onConfigurationChanged(conf);
    }
    @Override
    public Fragment getRootFragment(int identifier) {
        switch (identifier) {
            case FragNavController.TAB1:
                return MoviesPortal.newInstance();
            case FragNavController.TAB2:
                return AudioPortal.newInstance();
            case FragNavController.TAB3:
                return ArtPortal.newInstance();
            case FragNavController.TAB4:
                return GamesPortal.newInstance();
            case FragNavController.TAB5:
                return CommunityPortal.newInstance();
            case FragNavController.TAB6:
                return FeaturedPortal.newInstance();
        }
        throw new IllegalArgumentException("No such index");
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ContentFragmentsController.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (ContentDrawerLayout.isDrawerVisible(ContentLeftMenu)) ContentDrawerLayout.closeDrawer(ContentLeftMenu);
        if (ContentSearch.getVisibility() == View.VISIBLE && ContentSearch.isIconified()){ ContentSearch.setVisibility(View.GONE);}
        if (!HandleBackpressed()) {
            super.onBackPressed();
        }

    }

    private boolean HandleBackpressed() {
        if (getController().isRootFragment()) {
            if (getController().getCurrentFrag() instanceof FeaturedPortal) {
                getController().clearStack();
                return false;
            } else {
                getController().switchTab(FragNavController.TAB6);

                ContentBottomBar.getMenu().setGroupCheckable(0, false, false);
                return true;
            }
        } else {
            getController().popFragment();
            return true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        contentAppBarLayout.setExpanded(true, true);
        if (ContentDrawerLayout.isDrawerOpen(ContentLeftMenu))
            ContentDrawerLayout.closeDrawer(ContentLeftMenu);


        contentBottomBarBehaviour.slideUp(ContentBottomBar);


        switch (item.getItemId()) {
            case R.id.bottom_content_movies:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                getController().switchTab(FragNavController.TAB1);
                return true;}
            case R.id.bottom_content_audio:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                getController().switchTab(FragNavController.TAB2);
                return true;}
            case R.id.bottom_content_art:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                getController().switchTab(FragNavController.TAB3);
                return true;}
            case R.id.bottom_content_games:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                getController().switchTab(FragNavController.TAB4);
                return true;}
            case R.id.bottom_content_community:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                getController().switchTab(FragNavController.TAB5);
                return true;}


            case R.id.leftmenu_content_movies: {
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                ContentBottomBar.setSelectedItemId(R.id.bottom_content_movies);
                getController().switchTab(FragNavController.TAB1);
                return true;
            }
            case R.id.leftmenu_content_audio: {
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                ContentBottomBar.setSelectedItemId(R.id.bottom_content_audio);
                getController().switchTab(FragNavController.TAB2);
                return true;
            }
            case R.id.leftmenu_content_art:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                ContentBottomBar.setSelectedItemId(R.id.bottom_content_art);
                getController().switchTab(FragNavController.TAB3);
                return true;}
            case R.id.leftmenu_content_games:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                ContentBottomBar.setSelectedItemId(R.id.bottom_content_games);
                getController().switchTab(FragNavController.TAB4);
                return true;}
            case R.id.leftmenu_content_community:{
                ContentBottomBar.getMenu().setGroupCheckable(0, true, true);
                ContentBottomBar.setSelectedItemId(R.id.bottom_content_community);
                getController().switchTab(FragNavController.TAB5);
                return true;}




            case R.id.profileMenu_menu_actionLogOut:{
                NG_PreferencesData preferencesData = getPreferencesFromStore();
                if(preferencesData == null) preferencesData = new NG_PreferencesData();
                preferencesData.setLogged(false);
                updatePreferences(preferencesData);
                ContentDrawerLayout.closeDrawer(ProfileActionMenu);
                NG_Bus.get().post(new NG_Events.NG_UserLoggedOut());
            }




                default:{
                ContentBottomBar.setSelected(false);}
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (ContentFragmentsController != null) {
            getController().onSaveInstanceState(outState);

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoggedIn(NG_Events.NG_UserLoggedIn ULI){
        LoginButton.setVisibility(View.GONE);
        ProfileMenu.setVisibility(View.VISIBLE);
        ProfileActionMenu.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoggedOut(NG_Events.NG_UserLoggedOut ULO){
        if(ContentDrawerLayout.isDrawerOpen(ProfileActionMenu)) ContentDrawerLayout.closeDrawer(ProfileActionMenu);
        LoginButton.setVisibility(View.VISIBLE);
        ProfileMenu.setVisibility(View.GONE);
        ProfileActionMenu.setVisibility(View.GONE);
    }


    private void checkDrawer(){
        if(ContentDrawerLayout.isDrawerOpen(ContentLeftMenu)) ContentDrawerLayout.closeDrawer(ContentLeftMenu);
        else ContentDrawerLayout.openDrawer(ContentLeftMenu);
    }

    @Override
    public void onTabTransaction(@Nullable Fragment fragment, int i) {

    }

    @Override
    public void onFragmentTransaction(Fragment fragment, FragNavController.TransactionType transactionType) {
        ContentDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}