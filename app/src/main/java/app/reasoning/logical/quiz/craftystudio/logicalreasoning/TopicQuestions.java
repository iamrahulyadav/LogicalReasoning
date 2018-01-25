package app.reasoning.logical.quiz.craftystudio.logicalreasoning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;

import utils.FireBaseHandler;
import utils.Questions;
import utils.ZoomOutPageTransformer;

public class TopicQuestions extends AppCompatActivity {

    FireBaseHandler fireBaseHandler;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    ArrayList<Questions> mQuestionsList = new ArrayList<>();

    String topicName;

    boolean isMoreQuestionAvailable = true;

    static BottomSheetBehavior behavior;
    TextView mExplainationBottomsheetTextview;
    ProgressDialog progressDialog;
    Questions questions = new Questions();

    ImageView mTopicQuestionShareImageview;

    boolean isPushNotification=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_questions);
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


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.mainactivity_navigation_menu);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottombar_main_explaination:
                                putExplaination();
                                break;
                            case R.id.bottombar_main_share:
                                questions = mQuestionsList.get(mPager.getCurrentItem());
                                onShareClick();
                                break;

                        }
                        return true;
                    }
                });

        mExplainationBottomsheetTextview = (TextView) findViewById(R.id.randomTestactivity_explaination_textview);

        //share question
        mTopicQuestionShareImageview = (ImageView) findViewById(R.id.topicQuestion_share_imageview);
        mTopicQuestionShareImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick();
            }
        });

        mPager = (ViewPager) findViewById(R.id.mainActivity_viewpager);
        initializeViewPager();

        //questions by topic name
        topicName = getIntent().getExtras().getString("Topic");
        String questionUID = getIntent().getExtras().getString("questionUID");

        isPushNotification = getIntent().getBooleanExtra("pushNotification", false);

        if (questionUID != null) {

            // showDialog("Loading...Please Wait");
            downloadQuestion(questionUID);
        }
        showDialog("Loading...Please Wait");
        downloadQuestionByTopic(topicName);

        TextView questionTopicName = (TextView) findViewById(R.id.fragmentAptitudeQuiz_topicName_Textview);
        questionTopicName.setText(topicName);


        try{
            Answers.getInstance().logCustom(new CustomEvent("Topic open").putCustomAttribute("topic",topicName));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void putExplaination() {

        mExplainationBottomsheetTextview.setVisibility(View.VISIBLE);
        Questions question = mQuestionsList.get(mPager.getCurrentItem());
        //mExplainationBottomsheetTextview.setText(question.getQuestionExplaination());
    }

    public void downloadQuestion(String questionUID) {

        fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.downloadQuestion(questionUID, new FireBaseHandler.OnQuestionlistener() {
            @Override
            public void onQuestionDownLoad(Questions questions, boolean isSuccessful) {
                if (isSuccessful) {
                    mQuestionsList.add(0, questions);
                    initializeViewPager();
                    mPagerAdapter.notifyDataSetChanged();

                    hideDialog();

                }


            }

            @Override
            public void onQuestionListDownLoad(ArrayList<Questions> questionList, boolean isSuccessful) {

            }

            @Override
            public void onQuestionUpload(boolean isSuccessful) {

            }

        });

    }

    public void downloadMoreQuestionList() {


        fireBaseHandler.downloadMoreQuestionsList(topicName, 5, mQuestionsList.get(mQuestionsList.size() - 1).getQuestionUID(), new FireBaseHandler.OnQuestionlistener() {
            @Override
            public void onQuestionDownLoad(Questions questions, boolean isSuccessful) {

            }

            @Override
            public void onQuestionListDownLoad(ArrayList<Questions> questionList, boolean isSuccessful) {
                if (isSuccessful) {

                    for (Questions question : questionList) {

                        if (topicName.equalsIgnoreCase(question.getQuestionTopicName())) {
                            TopicQuestions.this.mQuestionsList.add(question);

                        } else {
                            isMoreQuestionAvailable = false;
                        }
                    }

                    //initializeNativeAds();
                    mPagerAdapter.notifyDataSetChanged();

                    hideDialog();


                } else {
                    hideDialog();
                    // openConnectionFailureDialog();
                }
            }

            @Override
            public void onQuestionUpload(boolean isSuccessful) {

            }
        });


    }

    /* Download questions according to TOPIC name*/
    public void downloadQuestionByTopic(String topic) {
        fireBaseHandler = new FireBaseHandler();
        fireBaseHandler.downloadQuestionList(topic, 8, new FireBaseHandler.OnQuestionlistener() {
            @Override
            public void onQuestionDownLoad(Questions questions, boolean isSuccessful) {

                hideDialog();
            }

            @Override
            public void onQuestionListDownLoad(ArrayList<Questions> questionList, boolean isSuccessful) {

                if (isSuccessful) {

                    for (Questions questions : questionList) {
                        mQuestionsList.add(questions);
                    }
                    initializeViewPager();

                    mPagerAdapter.notifyDataSetChanged();
                }
                hideDialog();


            }

            @Override
            public void onQuestionUpload(boolean isSuccessful) {

            }
        });


    }

    private void initializeViewPager() {

// Instantiate a ViewPager and a PagerAdapter.

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        //change to zoom
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //checkInterstitialAds();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            //  adsCount++;
            //getting more stories
            if (position == mQuestionsList.size() - 2) {

                if (isMoreQuestionAvailable) {
                    downloadMoreQuestionList();

                }
            }

            return AptitudeFragment.newInstance(mQuestionsList.get(position), TopicQuestions.this, false);
        }

        @Override
        public int getCount() {
            return mQuestionsList.size();
        }
    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();

        if (isPushNotification){
            Intent intent = new Intent(TopicQuestions.this,MainActivity.class);
            startActivity(intent);

        }

    }

    public void showDialog(String message) {
        progressDialog = new ProgressDialog(TopicQuestions.this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideDialog() {
        progressDialog.cancel();
    }

    private void onShareClick() {
        showDialog("Creating Link...Please Wait");
        questions = mQuestionsList.get(mPager.getCurrentItem());

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

            Answers.getInstance().logCustom(new CustomEvent("Share link created").putCustomAttribute("Content Id", questions.getQuestionUID())
                    .putCustomAttribute("topic name", questions.getQuestionTopicName()));

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
