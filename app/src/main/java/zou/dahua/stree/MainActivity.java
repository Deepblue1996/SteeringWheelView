package zou.dahua.stree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SteeringWheelView steeringWheelView = findViewById(R.id.steeringWheelView);

        steeringWheelView.setWheelTouch(new WheelTouch() {
            @Override
            public void upperTurn() {
                Log.i("WheelTouch", "upperTurn");
            }

            @Override
            public void lowerTurn() {
                Log.i("WheelTouch", "lowerTurn");
            }

            @Override
            public void leftTurn() {
                Log.i("WheelTouch", "leftTurn");
            }

            @Override
            public void rightTurn() {
                Log.i("WheelTouch", "rightTurn");

            }

            @Override
            public void MTurn() {
                Log.i("WheelTouch", "MTurn");
            }
        });
    }
}
