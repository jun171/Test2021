package tw.tcnr22.m0500f

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

//import androidx.appcompat.app.AppCompatActivity;


class M0500 : AppCompatActivity() {
    private var e001: EditText? = null
    private var b001: Button? = null
    private var t003: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.m0500d);
        //setupViewcomponent();
        setContentView(R.layout.m0500)
        setupViewcomponent()
    }

    private fun setupViewcomponent() {
        e001 = findViewById<View>(R.id.m0500_e001) as EditText
        b001 = findViewById<View>(R.id.m0500_b001) as Button
        t003 = findViewById<View>(R.id.m0500_t003) as TextView
        b001!!.setOnClickListener(b001ON)
    }

    private val b001ON = View.OnClickListener {
        val pondsFormat = DecimalFormat("0.0000")
        val outcomp = pondsFormat.format(e001!!.text.toString().toFloat() * 28.6)
        t003!!.text = outcomp
    } //    //--------------------------
    //    private void setupViewcomponent() {
    //        //設定layout配置
    //        e001 = (EditText) findViewById(R.id.m0500_e001);  //公斤輸入欄位
    //        b001 = (Button) findViewById(R.id.m0500_b001);  //執行按鈕變數名稱
    //        t003 = (TextView) findViewById(R.id.m0500_t003);  //輸出磅
    //        b001.setOnClickListener(b001ON);  //監聽按鈕程式
    //    }
    //
    //    private View.OnClickListener b001ON = new View.OnClickListener() {
    //
    //        @Override
    //        public void onClick(View v) {
    //            DecimalFormat pondsFormat = new DecimalFormat("0.0000");  //輸出格式
    //            String outcomp = pondsFormat.format(Float.parseFloat(e001.getText().toString()) * 2.20462262); //物件轉字串
    //            t003.setText(outcomp);
    //        }
    //    };
    ////--------------------------
    //    private View.OnClickListener b001ON=new View.OnClickListener(){     //監聽按鈕程式
    //
    //        @Override
    //        public void onClick(View v) {
    //
    //
    //        }
    //    };
    //    @Override
    //    protected void onCreate(Bundle savedInstanceState) {
    //        super.onCreate(savedInstanceState);
    //        setContentView(R.layout.m0500d);
    //    }
}