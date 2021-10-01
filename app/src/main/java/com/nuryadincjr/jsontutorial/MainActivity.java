package com.nuryadincjr.jsontutorial;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.jsontutorial.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ProgressDialog dialog;
    private ArrayList<HashMap<String, String>> contactList;
    private final String TAG = MainActivity.class.getSimpleName();
    private final String url = "https://app.fakejson.com/q/QlRaybY9?token=EwuNZ5kIloYxNoVml1K86g";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        contactList = new ArrayList<>();
        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject object = new JSONObject(jsonStr);
                    JSONArray contacts = object.getJSONArray("contacts");

                    for (int i = 0; i < contacts.length(); i++) {
                        String id, name, email, address, gender, mobile, home, office;

                        JSONObject object1 = contacts.getJSONObject(i);
                        id = object1.getString("id");
                        name = object1.getString("name");
                        email = object1.getString("email");
                        address = object1.getString("address");
                        gender = object1.getString("gender");

                        JSONObject phone = object1.getJSONObject("phone");
                        mobile = phone.getString("mobile");
                        home = phone.getString("home");
                        office = phone.getString("office");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Json parsing error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (dialog.isShowing())
                dialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,
                    contactList, R.layout.list_item,
                    new String[]{"name", "email", "mobile"},
                    new int[]{R.id.tvName, R.id.tvEmail, R.id.tvMobile});

            binding.rvList.setAdapter(adapter);
        }

    }
}