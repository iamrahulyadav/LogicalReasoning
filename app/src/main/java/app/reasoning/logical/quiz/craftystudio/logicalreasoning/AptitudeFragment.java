package app.reasoning.logical.quiz.craftystudio.logicalreasoning;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;

import utils.Questions;

/**
 * Created by Aisha on 9/30/2017.
 */

public class AptitudeFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    ProgressDialog progressDialog;
    static Context mainActivity;

    Questions questions;

    TextView optionA;
    TextView optionB;
    TextView optionC;
    TextView optionD;
    TextView questionExplaination;

    CardView optionACardView;
    CardView optionBCardView;
    CardView optionCCardView;
    CardView optionDCardView;


    CardView explainationDisplayCardview;


    public static AptitudeFragment newInstance(Questions questions, Context context, boolean randomTestOn) {

        if (randomTestOn) {
            RandomTestActivity.isRandomTestQuestions = true;
        }

        mainActivity = context;
        AptitudeFragment fragment = new AptitudeFragment();
        Bundle args = new Bundle();
        args.putSerializable("Question", questions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.questions = (Questions) getArguments().getSerializable("Question");

           /* Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName(story.getStoryTitle())
                    .putContentId(story.getStoryID())
            );
            */

        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_aptitude_quiz, container, false);
        //initializeView

        TextView questionName = (TextView) view.findViewById(R.id.fragmentAptitudeQuiz_QuestionName_Textview);
        optionA = (TextView) view.findViewById(R.id.fragmentAptitudeQuiz_optionA_Textview);
        optionB = (TextView) view.findViewById(R.id.fragmentAptitudeQuiz_optionB_Textview);
        optionC = (TextView) view.findViewById(R.id.fragmentAptitudeQuiz_optionC_Textview);
        optionD = (TextView) view.findViewById(R.id.fragmentAptitudeQuiz_optionD_Textview);
        TextView previousYearQuestions = (TextView) view.findViewById(R.id.fragmentAptitudeQuiz_previousYearName_Textview);

        questionExplaination = (TextView) view.findViewById(R.id.randomTestactivity_explaination_textview);
        explainationDisplayCardview = (CardView) view.findViewById(R.id.question_explaination_cardview);

        //   TextView randomNumber = (TextView) view.findViewById(R.id.fragmentAptitudeQuiz_randomNumber_Textview);


        optionACardView = (CardView) view.findViewById(R.id.fragmentAptitudeQuiz_optionA_Cardview);
        optionBCardView = (CardView) view.findViewById(R.id.fragmentAptitudeQuiz_optionB_Cardview);
        optionCCardView = (CardView) view.findViewById(R.id.fragmentAptitudeQuiz_optionC_Cardview);
        optionDCardView = (CardView) view.findViewById(R.id.fragmentAptitudeQuiz_optionD_Cardview);


        questionName.setText("Q. " + questions.getQuestionName() + " ");
        optionA.setText(questions.getOptionA());
        optionB.setText(questions.getOptionB());
        optionC.setText(questions.getOptionC());
        optionD.setText(questions.getOptionD());
        //   randomNumber.setText(questions.getRandomNumber() + "");
        previousYearQuestions.setText(questions.getPreviousYearsName());


        optionACardView.setOnClickListener(this);
        optionBCardView.setOnClickListener(this);
        optionCCardView.setOnClickListener(this);
        optionDCardView.setOnClickListener(this);


        getUserAnswers();


        return view;
    }


    private void getRightAnswer() {
        String correctANswer = questions.getCorrectAnswer();

        if (optionA.getText().toString().equalsIgnoreCase(correctANswer)) {
           // optionACardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            optionACardView.setBackgroundResource(R.drawable.mygreenbutton);


        } else if (optionB.getText().toString().equalsIgnoreCase(correctANswer)) {
            //optionBCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            optionBCardView.setBackgroundResource(R.drawable.mygreenbutton);

        } else if (optionC.getText().toString().equalsIgnoreCase(correctANswer)) {
           // optionCCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            optionCCardView.setBackgroundResource(R.drawable.mygreenbutton);

        } else if (optionD.getText().toString().equalsIgnoreCase(correctANswer)) {
           // optionDCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
            optionDCardView.setBackgroundResource(R.drawable.mygreenbutton);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View view) {

        //normal topic questions and normal test series
        if (!RandomTestActivity.isRandomTestQuestions) {

            switch (view.getId()) {

                case R.id.fragmentAptitudeQuiz_optionA_Cardview:
                    questions.setUserAnswer(questions.getOptionA());

                    if (questions.getOptionA().trim().equalsIgnoreCase(questions.getCorrectAnswer().trim())) {
                        //Toast.makeText(mainActivity, "Right Answer", Toast.LENGTH_SHORT).show();
                        // optionACardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));

                        optionACardView.setBackgroundResource(R.drawable.mygreenbutton);


                    } else {
                        // Toast.makeText(mainActivity, "Wrong Answer", Toast.LENGTH_SHORT).show();
                        // optionACardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionACardView.setBackgroundResource(R.drawable.myredbutton);

                        getRightAnswer();

                    }
                    break;

                case R.id.fragmentAptitudeQuiz_optionB_Cardview:
                    questions.setUserAnswer(questions.getOptionB());

                    if (questions.getOptionB().trim().equalsIgnoreCase(questions.getCorrectAnswer().trim())) {
                        //Toast.makeText(mainActivity, "Right Answer", Toast.LENGTH_SHORT).show();
                        //  optionBCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                        optionBCardView.setBackgroundResource(R.drawable.mygreenbutton);


                    } else {
                        // Toast.makeText(mainActivity, "Wrong Answer", Toast.LENGTH_SHORT).show();
                        //  optionBCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionBCardView.setBackgroundResource(R.drawable.myredbutton);

                        getRightAnswer();
                    }
                    break;
                case R.id.fragmentAptitudeQuiz_optionC_Cardview:

                    questions.setUserAnswer(questions.getOptionC());

                    if (questions.getOptionC().trim().equalsIgnoreCase(questions.getCorrectAnswer().trim())) {
                        //Toast.makeText(mainActivity, "Right Answer", Toast.LENGTH_SHORT).show();
                        // optionCCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                        optionCCardView.setBackgroundResource(R.drawable.mygreenbutton);


                    } else {
                        //  Toast.makeText(mainActivity, "Wrong Answer", Toast.LENGTH_SHORT).show();
                        //  optionCCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionCCardView.setBackgroundResource(R.drawable.myredbutton);

                        getRightAnswer();
                    }
                    break;
                case R.id.fragmentAptitudeQuiz_optionD_Cardview:
                    questions.setUserAnswer(questions.getOptionD());

                    if (questions.getOptionD().trim().equalsIgnoreCase(questions.getCorrectAnswer().trim())) {
                        //Toast.makeText(mainActivity, "Right Answer", Toast.LENGTH_SHORT).show();
                        //optionDCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                        optionDCardView.setBackgroundResource(R.drawable.mygreenbutton);


                    } else {
                        // Toast.makeText(mainActivity, "Wrong Answer", Toast.LENGTH_SHORT).show();
                        // optionDCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionDCardView.setBackgroundResource(R.drawable.myredbutton);

                        getRightAnswer();
                    }
                    break;
              /*  case R.id.fragmentAptitudeQuiz_topicName_Textview:
                    //open TOPIC ACTIVITY
                    Intent intent = new Intent(mainActivity, TopicActivity.class);
                    startActivity(intent);
                    break;
                    */

            }

            //setExplaination
            explainationDisplayCardview.setVisibility(View.VISIBLE);
            questionExplaination.setText(questions.getQuestionExplaination());

            try {
                Answers.getInstance().logContentView(new ContentViewEvent().putContentName("Question answered").putContentType(questions.getQuestionTopicName()).putContentId(questions.getQuestionUID()));
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {


            optionACardView.setBackgroundResource(R.color.colorWhiteBg);
            optionBCardView.setBackgroundResource(R.color.colorWhiteBg);
            optionCCardView.setBackgroundResource(R.color.colorWhiteBg);
            optionDCardView.setBackgroundResource(R.color.colorWhiteBg);

            CardView testCardview = (CardView) view;
            //linearLayout.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorYellow));
            testCardview.setBackgroundResource(R.drawable.mybutton);


            switch (view.getId()) {

                case R.id.fragmentAptitudeQuiz_optionA_Cardview:
                    questions.setUserAnswer(questions.getOptionA());
                    break;
                case R.id.fragmentAptitudeQuiz_optionB_Cardview:
                    questions.setUserAnswer(questions.getOptionB());
                    break;
                case R.id.fragmentAptitudeQuiz_optionC_Cardview:
                    questions.setUserAnswer(questions.getOptionC());
                    break;
                case R.id.fragmentAptitudeQuiz_optionD_Cardview:
                    questions.setUserAnswer(questions.getOptionD());
                    break;


            }
        }

    }

    private void getUserAnswers() {

        if (!RandomTestActivity.isRandomTestQuestions) {


            if (questions.getUserAnswer() != null) {

                //setExplaination
                explainationDisplayCardview.setVisibility(View.VISIBLE);
                questionExplaination.setText(questions.getQuestionExplaination());


                formatQuestion(questions);

                if (questions.getUserAnswer().equalsIgnoreCase(questions.getCorrectAnswer())) {
                    if (optionA.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                       // optionACardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                        optionACardView.setBackgroundResource(R.drawable.mygreenbutton);


                    } else if (optionB.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                        //optionBCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                        optionBCardView.setBackgroundResource(R.drawable.mygreenbutton);


                    } else if (optionC.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                      //  optionCCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                        optionCCardView.setBackgroundResource(R.drawable.mygreenbutton);


                    } else if (optionD.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                       // optionDCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGreen));
                        optionDCardView.setBackgroundResource(R.drawable.mygreenbutton);


                    }

                } else {
                    if (optionA.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                        //optionACardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionACardView.setBackgroundResource(R.drawable.myredbutton);


                    } else if (optionB.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                       // optionBCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionBCardView.setBackgroundResource(R.drawable.myredbutton);


                    } else if (optionC.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                        //optionCCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionCCardView.setBackgroundResource(R.drawable.myredbutton);

                    } else if (optionD.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                       // optionDCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorRed));
                        optionDCardView.setBackgroundResource(R.drawable.myredbutton);

                    }


                }

            }
        } else {
            if (questions.getUserAnswer() != null) {

                if (optionA.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                  //  optionACardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorYellow));
                    optionACardView.setBackgroundResource(R.drawable.mybutton);


                } else if (optionB.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                   // optionBCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorYellow));
                    optionBCardView.setBackgroundResource(R.drawable.mybutton);


                } else if (optionC.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                   // optionCCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorYellow));
                    optionCCardView.setBackgroundResource(R.drawable.mybutton);

                } else if (optionD.getText().toString().equalsIgnoreCase(questions.getUserAnswer())) {
                 //   optionDCardView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorYellow));
                    optionDCardView.setBackgroundResource(R.drawable.mybutton);

                }

            }
        }

    }

    private void formatQuestion(Questions questions) {

        questions.setCorrectAnswer(questions.getCorrectAnswer().trim());
        questions.setOptionA(questions.getOptionA().trim());
        questions.setOptionB(questions.getOptionB().trim());
        questions.setOptionC(questions.getOptionC().trim());
        questions.setOptionD(questions.getOptionD().trim());
        questions.setUserAnswer(questions.getUserAnswer().trim());


    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
