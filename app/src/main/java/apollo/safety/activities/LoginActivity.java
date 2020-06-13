package apollo.safety.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import apollo.safety.R;

public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText name;
    SharedPreferences preferences, sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        name = findViewById(R.id.loginName);

        //if sharedPreference has value skip to MainActivity
        try {
            sharedPreferences = getSharedPreferences("apollo.safety.USERNAME", MODE_PRIVATE);
            final String username = sharedPreferences.getString("Username", "");
            if (username.length() > 0) {
                name.setText(username);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                login = findViewById(R.id.buttonLogin);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userName = name.getText().toString().trim();
                               preferences = getSharedPreferences("apollo.safety.USERNAME", MODE_PRIVATE);
                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putString("Username", userName);
                               editor.apply();
                        if (userName.length() > 0) {

                            //if phone is running below Marshmallow
                            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));

                            else{
                                checkPermission();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Enter name please", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    public void checkPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.SEND_SMS
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {

                //if all permission granted go to MainActivity
                if (report.areAllPermissionsGranted()) {
                    Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                //if any permission denied go to Settings
                if (report.isAnyPermissionPermanentlyDenied()) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();
            }
        });
    }
}
