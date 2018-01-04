package pj.nearme;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Priyanshu on 30-12-2017.
 */

public class Send_Loc extends AsyncTask<String, Void, String> {

    Context context;
    String server;
    ProgressDialog progDailog;


    public Send_Loc(Context context)
    {
        this.context = context;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDailog = new ProgressDialog(context);
        progDailog.setMessage("Applying");
        progDailog.setIndeterminate(true);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();
    }

    @Override
    protected String doInBackground(String... params) {

        server = context.getString(R.string.server_url);

        String data, link, result;
        BufferedReader bufferedReader;
        try
        {
            link = server+"signup.php";
            String name,sex,lat,lng;
            name = params[0];
            sex = params[1];
            lat = params[2];
            lng = params[3];

            data = "?"+URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name,"UTF-8");
            data += "&" + URLEncoder.encode("sex", "UTF-8") + "=" + URLEncoder.encode(sex, "UTF-8");
            data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8");
            data += "&" + URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");
            link += data;
            URL url = new URL(link);
            Log.i("url",link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);



            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            result = bufferedReader.readLine();
            bufferedReader.close();
            return result;
        }
        catch (Exception ex)
        {
            //Toast.makeText(context,"Contact developer:"+ex.getMessage(),Toast.LENGTH_LONG).show();
            return new String("Exception : "+ex.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String s) {
        String[] name,sex,lat,lng;
        super.onPostExecute(s);
        progDailog.dismiss();
        Toast.makeText(context,s+"", Toast.LENGTH_SHORT).show();
        String res = s;
        JSONObject jsonObject;
        if(res != null)
        {
            try {
                jsonObject = new JSONObject(res);
                JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                name = new String[jsonArray.length()];
                sex = new String[jsonArray.length()];
                lat = new String[jsonArray.length()];
                lng = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    name[i] = jsonArray.getJSONObject(i).getString("name");
                    sex[i] = jsonArray.getJSONObject(i).getString("sex");
                    lat[i] = jsonArray.getJSONObject(i).getString("lat");
                    lng[i] = jsonArray.getJSONObject(i).getString("lng");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}
