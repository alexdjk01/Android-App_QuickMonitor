package ro.ase.dma.connectinfluxdb;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;

public class firstEngineActivity extends AppCompatActivity implements DataUpdateCallback {

    private InfluxDBContinuousFetcher influxDBContinuousFetcher= new InfluxDBContinuousFetcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine);
        //send data to this context
        influxDBContinuousFetcher.setDataChanged(this);
        influxDBContinuousFetcher.fetchContinuousData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (influxDBContinuousFetcher != null) {
            //unregister the callback function listener
            influxDBContinuousFetcher.setDataChanged(null);
            //stop the executor thread for fetching data
            influxDBContinuousFetcher.stopFetchingData();
        }
    }

    @Override
    public void onDataChanged(ArrayList<String> dataArray) {
        Log.w("Pause", "---------------------------------------------------------------------");
        for(String aux:dataArray)
        {
            Log.w("Engine Activity", aux );
        }


//        DMA 11 for graphs
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // Update your UI here with the data
//            }
//        });
    }
}