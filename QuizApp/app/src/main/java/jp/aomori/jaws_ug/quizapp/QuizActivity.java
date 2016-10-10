package jp.aomori.jaws_ug.quizapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import jp.aomori.jaws_ug.quizapp.bean.Question;
import jp.aomori.jaws_ug.quizapp.dialog.OkOnlyDialogFragment;
import jp.aomori.jaws_ug.quizapp.dialog.ProgressDialogFragment;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String LOG_TAG = QuizActivity.class.getSimpleName();

    private static final int QUESTION_NUM_MAX = 5;  // 問題数
    private static final int ANSWER_NUM_MAX = 4;    // 選択肢の数（今回は固定で4択にしている）

    TextView questionNumTextView;
    TextView questionSentenceTextView;
    ImageView iconImageView;
    Button answerButton1, answerButton2, answerButton3, answerButton4;

    ProgressDialogFragment progressDialog;
    OkOnlyDialogFragment okOnlyDialog;

    int score = 0;
    int qNum = 0;

    int[] rndNum;

    Question mQuestion;

    Drawable iconImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionNumTextView = (TextView) findViewById(R.id.quiz_text_question_num);
        questionSentenceTextView = (TextView) findViewById(R.id.quiz_text_question_sentence);

        iconImageView = (ImageView) findViewById(R.id.quiz_image_icon);

        answerButton1 = (Button) findViewById(R.id.quiz_btn_answer1);
        answerButton2 = (Button) findViewById(R.id.quiz_btn_answer2);
        answerButton3 = (Button) findViewById(R.id.quiz_btn_answer3);
        answerButton4 = (Button) findViewById(R.id.quiz_btn_answer4);

        answerButton1.setOnClickListener(this);
        answerButton2.setOnClickListener(this);
        answerButton3.setOnClickListener(this);
        answerButton4.setOnClickListener(this);

        nextQuestionOrEnd();
    }

    @Override
    public void onClick(View v) {
        int answerNum = 0;

        switch (v.getId()) {
            case R.id.quiz_btn_answer1:
                answerNum = rndNum[0];
                break;
            case R.id.quiz_btn_answer2:
                answerNum = rndNum[1];
                break;
            case R.id.quiz_btn_answer3:
                answerNum = rndNum[2];
                break;
            case R.id.quiz_btn_answer4:
                answerNum = rndNum[3];
                break;
            default:
                break;
        }

        // 1番目の答えが正解という仕様
        if (answerNum == 0) {
            onClickCorrectAnswer();
        } else {
            onClickIncorrectAnswer();
        }
    }

    /**
     * 問題の取得を開始する<br>全問終了の場合は結果画面へ飛ぶ
     */
    private void nextQuestionOrEnd() {
        if (qNum >= QUESTION_NUM_MAX) {
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("score", score);
            startActivity(intent);
            return;
        }

        clearQuestion();
        loadQuestionAsync();
    }

    /**
     * 表示をクリアする
     */
    private void clearQuestion() {
        questionSentenceTextView.setText(null);
        iconImageView.setImageDrawable(null);
        answerButton1.setText("");
        answerButton2.setText("");
        answerButton3.setText("");
        answerButton4.setText("");
    }

    /**
     * 問題用データを取得する
     */
    private void loadQuestionAsync() {
        progressDialog = ProgressDialogFragment.newInstance(
                getString(R.string.dialog_text_loading_title),
                getString(R.string.dialog_text_loading_message));

        progressDialog.show(getFragmentManager(), "progress");

        getSampleQuestion();
//        invokeFunction();
    }

    /**
     * 問題用の画像を取得する
     */
    private void loadImageAsync() {
        getSampleIconImage();
//        downloadIconImage();
    }

    /**
     * 問題を表示する
     */
    private void showNewQuestion() {
        Log.d(LOG_TAG, "showNewQuestion");

        if (progressDialog.getDialog().isShowing()) {
            progressDialog.getDialog().dismiss();
        }

        questionNumTextView.setText(getString(R.string.quiz_text_question, ++qNum, QUESTION_NUM_MAX));
        questionSentenceTextView.setText(mQuestion.getSentence());

        rndNum = createRandomArray(ANSWER_NUM_MAX);

        answerButton1.setText(mQuestion.getQuestionItem(rndNum[0]).getLabel());
        answerButton2.setText(mQuestion.getQuestionItem(rndNum[1]).getLabel());
        answerButton3.setText(mQuestion.getQuestionItem(rndNum[2]).getLabel());
        answerButton4.setText(mQuestion.getQuestionItem(rndNum[3]).getLabel());
    }

    /**
     * 正解の選択肢をクリックした時の処理
     */
    private void onClickCorrectAnswer() {
        score++;

        int buttonTextResId = qNum >= QUESTION_NUM_MAX ? R.string.dialog_btn_end : R.string.dialog_btn_ok;

        okOnlyDialog = OkOnlyDialogFragment.newInstance(
                getString(R.string.dialog_text_correct_title),
                getString(R.string.dialog_text_correct_message),
                getString(buttonTextResId));

        okOnlyDialog.setDialogListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nextQuestionOrEnd();
            }
        });

        okOnlyDialog.show(getFragmentManager(), "alert");
    }

    /**
     * 不正解の選択肢をクリックした時の処理
     */
    private void onClickIncorrectAnswer() {
        int buttonTextResId = qNum >= QUESTION_NUM_MAX ? R.string.dialog_btn_end : R.string.dialog_btn_ok;

        okOnlyDialog = OkOnlyDialogFragment.newInstance(
                getString(R.string.dialog_text_incorrect_title),
                getString(R.string.dialog_text_incorrect_message, mQuestion.getQuestionItem(0).getLabel()),
                getString(buttonTextResId));

        okOnlyDialog.setDialogListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nextQuestionOrEnd();
            }
        });

        okOnlyDialog.show(getFragmentManager(), "alert");
    }

    /**
     * ランダムな数値配列を取得する
     * @param n 要素数
     * @return ランダムに並べ換えた配列
     */
    private int[] createRandomArray(int n) {
        int data[] = new int[n];

        Random random1 = new Random();
        Random random2 = new Random();

        for(int i = 0; i < n; i++){
            data[i] = i;
        }

        for(int i = 0; i < n * 10; i++){
            int rnd1 = random1.nextInt(n);
            int rnd2 = random2.nextInt(n);

            int tmp = data[rnd1];
            data[rnd1] = data[rnd2];
            data[rnd2] = tmp;
        }

        return data;
    }

    /**
     * (動作確認用)assetsフォルダ内にある問題サンプルから、問題用データを作成する
     */
    private void getSampleQuestion() {
        InputStream is = null;
        BufferedReader br = null;
        String text = "";

        try {
            try {
                is = this.getAssets().open("sample_question.txt");
                br = new BufferedReader(new InputStreamReader(is));

                String str;
                while ((str = br.readLine()) != null) {
                    text += str + "\n";
                }

                mQuestion = new Question(text);
                loadImageAsync();

            } finally {
                if (is != null) is.close();
                if (br != null) br.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * (動作確認用)Drawableから問題に対応するリソースを取得する
     */
    private void getSampleIconImage() {
        // DLしてる感じを出すために、1秒後に表示するようにしている
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // 拡張子を削除する
                String filename = mQuestion.getQuestionItem(0).getFilename();
                int point = filename.lastIndexOf(".");

                int iconId = getResources().getIdentifier(filename.substring(0, point), "drawable", getPackageName());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iconImage = getDrawable(iconId);
                } else {
                    iconImage = getResources().getDrawable(iconId);
                }

                iconImageView.setImageDrawable(iconImage);
                showNewQuestion();
            }
        }, 1000);
    }
}
