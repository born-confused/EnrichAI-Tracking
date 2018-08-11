package product.enrichai.com.enrichaitracking;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by WELCOME on 6/13/2018.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.PositionViewHolder> {

    private ArrayList<Position> list_of_item;
    private Context context;

    StatusAdapter(ArrayList<Position> data, Context context){
        list_of_item = data;
        this.context = context;
    }


    @NonNull
    @Override
    public PositionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_location_update, parent, false);
        return new PositionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PositionViewHolder holder, int position) {
        holder.bindValues(position);
    }

    @Override
    public int getItemCount() {
        return list_of_item.size();
    }

    class PositionViewHolder extends RecyclerView.ViewHolder{

        TextView tv_id;
        TextView tv_lat;
        TextView tv_long;
        TextView tv_time;
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        public PositionViewHolder(View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.location_id);
            tv_lat = itemView.findViewById(R.id.location_lat);
            tv_long = itemView.findViewById(R.id.location_long);
            tv_time = itemView.findViewById(R.id.location_time);
        }

        private void bindValues(int position){
            tv_id.setText(list_of_item.get(getItemCount() - position - 1).getId() + "");
            tv_lat.setText(numberFormat.format(list_of_item.get(getItemCount() - position - 1).getLatitude()));
            tv_long.setText(numberFormat.format(list_of_item.get(getItemCount() - position - 1).getLongitude()));
            tv_time.setText(timeFormat.format(list_of_item.get(getItemCount() - position - 1).getTime()));
        }
    }
}
