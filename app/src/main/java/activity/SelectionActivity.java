package activity;

/**
 * Created by pram on 11/1/2016.
 */

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.pk.feedme.R;

public class SelectionActivity extends Activity {
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_selection.xml
        setContentView(R.layout.activity_selection);

        // Locate the button in activity_selection.xml
        button = (Button) findViewById(R.id.btnStudent);

        // Capture button clicks
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                // Start LoginActivity.class for Student
                Intent myIntent = new Intent(SelectionActivity.this,
                        LoginActivityStudent.class);
                startActivity(myIntent);
            }
        });

        button = (Button) findViewById(R.id.btnLecturer);

        // Capture button clicks
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                // Start LoginActivity.class for Lecturer
                Intent myIntent = new Intent(SelectionActivity.this,
                        LoginActivityLecturer.class);
                startActivity(myIntent);
            }
        });
    }


}
