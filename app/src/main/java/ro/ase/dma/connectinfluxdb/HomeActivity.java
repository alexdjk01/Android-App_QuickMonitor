package ro.ase.dma.connectinfluxdb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
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

// UNRELATED TO HOME ACTIVITY!!!!!!!!!!!!!!!!!!!!!!!!!
// DECI IN FUNCTIE DE DATELE PRIMITE DE LA INFLUX DB NE PUTEM DA SEAMA CATE MOTOARE TRANSMIT INFORMATII
// O SA CREEZ ATATEA OBIECTE DE TIP MOTOR CATE SUNT TRANSMISE SI VOI POPULA UN RECYCLER VIEW SAU CEVA DE GENU CU ACESTE MOTOARE SI VOI PERMITE SA LE
// ATINGI IN HOME ACTIVITY CA SA INTRII IN DASHBOARDUL FIECARUI MOTOR!

public class HomeActivity extends AppCompatActivity implements DataUpdateCallback {
    private BottomNavigationView bottomNavigation;
    private User receivedUserLogged = null;
    private InfluxDBContinuousFetcher influxDBContinuousFetcher= new InfluxDBContinuousFetcher();

    ArrayList<ArrayList<String>> groupedEngines = new ArrayList<>();
    ArrayList<ArrayList<String>> resultHistory = new ArrayList<>();

    Engine engineOne;
    Engine engineTwo;
    Engine engineThree;

    private Button btnEngine1;
    private Button btnEngine2;
    private Button btnEngine3;

    private TextView tvNumericalTemperature;

    private SemiCircularProgressBar pbPower;
    private SemiCircularProgressBar pbPowerFactor;

    private SemiCircularProgressBar pbTension;
    private SemiCircularProgressBar pbAmperage;

    private XYPlot graphPlotTemperature;

    private Spinner spinnerTimeTemperature;


    // global variable tht will keep track of which engine should be displayed!
    // default value is 1 to view engine no1
    private int currentEngineUI=2;
    public int graphInterval=9; //default shows ony 9 values

    //shared preferecnes

    SharedPreferences sharedPreferences;

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
        spinnerTimeTemperature = findViewById(R.id.spinnerTimeTemperature);

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
                populateGraphHistory(graphPlotTemperature,0,40,0,graphInterval); //default: show only the last 9 values taken in the graph
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
                populateGraphHistory(graphPlotTemperature,0,40,1,graphInterval);
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
                populateGraphHistory(graphPlotTemperature,0,40,2,graphInterval);
                updateUI(groupedEngines);
            }
        });

        //we need ArrayAdapted to populate a Spinner with the values in the string array situated in res/strings
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTimeTemperature.setAdapter(adapter);

       spinnerTimeTemperature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
               String clickedInterval = adapterView.getItemAtPosition(pos).toString();
               if(!resultHistory.isEmpty())
               {
                   if(clickedInterval.equals("1m")){
                       graphInterval=5;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
                   }
                   else if (clickedInterval.equals("2m")){
                       graphInterval=10;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
                   }
                   else if (clickedInterval.equals("3m")){
                       graphInterval=15;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
                   }
                   else if (clickedInterval.equals("10m")){
                       graphInterval=50;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
                   }
                   else if (clickedInterval.equals("30m")){
                       graphInterval=150;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
                   }
                   else if (clickedInterval.equals("1h")){
                       graphInterval=300;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
                   }
                   else if (clickedInterval.equals("3h")){
                       graphInterval=900;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
                   }
                   else if (clickedInterval.equals("24h")){
                       graphInterval=7200;
                       populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);
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




    public void graphPlotFunction(XYPlot plot, int lowerBoundry, int upperBoundry, ArrayList<String> domainLabels, Double[] seriesNumbers, int interval)
    {
        plot.clear();
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(seriesNumbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temperatures");

        //to show in focus only the last 10 elements
        int endIndex = series1.size()-1;
        int startIndex = Math.max(0,endIndex-interval);

        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                getApplicationContext().getColor(R.color.green_avocado),
                getApplicationContext().getColor(R.color.green_avocado),
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

    public void populateGraphHistory(XYPlot plot,int lowerBoundry,int upperBoundry,int index, int interval)
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
                graphPlotFunction(plot,lowerBoundry,upperBoundry,arrayTime, doubleArray, interval);
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
            // we need to work with the data inside this ArrayList.

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI(groupedEngines);
                }
            });

        }

    }

    //%d
    public void loadDataSeparatelyAndSaveItInResultHistoryEvenWhenNotInFocus(){
        resultHistory.get(0).add(String.format("T%d, %s, %f",0,engineOne.getTemperatureTime(),engineOne.getTemperatureValue()));
        resultHistory.get(1).add(String.format("T%d, %s, %f",1,engineTwo.getTemperatureTime(),engineTwo.getTemperatureValue()));
        resultHistory.get(2).add(String.format("T%d, %s, %f",2,engineThree.getTemperatureTime(),engineThree.getTemperatureValue()));
    }


    public void sendEmailsAlert(Double temperatureValue, Double powerValue, Double PFValue, Double tensionValue, Double amperageValue, int engineNo )
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

            if (temperatureValue < minTemperature)
            {
                alertMessage += String.format("The temperature of engine number %d is BELOW %.2f °C minimum threshold! \n", engineNo,minTemperature);
                ok=1;
            }
            else if (temperatureValue > maxTemperature)
            {
                alertMessage += String.format("The temperature of engine number %d is ABOVE %.2f °C maximum threshold! \n", engineNo,maxTemperature);
                ok=1;
            }

            if (powerValue < minPower)
            {
                alertMessage += String.format("The power (W) of engine number %d is BELOW %.2f °C minimum threshold! \n", engineNo,minPower);
                ok=1;
            }
            else if (powerValue > maxPower)
            {
                alertMessage += String.format("The  power (W) of engine number %d is ABOVE %.2f °C maximum threshold! \n", engineNo,maxPower);
                ok=1;
            }


            if (PFValue < minPowerFactor)
            {
                alertMessage += String.format("The power factor of engine number %d is BELOW %.2f °C minimum threshold! \n", engineNo,minPowerFactor);
                ok=1;
            }

            else if (PFValue > maxPowerFactor)
            {
                alertMessage += String.format("The  power factor of engine number %d is ABOVE %.2f °C maximum threshold! \n", engineNo,maxPowerFactor);
                ok=1;
            }


            if (tensionValue < minTension)
            {
                alertMessage += String.format("The power factor of engine number %d is BELOW %.2f °C minimum threshold! \n", engineNo,minTension);
                ok=1;
            }

            else if (tensionValue > maxTension)
            {
                alertMessage += String.format("The  power factor of engine number %d is ABOVE %.2f °C maximum threshold! \n", engineNo,maxTension);
                ok=1;
            }


            if (amperageValue < minAmperage)
            {
                alertMessage += String.format("The power factor of engine number %d is BELOW %.2f °C minimum threshold! \n", engineNo,minAmperage);
                ok=1;
            }

            else if (amperageValue > maxAmperage)
            {
                alertMessage += String.format("The  power factor of engine number %d is ABOVE %.2f °C maximum threshold! \n", engineNo,maxAmperage);
                ok=1;
            }


            //flag - if anything exceeds only then we send a email!
            if(ok == 1)
            {
                EmailCommunication emailCommunication = new EmailCommunication("ionelalexandru01@gmail.com","mfjhltkgndvfbksj","djkmata.djkmata@gmail.com");
                emailCommunication.sendEmail(alertMessage);
            }


        }
        catch(Error e)
        {
            e.printStackTrace();
        }




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

        //send emails
        //!!!!!!!!
        // Trimite prea DES emailuri!!!!
        //------- trb trimis mai rar nuj cum vedem!
        sendEmailsAlert(engineAux.getTemperatureValue(),engineAux.getPowerValue(),engineAux.getPowerFactorValue(),engineAux.getTensionValue(),engineAux.getAmperageValue(),currentEngineUI);

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

        populateGraphHistory(graphPlotTemperature,0,40,currentEngineUI,graphInterval);

    }


    //save direclty double not String
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

            //devide the string in order to get the exact values for each measurement
            String divideData[] = editedData.split(",\\s*") ;// in ordet to split by , and space

            // Create 5 atributes for each measurement of an engine
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
            }
            else if(engineNumber == 1)
            {
                engineTwo = new Engine(temperatureTime,temperatureValue,powerTime,powerValue,powerFactorTime,powerFactorValue,tensionTime,tensionValue,amperageTime,amperageValue);
            }
            else if(engineNumber ==2)
            {
                engineThree = new Engine(temperatureTime,temperatureValue,powerTime,powerValue,powerFactorTime,powerFactorValue,tensionTime,tensionValue,amperageTime,amperageValue);
            }


        }
    }


}

