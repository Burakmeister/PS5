package com.example.ps5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SensorActivity extends AppCompatActivity {
    public static final int[] SENSORS_WITH_DETAILS = new int[]{Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_LIGHT};
    public static final String SENSOR_TYPE = "SENSOR_TYPE";

    private SensorAdapter adapter;

    public SensorActivity() {
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        RecyclerView recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(adapter == null){
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        int count = adapter.getItemCount();
        String menu_sensors_count = getString(R.string.sensors_count, count);
        menu.getItem(0).setTitle(menu_sensors_count);
        return true;
    }

    class SensorAdapter extends RecyclerView.Adapter<SensorHolder>{
        private final List<Sensor> sensorList;

        public SensorAdapter(List<Sensor> sensorList){
            this.sensorList = sensorList;
        }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new SensorHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor = sensorList.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return this.sensorList.size();
        }
    }



    class SensorHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView sensorText;
        private final ImageView icon;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));

            this.sensorText = itemView.findViewById(R.id.textView);
            this.icon = itemView.findViewById(R.id.sensor_icon);
        }

        public void bind(Sensor sensor){
            this.sensorText.setText(sensor.getName());
            for(int s: SensorActivity.SENSORS_WITH_DETAILS){
                if(sensor.getType()==s) {
                    itemView.findViewById(R.id.sensor_list_item)
                            .setBackgroundColor(getResources().getColor(R.color.purple_200));
                    itemView.findViewById(R.id.sensor_list_item)
                            .setOnClickListener(view->
                            {
                                Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                                intent.putExtra(SENSOR_TYPE, sensor.getType());
                                startActivity(intent);
                            });

                }
            }

            if(sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
                itemView.findViewById(R.id.sensor_list_item)
                        .setBackgroundColor(getResources().getColor(R.color.teal_700));
                itemView.findViewById(R.id.sensor_list_item)
                        .setOnClickListener(view->
                        {
                            Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                            startActivity(intent);
                        });
            }


            this.icon.setImageResource(R.drawable.ic_sensor);
            Log.d("Sensor/Producent: ", sensor.getVendor()+"/"+sensor.getMaximumRange());
        }

        @Override
        public void onClick(View v) {

        }
    }
}