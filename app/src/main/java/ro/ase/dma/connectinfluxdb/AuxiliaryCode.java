package ro.ase.dma.connectinfluxdb;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONArray;
import org.json.JSONObject;
public class AuxiliaryCode {
    public String responseData;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    public void fetchContinuousData() {
        String query="SELECT value FROM T1 ORDER BY time DESC LIMIT 1";
        String databaseName="monitor";
        executor.scheduleAtFixedRate(() -> {
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
                JSONArray seriesArray1 = jsonObject.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONArray("series");

                //get the number of transmitters
                Log.e("T0 full specs:", String.valueOf(seriesArray1));

//                Log.e("Number of engines:", String.valueOf(seriesArray.length()));
                // Access the first item in the 'values' array
                JSONArray valuesArray = seriesArray1.getJSONObject(0).getJSONArray("values");
                // Get the value ()
                double value = valuesArray.getJSONArray(0).getDouble(1);

                // Process responseData (contains the fetched data)
                Log.w("Data:", String.valueOf(value));
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions
            }
        }, 0, 20, TimeUnit.SECONDS); // Fetch data every 20 second
    }

    public void stopFetchingData() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

}


///VAR cu toate ----------------------------------------------------------------------------------------------------------------------

//package ro.ase.dma.connectinfluxdb;
//        import android.util.Log;
//
//        import java.io.BufferedReader;
//        import java.io.InputStreamReader;
//        import java.net.HttpURLConnection;
//        import java.net.URL;
//        import java.util.concurrent.Executors;
//        import java.util.concurrent.ScheduledExecutorService;
//        import java.util.concurrent.TimeUnit;
//        import org.json.JSONArray;
//        import org.json.JSONObject;
//public class InfluxDBContinuousFetcher {
//    public String responseData;
//    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//    public void fetchContinuousData() {
//        String[]  measurements={"T0", "T1", "T2", "V_0", "V_1", "V_2", "P_testem3_0", "P_testem3_1", "P_testem3_2", "PF_0", "PF_1", "PF_2", "I_0", "I_1", "I_2"};
//        String databaseName="monitor";
//        executor.scheduleAtFixedRate(() -> {
//            for(String measurement:measurements) {
//                String query = String.format("SELECT last(value) FROM \"%s\"", measurement);
//
//                try {
//                    URL url = new URL("http://192.168.100.10:8086/query?q=" + query + "&db=" + databaseName);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//
//                    // Read the response
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        response.append(line);
//                    }
//                    reader.close();
//                    responseData = response.toString();
//
//                    // Convert StringBuilder to String
//                    String jsonResponse = response.toString();
//                    // Parse the JSON
//                    JSONObject jsonObject = new JSONObject(jsonResponse);
//                    // Access the 'series' array
//                    JSONArray seriesArray1 = jsonObject.getJSONArray("results")
//                            .getJSONObject(0)
//                            .getJSONArray("series");
//
//                    //get the number of transmitters
//                    Log.e("Data from " + measurement + " ", String.valueOf(seriesArray1));
//
////                Log.e("Number of engines:", String.valueOf(seriesArray.length()));
//                    // Access the first item in the 'values' array
//                    JSONArray valuesArray = seriesArray1.getJSONObject(0).getJSONArray("values");
//                    // Get the value ()
//                    double value = valuesArray.getJSONArray(0).getDouble(1);
//
//                    // Process responseData (contains the fetched data)
//                    //Log.w("Data from " + measurement, String.valueOf(value));
//                    conn.disconnect();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    // Handle exceptions
//                }
//            }
//        }, 0, 20, TimeUnit.SECONDS); // Fetch data every 20 second
//    }
//
//    public void stopFetchingData() {
//        if (executor != null && !executor.isShutdown()) {
//            executor.shutdownNow();
//        }
//    }
//
//}
//




//package ro.ase.dma.connectinfluxdb;
//
//        import android.app.Application;
//        import android.content.Context;
//        import android.os.Handler;
//        import android.os.Looper;
//        import android.widget.Toast;
//
//        import androidx.annotation.NonNull;
//        import androidx.work.Constraints;
//        import androidx.work.ExistingWorkPolicy;
//        import androidx.work.NetworkType;
//        import androidx.work.OneTimeWorkRequest;
//        import androidx.work.WorkInfo;
//        import androidx.work.WorkManager;
//        import androidx.work.Worker;
//        import androidx.work.WorkerParameters;
//
//        import java.util.List;
//        import java.util.concurrent.Executors;
//        import java.util.concurrent.ScheduledExecutorService;
//        import java.util.concurrent.TimeUnit;
//
//public class WorkManagerApplication extends Application {
//    private final ScheduledExecutorService backgroundExecutor = Executors.newSingleThreadScheduledExecutor();
//    private final Handler mainHandler = new Handler(Looper.getMainLooper());
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initWork();
//    }
//
//    private void initWork() {
//        backgroundExecutor.execute(() -> {
//            setupToastShowingWork(0); // No delay at first time
//            observeToastShowingWork(); // Observe work state changes
//        });
//    }
//
//    private void setupToastShowingWork(long delayInSeconds) {
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.UNMETERED) // When using WiFi
//                .build();
//
//        OneTimeWorkRequest oneTimeRequest = new OneTimeWorkRequest.Builder(ToastShower.class) // ToastShower is your Worker class
//                .setInitialDelay(delayInSeconds, TimeUnit.SECONDS) // Customizable delay (interval) time
//                .setConstraints(constraints)
//                .build();
//
//        WorkManager.getInstance(this).enqueueUniqueWork(
//                ToastShower.class.getSimpleName(), // Work name, use class name for convenience
//                ExistingWorkPolicy.KEEP, // If new work comes in with the same name, discard the new one
//                oneTimeRequest
//        );
//    }
//
//    private void observeToastShowingWork() {
//        WorkManager.getInstance(this).getWorkInfosForUniqueWorkLiveData(ToastShower.class.getSimpleName()).observeForever(workInfos -> {
//            if (workInfos != null && !workInfos.isEmpty() && workInfos.get(0).getState() == WorkInfo.State.SUCCEEDED) {
//                // When the work is done, schedule next
//                backgroundExecutor.schedule(() -> setupToastShowingWork(5), 5, TimeUnit.SECONDS); // Every 5 seconds
//            }
//        });
//    }
//
//    public static class ToastShower extends Worker {
//        public ToastShower(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//            super(context, workerParams);
//        }
//
//        @NonNull
//        @Override
//        public Result doWork() {
//            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), "Hey, I'm Sam! This message will appear every 5 seconds.", Toast.LENGTH_SHORT).show());
//            return Result.success();
//        }
//    }
//}