package com.openxc.openxcstarter;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.openxc.VehicleManager;
import com.openxc.measurements.FuelConsumed;
import com.openxc.measurements.FuelLevel;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.Odometer;
import com.openxc.measurements.SteeringWheelAngle;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.VehicleDoorStatus;
import com.openxc.measurements.VehicleDoorStatus.DoorId;
import com.openxc.remote.VehicleServiceException;
import com.openxc.units.State;

public class StarterActivity extends Activity {
    private static final String TAG = "StarterActivity";

    private VehicleManager mVehicleManager;
//    private TextView mEngineSpeedView;
    
    private TextView odometerValueLbl;
    private TextView engineSpeedCentralValueLbl;
    private TextView engineSpeedValueLbl;
    private TextView fuelConsumedValueLbl;
    private TextView fuelLevelValueLbl;
    private TextView steeringWheelAngleValueLbl;
//    private TextView vehicleDoorStatusValueLbl;
    
    private TextView driverDoorStatusValueLbl;
    private TextView passengerDoorStatusValueLbl;
    private TextView rearLeftDoorStatusValueLbl;
    private TextView rearRightDoorStatusValueLbl;
    
    private boolean serviceConnected = false;
    
    public boolean isServiceConnected(){
    	return serviceConnected;
    }
    
    public void setServiceConnected(boolean serviceConnected){
    	this.serviceConnected = serviceConnected;
    }
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        try{
        // grab a reference to the engine speed text object in the UI, so we can
        // manipulate its value later from Java code
//       	mEngineSpeedView = (TextView) findViewById(R.id.vehicle_speed);
       	
       	odometerValueLbl = (TextView)findViewById(R.id.odometerValueLbl);
       	engineSpeedCentralValueLbl = (TextView)findViewById(R.id.engineSpeedCentralValueLbl);
       	engineSpeedValueLbl = (TextView)findViewById(R.id.engineSpeedValueLbl);
        fuelConsumedValueLbl = (TextView)findViewById(R.id.fuelConsumedValueLbl);
        fuelLevelValueLbl = (TextView)findViewById(R.id.fuelLevelValueLbl);
        steeringWheelAngleValueLbl = (TextView)findViewById(R.id.steeringWheelAngleValueLbl);
//        vehicleDoorStatusValueLbl = (TextView)findViewById(R.id.vehicleDoorStatusValueLbl);
        
        driverDoorStatusValueLbl = (TextView)findViewById(R.id.driverDoorStatusValueLbl);
        passengerDoorStatusValueLbl = (TextView)findViewById(R.id.passengerDoorStatusValueLbl);
        rearLeftDoorStatusValueLbl = (TextView)findViewById(R.id.rearLeftDoorStatusValueLbl);
        rearRightDoorStatusValueLbl = (TextView)findViewById(R.id.rearRightDoorStatusValueLbl);
        }catch(Exception e){
        	Log.e(TAG, "onCreate(): "+e.getLocalizedMessage());
        	e.printStackTrace();
        }
        
    }

    @Override
    public void onPause() {
        super.onPause();
        // When the activity goes into the background or exits, we want to make
        // sure to unbind from the service to avoid leaking memory
        if(mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            try {
                // Remember to remove your listeners, in typical Android
                // fashion.
                mVehicleManager.removeListener(EngineSpeed.class, mSpeedListener);
            } catch (VehicleServiceException e) {
            	Log.e(TAG, "onPause(): "+e.getLocalizedMessage());
                e.printStackTrace();
            }
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // When the activity starts up or returns from the background,
        // re-connect to the VehicleManager so we can receive updates.
        try{
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        }catch(Exception e){
        	Log.e(TAG, "onResume():"+e.getLocalizedMessage());
        	e.printStackTrace();
        }
    }

    /* This is an OpenXC measurement listener object - the type is recognized
     * by the VehicleManager as something that can receive measurement updates.
     * Later in the file, we'll ask the VehicleManager to call the receive()
     * function here whenever a new EngineSpeed value arrives.
     */
    EngineSpeed.Listener mSpeedListener = new EngineSpeed.Listener() {
        public void receive(Measurement measurement) {
            // When we receive a new EngineSpeed value from the car, we want to
            // update the UI to display the new value. First we cast the generic
            // Measurement back to the type we know it to be, an EngineSpeed.
            final EngineSpeed speed = (EngineSpeed) measurement;
            // In order to modify the UI, we have to make sure the code is
            // running on the "UI thread" - Google around for this, it's an
            // important concept in Android.
            StarterActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    // Finally, we've got a new value and we're running on the
                    // UI thread - we set the text of the EngineSpeed view to
                    // the latest value
//                    mEngineSpeedView.setText("Engine speed (RPM): "
//                            + speed.getValue().doubleValue());
                	engineSpeedCentralValueLbl.setText(""+speed.getValue().doubleValue());
                    engineSpeedValueLbl.setText(""+speed.getValue().doubleValue());
                    
                }
            });
        }
    };
    
	Odometer.Listener mOdometerListener = new Odometer.Listener(){

		@Override
		public void receive(Measurement measurement) {
			final Odometer mOdometer = (Odometer)measurement;
			StarterActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					Log.i(TAG, "Odometer: "+mOdometer.getValue());
					odometerValueLbl.setText(mOdometer.getValue()+"");
				}
			});	
		}
	};
    
    FuelConsumed.Listener mFuelConsumedListener = new FuelConsumed.Listener(){

		@Override
		public void receive(Measurement measurement) {
			final FuelConsumed fuelConsumed = (FuelConsumed)measurement;
			StarterActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					//Log.i(TAG, ":My Fuel Consumption: "+fuelConsumed.getValue().doubleValue());
					fuelConsumedValueLbl.setText(String.format("%.2f",fuelConsumed.getValue().doubleValue()*100)+"%");
					
				}
			});
		}
	};
	
	FuelLevel.Listener mFuelLevelListener = new FuelLevel.Listener(){

		@Override
		public void receive(Measurement measurement) {
			final FuelLevel mFuelLevel = (FuelLevel)measurement;
			StarterActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					//Log.i(TAG, ":Fuel Level: "+mFuelLevel.getValue().doubleValue());
					fuelLevelValueLbl.setText(String.format("%.2f",mFuelLevel.getValue().doubleValue()*100)+"%");
				}
			});
		}
	};
	
	SteeringWheelAngle.Listener mSteeringWheelAngleListener = new SteeringWheelAngle.Listener(){

		@Override
		public void receive(Measurement measurement) {
			final SteeringWheelAngle mSteeringWheelAngle = (SteeringWheelAngle)measurement;
			StarterActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					Log.i(TAG, "Steering Wheel Angle: "+ mSteeringWheelAngle.getValue().doubleValue());
					steeringWheelAngleValueLbl.setText(String.format("%.2f",mSteeringWheelAngle.getValue().doubleValue())+"¼");
				}
			});
		}
	};
	
	VehicleDoorStatus.Listener mVehicleDoorStatusListener = new VehicleDoorStatus.Listener(){

		@Override
		public void receive(Measurement measurement) {
			final VehicleDoorStatus mVehicleDoorStatus = (VehicleDoorStatus)measurement;
			StarterActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					State<DoorId> tempState = mVehicleDoorStatus.getValue();
					Log.i(TAG, "Vehicle Door Status: "+mVehicleDoorStatus.getValue()+" Open: "+mVehicleDoorStatus.getEvent());
					
//extreme care here! you are using more than one if--else statement******************************					
					if(tempState.enumValue().equals(VehicleDoorStatus.DoorId.DRIVER))
						driverDoorStatusValueLbl.setText(mVehicleDoorStatus.getEvent()+"");
					else if(tempState.enumValue().equals(VehicleDoorStatus.DoorId.PASSENGER))
						passengerDoorStatusValueLbl.setText(mVehicleDoorStatus.getEvent()+"");
					else if(tempState.enumValue().equals(VehicleDoorStatus.DoorId.REAR_LEFT))
						rearLeftDoorStatusValueLbl.setText(mVehicleDoorStatus.getEvent()+"");
					else if(tempState.enumValue().equals(VehicleDoorStatus.DoorId.REAR_RIGHT))
						rearRightDoorStatusValueLbl.setText(mVehicleDoorStatus.getEvent()+"");
					
				}
			});
			
		}
		
	};
	

	


    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is established, i.e. bound.
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();
            setServiceConnected(true);
            // We want to receive updates whenever the EngineSpeed changes. We
            // have an EngineSpeed.Listener (see above, mSpeedListener) and here
            // we request that the VehicleManager call its receive() method
            // whenever the EngineSpeed changes
            try {
                mVehicleManager.addListener(EngineSpeed.class, mSpeedListener);
                mVehicleManager.addListener(FuelConsumed.class,mFuelConsumedListener);
                mVehicleManager.addListener(FuelLevel.class,mFuelLevelListener);
                mVehicleManager.addListener(VehicleDoorStatus.class,mVehicleDoorStatusListener);
                mVehicleManager.addListener(SteeringWheelAngle.class,mSteeringWheelAngleListener);
                mVehicleManager.addListener(Odometer.class,mOdometerListener);
            } catch (VehicleServiceException e) {
            	Log.e(TAG, "ServiceConnection(): 1:"+e.getLocalizedMessage());
                e.printStackTrace();
            } catch (UnrecognizedMeasurementTypeException e) {
            	Log.e(TAG, "ServiceConnection(): 2:"+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
            serviceConnected = false;
            mVehicleManager = null;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starter, menu);
        return true;
    }
}
