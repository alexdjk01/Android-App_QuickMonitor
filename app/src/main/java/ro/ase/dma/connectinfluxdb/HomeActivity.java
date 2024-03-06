package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

// UNRELATED TO HOME ACTIVITY!!!!!!!!!!!!!!!!!!!!!!!!!
// DECI IN FUNCTIE DE DATELE PRIMITE DE LA INFLUX DB NE PUTEM DA SEAMA CATE MOTOARE TRANSMIT INFORMATII
// O SA CREEZ ATATEA OBIECTE DE TIP MOTOR CATE SUNT TRANSMISE SI VOI POPULA UN RECYCLER VIEW SAU CEVA DE GENU CU ACESTE MOTOARE SI VOI PERMITE SA LE
// ATINGI IN HOME ACTIVITY CA SA INTRII IN DASHBOARDUL FIECARUI MOTOR!
//commit

public class HomeActivity extends AppCompatActivity implements DataUpdateCallback {
    private BottomNavigationView bottomNavigation;
    private User receivedUserLogged = null;
    private InfluxDBContinuousFetcher influxDBContinuousFetcher= new InfluxDBContinuousFetcher();

    ArrayList<ArrayList<String>> groupedEngines = new ArrayList<>();
    ArrayList<ArrayList<String>> resultHistory = new ArrayList<>();

    private ArrayList<String> timeLabelsTemperature = new ArrayList<>();
    private ArrayList<Double> seriesValuesTemperature = new ArrayList<>();

    private Button btnEngine1;
    private Button btnEngine2;
    private Button btnEngine3;
    private TextView tvNumericalTemperature;

    private SemiCircularProgressBar pbPower;
    private SemiCircularProgressBar pbPowerFactor;

    private SemiCircularProgressBar pbTension;
    private SemiCircularProgressBar pbAmperage;

    private XYPlot graphPlotTemperature;


    // global variable tht will keep track of which engine should be displayed!
    // default value is 1 to view engine no1
    private int currentEngineUI=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        influxDBContinuousFetcher.setDataChanged(this);
        influxDBContinuousFetcher.fetchContinuousData();

        btnEngine1 = findViewById(R.id.btnEngine1);
        btnEngine2 = findViewById(R.id.btnEngine2);
        btnEngine3 = findViewById(R.id.btnEngine3);
        tvNumericalTemperature = findViewById(R.id.tvNumericalTemperature);
        pbPower = findViewById(R.id.pbPower);
        pbPowerFactor = findViewById(R.id.pbPowerFactor);
        pbTension = findViewById(R.id.pbTension);
        pbAmperage = findViewById(R.id.pbAmperage);

        graphPlotTemperature = findViewById(R.id.graphPlot);

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
                populateGraphHistory(graphPlotTemperature,0,40,0);
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
                populateGraphHistory(graphPlotTemperature,0,40,1);
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
                populateGraphHistory(graphPlotTemperature,0,40,2);
                updateUI(groupedEngines);
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
    public void graphPlotFunction(XYPlot plot, int lowerBoundry, int upperBoundry, ArrayList<String> domainLabels, Double[] seriesNumbers)
    {
        plot.clear();
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(seriesNumbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temperatures");

        //to show in focus only the last 10 elements
        int endIndex = series1.size()-1;
        int startIndex = Math.max(0,endIndex-9);

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                getApplicationContext().getColor(R.color.green_avocado),
                getApplicationContext().getColor(R.color.red_measurement),
                getApplicationContext().getColor(R.color.blue_ediText),
                null);

        plot.addSeries(series1, series1Format);
        plot.setRangeBoundaries(lowerBoundry, upperBoundry, BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 4);

        plot.setDomainBoundaries(startIndex,endIndex,BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL,1);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition) {
                int index = Math.round(((Number) obj).floatValue());
                if (index >= 0 && index < domainLabels.size()) {
                    return stringBuffer.append(domainLabels.get(index));
                } else {
                    return stringBuffer;
                }
            }

            @Override
            public Object parseObject(String s, ParsePosition parsePosition) {
                return null;
            }
        });

        PanZoom.attach(plot);

    }

    public void populateGraphHistory(XYPlot plot,int lowerBoundry,int upperBoundry,int index)
    {
        ArrayList<String> arrayTime = new ArrayList<>();
        ArrayList<String> arrayValue= new ArrayList<>();
        ArrayList<String> engineValues = resultHistory.get(index);
        for(String data:engineValues)
        {
            String divideData[] = data.split(",\\s*") ;// in ordet to split by , and space
            String timePart = divideData[1].substring(divideData[1].indexOf('T') + 1, divideData[1].indexOf('.'));
            arrayTime.add(timePart);
            arrayValue.add(divideData[2]);
        }
        Log.w("Time", String.valueOf(arrayTime));
        Log.w("Value", String.valueOf(arrayValue));


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
                graphPlotFunction(plot,lowerBoundry,upperBoundry,arrayTime, doubleArray);
                plot.redraw();
            }
        });
    }

    public void splitValuesHistory(ArrayList<String> dataArray)
    {
        ArrayList<String> T0 = new ArrayList<>(); //0   -- index in resultHistory
        ArrayList<String> T1 = new ArrayList<>();   //1
        ArrayList<String> T2= new ArrayList<>();    //2
//        ArrayList<String> P_testem3_0= null;      //3
//        ArrayList<String> P_testem3_1= null;      //4
//        ArrayList<String> P_testem3_2= null;      //5
//        ArrayList<String> PF_0= null;                //6
//        ArrayList<String> PF_1= null;             //7
//        ArrayList<String> PF_2= null;             //8
//        ArrayList<String> V_0= null;              //9
//        ArrayList<String> V_1= null;              //10
//        ArrayList<String> V_2= null;              //11
//        ArrayList<String> I_0= null;              //12
//        ArrayList<String> I_1= null;              //13
//        ArrayList<String> I_2= null;              //14

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
        }

        Log.w("Graph History T0", String.valueOf(T0));
        Log.w("Graph History T1", String.valueOf(T1));
        Log.w("Graph History T2", String.valueOf(T2));

        resultHistory.add(T0);
        resultHistory.add(T1);
        resultHistory.add(T2);
    }

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
            // we need to work with the data inside this ArrayList
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI(groupedEngines);
                }
            });



        }

    }

    // func for update UI elements that will be called from dataChanged
    public void updateUI(ArrayList<ArrayList<String>> groupedEngines)
    {

        for( int engineNumber=0; engineNumber < groupedEngines.size(); engineNumber++)
        {
            if(currentEngineUI == engineNumber)
            {
                ArrayList<String> displayEngine = groupedEngines.get(engineNumber);

                //get all the information for engine as a String
                String allData = String.valueOf(displayEngine);
                Log.w("Engine " + (engineNumber+1), allData);

                //get rid of the [ in the begining and ] at the end in order to get unaltered values
                String editedData = allData.substring(1,allData.length()-1);

                //devide the string in order to get the exact values for each measurement
                String divideData[] = editedData.split(",\\s*") ;// in ordet to split by , and space

                // Create 5 atributes for each measurement of an engine
                String temperatureTime = divideData[1];     //T
                String temperatureValue = String.format("%.2f", Double.parseDouble(divideData[2])) ;    //T
                String powerTime = divideData[4];           //P
                String powerValue = String.format("%.2f", Double.parseDouble(divideData[5]));          //P
                String powerFactorTime = divideData[7];     //PF
                String powerFactorValue = String.format("%.2f", Double.parseDouble(divideData[8]));    //PF
                String tensionTime = divideData[10];        //V
                String tensionValue = String.format("%.2f", Double.parseDouble(divideData[11]));       //V
                String amperageTime = divideData[13];       //I
                String amperageValue = String.format("%.2f", Double.parseDouble(divideData[14]));      //I


                //update UI progresses
                tvNumericalTemperature.setText(temperatureValue + " °C");
                if(Double.parseDouble(temperatureValue) > 31)
                    tvNumericalTemperature.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));
                else
                    tvNumericalTemperature.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));

                resultHistory.get(currentEngineUI).add(String.format("T%d, %s, %s",currentEngineUI,temperatureTime,temperatureValue));

                pbPower.setMinMax(0,300);   //Wattage interval from 0 to 300;
                pbPower.setProgress(Double.parseDouble(powerValue));
                if(Double.parseDouble(powerValue) >150 && Double.parseDouble(powerValue) <250)
                    pbPower.setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_measurement));
                else if (Double.parseDouble(powerValue) >250)
                    pbPower.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));
                else
                    pbPower.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));

                pbPowerFactor.setMinMax(0,1);
                pbPowerFactor.setProgress(Double.parseDouble(powerFactorValue));
                if(Double.parseDouble(powerFactorValue) >70 && Double.parseDouble(powerFactorValue) <90 )
                    pbPowerFactor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_measurement));
                else if (Double.parseDouble(powerFactorValue) <70 )
                    pbPowerFactor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));
                else
                    pbPowerFactor.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));

                pbTension.setMinMax(0,400);
                pbTension.setProgress(Double.parseDouble(tensionValue));
                if(Double.parseDouble(tensionValue) >200 && Double.parseDouble(tensionValue) <240 )
                    pbTension.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green_measurement));
                else if (Double.parseDouble(tensionValue) >180 && Double.parseDouble(tensionValue) <=200 )
                    pbTension.setColor(ContextCompat.getColor(getApplicationContext(), R.color.yellow_measurement));
                else
                    pbTension.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red_measurement));

                pbAmperage.setMinMax(0,1);
                pbAmperage.setProgress(Double.parseDouble(amperageValue));

                populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI);



            }

        }
    }


}

