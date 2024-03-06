package ro.ase.dma.connectinfluxdb;

import java.util.ArrayList;

// callback interface in order to send data from the influxDB fethcer to the activity
public interface DataUpdateCallback {
    void onDataChanged(ArrayList<String> dataArray);
}
