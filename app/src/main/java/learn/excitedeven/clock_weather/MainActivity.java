package learn.excitedeven.clock_weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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

    SimpleDateFormat format_time,format_date,format_week;//系统时间，日期，星期
    Date date;

    String str_time,str_date,str_week,str_weather,str_temp;//字符串格式 时间，日期，星期，天气，温度
    TextView txt_time,txt_date,txt_week,txt_weather,txt_temp;//时间，日期，星期，天气，温度的文本框

    Gson gson = new Gson();
    JSONArray jsonArray;//从和风天气获取到的天气jsonArray
    JSONObject jsonTemp;//获取出来的即时天气“now”json

    Handler timeHandler =new Handler();//每隔0.5S刷新一次UI

    static int count = 0;//计数确定一小时刷新天气

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        refreshweather();
        myrunnbale();
    }
    /*
    全屏
    使用的SYSTEM_UI_FLAG_IMMERSIVE_STICKY官方称为粘性沉浸式模式。View.SYSTEM_UI_FLAG_IMMERSIVE为非粘性沉浸式，非粘性沉浸式状态栏和导航栏显示后不再自动隐藏。
	注意：onWindowFocusChanged方法里面的代码不能直接写在onCreate方法中，如果这么写当应用处于后台Stoped状态，下次启动应用便执行不到处理沉浸式的代码。Activity生命周期中，真正的visible时间点是onWindowFocusChanged()函数被执行时，所以我们将这段代码写在这个方法中。

	作者：dingyx
    链接：http://www.jianshu.com/p/3dc25a1ccb78
    來源：简书
    著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View mDecorView = getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }



    public void Init(){

        //和风天气初始化,使用和风天气SDK,详见https://www.heweather.com/documents/sdk/android
        HeConfig.init("HE1809250033051105", "f487216c67eb48179babb9be5c447595");//和风天气ID,Key.免费个人,每天1000访问次数
        HeConfig.switchToFreeServerNode();//切换免费服务器

        //初始化文本框
        txt_time = findViewById(R.id.txt_time);
        txt_date = findViewById(R.id.txt_date);
        txt_week = findViewById(R.id.txt_week);
        txt_weather = findViewById(R.id.txt_weather);
        txt_temp = findViewById(R.id.txt_temp);
    }

    @SuppressLint("SimpleDateFormat")
    /*
    刷新时间
     */
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

    /*
    刷新天气
     */
    protected void refreshweather(){
        //详见和风天气SDK文档:https://www.heweather.com/documents/sdk/android
        HeWeather.getWeatherNow(this, "zhuzhou", Lang.CHINESE_SIMPLIFIED, Unit.METRIC,//第二位可直接填代码,拼音。自用填株洲
                new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    //TODO 修改提示
                    public void onError(Throwable e) {
                        Log.i("Log", "onError: ", e);
                    }

                    @Override
                    public void onSuccess(List dataObject) {

                        try {
                            jsonArray = new JSONArray(gson.toJson(dataObject));//使用tojson获取dataObject数据(详见和风天气SDK),获取到的和风天气数据为JSONArray
                            jsonTemp = (JSONObject) ((JSONObject) jsonArray.get(0)).get("now");//第0个为"now",读取到jsonTemp
                            str_weather = jsonTemp.getString("cond_txt");//cond_txt,天气
                            str_temp = jsonTemp.getInt("tmp") + "℃";//tmp,温度

                            txt_weather.setText(str_weather);
                            txt_temp.setText(str_temp);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    //FIXME 测试用按钮记得去掉
    public void onclick(View view){
        refreshtime();
        refreshweather();
    }

    /*
    主线程调用,定时刷新UI
    使用handler的PostDelayed方法
     */
    protected void myrunnbale(){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                count++;
                //TODO 去掉Log
//                Log.i("Log", "" + count);
                if (count >= 7200)//每7200 * 0.5s = 3600s = 1h刷新一次天气
                {
                    refreshweather();
                    count = 0;
                }
                refreshtime();
                timeHandler.postDelayed(this, 500);//延时500ms一次
            }
        };
        timeHandler.post(r);
    }
}