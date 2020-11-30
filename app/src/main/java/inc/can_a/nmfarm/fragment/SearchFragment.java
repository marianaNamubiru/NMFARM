package inc.can_a.nmfarm.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import inc.can_a.nmfarm.R;
import inc.can_a.nmfarm.app.EndPoints;
import inc.can_a.nmfarm.model.HowToDo;


public class SearchFragment extends Fragment implements AdapterView.OnItemClickListener{

    AutoCompleteTextView autoCompView;


    private RecyclerView recyclerView;

    private ArrayList resultList = new ArrayList();
    private static ArrayList<HowToDo> problemCostArrayList = new ArrayList();

    private ModelsAutocompleteAdapter mAdapter;
    //private static HowToDoAdapter pcAdapter;

    private ProgressDialog pDialog;

	private static final String TAG = "SearchFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.search_frament, container, false);

        autoCompView = view.findViewById(R.id.tv_title);
        recyclerView = view.findViewById(R.id.recycler_view);


        mAdapter = new ModelsAutocompleteAdapter(getActivity(), R.layout.how_to_do_search_list_item,resultList);
        //pcAdapter = new HowToDoAdapter(getActivity(),problemCostArrayList);
        autoCompView.setAdapter(mAdapter);
        autoCompView.setThreshold(1);
        autoCompView.setOnItemClickListener(this);

        //LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        ////recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        //recyclerView.setAdapter(pcAdapter);

		return view;
	}

//	//very important utility for going back without hustling to get intents
//	@Override
//	public boolean onSupportNavigateUp() {
//		onBackPressed();
//		return true;
//	}


	public static ArrayList autocomplete(String input) {
		ArrayList resultList = null;
		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(EndPoints.BASE_URL+"/nm_farm/v1/get_all_search_results/");
			//sb.append("?key=" + API_KEY);
			//For now am making uganda the default
			//if(!countryCode.isEmpty()){
			//    //Log.e("coutnyr coded not empty" ,"code is "+countryCode);
			//    sb.append("&components=country:").append(countryCode);
			//}
			//sb.append("&components=country:ug");
			//sb.append("?phrase=").append(URLEncoder.encode(input, "utf8"));
			sb.append(URLEncoder.encode(input, "utf8"));
			Log.e("URL is" ,"URL is "+sb);
			URL url = new URL(sb.toString());

			//URL url = new URL("http://192.168.1.234:8000/api/mlist/");
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e("LOG_TAG", "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e("LOG_TAG", "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			//JSONArray jsonObj = new JSONArray(jsonResults.toString());
			Log.e("LOG_TAG", "Json object is " +jsonObj);
			JSONArray predsJsonArray = jsonObj.getJSONArray("results");
			//JSONArray predsJsonArray = jsonObj;
			Log.e("LOG_TAG", "Json array is " +predsJsonArray);

			// Extract the Place descriptions from the results
			resultList = new ArrayList(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				//System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
				//System.out.println("============================================================");

				//String description = predsJsonArray.getJSONObject(i).getString("description");
				//String lat = predsJsonArray.getJSONObject(i).getString("lat");
				//String lng = predsJsonArray.getJSONObject(i).getString("lng");
				//String place_name = predsJsonArray.getJSONObject(i).getString("place_name");
				//String vicinity = predsJsonArray.getJSONObject(i).getString("vicinity");
				//AutoCompleteObject aco = new AutoCompleteObject(lat,lng,place_name,vicinity);

				resultList.add(predsJsonArray.getJSONObject(i).getString("title"));

			}
		} catch (JSONException e) {
			Log.e("LOG_TAG", "Cannot process JSON results", e);
		}

		Log.e("LOG_TAG", "Final results list " +resultList);
		return resultList;
	}




	public void onItemClick(AdapterView adapterView, View view, int position, long id) {
		//AutoCompleteObject aco = adapterView.getItemAtPosition(position);
		String device_model = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(getActivity(), device_model, Toast.LENGTH_SHORT).show();
		//autoCompView.setVisibility(View.GONE);
		//String title = str;
		//Todo now fetch the problem using the model name from the auto search
		///fetchProblemCost(str);
        /*Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("address",str);
        intent.putExtra("latitude","");
        intent.putExtra("longitude","");
        startActivity(intent);*/

		//ProblemCostFragment.newInstance(str);

//        ProblemCostFragment fragment = new ProblemCostFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_DEVICE_MODEL, device_model);
//        fragment.setArguments(args);
//        //return fragment;
//
//        FragmentTransaction trans = getFragmentManager().beginTransaction();
//        trans.replace(R.id.root_frame,fragment);
//        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        trans.addToBackStack(null);
//        trans.commit();
	}

	class ModelsAutocompleteAdapter extends ArrayAdapter implements Filterable {
		private ArrayList resultList;

		private ModelsAutocompleteAdapter(Context context, int textViewResourceId, ArrayList onlineResults) {
			super(context, textViewResourceId,onlineResults);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			//return resultList.get(index);
			return resultList.get(index).toString();
		}

		@Override
		public Filter getFilter() {

			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocmplete results.
						resultList = autocomplete(constraint.toString());
						//resultList = fetchData();
						//resultList = onlineResults;
						// Assign the data to the FilterResults
						filterResults.count = resultList.size();
						filterResults.values = resultList;

					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_how_to_do_search, menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//noinspection SimplifiableIfStatement
		if (id ==R.id.search) {
			autoCompView.setVisibility(View.VISIBLE);

		}else if (id == android.R.id.home) {
			getActivity().onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}


}
