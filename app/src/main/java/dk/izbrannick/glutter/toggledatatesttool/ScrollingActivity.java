package dk.izbrannick.glutter.toggledatatesttool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScrollingActivity extends AppCompatActivity {

    private Context ctx;
    private boolean dataToggle = false;
    private boolean isONOFF = false;
    private Runnable runnable = null;
    private Handler handler;
    private TextView textView;
    private EditText editText;
    private long delayMills = 30000;
    private int togglCount = 0;
    private String onOffTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        ctx = getApplicationContext();
        textView = (TextView) findViewById(R.id.log_text);


        Spinner spinner = (Spinner) findViewById(R.id.minutes_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.minuntes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){
                    case 0:
                        delayMills = 30000;
                        break;
                    case 1:
                        delayMills = 60000;
                        break;
                    case 2:
                        delayMills = 120000;
                        break;
                }

                Snackbar.make(view, "Selected" +i+" minutes", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // turn wifi tathering ON
                setWifiTetheringEnabled(true);

                isONOFF = !isONOFF;

                if (isONOFF) {
                    Snackbar.make(view, "Data Toggle is "+isONOFF, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    run();
                }else
                {
                    Snackbar.make(view, "Data Toggle is "+isONOFF, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    runnable = null;
                }
            }
        });
    }

    private void toggleData()
    {
        dataToggle = !dataToggle;
        onOffTxt = dataToggle ? "ON" : "OFF";

        try {
            delayMills = Long.valueOf(String.valueOf(editText.getText()));
        }catch (Exception e)
        {
            Log.e("Delay", "Error catch delayMills = " + delayMills);
        }

        setMobileDataEnabled(dataToggle);
        setMobileDataEnabled22(dataToggle);

        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy/HH:mm:ss");
        String format = s.format(new Date());

        textView.append("\n" + togglCount++ +" - "  + "|" + onOffTxt + "| "  + format);

        Toast.makeText(ctx, String.valueOf(dataToggle), Toast.LENGTH_LONG).show();
    }

    private void setMobileDataEnabled22(boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setMobileDataEnabled(boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class<?> conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void setWifiTetheringEnabled(boolean enable) {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, null, enable);
                } catch (Exception ex) {
                }
                break;
            }
        }
    }

    private void run()
    {
        if(runnable != null)
            handler.removeCallbacks(runnable);

        if(handler == null) {
            handler = new Handler();

            runnable = new Runnable() {
                public void run() {
                    toggleData();
                    handler.postDelayed(this, delayMills ); // now is every 1 minutes
                }
            };

            handler.postDelayed(runnable , 3300); // Every 120000 ms (2 minutes)
        }

    }
}
