package product.enrichai.com.enrichaitracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static product.enrichai.com.enrichaitracking.PositionProvider.KEY_TRIP_COUNT;

public class StatusActivity extends AppCompatActivity {

    private static final int LIMIT = 20;
    private static final String TAG = StatusActivity.class.getSimpleName();

    private static final LinkedList<String> messages = new LinkedList<>();
    private static final Set<ArrayAdapter<String>> adapters = new HashSet<>();
    public static ArrayList<Position> display_list;
    private RecyclerView gvContentView;
    private StatusAdapter statusAdapter;
    private TripAdapter tripAdapter;

    public ArrayList<Position> getList(){
        return display_list;
    }

    private static void notifyAdapters() {
        for (ArrayAdapter<String> adapter : adapters) {
            adapter.notifyDataSetChanged();
        }
    }

    public static void addMessage(String message) {
        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
        message = format.format(new Date()) + " - " + message;
        messages.add(message);
        while (messages.size() > LIMIT) {
            messages.removeFirst();
        }
        notifyAdapters();
    }

    public void clearMessages() {
        display_list.clear();
        statusAdapter.notifyDataSetChanged();
    }

    private void initialize(){

    }

    public void segregateData(){

    }

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_trips);

        //initialize();
        LinearLayoutManager mLayoutManager;
        gvContentView = findViewById(R.id.rv_list);
        mLayoutManager = new LinearLayoutManager(this);
        gvContentView.setLayoutManager(mLayoutManager);
        SharedPreferences sharedPreferences = getSharedPreferences("product.enrichai.com.enrichaitracking", Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt(KEY_TRIP_COUNT, 0);
        tripAdapter = new TripAdapter(count, this);
        gvContentView.setAdapter(tripAdapter);
    }

    @Override
    protected void onDestroy() {
        adapters.remove(adapter);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.status, menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch(item.getItemId()){
            case R.id.clear:
                clearMessages();
                return true;
            case R.id.map:
                if(!display_list.isEmpty()) {
                    Intent intent = new Intent(this, DrawGeoJsonLineActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(this, "Data empty! PLease re-load again ", Toast.LENGTH_LONG).show();
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

}
