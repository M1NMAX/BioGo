package dev.biogo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dev.biogo.Adapters.ApiSpeciesAdapter;
import dev.biogo.Models.ApiSpecie;

public class ApiSpecieSearchActivity extends AppCompatActivity implements ApiSpeciesAdapter.OnItemListener {
    private static final String TAG = "ApiSpecieSearchActivity";
    private ArrayList<ApiSpecie> apiSpeciesList;
    private ApiSpeciesAdapter apiSpeciesListAdapter;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_specie_search);
        intent = getIntent();

        EditText specieInput = findViewById(R.id.apiSearch_string);

        RecyclerView apiSearchRView = findViewById(R.id.apiSearchRecyclerView);
        apiSearchRView.setHasFixedSize(true);
        apiSearchRView.setLayoutManager(new LinearLayoutManager(this));

        apiSpeciesList = new ArrayList<>();

        apiSpeciesListAdapter = new ApiSpeciesAdapter(this, apiSpeciesList, R.layout.api_specie_list_item,this);
        apiSearchRView.setAdapter(apiSpeciesListAdapter);





        //Search button
        Button searchButton = (Button) findViewById(R.id.apiSearch_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiSpeciesList.clear();
                String input = specieInput.getText().toString();

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                //String url ="https://www.inaturalist.org/observations.json?view=species&per_page=5&per_page=3&taxon_name=" + input;
                String url ="https://www.inaturalist.org/observations.json?view=species&per_page=5&quality_grade=research&per_page=3&taxon_name=" + input;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonResponseArray = new JSONArray (response);
                                    //Log.d("apierror", "onResponse: " + String.valueOf(jsonResponseArray.length()));
                                    if(!(jsonResponseArray.length()==0)) {
                                        int i = 0;
                                        while (i < jsonResponseArray.length()) {
                                            try {
                                                JSONObject specie = jsonResponseArray
                                                        .getJSONObject(i);

                                                Object obj = specie.getJSONObject("taxon").getJSONObject("common_name").get("name");
                                                Object objPhotos = specie.get("photos");
                                                JSONArray array= specie.getJSONArray("photos");

                                                //Get api specieName and id
                                                String specieName = obj.toString();
                                                String id = specie.getJSONObject("taxon").get("id").toString();

                                                //Get api photo
                                                JSONObject secondArray = (JSONObject) array.get(0);
                                                Object medium_photo = secondArray.get("medium_url");
                                                String imageUri = medium_photo.toString();

                                                //Create ApiSpecie
                                                ApiSpecie apiSpecie = new ApiSpecie(id, specieName, imageUri, "1");

                                                if (listCheck(apiSpecie.getSpecieName(), apiSpeciesList))
                                                    apiSpeciesList.add(apiSpecie);
                                                //Log.d("apierror", "List_Added" + input);
                                                i++;

                                            } catch (JSONException e) {
                                                i++;
                                                Log.d("apierror", "API_Error");
                                            }
                                        }
                                        ;

                                        apiSpeciesListAdapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    Log.d("apierror", "onResponse: JSON ERROR");

                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("apierror", "onErrorResponse: API Error");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
    }


    @Override
    public void OnItemClick(int position) {
        ApiSpecie specie = apiSpeciesList.get(position);
        Intent newIntent = new Intent();
        newIntent.putExtra("apiSpecie", specie);
        setResult(RESULT_OK, newIntent);
        finish();
    }

    public boolean listCheck(String specieName, ArrayList<ApiSpecie> specieList){
        for(int i = 0; i < specieList.size(); i++){
            if(specieName.equals(specieList.get(i).getSpecieName())){
                return false;
            }
        }
        return true;
    }
}