package product.enrichai.com.enrichaitracking;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static product.enrichai.com.enrichaitracking.BuildConfig.VERSION_CODE;

/**
 * Created by WELCOME on 6/18/2018.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private ArrayList<Position> positionArrayList = new ArrayList<>();
    private final String TAG = TripAdapter.class.getSimpleName();
    private ArrayList<Position> temporary = new ArrayList<>();
    private int tripCount;
    private Context context;

    private void initialize(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.selectAsync(new DatabaseHelper.DatabaseHandler<ArrayList<Position>>() {
            @Override
            public void onComplete(boolean success, ArrayList<Position> result) {
                try {
                    Log.d(TAG, "Success : " + success);
                    if (success) {
                        if (result != null)
                            positionArrayList = result;
                        else{
                            Log.d(TAG, "List is empty!");
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    Log.d(TAG,"Error Message :"+e.getMessage());
                }
            }
        });
    }

    public TripAdapter(int tripCount, Context context){
        this.tripCount = tripCount;
        this.context = context;
        initialize();
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_trip_details, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripViewHolder holder, int position) {
        holder.bindValues(position);
        holder.tv_timeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temporary.clear();
                int trip = holder.getAdapterPosition() + 1;
                Log.d(TAG, positionArrayList.toString());
                for(Position row: positionArrayList)
                    if(row.getTripId() == trip)
                        temporary.add(row);
                Log.d("Position", temporary.toString());
                if(!temporary.isEmpty()) {
                    Intent intent = new Intent(context, DrawGeoJsonLineActivity.class);
                    intent.putExtra("PositionListExtra", temporary);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else{
                    Toast.makeText(context, "Data empty! PLease re-load again ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripCount;
    }

    public class TripViewHolder extends RecyclerView.ViewHolder{
        TextView tv_tripId;
        TextView tv_timeDate;

        public TripViewHolder(View itemView) {
            super(itemView);
            tv_tripId = itemView.findViewById(R.id.tv_trip_id);
            tv_timeDate = itemView.findViewById(R.id.tv_map_the_trip);
        }

        private void bindValues(int position){
            int pos = position+1;
            try {
                tv_tripId.setText("TRIP ID - " + pos);
                tv_timeDate.setText(R.string.trip_adapter_map_it);
                Log.wtf(TAG, tv_tripId.getText()+" "+tv_timeDate.getText());
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
            }
            //tv_timeDate.setText(tripDetails.get(position).getStartTime()+" - "+tripDetails.get(position).getEndTime());
        }
    }

}
