package dk.izbrannick.glutter.toggledatatesttool;

import android.content.Context;
import android.net.ConnectivityManager;
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
import android.widget.EditText;
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
    private long delayMills = 6000;
    private int togglCount = 0;
    private String onOffTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);



        ctx = getApplicationContext();
        textView = (TextView) findViewById(R.id.log_text);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
