package com.example.sadokmm.student.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.CacheDispatcher;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.ImageRequest;
import com.android.volley.request.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.androidquery.util.Progress;
import com.example.sadokmm.student.Adapters.PostAdapter;
import com.example.sadokmm.student.Objects.Post;
import com.example.sadokmm.student.R;
import com.example.sadokmm.student.Services.ServiceCommentNotifcation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.sadokmm.student.Activities.firstActivity.getResizedBitmap;
import static com.example.sadokmm.student.Activities.firstActivity.publicUrl;

public class PostFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static Boolean IS_ACTIVE = false ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView postRv;
    public static PostAdapter postAdapter;
    public static ArrayList<Post> ll;
    ProgressDialog prgDialog;
    ProgressBar progressBarPost;
    ServiceCommentNotifcation mServicee;

    private RequestQueue requestQueuePost;


    public PostFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipePost);
        swipeRefreshLayout.setOnRefreshListener(this);
        postRv = (RecyclerView) view.findViewById(R.id.postRV);
        postAdapter = new PostAdapter(getActivity());
        requestQueuePost = Volley.newRequestQueue(getActivity());
        progressBarPost = (ProgressBar)view.findViewById(R.id.progressBarPost);
        IS_ACTIVE=true;

        ll = new ArrayList<>();
        postAdapter.setMyList(ll);
        if (isNetworkAvailable())
            charger(0, 0);
        else
            Toast.makeText(getActivity(),"Pas de connexion internet",Toast.LENGTH_LONG).show();
        //postAdapter.setMyList(ll);
        postRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRv.setAdapter(postAdapter);




        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    public void charger(int deb, int fin) {


        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        String url = publicUrl + "student/postg/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (ll.size() != 0 ) ll.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject postJson = response.getJSONObject(i);
                                String id , txtpost, datepost, idusr;
                                id = postJson.getString("_id");
                                txtpost = postJson.getString("txtpost");
                                datepost = postJson.getString("datepost");
                                idusr = postJson.getString("idusr");
                                JSONArray imgpost = postJson.getJSONArray("imgpost");
                                ArrayList<String> imgListPost = new ArrayList<>();
                                    for (int j=0 ; j<imgpost.length() ; j++) {
                                        imgListPost.add(imgpost.getString(j));
                                    }

                                Post p = new Post(txtpost, idusr, imgListPost , id );
                                p.setDatepost(datepost);
                                ll.add(0,p);



                            }
                            if(postAdapter.getMyListPost().size()!=0) postAdapter.getMyListPost().clear();
                            postAdapter.setMyList(ll);
                            postAdapter.notifyDataSetChanged();
                            progressBarPost.setVisibility(View.INVISIBLE);

                            //Toast.makeText(getContext(),,Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                           Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                /*Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();*/
            }
        });

        /*if (isNetworkAvailable())
            requestQueuePost.getCache().clear();*/

        requestQueuePost.add(jsonArrayRequest);



    }


    //download img using volley
    public void getImageByUrl(String url, final int position) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        final Bitmap[] im = new Bitmap[1];
        im[0] = null;

        prgDialog = new ProgressDialog(getContext());
        prgDialog.setMessage("Téléchargementt en cours ...");
        prgDialog.setIndeterminate(false);
        //prgDialog.setMax(100);
        prgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prgDialog.setCancelable(true);
        prgDialog.show();

        // Initialize a new ImageRequest
        ImageRequest imageRequest = new ImageRequest(url, getContext().getResources(), getContext().getContentResolver(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                im[0] = response;
                prgDialog.dismiss();
                //postAdapter.getMyListPost().get(position).setImgpost(response);
                postAdapter.notifyDataSetChanged();

            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        });


        // Add ImageRequest to the RequestQueue
        //requestQueue.getCache().clear();
        requestQueue.add(imageRequest);


    }

    @Override
    public void onRefresh() {

        if (isNetworkAvailable()) {
            charger(0,0);
            swipeRefreshLayout.setRefreshing(false);
        }

        else {
            Toast.makeText(getActivity(),"Pas de connexion internet",Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }


    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
