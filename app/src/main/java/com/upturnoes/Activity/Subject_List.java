package com.upturnoes.Activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.upturnoes.Adapter.Subject_Adapter;
import com.upturnoes.Class.MyConfig;

import com.upturnoes.Model.Subject_Model_List;
import com.upturnoes.R;
import com.upturnoes.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Subject_List extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Subject_Adapter mAdapter;
    List<Subject_Model_List> rowListItem;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    AutoCompleteTextView edit_search;
    LinearLayout empty_view;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject__list);

        initToolbar();
        showDialog();

        empty_view  = (LinearLayout) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        edit_search=(AutoCompleteTextView)findViewById( R.id.et_search);
        rowListItem=new ArrayList<Subject_Model_List>();
        get_SUbjects();


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Subjects");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void showDialog() {
        progressDialog = new ProgressDialog(Subject_List.this);
        progressDialog.setTitle("Subjects");
        progressDialog.setMessage("Please wait while showing Data ...  ");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        ProgressBar progressbar=(ProgressBar)progressDialog.findViewById(android.R.id.progress);
        progressbar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#FF7043"), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    public void get_SUbjects(){
        StringRequest stringRequest = new StringRequest( Request.Method.POST, MyConfig.URL_GET_SUBJECTS,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray(JSON_ARRAY);
                            getCategory(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                sharedPreferences=getApplicationContext().getSharedPreferences("Mydata",MODE_PRIVATE);
                sharedPreferences.edit();
                String student_class= sharedPreferences.getString("student_class",null);

                Log.e("student_class",student_class);

                params.put("student_class",student_class);

                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(Subject_List.this);
        queue.add(stringRequest);
    }
    private void getCategory(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                rowListItem.add(new Subject_Model_List(json.getString("sub_id"),
                        json.getString("sub_name"),
                        json.getString("dept_name"),
                        json.getString("class_name"),
                        json.getString("reg_date")

                ) );

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (rowListItem.size() > 0){
            progressDialog.dismiss();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1, LinearLayoutManager.VERTICAL,false);
            mRecyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
            //rowListItem = get_ALL_CATEGORY();
            mAdapter =new Subject_Adapter(Subject_List.this,rowListItem);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

        }else{
            progressDialog.dismiss();
            empty_view.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }


        // Capture Text in EditText
        edit_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = edit_search.getText().toString().toLowerCase( Locale.getDefault());
                mAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });


    }


}
