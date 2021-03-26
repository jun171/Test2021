package tw.tcnr22.m0502

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class M0502 : AppCompatActivity() {
    private val e001: EditText? = null
    private var b001: Button? = null
    private val s001: Spinner? = null
    private var f000: TextView? = null
    private val sSex: String? = null
    private var rb01: RadioGroup? = null
    private var rb02: RadioGroup? = null
    private var rb021: RadioButton? = null
    private var rb022: RadioButton? = null
    private var rb023: RadioButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.m0502)
        setupViewcomponent()
    }

    private fun setupViewcomponent() {

//        e001 = (EditText) findViewById(R.id.m0502_e001);
//        s001 = (Spinner) findViewById(R.id.m0502_s001);
        b001 = findViewById<View>(R.id.m0502_b001) as Button
        f000 = findViewById<View>(R.id.m0500_f000) as TextView
        rb01 = findViewById<View>(R.id.m0502_r001) as RadioGroup
        rb02 = findViewById<View>(R.id.m0502_r002) as RadioGroup
        rb021 = findViewById<View>(R.id.m0502_r002a) as RadioButton
        rb022 = findViewById<View>(R.id.m0502_r002b) as RadioButton
        rb023 = findViewById<View>(R.id.m0502_r002c) as RadioButton
        rb01!!.setOnCheckedChangeListener(rb01ON)
        b001!!.setOnClickListener(b001ON)
    }

    private val rb01ON =
        RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.m0502_r001_a -> {
                    rb021!!.setText(R.string.m0502_r002aa)
                    rb022!!.setText(R.string.m0502_r002ab)
                    rb023!!.setText(R.string.m0502_r002ac)
                }
                R.id.m0502_r001_b -> {
                    rb021!!.setText(R.string.m0502_r002ba)
                    rb022!!.setText(R.string.m0502_r002bb)
                    rb023!!.setText(R.string.m0502_r002bc)
                }
            }
        }

    //    private View.OnClickListener b001ON = new View.OnClickListener(){
    //        @Override
    //        public void onClick(View v) {
    //
    //        }
    //    };
    //=====================================================================
    //    private Spinner.OnItemSelectedListener s001ON = new Spinner.OnItemSelectedListener() {
    //
    //
    //        @Override
    //        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    //            sSex = parent.getSelectedItem().toString();
    //        }
    //
    //        @Override
    //        public void onNothingSelected(AdapterView<?> parent) {
    //
    //        }
    //    };
    //=====================================================================
    private val b001ON: View.OnClickListener = object : View.OnClickListener {
        private val iAge = 0
        override fun onClick(v: View) {

            //String strSug = getString(R.string.m0502_f000);
            var strSug = getString(R.string.m0502_f000)
            val r01chk = rb01!!.checkedRadioButtonId
            when (r01chk) {
                R.id.m0502_r001_a -> strSug += when (rb02!!.checkedRadioButtonId) {
                    R.id.m0502_r002a -> getString(R.string.m0502_f001)
                    R.id.m0502_r002b -> getString(R.string.m0502_f002)
                    else -> getString(R.string.m0502_f003)
                }
                R.id.m0502_r001_b -> strSug += when (rb02!!.checkedRadioButtonId) {
                    R.id.m0502_r002a -> getString(R.string.m0502_f004)
                    R.id.m0502_r002b -> getString(R.string.m0502_f005)
                    else -> getString(R.string.m0502_f006)
                }
            }
            f000!!.text = strSug //請勿輸入空白
        }
    }
}