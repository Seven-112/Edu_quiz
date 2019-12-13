package com.brightfuture.eduquiz.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.brightfuture.eduquiz.Constant;
import com.brightfuture.eduquiz.R;
import com.brightfuture.eduquiz.activity.CategoryActivity;
import com.brightfuture.eduquiz.activity.CommunityQuestionActivity;
import com.brightfuture.eduquiz.activity.LearnMcqListActivity;
import com.brightfuture.eduquiz.adapter.CommunityQuestionAdapter;
import com.brightfuture.eduquiz.helper.AppController;
import com.brightfuture.eduquiz.helper.Session;
import com.brightfuture.eduquiz.helper.Utils;
import com.brightfuture.eduquiz.model.CommunityQuestion;
import com.brightfuture.eduquiz.model.SubCategory;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.brightfuture.eduquiz.Constant.ISPREMIUM;


public class AllQuestionsFragment extends Fragment {
    //
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    View view;
    TextView tv;
    private RecyclerView recyclerView;
    public ProgressBar progressBar;
    public ArrayList<CommunityQuestion> questionList;
    public AdView mAdView;
    public TextView txtBlankList;
    public RelativeLayout layout;
    public static Context context;
    public static String pos;
    public Snackbar snackbar;

    public SwipeRefreshLayout swipeRefreshLayout;

    private OnFragmentInteractionListener mListener;

    public AllQuestionsFragment() {
        // Required empty public constructor
    }


    public static AllQuestionsFragment newInstance(String position, Context activity) {
        AllQuestionsFragment fragment = new AllQuestionsFragment();
        Bundle args = new Bundle();
        pos=position;
        args.putString(ARG_PARAM1, position);
        context=activity;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_all_questions, container, false);

        mAdView = view.findViewById(R.id.banner_AdView);
        boolean isPremium= Session.getBooleanData(ISPREMIUM, context);
        mAdView = view.findViewById(R.id.banner_AdView);
        if(isPremium)
        {

        }
        else {
            mAdView.loadAd(new AdRequest.Builder().build());
        }


        txtBlankList = view.findViewById(R.id.txtblanklist);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        txtBlankList.setText(getString(R.string.no_category));

        questionList = new ArrayList<>();
        getData();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                questionList.clear();
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });




        return view;

    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        if (Utils.isNetworkAvailable((Activity) context)) {
            getCommunityQuestionsFromJson();

        } else {
            setSnackBar();
            progressBar.setVisibility(View.GONE);
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void setSnackBar() {
     /*   snackbar = Snackbar
                .make(context.findViewById(android.R.id.content), getString(R.string.msg_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getData();
                    }
                });

        snackbar.show();*/
    }


    public void getCommunityQuestionsFromJson() {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean error = jsonObject.getBoolean(Constant.ERROR);

                            if (!error) {
                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    CommunityQuestion cq = new CommunityQuestion();
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    cq.setCq_id(object.getString(Constant.QUESTIONID));
                                    cq.setCq_question(object.getString(Constant.CQQUESTION));
                                    cq.setCq_location(object.getString(Constant.CQLOCATION));
                                    cq.setCq_created(object.getString(Constant.CQCREATED));
                                    cq.setU_name(object.getString(Constant.CQUSERNAME));
                                    cq.setU_id(object.getString(Constant.CQUID));


                                        questionList.add(cq);

                                }
                                if (questionList.size() == 0) {
                                    txtBlankList.setVisibility(View.VISIBLE);
                                    txtBlankList.setText(getString(R.string.no_sub_category));
                                }
                                CommunityQuestionAdapter adapter = new CommunityQuestionAdapter(questionList,context);
                                recyclerView.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.setVisibility(View.GONE);
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);

                    params.put(Constant.GET_ALL_COMMUNITY_QUESTIONS, "1");


                System.out.println("params  " + params.toString());
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
