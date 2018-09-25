package learn.excitedeven.clock_weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class MainActivity extends Activity{

    SimpleDateFormat format_time,format_date,format_week;
    Date date;
    String str_time,str_date,str_week,str_json,str_weather,str_temp;
    JSONArray jsonArray;
    JSONObject jsonTemp;
    TextView txt_time,txt_date,txt_week,txt_weather,txt_temp;

    Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        refreshtime();
        refreshweather();
    }

    public void Init(){

        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        uiFlags |= 0x00001000;
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);

        //HeWeather Init
        HeConfig.init("HE1809250033051105", "f487216c67eb48179babb9be5c447595");
        HeConfig.switchToFreeServerNode();

        txt_time = findViewById(R.id.txt_time);
        txt_date = findViewById(R.id.txt_date);
        txt_week = findViewById(R.id.txt_week);
        txt_weather = findViewById(R.id.txt_weather);
        txt_temp = findViewById(R.id.txt_temp);
    }

    @SuppressLint("SimpleDateFormat")
    protected void refreshtime(){
        format_time = new SimpleDateFormat("HH:mm:ss");
        format_date = new SimpleDateFormat("yyyy-MM-dd");
        format_week = new SimpleDateFormat("E");

        date = new Date(System.currentTimeMillis());
        str_time = format_time.format(date);
        str_date = format_date.format(date);
        str_week = format_week.format(date);

        txt_time.setText(str_time);
        txt_date.setText(str_date);
        txt_week.setText(str_week);
    }

    protected void refreshweather(){
        HeWeather.getWeatherNow(this, "zhuzhou", Lang.CHINESE_SIMPLIFIED, Unit.METRIC,
                new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    //TODO 修改提示
                    public void onError(Throwable e) {
                        Log.i("Log", "onError: ", e);
                    }

                    @Override
                    public void onSuccess(List dataObject) {
                        str_json = gson.toJson(dataObject);
                        try {

                            jsonArray = new JSONArray(str_json);
                            jsonTemp = (JSONObject) ((JSONObject) jsonArray.get(0)).get("now");
                            str_weather = jsonTemp.getString("cond_txt");
                            str_temp = jsonTemp.getInt("tmp") + "℃";

                            txt_weather.setText(str_weather);
                            txt_temp.setText(str_temp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void onclick(View view){
        refreshtime();
        refreshweather();
    }
}
