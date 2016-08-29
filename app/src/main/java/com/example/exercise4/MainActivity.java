package com.example.exercise4;

/**
 * Activity to display jokes based on their length
 * Button to show one joke and another to show 3 jokes
 * Length can be chosen using a spinner item
 *
 * @auther anon
 * @version 1.0
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView two_joke_textview;
    private TextView one_joke_textview;
    private TextView three_joke_textview;
    private String[] jokes = new String[3];
    private Spinner joke_spinner;
    private URL jokeURL;

    /**
     * Method is called on the start of the activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button one_joke_button = (Button) findViewById(R.id.one_joke);
        one_joke_button.setOnClickListener(this);

        Button three_joke_button = (Button) findViewById(R.id.three_joke);
        three_joke_button.setOnClickListener(this);

        one_joke_textview = (TextView) findViewById(R.id.joke1_textview);
        two_joke_textview = (TextView) findViewById(R.id.joke2_textview);
        three_joke_textview = (TextView) findViewById(R.id.joke3_textview);

        joke_spinner = (Spinner) findViewById(R.id.joke_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        joke_spinner.setAdapter(adapter);

        /**
         * Select specific url based on spinner item of joke length selected
         */
        joke_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, joke_spinner.getSelectedItem().toString() + " jokes selected", Toast.LENGTH_SHORT).show();
                try {
                    jokeURL = new URL("http://www-staff.it.uts.edu.au/~rheise/sarcastic.cgi?len=" + joke_spinner.getSelectedItem().toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Method handles button clicks
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        int numJokes = 3;
        switch (id) {
            case R.id.one_joke:
                new Download1JokeAsyncTask().execute();
                break;
            case R.id.three_joke:
                new DownloadNJokesAsyncTask().execute();
                break;
        }
    }

    /**
     * Async task to connect to URL, download and set joke
     */
    private class Download1JokeAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Downloading joke...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Open a connection to the web service
                URL url = jokeURL;
                URLConnection conn = url.openConnection();

                // Obtain input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // The joke is a one liner, so just read the one line
                String joke = in.readLine();

                // Close the connection
                in.close();

                return joke;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to download joke";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            two_joke_textview.setText(s);
            one_joke_textview.setText(null);
            three_joke_textview.setText(null);
        }
    }

    /**
     * Async task to connect to URL, download and set joke
     */
    private class DownloadNJokesAsyncTask extends AsyncTask<Void, Integer, String[]> {
        private ProgressDialog dialog;
        int jokeNumber = 1;
        int jokeProgress = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(3);
            dialog.setMessage("Downloading joke " + jokeNumber + "...");
            dialog.show();
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            try {

                for (int i = 0; i < 3; i++) {
                    // Open a connection to the web service
                    URL url = jokeURL;
                    URLConnection conn = url.openConnection();

                    // Obtain input stream
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    // The joke is a one liner, so just read the one line
                    jokes[i] = in.readLine();

                    // Close the connection
                    in.close();
                    if (jokeNumber < 3 && jokeProgress < 2) {
                        publishProgress(jokeNumber++);
                        publishProgress(jokeProgress++);
                    }
                }

                return jokes;
            } catch (Exception e) {
                e.printStackTrace();
                String error[] = {"Failed to download jokes"};
                return error;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setMessage("Downloading joke " + jokeNumber + "...");
            dialog.setProgress(jokeProgress);
        }
//        sup
        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            dialog.dismiss();

            try {
                String joke1 = strings[0];
                String joke2 = strings[1];
                String joke3 = strings[2];

                one_joke_textview.setText(joke1);
                two_joke_textview.setText(joke2);
                three_joke_textview.setText(joke3);
            } catch (Exception e) {
                e.printStackTrace();
                two_joke_textview.setText(strings[0]);
                one_joke_textview.setText(null);
                three_joke_textview.setText(null);
            }
        }
    }
}
