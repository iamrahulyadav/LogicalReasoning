package app.reasoning.logical.quiz.craftystudio.logicalreasoning;

import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import java.util.ArrayList;

import utils.ClickListener;
import utils.DataBaseHandler;
import utils.FireBaseHandler;
import utils.Questions;
import utils.RightNavAdapter;
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

    boolean isPushNotification = false;

    int check;


    TextView mQuestionNumberTextview;
    TextView mQuestionTimerTextview;
    long totalSeconds = 3600;
    long intervalSeconds = 1;
    static CountDownTimer timer;

    DrawerLayout drawer;


    ListView questionNameDisplayListview;
    RightNavAdapter questionNameAdapter;
    ArrayList<Object> mQuestionsNameList = new ArrayList<>();
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_right_nav_topic_question);
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


        drawer = (DrawerLayout) findViewById(R.id.drawer_right_nav_main_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerSlideAnimationEnabled(true);


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

        //to differentiate between topic and date questions
        check = getIntent().getExtras().getInt("check");

        //questions by topic name
        topicName = getIntent().getExtras().getString("Topic");
        String questionUID = getIntent().getExtras().getString("questionUID");

        isPushNotification = getIntent().getBooleanExtra("pushNotification", false);


        TextView questionTopicName = (TextView) findViewById(R.id.fragmentAptitudeQuiz_topicName_Textview);
        questionTopicName.setText(topicName);

        if (check == 0) {

            if (questionUID != null) {

                // showDialog("Loading...Please Wait");
                downloadQuestion(questionUID);
            }
            showDialog("Loading...Please Wait");
            downloadQuestionByTopic(topicName);

        } else if (check == 1) {
            showDialog("Loading...Please Wait");
            downloadQuestionByDateName(topicName);
        }

        //display question number
        mQuestionNumberTextview = (TextView) findViewById(R.id.fragmentAptitudeQuiz_questionNumber_Textview);

        //display timer for each question
        mQuestionTimerTextview = (TextView) findViewById(R.id.fragmentAptitudeQuiz_question_timer_Textview);
        timer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {

            public void onTick(long millisUntilFinished) {
                //  Log.d("seconds elapsed: ", (totalSeconds * 1000 - millisUntilFinished) / 1000 + "");

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


        questionNameDisplayListview = (ListView) findViewById(R.id.question_name_display_main_listview);
        questionNameDisplayListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (!questionNameDisplayListview.canScrollVertically(1)) {

                    if (!isLoading) {

                        if (isMoreQuestionAvailable) {
                            downloadMoreQuestionList();
                        }
                        //Toast.makeText(MainActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


        questionNameDisplayListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPager.setCurrentItem(i);
                drawer.closeDrawers();
                // Toast.makeText(MainActivity.this, mQuestionsNameList.get(i), Toast.LENGTH_SHORT).show();
            }
        });

        try {
            Answers.getInstance().logCustom(new CustomEvent("Topic open").putCustomAttribute("topic", topicName));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onOpenRightNav(View view) {
        drawer.openDrawer(Gravity.RIGHT);
    }


    private void onAddingQuestionName(ArrayList<Questions> arrayList) {
        // DIsplay Question Name Right Navigation


        // Toast.makeText(this, arrayList.size() + "", Toast.LENGTH_SHORT).show();

        for (int i = 0; i < arrayList.size(); i++) {

            mQuestionsNameList.add(arrayList.get(i).getQuestionName());

        }

        questionNameAdapter = new RightNavAdapter(TopicQuestions.this, R.layout.custom_right_side_nav, mQuestionsNameList);

        questionNameAdapter.setOnItemCLickListener(new ClickListener() {
            @Override
            public void onItemCLickListener(View view, int position) {
                mPager.setCurrentItem(position);
                drawer.closeDrawers();
                // Toast.makeText(MainActivity.this, mQuestionsNameList.get(i), Toast.LENGTH_SHORT).show();

            }
        });

       /* questionNameAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mQuestionsNameList);
                */
        questionNameDisplayListview.setAdapter(questionNameAdapter);


        //  Toast.makeText(this, mQuestionsNameList.size() + "", Toast.LENGTH_SHORT).show();

    }

    private void putExplaination() {

        mExplainationBottomsheetTextview.setVisibility(View.VISIBLE);
        Questions question = mQuestionsList.get(mPager.getCurrentItem());
        //mExplainationBottomsheetTextview.setText(question.getQuestionExplaination());
    }

    /* Download questions according to TEST name*/
    public void downloadQuestionByDateName(String dateName) {
        fireBaseHandler = new FireBaseHandler();

        isMoreQuestionAvailable = false;
        fireBaseHandler.downloadQuestionList(30, dateName, new FireBaseHandler.OnQuestionlistener() {
            @Override
            public void onQuestionDownLoad(Questions questions, boolean isSuccessful) {

                hideDialog();
            }

            @Override
            public void onQuestionListDownLoad(ArrayList<Questions> questionList, boolean isSuccessful) {

                if (isSuccessful) {

                    mQuestionsList.clear();

                    for (Questions questions : questionList) {
                        mQuestionsList.add(questions);
                    }
                    initializeViewPager();

                    mPagerAdapter.notifyDataSetChanged();


                    onAddingQuestionName(mQuestionsList);
                    timer.start();
                }

                hideDialog();

            }

            @Override
            public void onQuestionUpload(boolean isSuccessful) {


            }
        });


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

                    timer.start();

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


        isLoading = true;

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
                            mQuestionsNameList.add(question.getQuestionName());


                        } else {
                            isMoreQuestionAvailable = false;
                        }
                    }

                    //initializeNativeAds();
                    mPagerAdapter.notifyDataSetChanged();

                    questionNameAdapter.notifyDataSetChanged();

                    hideDialog();


                } else {
                    hideDialog();
                    // openConnectionFailureDialog();
                }

                isLoading = false;
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

                    onAddingQuestionName(mQuestionsList);
                    timer.start();
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

                mQuestionNumberTextview.setText("Question " + (position + 1));

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


    public void onBookMarkQuestion(View view) {
        //Adding Question to Bookmark
        DataBaseHandler db = new DataBaseHandler(TopicQuestions.this);
        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        //Log.d("Insert: ", "Inserting ..");
        db.addBookMark(mQuestionsList.get(mPager.getCurrentItem()));
        Toast.makeText(this, "Question Bookmarked", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onBackPressed() {


        super.onBackPressed();

        if (isPushNotification) {
            Intent intent = new Intent(TopicQuestions.this, MainActivity.class);
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
