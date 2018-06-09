package app.reasoning.logical.quiz.craftystudio.logicalreasoning;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;

import utils.ClickListener;
import utils.FireBaseHandler;
import utils.Questions;
import utils.RightNavAdapter;
import utils.ZoomOutPageTransformer;

public class RandomTestActivity extends AppCompatActivity {


    FireBaseHandler fireBaseHandler;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<Questions> mQuestionsList = new ArrayList<>();

    static boolean isRandomTestQuestions = false;

    static boolean isTestActivity = false;

    CountDownTimer countDownTimer;

    ProgressDialog progressDialog;

    TextView mCountDownTimer;
    Questions questions;


    ListView questionNameDisplayListview;
    RightNavAdapter questionNameAdapter;
    ArrayList<Object> mQuestionsNameList = new ArrayList<>();


    TextView mQuestionNumberTextview;
    TextView mQuestionTimerTextview;
    long totalSeconds = 1200;
    long intervalSeconds = 1;
    CountDownTimer timer;

    DrawerLayout drawer;


    private InterstitialAd interstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_test_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //side nav view to display question name
        drawer = (DrawerLayout) findViewById(R.id.drawer_random_activity_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerSlideAnimationEnabled(true);


        //Bottombar Navigation for refresh,submit,explaination amd share
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.randomactivity_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottombar_test_refresh:
                        onRefreshTest();
                        break;
                    case R.id.bottombar_test_share:
                        onShareQuestion();
                        break;
                    case R.id.bottombar_test_submit:
                        onSubmitTest();
                        break;
                }
                return true;
            }
        });


        mPager = (ViewPager) findViewById(R.id.random_test_activity_viewpager);
        mCountDownTimer = (TextView) findViewById(R.id.randomactivity_countdown_timer);


        //display question number
        mQuestionNumberTextview = (TextView) findViewById(R.id.randomactivity_question_number_textview);

        //display timer for each question
        mQuestionTimerTextview = (TextView) findViewById(R.id.randomactivity_question_timer_textview);
        timer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {

            public void onTick(long millisUntilFinished) {
                //  Log.d("seconds elapsed: ", (totalSeconds * 1000 - millisUntilFinished) / 1000 + "");

                if (mQuestionsList.size()<=0){
                    return;
                }

                int seconds = 0, minutes = 0;

                questions = mQuestionsList.get(mPager.getCurrentItem());


                if (questions.getUserAnswer() == null) {
                    questions.setQuestionTimer(questions.getQuestionTimer() + 1);

                } else {


                    //timeElapsed = seconds;
                }
                seconds = questions.getQuestionTimer();

                minutes = seconds / 60;
                seconds = seconds % 60;

                mQuestionTimerTextview.setText(String.format("%02d", minutes) + ":" + String.format("%02d", seconds));


            }

            public void onFinish() {
                Log.d("done!", "Time's up!");
            }

        };


        //side nav listview
        questionNameDisplayListview = (ListView) findViewById(R.id.randomactiity_question_name_display_listview_nav);
        questionNameDisplayListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPager.setCurrentItem(i);
                drawer.closeDrawers();
                // Toast.makeText(MainActivity.this, mQuestionsNameList.get(i), Toast.LENGTH_SHORT).show();
            }
        });
        //download Random Questions
        downloadRandomQuestionList();
        initializeViewPager();


        initializeInterstitial();

    }

    private void initializeInterstitial() {

        interstitialAd = new InterstitialAd(this, "204879970145805_205254126775056");



        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

                Log.d("Ads", "onError: "+adError);

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });


        interstitialAd.loadAd();

    }


    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    private void onAddingQuestionName(ArrayList<Questions> arrayList) {
        // DIsplay Question Name Right Navigation


        // Toast.makeText(this, arrayList.size() + "", Toast.LENGTH_SHORT).show();

        for (int i = 0; i < arrayList.size(); i++) {

            mQuestionsNameList.add(arrayList.get(i).getQuestionName());

        }

        questionNameAdapter = new RightNavAdapter(RandomTestActivity.this, R.layout.custom_right_side_nav, mQuestionsNameList);

        questionNameAdapter.setOnItemCLickListener(new ClickListener() {
            @Override
            public void onItemCLickListener(View view, int position) {
                mPager.setCurrentItem(position);
                drawer.closeDrawers();
                // Toast.makeText(MainActivity.this, mQuestionsNameList.get(i), Toast.LENGTH_SHORT).show();

            }
        });
        questionNameDisplayListview.setAdapter(questionNameAdapter);


        //  Toast.makeText(this, mQuestionsNameList.size() + "", Toast.LENGTH_SHORT).show();

    }

    private void onRefreshTest() {


        AlertDialog.Builder builder = new AlertDialog.Builder(RandomTestActivity.this);

        builder.setMessage("\n Do you Want to Start a New Test? ")
                .setTitle("Refresh");

        builder.setPositiveButton(" Yes ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                countDownTimer.cancel();
                mQuestionsList.clear();

                downloadRandomQuestionList();
                initializeViewPager();

            }
        });

        builder.setNegativeButton("Not Yet!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog mydialog = builder.create();
        mydialog.show();


    }

    private void calculateResult() {

        try {

            if (interstitialAd != null) {

                if (interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }
            }

            interstitialAd.show();

            mCountDownTimer.setText("00:00");
            int rightAnswer = 0, wrongAnswer = 0;
            for (Questions question : mQuestionsList) {
                if (question.getCorrectAnswer().equalsIgnoreCase(question.getUserAnswer())) {
                    rightAnswer++;
                }
            }


            wrongAnswer = 10 - rightAnswer;

            AlertDialog.Builder builder = new AlertDialog.Builder(RandomTestActivity.this);

            builder.setMessage("Right Answers = " + rightAnswer + " \n\n Wrong Answers = " + wrongAnswer)
                    .setTitle("Results");

            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });
            final AlertDialog mydialog = builder.create();
            //   mydialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextSize(16);
            mydialog.show();
            // displayRightAnswers.setText("Right = " + rightAnswer + " Wrong = " + wrongAnswer);
            // Toast.makeText(this, "right answers " + rightAnswer + " wrong answers " + wrongAnswer, Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initializeViewPager() {

        // Instantiate a ViewPager and a PagerAdapter.

        mPagerAdapter = new RandomTestActivity.ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //change to zoom
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mQuestionNumberTextview.setText("Question " + (position + 1));

                //checkInterstitialAds();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void onSubmitTest() {

        calculateResult();

        // displayRightAnswers.setVisibility(View.VISIBLE);
        isRandomTestQuestions = false;

        isTestActivity = true;
        initializeViewPager();

        countDownTimer.cancel();


    }

    public void onShareQuestion() {
        questions = mQuestionsList.get(mPager.getCurrentItem());
        onShareClick();
    }

    public void onOpenRightNav(View view) {
        drawer.openDrawer(Gravity.RIGHT);
    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //  adsCount++;
            /*
            //getting more stories
            if (position == mQuestionsList.size() - 2) {

                if (isMoreQuestionAvailable) {
                    downloadMoreQuestionList();

                }
            }
            */

            return AptitudeFragment.newInstance(mQuestionsList.get(position), RandomTestActivity.this, isRandomTestQuestions);
        }

        @Override
        public int getCount() {
            return mQuestionsList.size();
        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();
        isRandomTestQuestions = false;

        isTestActivity = true;


    }


    public void downloadRandomQuestionList() {

        showDialog();
        isRandomTestQuestions = true;
        fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.downloadQuestionList(10, new FireBaseHandler.OnQuestionlistener() {
            @Override
            public void onQuestionDownLoad(Questions questions, boolean isSuccessful) {

            }

            @Override
            public void onQuestionListDownLoad(ArrayList<Questions> questionList, boolean isSuccessful) {
                if (isSuccessful) {

                    for (Questions question : questionList) {
                        RandomTestActivity.this.mQuestionsList.add(question);
                    }

                    // initializeNativeAds();
                    mPagerAdapter.notifyDataSetChanged();


                    //countdown timer for random test
                    countDownTimer = new CountDownTimer(1200000, 1000) { // adjust the milli seconds here

                        public void onTick(long millisUntilFinished) {
                            int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                            int seconds = (int) (millisUntilFinished / 1000) % 60;
                            mCountDownTimer.setText(minutes + ":" + seconds);
                        }

                        public void onFinish() {
                            mCountDownTimer.setText("Time up!");
                            calculateResult();
                            isRandomTestQuestions = false;

                            initializeViewPager();
                        }
                    }.start();

                    onAddingQuestionName(mQuestionsList);

                    timer.start();

                } else {
                    //  openConnectionFailureDialog();
                }
                hideDialog();
            }

            @Override
            public void onQuestionUpload(boolean isSuccessful) {

            }

        });

    }


    public void showDialog() {
        progressDialog = new ProgressDialog(RandomTestActivity.this);
        progressDialog.setMessage("Please wait..Creating link");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideDialog() {
        try {
            progressDialog.cancel();
        }catch ( Exception e)
        {
            e.printStackTrace();
        }
    }


    private void onShareClick() {
        showDialog();
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/hBbEWP?questionID=" + questions.getQuestionUID() + "&questionTopic=" + questions.getQuestionTopicName()))
                .setDynamicLinkDomain("rxyj5.app.goo.gl")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("app.reasoning.logical.quiz.craftystudio.logicalreasoning")
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(questions.getQuestionName())
                                .setDescription(questions.getQuestionTopicName())
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource("share")
                                .setMedium("social")
                                .setCampaign("example-promo")
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();

                            openShareDialog(shortLink);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        hideDialog();
                    }
                });

    }


    private void openShareDialog(Uri shortUrl) {

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

        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "\nCan you Solve this question? \n\n " + questions.getQuestionName() + "\n\n" + "1. " + questions.getOptionA()
                        + "\n2. " + questions.getOptionB() + "\n3. " + questions.getOptionC() + "\n4. " + questions.getOptionD() + "\n\n See the Explaination here\n " + shortUrl);
        startActivity(Intent.createChooser(sharingIntent, "Share Logical Reasoning Question via"));

    }

}
