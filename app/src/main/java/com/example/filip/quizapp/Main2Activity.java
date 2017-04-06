package com.example.filip.quizapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Main2Activity extends AppCompatActivity {

    // ArrayList<String> questions;
    String randomQuestion;
    String splitedQuestion;
    String answer;
    Random rndQuestion;
    TextView questionField;
    TextView answerOne;
    TextView answerTwo;
    TextView answerThree;
    TextView answerFour;
    SharedPreferences nameAndScores;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nameAndScores = getSharedPreferences("scores", MODE_PRIVATE);
        SharedPreferences.Editor edit = nameAndScores.edit();
        edit.putString("nAndS", MainActivity.name + " " + MainActivity.score);
        edit.apply();
        // questions = new ArrayList<>();
        randomQuestion = "";
        rndQuestion = new Random();
        questionField = (TextView) findViewById(R.id.questionField);
        answerOne = (TextView) findViewById(R.id.answerOne);
        answerTwo = (TextView) findViewById(R.id.answerTwo);
        answerThree = (TextView) findViewById(R.id.answerThree);
        answerFour = (TextView) findViewById(R.id.answerFour);
        final Intent intent = new Intent(this, Main2Activity.class);


        //geting random 10 questions while number of questions is >0
        //when numOfQuestions is 0 game is over
        //numOfQuestions,score(static fields in MainActivity)
        if (MainActivity.numOfQuestions > 0) {
            answer = "";
            try {
                setRandomQuestion();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainActivity.numOfQuestions--;
        } else {
            Toast.makeText(this, "Player:" + MainActivity.name + " got " + MainActivity.score + " points", Toast.LENGTH_SHORT).show();
            //posting results to server
            putScoresToServer();
            finish();
        }


        //on click if answer is green than it is correct else it is wrong
        answerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerOne.getText().toString().equals(answer)) {
                    answerOne.setBackgroundColor(GREEN);
                    MainActivity.score++;
                    startActivity(intent);
                    finish();
                } else {
                    answerOne.setBackgroundColor(RED);
                    startActivity(intent);
                    finish();
                }
            }
        });
        answerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerTwo.getText().toString().equals(answer)) {
                    answerTwo.setBackgroundColor(GREEN);
                    MainActivity.score++;
                    startActivity(intent);
                    finish();
                } else {
                    answerTwo.setBackgroundColor(RED);
                    startActivity(intent);
                    finish();
                }
            }
        });
        answerThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerThree.getText().toString().equals(answer)) {
                    answerThree.setBackgroundColor(GREEN);
                    MainActivity.score++;
                    startActivity(intent);
                    finish();
                } else {
                    answerThree.setBackgroundColor(RED);
                    startActivity(intent);
                    finish();
                }
            }
        });

        answerFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerFour.getText().toString().equals(answer)) {
                    answerFour.setBackgroundColor(GREEN);
                    MainActivity.score++;
                    startActivity(intent);
                    finish();
                } else {
                    answerFour.setBackgroundColor(RED);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


    //getting questions from txt file
  /*  public ArrayList<String> getQuestions() throws IOException {

        ArrayList<String> questions = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("questions.txt")));

            String question;
            // System.out.println(reader.readLine());
            while ((question = reader.readLine()) != null) {
                questions.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    } */


  //seting random questions from questions array(MainActivity)
    public void setRandomQuestion() throws InterruptedException {
        int i = rndQuestion.nextInt(10);
        randomQuestion = MainActivity.questions.get(i);
        //parsing questions and starting quiz
        //Which German Count invented the zeppelin?@,Count(Von),Zeppelin Josephine,John Adams,MIco Dukic,1
        questionField.setText(randomQuestion.split("@")[0]);
        splitedQuestion = randomQuestion.split("@")[1];
        String firstAnswer = splitedQuestion.split(",")[0];
        String secondAnswer = splitedQuestion.split(",")[1];
        String thirdAnswer = splitedQuestion.split(",")[2];
        String fourthAnswer = splitedQuestion.split(",")[3];
        String numOfAnswer = splitedQuestion.split(",")[4];
        if (numOfAnswer.equals("1"))
            answer = firstAnswer;
        else if (numOfAnswer.equals("2"))
            answer = secondAnswer;
        else if (numOfAnswer.equals("3"))
            answer = thirdAnswer;
        else
            answer = fourthAnswer;

        answerOne.setText(firstAnswer);
        answerTwo.setText(secondAnswer);
        answerThree.setText(thirdAnswer);
        answerFour.setText(fourthAnswer);


    }

    public void putScoresToServer(){

        //posting results to server
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", MainActivity.name);
        params.put("score", MainActivity.score);

        //sending name of player and score to server
        client.post("http://zoran.ogosense.net/api/set-score", params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        System.out.println("Error");
                        Toast.makeText(Main2Activity.this, "Updating score faild!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        System.out.println("Seccessfully updated scores on server");
                        Toast.makeText(Main2Activity.this, "Updated scores to server successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


}
