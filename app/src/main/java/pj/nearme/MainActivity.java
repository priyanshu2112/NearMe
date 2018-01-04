package pj.nearme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
EditText editText;RadioButton rb;RadioGroup rbg;Button btn;String name,sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        ActionBar t = getSupportActionBar();
        t.setLogo(R.drawable.ic_icon);
        t.setDisplayUseLogoEnabled(true);
        t.setDisplayShowHomeEnabled(true);
        
        int off = 0;
        try {
            off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if(off==0) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                final String message = "Please enable GPS to continue.";

                builder.setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
                builder.create().show();
                if(!sharedPreferences.getString("name","NOT_PRESENT").equals("NOT_PRESENT"))
                {
                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    name = sharedPreferences.getString("name","NOT_PRESENT");
                    sex = sharedPreferences.getString("sex","NOT_PRESENT");
                    i.putExtra("name",name);
                    i.putExtra("sex",sex);
                    startActivity(i);
                }
            }
            else
            {
                if(!sharedPreferences.getString("name","NOT_PRESENT").equals("NOT_PRESENT"))
                {
                    Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    name = sharedPreferences.getString("name","NOT_PRESENT");
                    sex = sharedPreferences.getString("sex","NOT_PRESENT");
                    i.putExtra("name",name);
                    i.putExtra("sex",sex);
                    startActivity(i);
                }
            }

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace(); }

        sex="N";
        editText = (EditText) findViewById(R.id.et_name);
        rbg = (RadioGroup) findViewById(R.id.r_group);
        btn = (Button) findViewById(R.id.btn_in);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editText.getText().toString();
                rb = (RadioButton) findViewById(rbg.getCheckedRadioButtonId());
                if(rb.getText().toString().equals("Male"))
                    sex="M";
                else if(rb.getText().toString().equals("Female"))
                    sex="F";
                else
                    sex="N";

                if(!name.equals("") & !sex.equals("N"))
                {Intent i = new Intent(MainActivity.this, MapsActivity.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name",name);
                    editor.putString("sex",sex);
                    editor.commit();
                    i.putExtra("name",name);
                    i.putExtra("sex",sex);
                    //Toast.makeText(MainActivity.this,name+"="+sex, Toast.LENGTH_SHORT).show();
                    startActivity(i);

                        }
                else
                {
                    Toast.makeText(MainActivity.this,"Please enter correct details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
