package app.reasoning.logical.quiz.craftystudio.logicalreasoning;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;

import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.Random;

import utils.AppRater;
import utils.ClickListener;
import utils.FireBaseHandler;
import utils.Questions;
import utils.TopicListAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    FireBaseHandler fireBaseHandler;

    ArrayList<String> mArraylist;
    ListView topicAndTestListview;
    TopicListAdapter adapter;

    Toolbar toolbar;
    int check;

    ProgressDialog progressDialog;
    private LinearLayout linearLayout;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fireBaseHandler = new FireBaseHandler();
        openDynamicLink();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        FirebaseMessaging.getInstance().subscribeToTopic("subscribed");
        try {
            AppRater.app_launched(MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.mainactivity_navigation_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottombar_main_topic:
                                downloadTopicList();
                                break;
                            case R.id.bottombar_main_daily:
                                downloadDateList();
                                break;

                        }
                        return true;
                    }
                });


        MobileAds.initialize(this, "ca-app-pub-8455191357100024~2906265563");


        setListViewHeader();
        setListViewFooter();

    }




    private void setListViewHeader() {

        try {
            linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            topicAndTestListview.addHeaderView(linearLayout);

            final View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_textview, null, false);

            TextView topicNameTextview = (TextView) footerView.findViewById(R.id.custom_textview);
            topicNameTextview.setText("Take Test");
            topicNameTextview.setTextColor(Color.parseColor("#000000"));


            footerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openRandomTestActivity(footerView);
                }
            });

            linearLayout.addView(footerView);


            // Instantiate an AdView view
            adView = new AdView(this, "204879970145805_204880070145795", AdSize.BANNER_HEIGHT_50);
            adView.setAdListener(new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    try {
                        Answers.getInstance().logCustom(new CustomEvent("Ad failed").putCustomAttribute("message", adError.getErrorMessage()).putCustomAttribute("Placement", "banner"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdLoaded(Ad ad) {

                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            });

            View headerAdView = ((LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_textview, null, false);


            LinearLayout cardAdView = (LinearLayout) headerAdView.findViewById(R.id.custom_background_linearLayout);
            cardAdView.removeAllViews();
            cardAdView.addView(adView);

            adView.loadAd();

            linearLayout.addView(headerAdView);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void setListViewFooter() {

        try {
            LinearLayout footerLinearLayout = new LinearLayout(this);
            footerLinearLayout.setOrientation(LinearLayout.VERTICAL);


            com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(this);
            adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-8455191357100024/7091663406");

            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new com.google.android.gms.ads.AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);

                    Log.d("Ads", "onAdFailedToLoad: "+i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });

            View footerAdView = ((LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_textview, null, false);

            LinearLayout cardAdView =(LinearLayout) footerAdView.findViewById(R.id.custom_background_linearLayout);
            cardAdView.removeAllViews();
            cardAdView.addView(adView);

            footerLinearLayout.addView(footerAdView);

            View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_textview, null, false);

            TextView topicNameTextview = (TextView) footerView.findViewById(R.id.custom_textview);
            topicNameTextview.setText("Aptitude Master");
            topicNameTextview.setTextColor(getResources().getColor(R.color.colorRed));


            footerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAptitudeClick();
                }
            });

            footerLinearLayout.addView(footerView);

            topicAndTestListview.addFooterView(footerLinearLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onAptitudeClick() {
        try {
            String link = "https://play.google.com/store/apps/details?id=app.aptitude.quiz.craftystudio.aptitudequiz";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));

            Answers.getInstance().logCustom(new CustomEvent("Logical Reasoning Click"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void uploadTopicName() {
        FireBaseHandler fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.uploadTopicName("Time and Work", new FireBaseHandler.OnTopiclistener() {
            @Override
            public void onTopicDownLoad(String topic, boolean isSuccessful) {

                if (isSuccessful) {
                    Toast.makeText(MainActivity.this, topic + " Topic Uploaded ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTopicListDownLoad(ArrayList<String> topicList, boolean isSuccessful) {

            }

            @Override
            public void onTopicUpload(boolean isSuccessful) {


            }
        });

    }


    public void initializeActivity() {


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mArraylist = new ArrayList<>();
        topicAndTestListview = (ListView) findViewById(R.id.topicActivity_topic_listview);


        //calling rate now dialog
        // AppRater appRater = new AppRater();
        // appRater.app_launched(TopicActivity.this);


        //download list of Topics
        showDialog("Loading...Please wait");
        downloadTopicList();


    }

    private void downloadTopicList() {
        FireBaseHandler fireBaseHandler = new FireBaseHandler();

        fireBaseHandler.downloadTopicList(30, new FireBaseHandler.OnTopiclistener() {
            @Override
            public void onTopicDownLoad(String topic, boolean isSuccessful) {

            }

            @Override
            public void onTopicListDownLoad(ArrayList<String> topicList, boolean isSuccessful) {

                if (isSuccessful) {
                    //  Toast.makeText(TopicActivity.this, "size is " + topicList.size(), Toast.LENGTH_SHORT).show();

                    mArraylist = topicList;
                    adapter = new TopicListAdapter(getApplicationContext(), R.layout.custom_textview, mArraylist);

                    adapter.setOnItemCLickListener(new ClickListener() {
                        @Override
                        public void onItemCLickListener(View view, int position) {
                            String topicString = mArraylist.get(position);

                            openTopicQuestionActivity(0, topicString, null);
                            //  Toast.makeText(TopicActivity.this, " Selected " + textview.getText().toString(), Toast.LENGTH_SHORT).show();

                        }
                    });

                    topicAndTestListview.post(new Runnable() {
                        public void run() {
                            topicAndTestListview.setAdapter(adapter);
                        }
                    });


                    hideDialog();

                }
                hideDialog();

            }

            @Override
            public void onTopicUpload(boolean isSuccessful) {

            }
        });


    }


    public void downloadDateList() {
        fireBaseHandler.downloadDateList(15, new FireBaseHandler.OnTestSerieslistener() {
            @Override
            public void onTestDownLoad(String test, boolean isSuccessful) {

            }

            @Override
            public void onTestListDownLoad(ArrayList<String> testList, boolean isSuccessful) {
                if (isSuccessful) {

                    mArraylist.clear();

                    for (String name : testList) {
                        mArraylist.add(name);
                    }

                    //check = 1 is Test Series
                    check = 1;
                    adapter = new TopicListAdapter(MainActivity.this, R.layout.custom_textview, mArraylist);

                    adapter.setOnItemCLickListener(new ClickListener() {
                        @Override
                        public void onItemCLickListener(View view, int position) {

                            String string = mArraylist.get(position);

                            if (check == 1) {

                                openTopicQuestionActivity(1, string, null);

                                //  Toast.makeText(TopicActivity.this, "In Test " + " Selected " + textview.getText().toString() + " Postion is " + position, Toast.LENGTH_SHORT).show();

                                try {
                                    Answers.getInstance().logCustom(new CustomEvent("Daily Quiz open").putCustomAttribute("Date Name", string));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });


                    topicAndTestListview.post(new Runnable() {
                        public void run() {
                            topicAndTestListview.setAdapter(adapter);
                        }
                    });


                    hideDialog();

                }
                hideDialog();
            }

            @Override
            public void onTestUpload(boolean isSuccessful) {

            }

        });
    }


    public void openRandomTestActivity(View view) {
        Intent intent = new Intent(MainActivity.this, RandomTestActivity.class);
        startActivity(intent);
    }

    private void openDynamicLink() {

        //dowmload topiclist
        initializeActivity();

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("DeepLink", "onSuccess: " + deepLink);

                            String questionID = deepLink.getQueryParameter("questionID");
                            String questionTopicName = deepLink.getQueryParameter("questionTopic");


                            //download question
                            if (questionID != null) {
                                openTopicQuestionActivity(0, questionTopicName, questionID);
                                try {
                                    Answers.getInstance().logCustom(new CustomEvent("Via dyanamic link").putCustomAttribute("question id", questionID));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            Log.d("DeepLink", "onSuccess: ");

                            //download story list


                            try {
                                Intent intent = getIntent();
                                String questionID = intent.getStringExtra("questionID");
                                String questionTopicName = intent.getStringExtra("questionTopic");
                                if (questionID == null) {

                                } else {
                                    //download story
                                    openTopicQuestionActivity(0, questionTopicName, questionID);
                                    try {
                                        Answers.getInstance().logCustom(new CustomEvent("Via push notification").putCustomAttribute("Question id", questionID));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //   Toast.makeText(this, "Story id is = "+storyID, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {

                                e.printStackTrace();
                            }


                        }


                        // Handle the deep link. For example, open the linked
                        // content, or apply promotional credit to the user's
                        // account.
                        // ...

                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        initializeActivity();
                        Log.w("DeepLink", "getDynamicLink:onFailure", e);
                    }
                });
    }

    public void openTopicQuestionActivity(int check, String topicName, String QuestionUID) {

        Intent intent = new Intent(MainActivity.this, TopicQuestions.class);
        Bundle bundle = new Bundle();
        bundle.putInt("check", check);
        bundle.putString("Topic", topicName);
        bundle.putString("questionUID", QuestionUID);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottombar_main_topic:
                    downloadTopicList();
                    return true;

                case R.id.bottombar_main_daily:
                    downloadDateList();
                    return true;


            }
            return false;
        }
    };

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_test) {

            Intent intent = new Intent(MainActivity.this, RandomTestActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_bookmark) {
            Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
            startActivity(intent);

            // onRateUs();
        } else if (id == R.id.nav_rate) {
            onRateUs();
        } else if (id == R.id.nav_share) {

            ShareApp();
        } else if (id == R.id.nav_suggestion) {
            giveSuggestion();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showDialog(String message) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideDialog() {
        try {
            progressDialog.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void giveSuggestion() {

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"acraftystudio@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion For " + getResources().getString(R.string.app_name));
        emailIntent.setType("text/plain");
        startActivity(Intent.createChooser(emailIntent, "Send mail From..."));

    }

    private void onRateUs() {
        try {
            String link = "https://goo.gl/DesxPy";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (Exception e) {

        }
    }

    private void ShareApp() {

        try {
            /*
            Answers.getInstance().logCustom(new CustomEvent("Share link created").putCustomAttribute("Content Id", questions.getQuestionUID())
                    .putCustomAttribute("Shares", questions.getQuestionTopicName()));
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

        hideDialog();

        String Link = "https://goo.gl/DesxPy";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Link + "\n Logical Reasoning App \n Download it Now! ");
        startActivity(Intent.createChooser(sharingIntent, "Share Logical Reasoning Question via"));

    }

}
