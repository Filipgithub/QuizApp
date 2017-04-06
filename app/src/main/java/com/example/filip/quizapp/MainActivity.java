package com.example.filip.quizapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.password;

public class MainActivity extends AppCompatActivity {

    public static String name;
    public static Integer score;
    public static Integer numOfQuestions;
    private TextView scoreList;
    public static ArrayList<String> scoresForBoard;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog1;
    public static ArrayList<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreList = (TextView) findViewById(R.id.scoreList);
        //intent for start to play quiz
        final Intent intent = new Intent(this, Main2Activity.class);
        //static number of questions
        numOfQuestions = 10;
        //start score for each player
        score = 0;
        //here we keep scores for offline playing
        scoresForBoard = new ArrayList<>();

        //button for starting quiz
        Button startQuiz = (Button) findViewById(R.id.startQuiz);

        //init for questions array
        questions = new ArrayList<>();


        //getting scores form server
        setScoreBoard();
        //getting questions
        getJSONQuestions();


        startQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alert dialog for player to enter the name
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                View view = inflater.inflate(R.layout.dialog_custom, null);

                final EditText editText = (EditText) view.findViewById(R.id.editText);
                builder.setTitle("Enter a name: ")
                        // Specify the list array, the items to be selected by default (null for none),
                        // and the listener through which to receive callbacks when items are selected
                        .setView(view)
                        // Set the action buttons
                        .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                name = editText.getText().toString();
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateScore();
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void updateScore() {
        //commented lines are for my own questions

      /*  if( !scoreList.getText().equals("") || numOfQuestions == 0)
        {  */
        for (int i = 0; i < scoresForBoard.size(); i++)
            scoreList.append("\n" + scoresForBoard.get(i));
     /*   }else {
            scoreList.setHint("Empty list");
        }*/
        numOfQuestions = 10;
        score = 0;
    }

    public void setScoreBoard() {

        AsyncHttpClient client = new AsyncHttpClient();

        //getting scores form sever
        client.get("http://zoran.ogosense.net/api/get-leaderboard", new JsonHttpResponseHandler() {


            @Override
            public void onStart() {
                // called before request is started
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

//                        System.out.println(response);

                    // Check for success of login

                    // Store data in SharedPreferences
                    JSONArray namesAndScores = response.getJSONArray("data");

                    for (int i = 0; i < namesAndScores.length(); i++) {
                        JSONObject q = namesAndScores.getJSONObject(i);
                        String name = q.getString("name");
                        String score = q.getString("score");
                        scoresForBoard.add(name + " " + score);
                    }

                    //updating score board
                    updateScore();
                    //getJSONQuestions();


                } catch (Exception e) {
                    System.out.print(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(MainActivity.this, "Please check your internet connection..", Toast.LENGTH_SHORT).show();
                System.out.println(errorResponse);
                progressDialog.dismiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();


            }
        });

    }

    public void getJSONQuestions() {
        AsyncHttpClient client = new AsyncHttpClient();

        //getting questions
        client.get("http://zoran.ogosense.net/api/get-questions", new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
                progressDialog1 = new ProgressDialog(MainActivity.this);
                progressDialog1.setMessage("Getting questions...");
                progressDialog1.show();
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    JSONArray questionS = response.getJSONArray("data");


                    //parsing questions for my onw purposes,to be in format
                    // question@answer1,answer2,answer3,answer3,answer4,correct_answer
                    for (int i = 0; i < questionS.length(); i++) {
                        JSONObject q = questionS.getJSONObject(i);
                        String question = q.getString("question");
                        String answer1 = q.getString("answer1");
                        String answer2 = q.getString("answer2");
                        String answer3 = q.getString("answer3");
                        String answer4 = q.getString("answer4");
                        String correctAnswer = q.getString("correct_answer");
                        questions.add(new String(question + "@" + answer1 + "," + answer2 + "," + answer3 + "," + answer4 + "," + correctAnswer));
                        System.out.println(questions.get(i));

                    }


                } catch (Exception e) {
                    System.out.print(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(MainActivity.this, "Please check your internet connection..", Toast.LENGTH_SHORT).show();
                System.out.println(errorResponse);
                progressDialog1.dismiss();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog1.dismiss();


            }
        });

    }


}
