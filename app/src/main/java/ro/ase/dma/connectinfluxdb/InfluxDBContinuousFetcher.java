package ro.ase.dma.connectinfluxdb;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import android.content.SharedPreferences;
import org.json.JSONObject;
public class InfluxDBContinuousFetcher {
    private DataUpdateCallback dataUpdateCallback = null;
    public String responseData;
    public SharedPreferences sharedPreferences;
    private Context context;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public InfluxDBContinuousFetcher(Context context)
    {
        this.context = context;
    }

    public void fetchContinuousData() {
        fetchHistoryData();
        // all measurements from the database
        String[]  measurements={"T0", "P_testem3_0", "PF_0", "V_0", "I_0", "T1", "P_testem3_1", "PF_1", "V_1", "I_1", "T2", "P_testem3_2", "PF_2", "V_2", "I_2"};
        String databaseName="monitor";
        executor.scheduleAtFixedRate(() -> {
            sharedPreferences = context.getSharedPreferences("loginData", Context.MODE_PRIVATE);
            String URL = sharedPreferences.getString("URL","192.168.100.10:8086");
            //save the data taken in the last fixed interval loop into an arraylist
            ArrayList<String> dataList = new ArrayList<>();
                //start for
                for(String measurement:measurements) {
                    // influx DB set on universal time, -2h from romania time
                    // take all the values in the past hour
                    String query = String.format("SELECT last(value) FROM \"%s\"", measurement);

                    try {
                        URL url = new URL("http://"+URL+"/query?q=" + query + "&db=" + databaseName);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        // Read the response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        responseData = response.toString();

                        // Convert StringBuilder to String
                        String jsonResponse = response.toString();
                        // Parse the JSON
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        // Access the 'series' array
                        // the first format:  {"results":[{"statement_id":0,"series":[{"name":"T2","columns":["time","value"],"values":[["2024-02-22T12:25:00.914779735Z",20.9]
                        JSONArray seriesArray1 = jsonObject.getJSONArray("results")
                                .getJSONObject(0)
                                .getJSONArray("series");

                        //seriesArray1 format example : [{"name":"T2","columns":["time","value"],"values":[["2024-02-22T12:25:00.914779735Z",20.9]

                        //-----------------------
                        //formatting the result in order to be easier to work with data
                        for (int i = 0; i < seriesArray1.length(); i++) {
                            JSONObject series = seriesArray1.getJSONObject(i);
                            String name = series.getString("name"); // Measurement name, e.g., "T2"
                            JSONArray valuesArray = series.getJSONArray("values");

                            // loop through each value ( first one is time second is value )
                            for (int j = 0; j < valuesArray.length(); j++) {
                                JSONArray valueEntry = valuesArray.getJSONArray(j);
                                String time = valueEntry.getString(0); // Time value
                                double value = valueEntry.getDouble(1); // Measurement value

                                // Format it more in order to obtain easy working values
                                String formattedOutput = String.format("%s, %s, %f", name, time, value);
                                dataList.add(formattedOutput); //add the full line
                                //current format example: T0, 2024-02-22T12:29:35.858145939Z, 28.200000
                            }
                        }
                        //------------------------


                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle exceptions
                    }
                }
                //end for
                // now the arrayList is populated with values, in our case 15 values, each engine having 5 stats
//                for(String m:dataList)
//                {
//                    Log.w("Data", m);
//                }

            //use the callback and input the new data that is being processed
            if (dataUpdateCallback != null) {
                dataUpdateCallback.onDataChanged(dataList);
            }



        }, 0, 10, TimeUnit.SECONDS); // Fetch data every 10 second
    }


    public void fetchHistoryData() {
        // all measurements from the database
        String[]  measurements={"T0", "P_testem3_0", "PF_0", "V_0", "I_0", "T1", "P_testem3_1", "PF_1", "V_1", "I_1", "T2", "P_testem3_2", "PF_2", "V_2", "I_2"};
        String databaseName="monitor";
        executor.execute(() -> {

            //save the data taken in the last fixed interval loop into an arraylist
            ArrayList<String> dataList = new ArrayList<>();
            //start for
            for(String measurement:measurements) {
                // influx DB set on universal time, -2h from romania time
                // take all the values in the past hour
                String query = String.format("SELECT value FROM \"%s\"  WHERE time > now() - 24h", measurement);

                try {
                    URL url = new URL("http://192.168.100.10:8086/query?q=" + query + "&db=" + databaseName);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    // Read the response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    responseData = response.toString();

                    // Convert StringBuilder to String
                    String jsonResponse = response.toString();
                    // Parse the JSON
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    // Access the 'series' array
                    // the first format:  {"results":[{"statement_id":0,"series":[{"name":"T2","columns":["time","value"],"values":[["2024-02-22T12:25:00.914779735Z",20.9]
                    JSONArray seriesArray1 = jsonObject.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONArray("series");

                    //seriesArray1 format example : [{"name":"T2","columns":["time","value"],"values":[["2024-02-22T12:25:00.914779735Z",20.9]

                    //-----------------------
                    //formatting the result in order to be easier to work with data
                    for (int i = 0; i < seriesArray1.length(); i++) {
                        JSONObject series = seriesArray1.getJSONObject(i);
                        String name = series.getString("name"); // Measurement name, e.g., "T2"
                        JSONArray valuesArray = series.getJSONArray("values");

                        // loop through each value ( first one is time second is value )
                        for (int j = 0; j < valuesArray.length(); j++) {
                            JSONArray valueEntry = valuesArray.getJSONArray(j);
                            String time = valueEntry.getString(0); // Time value
                            double value = valueEntry.getDouble(1); // Measurement value

                            // Format it more in order to obtain easy working values
                            String formattedOutput = String.format("%s, %s, %f", name, time, value);
                            dataList.add(formattedOutput); //add the full line
                            //current format example: T0, 2024-02-22T12:29:35.858145939Z, 28.200000
                        }
                    }
                    //------------------------


                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle exceptions
                }
            }
            //end for
            // now the arrayList is populated with values, in our case 15 values, each engine having 5 stats
//                for(String m:dataList)
//                {
//                    Log.w("Data", m);
//                }

            //use the callback and input the new data that is being processed
            if (dataUpdateCallback != null) {
                dataUpdateCallback.onDataChanged(dataList);
            }



        }); // Fetch data history only once
    }


    //close the executor thus not receive data anymroe
    public void stopFetchingData() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }


    //documentation!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void setDataChanged(DataUpdateCallback callable)
    {
        this.dataUpdateCallback = callable;
    }

}

//get the number of transmitters
//                        Log.e("Data from " + measurement + " ", String.valueOf(seriesArray1));


//                          Log.e("Number of engines:", String.valueOf(seriesArray.length()));
// Access the first item in the 'values' array
//                          JSONArray valuesArray = seriesArray1.getJSONObject(0).getJSONArray("values");
//                          // Get the value ()
//                          double value = valuesArray.getJSONArray(0).getDouble(1);

// Process responseData (contains the fetched data)
//Log.w("Data from " + measurement, String.valueOf(value));