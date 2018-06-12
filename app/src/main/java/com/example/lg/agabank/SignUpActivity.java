package com.example.lg.agabank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEditTextName;
    private EditText mEditTextId;
    private EditText mEditTextPass;
    private EditText mEditTextPhone;
    private TextView mTextViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEditTextName = (EditText)findViewById(R.id.name);
        mEditTextId = (EditText)findViewById(R.id.id);
        mEditTextPass = (EditText)findViewById(R.id.pass);
        mEditTextPhone = (EditText)findViewById(R.id.phone);
        mTextViewResult=(TextView)findViewById(R.id.result);

        Button buttonInsert = (Button)findViewById(R.id.signup);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mEditTextName.getText().toString();
                String pass = mEditTextPass.getText().toString();
                String id = mEditTextId.getText().toString();
                String phone = mEditTextPhone.getText().toString();

                //Namego =name;

                InsertData task = new InsertData();
                task.execute(name, id, pass, phone);


                mEditTextName.setText("");
                mEditTextPass.setText("");

            }
        });
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignUpActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
             mTextViewResult.setText(result);//php에서 echo 해주는 내용 출력해준다
            if (result.equals("ok")) {
                Log.d("od", "dddd");
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                //intent.putExtra("id", Namego);
                startActivity(intent);
                finish();
            }
            //Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String name = (String) params[0];
            String id = (String) params[1];
            String pass = (String) params[2];
            String phone = (String) params[3];

            String serverURL = "http://wwhurin0834.dothome.co.kr/insertUser.php";
            String postParameters = "name=" + name + "&pass=" + pass+"&id="+id+"&phone="+phone;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                //Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                //Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
