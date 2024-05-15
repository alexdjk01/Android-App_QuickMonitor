package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements DataUpdateCallback {
    private BottomNavigationView bottomNavigation;
    private User receivedUserLogged = null;
    private InfluxDBContinuousFetcher influxDBContinuousFetcher= new InfluxDBContinuousFetcher(this);

    ArrayList<ArrayList<String>> groupedEngines = new ArrayList<>();
    ArrayList<ArrayList<String>> resultHistory = new ArrayList<>();

    Engine engineOne;
    Engine engineTwo;
    Engine engineThree;

    // in order not to spam, this string will save which emails were sent for every measurement.
    // made them static in order to keep the value even though i navigate to another activity and come back.
    static String engineOneAlertsSent =" ";
    static String engineTwoAlertsSent =" ";
    static String engineThreeAlertsSent =" ";

    // dynamically renders the UI for that specific engine
    private Button btnEngine1;
    private Button btnEngine2;
    private Button btnEngine3;

    //exports the history data up the current moment to the logged in email
    private Button btnExportMail;

    // instead of a progress bar i chose to show the temperature using a text view
    //because is the most important aspect of the engine and should be seen reality
    private TextView tvNumericalTemperature;

    // Semi-Circular progress bars that will display the current data from the engine in real time.
    private SemiCircularProgressBar pbPower;
    private SemiCircularProgressBar pbPowerFactor;
    private SemiCircularProgressBar pbTension;
    private SemiCircularProgressBar pbAmperage;

    //graph and drop down button for TEMPERATURE graph
    private XYPlot graphPlotTemperature;
    private Spinner spinnerTimeTemperature;

    //graph and drop down button for POWER graph
    private XYPlot graphPlotPower;
    private Spinner spinnerTimePower;

    //graph and drop down button for TENSION graph
    private XYPlot graphPlotTension;
    private Spinner spinnerTimeTension;

    //graph and drop down button for AMPERAGE graph
    private XYPlot graphPlotAmperage;
    private Spinner spinnerTimeAmperage;

    //TextViews that will be populated dynamically with min/max/avg values for each measurement type
    private TextView tvMinimumValueTemperature;
    private TextView tvMaximumValueTemperature;
    private TextView tvAverageValueTemperature;
    private TextView tvMinimumValuePower;
    private TextView tvMaximumValuePower;
    private TextView tvAverageValuePower;
    private TextView tvMinimumValuePowerFactor;
    private TextView tvMaximumValuePowerFactor;
    private TextView tvAverageValuePowerFactor;
    private TextView tvMinimumValueTension;
    private TextView tvMaximumValueTension;
    private TextView tvAverageValueTension;
    private TextView tvMinimumValueAmperage;
    private TextView tvMaximumValueAmperage;
    private TextView tvAverageValueAmperage;

    // global variable tht will keep track of which engine should be displayed!
    // default value is 2 to view engine no3
    private int currentEngineUI=2;

    // this graph Interval is the default number of items that are shown on the graph
    public int[] graphInterval = new int[5]; //default shows ony 9 values

    //shared preferecnes to trasnfer email or URL or other things
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        graphInterval[0] = 9; graphInterval[1] =9; graphInterval[2]=9;
        graphInterval[3]=9; graphInterval[4]=9;

        influxDBContinuousFetcher.setDataChanged(this);
        influxDBContinuousFetcher.fetchContinuousData();

        btnEngine1 = findViewById(R.id.btnEngine1);
        btnEngine2 = findViewById(R.id.btnEngine2);
        btnEngine3 = findViewById(R.id.btnEngine3);
        btnExportMail = findViewById(R.id.btnExportMail);

        tvNumericalTemperature = findViewById(R.id.tvNumericalTemperature);
        pbPower = findViewById(R.id.pbPower);
        pbPowerFactor = findViewById(R.id.pbPowerFactor);
        pbTension = findViewById(R.id.pbTension);
        pbAmperage = findViewById(R.id.pbAmperage);

        graphPlotTemperature = findViewById(R.id.graphPlot);
        spinnerTimeTemperature = findViewById(R.id.spinnerTimeTemperature);

        graphPlotPower = findViewById(R.id.graphPlotPower);
        spinnerTimePower = findViewById(R.id.spinnerTimePower);

        graphPlotTension = findViewById(R.id.graphPlotTension);
        spinnerTimeTension = findViewById(R.id.spinnerTimeTension);

        graphPlotAmperage = findViewById(R.id.graphPlotAmpergae);
        spinnerTimeAmperage = findViewById(R.id.spinnerTimeAmperage);

        tvAverageValueTemperature = findViewById(R.id.tvAvgValTemp);
        tvMinimumValueTemperature = findViewById(R.id.tvMinValTemp);
        tvMaximumValueTemperature = findViewById(R.id.tvMaxValTemp);
        tvAverageValuePower = findViewById(R.id.tvAvgValPower);
        tvMinimumValuePower = findViewById(R.id.tvMinValPower);
        tvMaximumValuePower = findViewById(R.id.tvMaxValPower);
        tvAverageValuePowerFactor = findViewById(R.id.tvAvgValPowerFactor);
        tvMinimumValuePowerFactor = findViewById(R.id.tvMinValPowerFactor);
        tvMaximumValuePowerFactor = findViewById(R.id.tvMaxValPowerFactor);
        tvAverageValueTension = findViewById(R.id.tvAvgValTension);
        tvMinimumValueTension = findViewById(R.id.tvMinValTension);
        tvMaximumValueTension = findViewById(R.id.tvMaxValTension);
        tvAverageValueAmperage = findViewById(R.id.tvAvgValAmperage);
        tvMinimumValueAmperage = findViewById(R.id.tvMinValAmperage);
        tvMaximumValueAmperage = findViewById(R.id.tvMaxValAmperage);

        bottomNavigation = findViewById(R.id.navigationMenuBar);

        Intent receivedIntentLogged = getIntent();
        if(receivedIntentLogged!=null) {
            receivedUserLogged = receivedIntentLogged.getParcelableExtra("keyLogin");
            if (receivedUserLogged != null) {
                Log.e("Home activity: ", receivedUserLogged.toString());
                // data was loaded correctly
            }
        }

        btnEngine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEngine1.setTextColor(getApplicationContext().getColor(R.color.orange_alert));
                btnEngine2.setTextColor(getApplicationContext().getColor(R.color.green_avocado));
                btnEngine3.setTextColor(getApplicationContext().getColor(R.color.green_avocado));
                currentEngineUI=0;
                populateGraphHistory(graphPlotTemperature,0,40,0,graphInterval[0],"Temperature",4,1); //default: show only the last 9 values taken in the grap
                populateGraphHistory(graphPlotPower,0,100,3,graphInterval[1],"Power", 20F,1); //default: show only the last 9 values taken in the graph
                populateGraphHistory(graphPlotTension,0,500,9,graphInterval[2],"Tension",40F,1);
                populateGraphHistory(graphPlotAmperage,0,1,12,graphInterval[3],"Amperage", 0.1F,1);
                updateUI(groupedEngines);
            }
        });

        btnEngine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEngine2.setTextColor(getApplicationContext().getColor(R.color.orange_alert));
                btnEngine1.setTextColor(getApplicationContext().getColor(R.color.green_avocado));
                btnEngine3.setTextColor(getApplicationContext().getColor(R.color.green_avocado));
                currentEngineUI=1;
                populateGraphHistory(graphPlotTemperature,0,40,1,graphInterval[0],"Temperature",4,1);
                populateGraphHistory(graphPlotPower,0,100,4,graphInterval[1],"Power",20F,1);
                populateGraphHistory(graphPlotTension,0,500,10,graphInterval[2],"Tension",40F,1);
                populateGraphHistory(graphPlotAmperage,0,1,13,graphInterval[3],"Amperage", 0.1F,1);
                updateUI(groupedEngines);
            }
        });

        btnEngine3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEngine3.setTextColor(getApplicationContext().getColor(R.color.orange_alert));
                btnEngine1.setTextColor(getApplicationContext().getColor(R.color.green_avocado));
                btnEngine2.setTextColor(getApplicationContext().getColor(R.color.green_avocado));
                currentEngineUI=2;
                //Log.w("Check restulH", String.valueOf(resultHistory.get(2)));
                populateGraphHistory(graphPlotTemperature,0,40,2,graphInterval[0],"Temperature",4,1);
                populateGraphHistory(graphPlotPower,0,200,5,graphInterval[1],"Power",20F,1);
                populateGraphHistory(graphPlotTension,0,500,11,graphInterval[2],"Tension",40F,1);
                populateGraphHistory(graphPlotAmperage,0,1,14,graphInterval[3],"Amperage", 0.1F,1);
                updateUI(groupedEngines);
            }
        });

        btnExportMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailsExportHistory(resultHistory);
            }
        });

        //we need ArrayAdapted to populate a Spinner with the values in the string array situated in res/strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTimeTemperature.setAdapter(adapter);
        spinnerTimePower.setAdapter(adapter);
        spinnerTimeTension.setAdapter(adapter);
        spinnerTimeAmperage.setAdapter(adapter);

       spinnerTimeTemperature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               String clickedInterval = adapterView.getItemAtPosition(pos).toString();
               if(!resultHistory.isEmpty())
               {
                   if(clickedInterval.equals("1m")){
                       graphInterval[0]=5;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
                   else if (clickedInterval.equals("2m")){
                       graphInterval[0]=10;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
                   else if (clickedInterval.equals("3m")){
                       graphInterval[0]=15;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
                   else if (clickedInterval.equals("10m")){
                       graphInterval[0]=50;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
                   else if (clickedInterval.equals("30m")){
                       graphInterval[0]=150;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
                   else if (clickedInterval.equals("1h")){
                       graphInterval[0]=300;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
                   else if (clickedInterval.equals("3h")){
                       graphInterval[0]=900;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
                   else if (clickedInterval.equals("24h")){
                       graphInterval[0]=7200;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
                   }
               }
               else
               {
                   Log.w("resultHistoryEmpty", resultHistory.toString());
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

        spinnerTimePower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String clickedInterval = adapterView.getItemAtPosition(pos).toString();
                if(!resultHistory.isEmpty())
                {
                    if(clickedInterval.equals("1m")){
                        graphInterval[1]=5;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                    else if (clickedInterval.equals("2m")){
                        graphInterval[1]=10;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                    else if (clickedInterval.equals("3m")){
                        graphInterval[1]=15;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                    else if (clickedInterval.equals("10m")){
                        graphInterval[1]=50;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                    else if (clickedInterval.equals("30m")){
                        graphInterval[1]=150;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                    else if (clickedInterval.equals("1h")){
                        graphInterval[1]=300;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                    else if (clickedInterval.equals("3h")){
                        graphInterval[1]=900;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                    else if (clickedInterval.equals("24h")){
                        graphInterval[1]=7200;
                        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
                    }
                }
                else
                {
                    Log.w("resultHistoryEmpty", resultHistory.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerTimeTension.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String clickedInterval = adapterView.getItemAtPosition(pos).toString();
                if(!resultHistory.isEmpty())
                {
                    if(clickedInterval.equals("1m")){
                        graphInterval[2]=5;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);                    }
                    else if (clickedInterval.equals("2m")){
                        graphInterval[2]=10;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);                    }
                    else if (clickedInterval.equals("3m")){
                        graphInterval[2]=15;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);
                    }
                    else if (clickedInterval.equals("10m")){
                        graphInterval[2]=50;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);
                    }
                    else if (clickedInterval.equals("30m")){
                        graphInterval[2]=150;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);                    }
                    else if (clickedInterval.equals("1h")){
                        graphInterval[2]=300;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);                    }
                    else if (clickedInterval.equals("3h")){
                        graphInterval[2]=900;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);                    }
                    else if (clickedInterval.equals("24h")){
                        graphInterval[2]=7200;
                        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);                    }
                }
                else
                {
                    Log.w("resultHistoryEmpty", resultHistory.toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerTimeAmperage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                String clickedInterval = adapterView.getItemAtPosition(pos).toString();
                if(!resultHistory.isEmpty())
                {
                    if(clickedInterval.equals("1m")){
                        graphInterval[3]=5;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);                    }
                    else if (clickedInterval.equals("2m")){
                        graphInterval[3]=10;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);                    }
                    else if (clickedInterval.equals("3m")){
                        graphInterval[3]=15;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);
                    }
                    else if (clickedInterval.equals("10m")){
                        graphInterval[3]=50;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);
                    }
                    else if (clickedInterval.equals("30m")){
                        graphInterval[3]=150;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);                    }
                    else if (clickedInterval.equals("1h")){
                        graphInterval[3]=300;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);                    }
                    else if (clickedInterval.equals("3h")){
                        graphInterval[3]=900;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);                    }
                    else if (clickedInterval.equals("24h")){
                        graphInterval[3]=7200;
                        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);                    }
                }
                else
                {
                    Log.w("resultHistoryEmpty", resultHistory.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //we use setOnItemSelected for the bottom navigation bar and will write the implementation of each "button" in the menu
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navHome)
                {

                }
                else if(item.getItemId() == R.id.navSettings)
                {

                    Intent toSettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                    toSettingsIntent.putExtra("keyHome",receivedUserLogged);
                    toSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(toSettingsIntent);

                }
                else
                {
                    Intent toProfileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                    toProfileIntent.putExtra("keyHome",receivedUserLogged);
                    toProfileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(toProfileIntent);
                }
                return true;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // sets the activity intent to the new intent
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




    // method that is in charge with the design of the graph.
    // here the graph specifications are made and the arrays are inserted into the graph
    public void graphPlotFunction(XYPlot plot, int lowerBoundry, int upperBoundry, ArrayList<String> domainLabels,
                                  Double[] seriesNumbers, int interval, String title, float increment_range_by, float increment_domain_by)
    {
        plot.clear();
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(seriesNumbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, title);
        int endIndex = series1.size()-1;
        int startIndex = Math.max(0,endIndex-interval);
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                getApplicationContext().getColor(R.color.green_avocado),
                getApplicationContext().getColor(R.color.green_avocado),
                getApplicationContext().getColor(R.color.blue_ediText),
                null);
        plot.addSeries(series1, series1Format);
        plot.setRangeBoundaries(lowerBoundry, upperBoundry, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, increment_range_by);
        plot.setDomainBoundaries(startIndex,endIndex,BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL,increment_domain_by);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                int index = Math.round(((Number) obj).floatValue());
                if(endIndex-startIndex <= 10)
                {
                    if (index >= 0 && index < domainLabels.size()) {
                        return stringBuffer.append(domainLabels.get(index));
                    } else {
                        return stringBuffer;
                    }
                }
                else
                    return stringBuffer.append("");
            }
            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });
        PanZoom.attach(plot);
    }

    // this method is the one that gets the final arrays that will be inserted into the graph
    // using that function graphPlotfunction() on a separate thread we INSERT and display them on the graph
    public void populateGraphHistory(XYPlot plot,int lowerBoundry,int upperBoundry,int index, int interval,String title, float increment_range_by, float increment_domain_by)
    {
        ArrayList<String> arrayTime = new ArrayList<>();
        ArrayList<String> arrayValue= new ArrayList<>();
        ArrayList<String> engineValues = resultHistory.get(index);
        Log.w("ResultH Current index", String.valueOf(index));
        for (String data:engineValues)
        {
            Log.w("ResultH Current Value", data);
        }
        for(String data:engineValues)
        {
            String divideData[] = data.split(",\\s*") ;// in ordet to split by , and space
            String timePart = divideData[1].substring(divideData[1].indexOf('T') + 1, divideData[1].indexOf('.'));
            arrayTime.add(timePart);
            arrayValue.add(divideData[2]);
        }

        Log.w("Value", String.valueOf(arrayValue));

        // time values in arrayTime are in UTC format. However Romania is situated in UTC+3
        // using this method to covert the UTC time in UTC+3
        ArrayList<String> convertedTimesArray = TimeConversionUTC.parseToUtcPlus3(arrayTime);
        Log.w("Time", String.valueOf(convertedTimesArray));

        ArrayList<Double> doubleValues = new ArrayList<>();
        for (String strValue : arrayValue) {
            try {
                double doubleValue = Double.parseDouble(strValue);
                doubleValues.add(doubleValue);
            } catch (NumberFormatException e) {
                // Handle parsing errors if needed
                e.printStackTrace();
            }
        }

        Double[] doubleArray = doubleValues.toArray(new Double[doubleValues.size()]);
        Log.w("DoubleArray", Arrays.toString(doubleArray));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                graphPlotFunction(plot,lowerBoundry,upperBoundry,convertedTimesArray, doubleArray, interval,title,increment_range_by, increment_domain_by);
                plot.redraw();
            }
        });
    }

    // this method clears the resultHistory , Adds the data from influxDb into separate arrays for each measurement(15 measurements for 3 engines),
    // and then populates the reusltHistory again with the new received history from the databse
    // it clears the history beacuse maybe in the meantime noew values were loaded into the database and we need the lastest database.
    public void splitValuesHistory(ArrayList<String> dataArray)
    {
        ArrayList<String> T0 = new ArrayList<>(); //0   -- index in resultHistory
        ArrayList<String> T1 = new ArrayList<>();   //1
        ArrayList<String> T2= new ArrayList<>();    //2
        ArrayList<String> P_testem3_0= new ArrayList<>();      //3
        ArrayList<String> P_testem3_1= new ArrayList<>();      //4
        ArrayList<String> P_testem3_2= new ArrayList<>();      //5
        ArrayList<String> PF_0= new ArrayList<>();                //6
        ArrayList<String> PF_1= new ArrayList<>();             //7
        ArrayList<String> PF_2= new ArrayList<>();             //8
        ArrayList<String> V_0= new ArrayList<>();              //9
        ArrayList<String> V_1= new ArrayList<>();              //10
        ArrayList<String> V_2= new ArrayList<>();              //11
        ArrayList<String> I_0= new ArrayList<>();              //12
        ArrayList<String> I_1= new ArrayList<>();              //13
        ArrayList<String> I_2= new ArrayList<>();              //14

        resultHistory.clear();
        for(String data:dataArray)
        {
            String divideData[] = data.split(",\\s*") ;// in ordet to split by , and space
            if(divideData[0].equals("T0"))
            {
                T0.add(data);
            }
            else if(divideData[0].equals("T1"))
            {
                T1.add(data);
            }
            else if(divideData[0].equals("T2"))
            {
                T2.add(data);
            }
            else if(divideData[0].equals("P_testem3_0"))
            {
                P_testem3_0.add(data);
            }
            else if(divideData[0].equals("P_testem3_1"))
            {
                P_testem3_1.add(data);
            }
            else if(divideData[0].equals("P_testem3_2"))
            {
                P_testem3_2.add(data);
            }
            else if(divideData[0].equals("PF_0"))
            {
                PF_0.add(data);
            }
            else if(divideData[0].equals("PF_1"))
            {
                PF_1.add(data);
            }
            else if(divideData[0].equals("PF_2"))
            {
                PF_2.add(data);
            }
            else if(divideData[0].equals("V_0"))
            {
                V_0.add(data);
            }
            else if(divideData[0].equals("V_1"))
            {
                V_1.add(data);
            }
            else if(divideData[0].equals("V_2"))
            {
                V_2.add(data);
            }
            else if(divideData[0].equals("I_0"))
            {
                I_0.add(data);
            }
            else if(divideData[0].equals("I_1"))
            {
                I_1.add(data);
            }
            else if(divideData[0].equals("I_2"))
            {
                I_2.add(data);
            }
        }

        Log.w("Graph History T0", String.valueOf(T0));
        Log.w("Graph History T1", String.valueOf(T1));
        Log.w("Graph History T2", String.valueOf(T2));
        Log.w("Graph History P0", String.valueOf(P_testem3_0));
        Log.w("Graph History P1", String.valueOf(P_testem3_1));
        Log.w("Graph History P2", String.valueOf(P_testem3_2));
        Log.w("Graph History PF0", String.valueOf(PF_0));
        Log.w("Graph History PF1", String.valueOf(PF_1));
        Log.w("Graph History PF2", String.valueOf(PF_2));
        Log.w("Graph History T0", String.valueOf(V_0));
        Log.w("Graph History T1", String.valueOf(V_1));
        Log.w("Graph History T2", String.valueOf(V_2));
        Log.w("Graph History A0", String.valueOf(I_0));
        Log.w("Graph History A1", String.valueOf(I_1));
        Log.w("Graph History A2", String.valueOf(I_2));

        resultHistory.add(T0);
        resultHistory.add(T1);
        resultHistory.add(T2);
        resultHistory.add(P_testem3_0);
        resultHistory.add(P_testem3_1);
        resultHistory.add(P_testem3_2);
        resultHistory.add(PF_0);
        resultHistory.add(PF_1);
        resultHistory.add(PF_2);
        resultHistory.add(V_0);
        resultHistory.add(V_1);
        resultHistory.add(V_2);
        resultHistory.add(I_0);
        resultHistory.add(I_1);
        resultHistory.add(I_2);
    }

    // when new data is transmitted from the InfluxDB, this method checks if history data is transmitted or current data
    // in case of history data (the data array is larger that 15 entries), this method calls another method splitValuesHistory to clear the history data and add again the values.
    // in case of current data, it clears the engines array and populates a holder with maximum 5 values(there are exact 5 measurements)- basicly an engine - and adds it again to the engine Array
    @Override
    public void onDataChanged(ArrayList<String> dataArray) {

        // The current data for 3 engines with 5 variables each is in total 15 variables
        // If the Array List that is received exceed 15 variables it means that the history is transmitted!
        // We have an if clause to check that
        if(dataArray.size() > 15)
        {
            Log.w("History", "History loaded!");
            splitValuesHistory(dataArray);
        }
        else
        {
            Log.w("Current", "Live data loaded!");
            // dataArray has 15 variables, 5 for each engine.
            Log.w("Current", String.valueOf(dataArray));
            //arrayList to group all 3 engines that will be stored also in ArrayLists

            // Clear the current groupedEngines list to refill with updated data
            groupedEngines.clear();


            // arrayList holder for one engine
            ArrayList<String> holderEngine = new ArrayList<>();

            for ( int i=0; i<dataArray.size(); i++)
            {
                holderEngine.add(dataArray.get(i));

                //when this holder reaches 5 values, that means a complete engine variables had been loaded
                if(holderEngine.size() ==5)
                {
                    // use new arrayList in order to create a new instance of the array list because when i will delete info from honder engine it will also delete everywhere it is the same reference
                    groupedEngines.add(new ArrayList<>(holderEngine));
                    holderEngine.clear();
                }
            }
            // now the engines are grouped in the groupedEngine arrayList that will contain 3 engines
            // we need to work with the data inside this ArrayList.

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI(groupedEngines);
                }
            });

        }

    }

    // method to save the current data that enters the application into resultHistory even though the engine is not in focus on the UI
    public void loadDataSeparatelyAndSaveItInResultHistoryEvenWhenNotInFocus(){
        resultHistory.get(0).add(String.format("T%d, %s, %f",0,engineOne.getTemperatureTime(),engineOne.getTemperatureValue()));
        resultHistory.get(1).add(String.format("T%d, %s, %f",1,engineTwo.getTemperatureTime(),engineTwo.getTemperatureValue()));
        resultHistory.get(2).add(String.format("T%d, %s, %f",2,engineThree.getTemperatureTime(),engineThree.getTemperatureValue()));
        resultHistory.get(3).add(String.format("P_testem3_%d, %s, %f",0,engineOne.getPowerTime(),engineOne.getPowerValue()));
        resultHistory.get(4).add(String.format("P_testem3_%d, %s, %f",1,engineTwo.getPowerTime(),engineTwo.getPowerValue()));
        resultHistory.get(5).add(String.format("P_testem3_%d, %s, %f",2,engineThree.getPowerTime(),engineThree.getPowerValue()));
        resultHistory.get(6).add(String.format("PF_%d, %s, %f",0,engineOne.getPowerFactorTime(),engineOne.getPowerFactorValue()));
        resultHistory.get(7).add(String.format("PF_%d, %s, %f",1,engineTwo.getPowerFactorTime(),engineTwo.getPowerFactorValue()));
        resultHistory.get(8).add(String.format("PF_%d, %s, %f",2,engineThree.getPowerFactorTime(),engineThree.getPowerFactorValue()));
        resultHistory.get(9).add(String.format("V_%d, %s, %f",0,engineOne.getTensionTime(),engineOne.getTensionValue()));
        resultHistory.get(10).add(String.format("V_%d, %s, %f",1,engineTwo.getTensionTime(),engineTwo.getTensionValue()));
        resultHistory.get(11).add(String.format("V_%d, %s, %f",2,engineThree.getTensionTime(),engineThree.getTensionValue()));
        resultHistory.get(12).add(String.format("I_%d, %s, %f",0,engineOne.getAmperageTime(),engineOne.getAmperageValue()));
        resultHistory.get(13).add(String.format("I_%d, %s, %f",1,engineTwo.getAmperageTime(),engineTwo.getAmperageValue()));
        resultHistory.get(14).add(String.format("I_%d, %s, %f",2,engineThree.getAmperageTime(),engineThree.getAmperageValue()));
    }

    //method for exporting all the history up to current values in the database
    public void sendEmailsExportHistory(ArrayList<ArrayList<String>> resultHistory)
    {
        ArrayList<String> arrayTimeTemperature = new ArrayList<>();
        ArrayList<String> arrayValueTemperature= new ArrayList<>();
        ArrayList<String> arrayTimePower = new ArrayList<>();
        ArrayList<String> arrayValuePower= new ArrayList<>();
        ArrayList<String> arrayTimePF = new ArrayList<>();
        ArrayList<String> arrayValuePF= new ArrayList<>();
        ArrayList<String> arrayTimeTension = new ArrayList<>();
        ArrayList<String> arrayValueTension= new ArrayList<>();
        ArrayList<String> arrayTimeAmperage = new ArrayList<>();
        ArrayList<String> arrayValueAmperage= new ArrayList<>();

        for (int index=currentEngineUI; index<=14;index=index+3)    //use currentEgnineUi instead of a new variable beacuse the indexes should start from 0,1,2 and currentUi does the same thing
        {
            ArrayList<String> engineValues = resultHistory.get(index);
            for(String data:engineValues)
            {
                String divideData[] = data.split(",\\s*") ;// in ordet to split by , and space
                String timePart = divideData[1].substring(divideData[1].indexOf('T') + 1, divideData[1].indexOf('.'));
                if(index <3)
                {
                    arrayTimeTemperature.add(timePart);
                    arrayValueTemperature.add(divideData[2]);
                }
                else if(index >=3 && index<6 )
                {
                    arrayTimePower.add(timePart);
                    arrayValuePower.add(divideData[2]);
                }
                else if(index >=6 && index <9)
                {
                    arrayTimePF.add(timePart);
                    arrayValuePF.add(divideData[2]);
                }
                else if(index>=9 && index <12)
                {
                    arrayTimeTension.add(timePart);
                    arrayValueTension.add(divideData[2]);
                }
                else if(index>=12 && index <15)
                {
                    arrayTimeAmperage.add(timePart);
                    arrayValueAmperage.add(divideData[2]);
                }

            }
        }
        // time values in arrayTime are in UTC format. However Romania is situated in UTC+3
        // using this method to covert the UTC time in UTC+3
        ArrayList<String> convertedTimesArrayTemperature = TimeConversionUTC.parseToUtcPlus3(arrayTimeTemperature);
        ArrayList<String> convertedTimesArrayPower = TimeConversionUTC.parseToUtcPlus3(arrayTimePower);
        ArrayList<String> convertedTimesArrayPF = TimeConversionUTC.parseToUtcPlus3(arrayTimePF);
        ArrayList<String> convertedTimesArrayTension = TimeConversionUTC.parseToUtcPlus3(arrayTimeTension);
        ArrayList<String> convertedTimesArrayAmperage = TimeConversionUTC.parseToUtcPlus3(arrayTimeAmperage);

        String timeArrayAsStringTemperature = convertedTimesArrayTemperature.toString();
        String valueArrayAsStringTemperature =arrayValueTemperature.toString();
        String timeArrayAsStringPower = convertedTimesArrayPower.toString();
        String valueArrayAsStringPower =arrayValuePower.toString();
        String timeArrayAsStringPF = convertedTimesArrayPF.toString();
        String valueArrayAsStringPF =arrayValuePF.toString();
        String timeArrayAsStringTension = convertedTimesArrayTension.toString();
        String valueArrayAsStringTension =arrayValueTension.toString();
        String timeArrayAsStringAmperage = convertedTimesArrayAmperage.toString();
        String valueArrayAsStringAmperage =arrayValueAmperage.toString();


        @SuppressLint("DefaultLocale")
        String exportedMessage = String.format("For ENGINE number %d : \n \n TEMPERATURE VALUES: \n\n %s \n\n TEMPERATURE TIMES: \n\n %s \n\n" +
                        "POWER VALUES: \n\n %s \n\n POWER TIMES: \n\n %s \n\n POWER FACTOR VALUES: \n\n %s \n\n POWER FACTOR TIMES: \n\n %s \n\n" +
                        "TENSION VALUES: \n\n %s \n\n TENSION TIMES: \n\n %s \n\n AMPERAGE VALUES: \n\n %s \n\n AMPERAGE TIMES \n\n %s \n"
                                            ,currentEngineUI+1,valueArrayAsStringTemperature,timeArrayAsStringTemperature
                                            ,valueArrayAsStringPower,timeArrayAsStringPower
                                            ,valueArrayAsStringPF,timeArrayAsStringPF
                                            ,valueArrayAsStringTension,timeArrayAsStringTension
                                            ,valueArrayAsStringAmperage,timeArrayAsStringAmperage);

        sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
        String receiverEmail = sharedPreferences.getString("email","djkmata.djkmata@gmail.com");
        EmailCommunication emailCommunication = new EmailCommunication("ionelalexandru01@gmail.com","mfjhltkgndvfbksj",receiverEmail);
        emailCommunication.sendEmail(exportedMessage);

    }

    public void sendNotification(String notificationMessage)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission (HomeActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{ android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
        String chanelID = "QUICK_MONITOR_NOTIFICATION";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),chanelID);
        builder.setSmallIcon(R.drawable.icon_notification);
        builder.setContentTitle("Quick Monitor Alert");
        builder.setContentText(notificationMessage);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data_notification","NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationManager managerNotifications = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = managerNotifications.getNotificationChannel(chanelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel (chanelID, "Some description", importance);
                notificationChannel.setLightColor(Color.BLUE);
                notificationChannel.enableVibration (true);
                managerNotifications.createNotificationChannel(notificationChannel);
            }
        }
        managerNotifications.notify(0,builder.build());
    }

    // sends emails to the email address logged regarding measurements that exceed or are below the limits set in settings
    public String sendEmailsAlert(Engine engine, int engineNo, String engineAlerts )
    {
        int ok=0;
        try{
            sharedPreferences = getSharedPreferences("notifications",MODE_PRIVATE);
            Double minTemperature = (double) sharedPreferences.getFloat("minTemperature",0.0f);
            Double maxTemperature = (double) sharedPreferences.getFloat("maxTemperature",0.0f);
            Double minPower = (double) sharedPreferences.getFloat("minPower",0.0f);
            Double maxPower = (double) sharedPreferences.getFloat("maxPower",0.0f);
            Double minPowerFactor = (double) sharedPreferences.getFloat("minPowerFactor",0.0f);
            Double maxPowerFactor = (double) sharedPreferences.getFloat("maxPowerFactor",0.0f);
            Double minTension = (double) sharedPreferences.getFloat("minTension",0.0f);
            Double maxTension = (double) sharedPreferences.getFloat("maxTension",0.0f);
            Double minAmperage = (double) sharedPreferences.getFloat("minAmperage",0.0f);
            Double maxAmperage = (double) sharedPreferences.getFloat("maxAmperage",0.0f);

            String alertMessage="";

            if (engine.getTemperatureValue() < minTemperature && !engineAlerts.contains("minTemp"))
            {
                engineAlerts = engineAlerts + "minTemp" + " ";
                alertMessage += String.format("The temperature of engine number %d is BELOW %.2f °C minimum threshold! Current value: %.2f \n", engineNo,minTemperature,engine.getTemperatureValue());
                ok=1;
            }
            else if (engine.getTemperatureValue() > maxTemperature  && !engineAlerts.contains("maxTemp"))
            {
                engineAlerts = engineAlerts + "maxTemp" + " ";
                alertMessage += String.format("The temperature of engine number %d is ABOVE %.2f °C maximum threshold! Current value: %.2f \n", engineNo,maxTemperature,engine.getTemperatureValue());
                ok=1;
            }
            if (engine.getPowerValue() < minPower  && !engineAlerts.contains("minPower"))
            {
                engineAlerts = engineAlerts + "minPower" + " ";
                alertMessage += String.format("The power (W) of engine number %d is BELOW %.2f W minimum threshold! Current value: %.2f \n", engineNo,minPower,engine.getPowerValue());
                ok=1;
            }
            else if (engine.getPowerValue() > maxPower && !engineAlerts.contains("maxPower"))
            {
                engineAlerts = engineAlerts + "maxPower" + " ";
                alertMessage += String.format("The  power (W) of engine number %d is ABOVE %.2f W maximum threshold! Current value: %.2f\n", engineNo,maxPower,engine.getPowerValue());
                ok=1;
            }
            if (engine.getPowerFactorValue() < minPowerFactor && !engineAlerts.contains("minPowerFactor"))
            {
                engineAlerts = engineAlerts + "minPowerFactor" + " ";
                alertMessage += String.format("The power factor of engine number %d is BELOW %.2f W minimum threshold! Current value: %.2f\n", engineNo,minPowerFactor,engine.getPowerFactorValue());
                ok=1;
            }
            else if (engine.getPowerFactorValue() > maxPowerFactor && !engineAlerts.contains("maxPowerFactor"))
            {
                engineAlerts = engineAlerts + "maxPowerFactor" + " ";
                alertMessage += String.format("The  power factor of engine number %d is ABOVE %.2f W maximum threshold! Current value: %.2f\n", engineNo,maxPowerFactor,engine.getPowerFactorValue());
                ok=1;
            }
            if (engine.getTensionValue() < minTension && !engineAlerts.contains("minTension"))
            {
                engineAlerts = engineAlerts + "minTension" + " ";
                alertMessage += String.format("The power factor of engine number %d is BELOW %.2f V minimum threshold! Current value: %.2f \n", engineNo,minTension,engine.getTensionValue());
                ok=1;
            }
            else if (engine.getTensionValue() > maxTension && !engineAlerts.contains("maxTension"))
            {
                engineAlerts = engineAlerts + "maxTension" + " ";
                alertMessage += String.format("The  power factor of engine number %d is ABOVE %.2f V maximum threshold! Current value: %.2f \n", engineNo,maxTension,engine.getTensionValue());
                ok=1;
            }
            if (engine.getAmperageValue() < minAmperage && !engineAlerts.contains("minAmperage"))
            {
                engineAlerts = engineAlerts + "minAmperage" + " ";
                alertMessage += String.format("The power factor of engine number %d is BELOW %.2f A minimum threshold! Current value: %.2f \n", engineNo,minAmperage,engine.getAmperageValue());
                ok=1;
            }
            else if (engine.getAmperageValue() > maxAmperage && !engineAlerts.contains("maxAmperage"))
            {
                engineAlerts = engineAlerts + "maxAmperage" + " ";
                alertMessage += String.format("The  power factor of engine number %d is ABOVE %.2f A maximum threshold! Current value: %.2f \n", engineNo,maxAmperage,engine.getAmperageValue());
                ok=1;
            }
            //flag - if anything exceeds only then we send a email!
            if(ok == 1)
            {
                sharedPreferences = getSharedPreferences("loginData", MODE_PRIVATE);
                String receiverEmail = sharedPreferences.getString("email","djkmata.djkmata@gmail.com");
                EmailCommunication emailCommunication = new EmailCommunication("ionelalexandru01@gmail.com","mfjhltkgndvfbksj",receiverEmail);
                emailCommunication.sendEmail(alertMessage);
                sendNotification(alertMessage);
            }

        }
        catch(Error e)
        {
            e.printStackTrace();
        }
        return engineAlerts;
    }

    //calculate minimum value of a measurement array
    public Double getCalculatedMinimumValue(ArrayList<String> valuesArray)
    {
        double minimumResult= 9999;
        for(String value : valuesArray)
        {
            double valueAsDouble = Double.parseDouble(value);
            if(valueAsDouble < minimumResult)
            {
                minimumResult=valueAsDouble;
            }
        }
        return minimumResult;
    }

    //calculate maximum value of a measurement array
    public Double getCalculatedMaximumValue(ArrayList<String> valuesArray)
    {
        double maximumResult= 0;
        for(String value : valuesArray)
        {
            double valueAsDouble = Double.parseDouble(value);
            if(valueAsDouble > maximumResult)
            {
                if(valueAsDouble <300) //because sometimes the sensor of temperature spikes to 999 when plugged in
                    maximumResult=valueAsDouble;
            }
        }
        return maximumResult;
    }

    //calculate average value of a measurement array
    public Double getCalculatedAverageValue(ArrayList<String> valuesArray)
    {
        double averageResult= 0;
        double totalSum=0;
        double totalSize=0;
        for(String value : valuesArray)
        {
            double valueAsDouble = Double.parseDouble(value);
            if(valueAsDouble <300) //because sometimes the sensor of temperature spikes to 999 when plugged in
            {
                totalSize++;
                totalSum+=valueAsDouble;
            }
        }
        averageResult=totalSum/totalSize;
        return averageResult;
    }

    // method to calculate the minimum, maximum and average values from history up to current for each measurement.
    public void setMeasurementsMinMaxAvg()
    {
        ArrayList<String> arrayValueTemperature= new ArrayList<>();
        ArrayList<String> arrayValuePower= new ArrayList<>();
        ArrayList<String> arrayValuePF= new ArrayList<>();
        ArrayList<String> arrayValueTension= new ArrayList<>();
        ArrayList<String> arrayValueAmperage= new ArrayList<>();
        for (int index=currentEngineUI; index<=14;index=index+3)    //use currentEngineUI to get the data only for the current displayed engine
        {
            ArrayList<String> engineValues = resultHistory.get(index);
            for(String data:engineValues)
            {
                String divideData[] = data.split(",\\s*") ;// in ordet to split by , and space
                if(index <3)
                {
                    arrayValueTemperature.add(divideData[2]);
                }
                else if(index >=3 && index<6 )
                {
                    arrayValuePower.add(divideData[2]);
                }
                else if(index >=6 && index <9)
                {
                    arrayValuePF.add(divideData[2]);
                }
                else if(index>=9 && index <12)
                {
                    arrayValueTension.add(divideData[2]);
                }
                else if(index>=12 && index <15)
                {
                    arrayValueAmperage.add(divideData[2]);
                }
            }
        }

        tvMinimumValueTemperature.setText(getCalculatedMinimumValue(arrayValueTemperature).toString());
        tvMaximumValueTemperature.setText(getCalculatedMaximumValue(arrayValueTemperature).toString());
        tvAverageValueTemperature.setText(String.format("%.2f", getCalculatedAverageValue(arrayValueTemperature)));
        tvMinimumValuePower.setText(getCalculatedMinimumValue(arrayValuePower).toString());
        tvMaximumValuePower.setText(getCalculatedMaximumValue(arrayValuePower).toString());
        tvAverageValuePower.setText(String.format("%.2f", getCalculatedAverageValue(arrayValuePower)));
        tvMinimumValueTension.setText(getCalculatedMinimumValue(arrayValueTension).toString());
        tvMaximumValueTension.setText(getCalculatedMaximumValue(arrayValueTension).toString());
        tvAverageValueTension.setText(String.format("%.2f", getCalculatedAverageValue(arrayValueTension)));
        tvMinimumValuePowerFactor.setText(getCalculatedMinimumValue(arrayValuePF).toString());
        tvMaximumValuePowerFactor.setText(getCalculatedMaximumValue(arrayValuePF).toString());
        tvAverageValuePowerFactor.setText(String.format("%.2f", getCalculatedAverageValue(arrayValuePF)));
        tvMinimumValueAmperage.setText(getCalculatedMinimumValue(arrayValueAmperage).toString());
        tvMaximumValueAmperage.setText(getCalculatedMaximumValue(arrayValueAmperage).toString());
        tvAverageValueAmperage.setText(String.format("%.2f", getCalculatedAverageValue(arrayValueAmperage)));
    }

    // func for update UI elements that will be called from dataChanged
    public void updateUI(ArrayList<ArrayList<String>> groupedEngines)
    {
        fetchLiveDataForEachEngine(groupedEngines);
        loadDataSeparatelyAndSaveItInResultHistoryEvenWhenNotInFocus();
        Engine engineAux;
        if(currentEngineUI == 0)
            engineAux = new Engine(engineOne);
        else if(currentEngineUI ==1)
            engineAux = new Engine(engineTwo);
        else
            engineAux = new Engine(engineThree);

        //update UI progresses
        tvNumericalTemperature.setText(String.format("%.2f °C", engineAux.getTemperatureValue()));
        if(engineAux.getTemperatureValue() > 31)
            tvNumericalTemperature.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));
        else
            tvNumericalTemperature.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));


        pbPower.setMinMax(0,300);   //Wattage interval from 0 to 300;
        pbPower.setProgress(engineAux.getPowerValue());
        if(engineAux.getPowerValue() >150 && engineAux.getPowerValue() <250)
            pbPower.setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_measurement));
        else if (engineAux.getPowerValue() >250)
            pbPower.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));
        else
            pbPower.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));

        pbPowerFactor.setMinMax(0,1);
        pbPowerFactor.setProgress(engineAux.getPowerFactorValue());
        if(engineAux.getPowerFactorValue() >70 && engineAux.getPowerFactorValue() <90 )
            pbPowerFactor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_measurement));
        else if (engineAux.getPowerFactorValue() <70 )
            pbPowerFactor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));
        else
            pbPowerFactor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));

        pbTension.setMinMax(0,400);
        pbTension.setProgress(engineAux.getTensionValue());
        if(engineAux.getTensionValue() >200 && engineAux.getTensionValue() <240 )
            pbTension.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));
        else if (engineAux.getTensionValue() >180 && engineAux.getTensionValue() <=200 )
            pbTension.setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_measurement));
        else
            pbTension.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));

        pbAmperage.setMinMax(0,1);
        pbAmperage.setProgress(engineAux.getAmperageValue());

        // methods to populate the graphs
        populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval[0],"Temperature",4,1);
        populateGraphHistory(graphPlotPower,0,200,currentEngineUI+3,graphInterval[1],"Power",20F,1);
        populateGraphHistory(graphPlotTension,0,500,currentEngineUI+9,graphInterval[2],"Tension",40F,1);
        populateGraphHistory(graphPlotAmperage,0,1,currentEngineUI+12,graphInterval[3],"Amperage", 0.1F,1);

        //method to populate the Measurement section (min,max,avg values)
        setMeasurementsMinMaxAvg();
    }

    //method for populating the engines with their values and send emails if they exceed the maximum threshold
    public void fetchLiveDataForEachEngine(ArrayList<ArrayList<String>> groupedEngines)
    {
        for( int engineNumber=0; engineNumber < groupedEngines.size(); engineNumber++)
        {

            ArrayList<String> displayEngine = groupedEngines.get(engineNumber);

            //get all the information for engine as a String
            String allData = String.valueOf(displayEngine);
            Log.w("Engine " + (engineNumber+1), allData);

            //get rid of the [ in the begining and ] at the end in order to get unaltered values
            String editedData = allData.substring(1,allData.length()-1);

            //divide the string in order to get the exact values for each measurement
            String divideData[] = editedData.split(",\\s*") ;// in ordet to split by , and space

            // Create 5 attributes for each measurement of an engine
            String temperatureTime = divideData[1];     //T
            Double temperatureValue = Double.parseDouble(divideData[2]) ;    //T
            String powerTime = divideData[4];           //P
            Double powerValue =  Double.parseDouble(divideData[5]);          //P
            String powerFactorTime = divideData[7];     //PF
            Double powerFactorValue = Double.parseDouble(divideData[8]);    //PF
            String tensionTime = divideData[10];        //V
            Double tensionValue = Double.parseDouble(divideData[11]);       //V
            String amperageTime = divideData[13];       //I
            Double amperageValue =  Double.parseDouble(divideData[14]);      //I

            if(engineNumber == 0)
            {
                engineOne = new Engine(temperatureTime,temperatureValue,powerTime,powerValue,powerFactorTime,powerFactorValue,tensionTime,tensionValue,amperageTime,amperageValue);
                engineOneAlertsSent=sendEmailsAlert(engineOne,1, engineOneAlertsSent);
            }
            else if(engineNumber == 1)
            {
                engineTwo = new Engine(temperatureTime,temperatureValue,powerTime,powerValue,powerFactorTime,powerFactorValue,tensionTime,tensionValue,amperageTime,amperageValue);
                engineTwoAlertsSent=sendEmailsAlert(engineTwo,2, engineTwoAlertsSent);
            }
            else if(engineNumber ==2)
            {
                engineThree = new Engine(temperatureTime,temperatureValue,powerTime,powerValue,powerFactorTime,powerFactorValue,tensionTime,tensionValue,amperageTime,amperageValue);
                engineThreeAlertsSent=sendEmailsAlert(engineThree,3, engineThreeAlertsSent);
            }
        }
    }


}

