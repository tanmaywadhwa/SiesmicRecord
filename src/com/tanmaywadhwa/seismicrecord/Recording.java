
package com.tanmaywadhwa.seismicrecord;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

public class Recording extends Activity implements SensorEventListener {
	
	public float x, y, z;
    public float calx, caly, calz;
    public static final String TAG="Seismic";
    private SensorManager mSensorManager;
    Sensor acceleration;
    TextView xTV;
    TextView yTV;
    TextView zTV;
    GraphicalView mChart;
    LinearLayout layout;
    XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    XYSeries xTimeSeries;
    XYSeries yTimeSeries;
    XYSeries zTimeSeries;
    XYSeriesRenderer xRenderer;
    XYSeriesRenderer yRenderer;
    XYSeriesRenderer zRenderer;
    int count=0;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        layout = (LinearLayout) findViewById(R.id.chart);
        xTV= (TextView)findViewById(R.id.textView1);
        yTV= (TextView)findViewById(R.id.textView2);
        zTV= (TextView)findViewById(R.id.textView3);
        mSensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acceleration=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        calx=-x;
        caly=-y;
        calz=-z;
        if(acceleration==null){
            Toast t=new Toast(this);
            t=Toast.makeText(this, "Required Sensor not Found!", Toast.LENGTH_LONG);
            t.show();
            System.exit(1);
        }
        Toast t=new Toast(this);
        t=Toast.makeText(this, "Found a sensor that can be used", Toast.LENGTH_LONG);
        t.show();
        
        
       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recording, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        //Log.i(TAG,""+event.values[0]);
    	if(calx==0.0 && caly==0.0 && calz==0.0){
            calx=-event.values[0];
            caly=-event.values[1];
            calz=-event.values[2];
        }


    	x=calx+event.values[0] + 0;
    	y=caly+event.values[1] + 5;
    	z=calz+event.values[2] - 5;




        count++;
        xTV.setText("x: "+x);
        xTimeSeries.add(count, x);
        yTV.setText("y:"+(y-5));
        yTimeSeries.add(count, y);
        zTV.setText("z:"+(z+5));
        zTimeSeries.add(count, z);
        
        mRenderer.setXAxisMin(count-250);
        mRenderer.setXAxisMax(count);
        

        mChart.repaint();
        
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
        Log.i(TAG,"accuracyChanged");
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mSensorManager.registerListener(this, acceleration, SensorManager.SENSOR_DELAY_UI);
        
        //// Taking calibration values
        calx=(float)0.0;
        caly=(float)0.0;
        calz=(float)0.0;
        
        if (mChart == null) {
            initChart();
            addSampleData();
            mChart = ChartFactory.getCubeLineChartView(this, mDataset, mRenderer, 0.3f);
            layout.addView(mChart);

        } else {
            mChart.repaint();
            //one.execute(y);
        }
    }
    
    private void initChart() {
      //initialize the chart
        xTimeSeries = new XYSeries("x");
        yTimeSeries = new XYSeries("y");
        zTimeSeries = new XYSeries("z");
        int margins[] = new int[] {2,2,2,2};
        mDataset.addSeries(xTimeSeries);
        mDataset.addSeries(yTimeSeries);
        mDataset.addSeries(zTimeSeries);
        xRenderer = new XYSeriesRenderer();
        yRenderer = new XYSeriesRenderer();
        zRenderer = new XYSeriesRenderer();
        xRenderer.setColor(Color.DKGRAY);
        yRenderer.setColor(Color.BLUE);
        zRenderer.setColor(Color.RED);
        
        mRenderer.addSeriesRenderer(xRenderer);
        mRenderer.addSeriesRenderer(yRenderer);
        mRenderer.addSeriesRenderer(zRenderer);
        mRenderer.setZoomEnabled(false,true);
        mRenderer.setPanEnabled(true, false);
        mRenderer.setClickEnabled(false);
        mRenderer.setChartTitle("Graphical Motion Representation");
        mRenderer.setYAxisMin(-10);
        mRenderer.setYAxisMax(10);
        mRenderer.setLabelsTextSize(20);
        mRenderer.setMargins(margins);
        mRenderer.setLegendHeight(1);

    }
    
    private void addSampleData() {
        //adding initial data to the chart!
        
        

    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        // Unregistering the sensor while the app is paused.
        mSensorManager.unregisterListener(this);
        super.onPause();
    }
}
