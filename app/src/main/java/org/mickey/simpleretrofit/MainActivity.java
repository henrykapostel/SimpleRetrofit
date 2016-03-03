package org.mickey.simpleretrofit;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.POST;

public class MainActivity extends AppCompatActivity {

    public interface GitHubService {
        @POST("/api/create")
        Call<TokenResponse> GetData(@Body Phone phone);
    }

    public class Phone{
        private String phone;
        private String internationalCode;
        public Phone(String _phone, String _code){
            phone = _phone;
            internationalCode = _code;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onButton(View vw) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        String txtPhone = ((EditText) findViewById(R.id.txtPhone)).getText().toString();
        if (txtPhone.length() > 10 || txtPhone.length() < 1) {
            Toast.makeText(this, "Make sure Phone Number length:1~10", Toast.LENGTH_LONG).show();
            return;
        }
        String txtCode = ((EditText) findViewById(R.id.txtCode)).getText().toString();
        if (txtCode.length() != 3) {
            Toast.makeText(this, "Make sure Code length:3", Toast.LENGTH_LONG).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://bebetrack.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubService service = retrofit.create(GitHubService.class);
        Call<TokenResponse> call = service.GetData(new Phone(txtPhone, txtCode));
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Response<TokenResponse> response, Retrofit retrofit) {
                System.out.println("Response status code: " + response.code());

                // isSuccess is true if response code => 200 and <= 300
                if (!response.isSuccess()) {
                    // print response body if unsuccessful
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        // do nothing
                    }
                    return;
                }
                if (response.body() == null) return;
                ((TextView)findViewById(R.id.txtResult)).setText(response.body().pin);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}