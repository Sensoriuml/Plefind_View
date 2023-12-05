package com.android.plefind.plefindview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static java.util.Arrays.asList;

public class CloudFragment extends Fragment {

    private int width, height;

    private Context context;
    private RecyclerView recyclerView;
    private RelativeLayout mainlayout1, mainlayout2;
    public static FloatingActionButton select;

    private Spinner jspinner;
    private RadioGroup radioGroupSex, radioGroupLung, radioGroupPleeff, radioGroupThoraco;
    private RadioButton rbFemale, rbMale, rbLeft, rbRight, rbPEyes, rbPEno, rbThoraYes, rbThoraNo;
    private EditText notesTxt, heightTxt, weightTxt, filenameText;
    private Button birthdateb;
    private TextView dateMeasurement;
    private ImageButton mood0, mood1, mood2, mood3;
    private LineChart linechart, linechart2;
    private double[][] h_Nmeanstd, h_LBmeanstd;
    FloatingActionButton next, previous;
    FloatingActionButton edit, save, cancel;

    private int currentNumber = 0;

    final List<TableRow> table = new ArrayList<>();
    List<TableRow> selectedData = new ArrayList<>();
    private ListAdapter adapter ;
    private TableViewAdapter adapterAdmin;
    private int mood = 4;
    private String birthdate = "";
    private String priv = "";
    private String ownerid = "";
    private ListView lv;
    private String[] patientid;
    private boolean loadmore = true;
    private int pagesize = 100;
    private int offset;
    private ProgressBar loadMoreProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        context = getContext();
        context.setTheme(android.R.style.Theme_Holo_Light);
        width = getActivity().getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        height = getActivity().getApplicationContext().getResources().getDisplayMetrics().heightPixels;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainlayout1 = view.findViewById(R.id.mainLayout);
        mainlayout2 = view.findViewById(R.id.mainLayout2);

        priv = HomeFragment.priv;
        ownerid = HomeFragment.objectid;

        getGraphFromCloud("achtung");

        if (priv == null){
            Toast.makeText(getActivity(), "You do not have permission to access", Toast.LENGTH_LONG).show();
        } else if (priv.isEmpty()){
            Toast.makeText(getActivity(), "You need to be logged in", Toast.LENGTH_LONG).show();
        } else if (priv.equals("admin")) {
            setWindowAdmin();
        } else if (priv.equals("demo")) {
            setWindowDemo();
            System.out.println(priv);
        }

    }

    private void setWindowDemo(){

        System.out.println("demo");

        RelativeLayout.LayoutParams rllpprogress = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllpprogress.addRule(RelativeLayout.CENTER_IN_PARENT);
        final ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(rllpprogress);
        mainlayout1.addView(progressBar);

        final ScrollView scrollView = new ScrollView(context);
        scrollView.setVisibility(View.GONE);
        //mainlayout1.addView(scrollView);

        lv = new ListView(context);
        RelativeLayout.LayoutParams rllv = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllv.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rllv.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lv.setLayoutParams(rllv);
        mainlayout1.addView(lv);

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "userID = '" + ownerid + "'" );
        queryBuilder.setSortBy( "achtung DESC", "updated DESC" );

        Backendless.Data.of( "PATIENTdata" ).find( queryBuilder,
                new AsyncCallback<List<Map>>()
                {
                    @Override
                    public void handleResponse( List<Map> response )
                    {
                        System.out.println(response.size());
                        int count = response.size();

                        final String[] patientnames = new String[count];
                        final int[] images = new int[count];
                        final int[] background = new int[count];
                        patientid = new String[count];
                        int i = 0;

                        for(Map object : response){
                            if (object.get("sex")!=null && object.get("sex").toString().contains("F")){
                                images[i] = R.drawable.female_icon;
                            }
                            else if (object.get("sex")!=null && object.get("sex").toString().contains("M")){
                                images[i] = R.drawable.male_icon;
                            }
                            if (object.get("achtung")!=null && (Boolean) object.get("achtung") == true){
                                background[i] = R.drawable.background_achtung_true;
                            }
                            else if (object.get("achtung")!=null && (Boolean) object.get("achtung") == false){
                                background[i] = R.drawable.background_achtung_false;
                            }
                            patientnames[i] = (String) object.get("name");
                            patientid[i] = (String) object.get("objectId");
                            i++;
                        }

                        adapter = new ListAdapter(getActivity(), patientnames, images, background);
                        lv.setAdapter(adapter);

                        progressBar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        System.out.println(fault.getMessage());
                    }
                });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                getLABdata(patientid[i]);

            }
        });

    }

    private void getLABdata(String patientid){

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "patientID = '" + patientid + "'" );
        queryBuilder.setSortBy( "DateMeasurement DESC" );

        Backendless.Data.of( "LABdata" ).find( queryBuilder,
                new AsyncCallback<List<Map>>()
                {
                    @Override
                    public void handleResponse( List<Map> response )
                    {
                        System.out.println(response.size());
                        int i = 0;

                        for(Map object : response){
                            String sex = null;
                            if (object.get("Sex")!=null && object.get("Sex").toString().contains("F")){
                                sex = "Female";
                            }
                            else if (object.get("Sex")!=null && object.get("Sex").toString().contains("M")){
                                sex = "Male";
                            }
                            String lung = null;
                            if (object.get("Lung")!=null && object.get("Lung").toString().contains("R"))
                                lung = "Right";
                            else if (object.get("Lung")!=null && object.get("Lung").toString().contains("L"))
                                lung = "Left";
                            String healthy = null;
                            if (object.get("Healthy") != null && (Boolean) object.get("Healthy") == false)
                                healthy = "Yes";
                            else if (object.get("Healthy") != null && (Boolean) object.get("Healthy") == true)
                                healthy = "No";
                            String thoraco = null;
                            if (object.get("Thoracocentesis") != null && (Boolean) object.get("Thoracocentesis") == false)
                                thoraco = "No";
                            else if (object.get("Thoracocentesis") != null && (Boolean) object.get("Thoracocentesis") == true)
                                thoraco = "Yes";
                            String mood = null;
                            if (object.get("mood") != null)
                                mood = object.get("mood").toString();
                            //System.out.println(object.get("name")+"\n");
                            table.add(new TableRow((Date) object.get("age"),
                                    (String) object.get("diagnosis"),
                                    (String) object.get("hardware"),
                                    healthy,
                                    (String) object.get("height"),
                                    lung,
                                    (String) object.get("notes"),
                                    sex,
                                    (String) object.get("software"),
                                    (String) object.get("weight"),
                                    (String) object.get("objectId"),
                                    (String) object.get("name"),
                                    ((String) object.get("measurement") + (String) object.get("measurementcont")),
                                    mood,
                                    thoraco,
                                    (Date) object.get("DateMeasurement")));
                            i++;
                        }

                        //for (int j = 0; j < table.size(); j++){
                            //selectedData.add(table.get(j));
                        //}
                        selectedData = table;
                        setWindow2();
                        fillAndDraw(0);
                        mainlayout1.setVisibility(View.GONE);
                        mainlayout2.setVisibility(View.VISIBLE);

                        //progressBar.setVisibility(View.GONE);
                        //scrollView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        System.out.println(fault.getMessage());
                    }
                });
    }

    private void setWindowAdmin(){

        RelativeLayout.LayoutParams rllpprogress = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllpprogress.addRule(RelativeLayout.CENTER_IN_PARENT);
        final ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(rllpprogress);
        mainlayout1.addView(progressBar);

        final ScrollView scrollView = new ScrollView(context);
        scrollView.setVisibility(View.GONE);
        mainlayout1.addView(scrollView);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        linearLayout.addView(horizontalScrollView);

        recyclerView = new RecyclerView(context);
        horizontalScrollView.addView(recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);

                //int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
                //if (lastVisiblePosition == recyclerView.getChildCount()) {
                if (!recyclerView.canScrollVertically(1)) {

                    System.out.println("scrolled");
                    //System.out.println(lastVisiblePosition);
                    //System.out.println(recyclerView.getChildCount());
                    if (loadmore) {
                        loadmore = false;
                        loadMoreProgress.setVisibility(View.VISIBLE);
                        loadMoreMethod();
                    }
                }
            }
        });

        loadMoreProgress = new ProgressBar(context);
        loadMoreProgress.setVisibility(View.GONE);
        linearLayout.addView(loadMoreProgress);

        Backendless.Data.of( "LABdata" ).getObjectCount(new AsyncCallback<Integer>()
        {
            @Override
            public void handleResponse( Integer integer )
            {
                Log.i( "MYAPP", "total objects in the Order table - " + integer );
            }

            @Override
            public void handleFault( BackendlessFault backendlessFault )
            {
                Log.i( "MYAPP", "error - " + backendlessFault.getMessage() );
            }
        } );

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(pagesize);
        queryBuilder.setSortBy( "DateMeasurement DESC" );
        Backendless.Data.of( "LABdata" ).find( queryBuilder,
                new AsyncCallback<List<Map>>()
                {
                    @Override
                    public void handleResponse( List<Map> response )
                    {
                        System.out.println(response.size());
                        for(Map object : response){
                            String sex = null;
                            if (object.get("Sex")!=null && object.get("Sex").toString().contains("F"))
                                sex = "Female";
                            else if (object.get("Sex")!=null && object.get("Sex").toString().contains("M"))
                                sex = "Male";
                            String lung = null;
                            if (object.get("Lung")!=null && object.get("Lung").toString().contains("R"))
                                lung = "Right";
                            else if (object.get("Lung")!=null && object.get("Lung").toString().contains("L"))
                                lung = "Left";
                            String healthy = null;
                            if (object.get("Healthy") != null && (Boolean) object.get("Healthy") == false)
                                healthy = "Yes";
                            else if (object.get("Healthy") != null && (Boolean) object.get("Healthy") == true)
                                healthy = "No";
                            String thoraco = null;
                            if (object.get("Thoracocentesis") != null && (Boolean) object.get("Thoracocentesis") == false)
                                thoraco = "No";
                            else if (object.get("Thoracocentesis") != null && (Boolean) object.get("Thoracocentesis") == true)
                                thoraco = "Yes";
                            String mood = null;
                            if (object.get("mood") != null)
                                mood = object.get("mood").toString();
                            //System.out.println(object.get("name")+"\n");
                            table.add(new TableRow((Date) object.get("age"),
                                    (String) object.get("diagnosis"),
                                    (String) object.get("hardware"),
                                    healthy,
                                    (String) object.get("height"),
                                    lung,
                                    (String) object.get("notes"),
                                    sex,
                                    (String) object.get("software"),
                                    (String) object.get("weight"),
                                    (String) object.get("objectId"),
                                    (String) object.get("name"),
                                    ((String) object.get("measurement") + (String) object.get("measurementcont")),
                                    mood,
                                    thoraco,
                                    (Date) object.get("DateMeasurement")));
                        }

                        adapterAdmin = new TableViewAdapter(table);
                        recyclerView.setAdapter(adapterAdmin);

                        progressBar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                        offset = offset + pagesize;
                    }

                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        System.out.println(fault.getMessage());
                    }
                });

        RelativeLayout.LayoutParams selectparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        selectparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        selectparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        selectparams.setMargins(32, 32, 32, 32);

        select = new FloatingActionButton(context);
        select.setContentDescription("Select");
        select.setImageResource(R.drawable.ic_arrow_forward_white_24dp);
        select.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D81B60")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            select.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        select.setLayoutParams(selectparams);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vector<Integer> selected = TableViewAdapter.getSelected();
                for (int i = 0; i < selected.size(); i++){
                    selectedData.add(table.get(selected.get(i)-1));
                }
                setWindow2();
                fillAndDraw(0);
                mainlayout1.setVisibility(View.GONE);
                mainlayout2.setVisibility(View.VISIBLE);
            }
        });
        select.setAlpha(.2f);
        select.setEnabled(false);
        mainlayout1.addView(select);

    }

    private void loadMoreMethod(){

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(pagesize);
        queryBuilder.setOffset(offset);
        queryBuilder.setSortBy( "DateMeasurement DESC" );
        Backendless.Data.of( "LABdata" ).find( queryBuilder,
                new AsyncCallback<List<Map>>()
                {
                    @Override
                    public void handleResponse( List<Map> response )
                    {
                        System.out.println(response.size());
                        for(Map object : response){
                            String sex = null;
                            if (object.get("Sex")!=null && object.get("Sex").toString().contains("F"))
                                sex = "Female";
                            else if (object.get("Sex")!=null && object.get("Sex").toString().contains("M"))
                                sex = "Male";
                            String lung = null;
                            if (object.get("Lung")!=null && object.get("Lung").toString().contains("R"))
                                lung = "Right";
                            else if (object.get("Lung")!=null && object.get("Lung").toString().contains("L"))
                                lung = "Left";
                            String healthy = null;
                            if (object.get("Healthy") != null && (Boolean) object.get("Healthy") == false)
                                healthy = "Yes";
                            else if (object.get("Healthy") != null && (Boolean) object.get("Healthy") == true)
                                healthy = "No";
                            String thoraco = null;
                            if (object.get("Thoracocentesis") != null && (Boolean) object.get("Thoracocentesis") == false)
                                thoraco = "No";
                            else if (object.get("Thoracocentesis") != null && (Boolean) object.get("Thoracocentesis") == true)
                                thoraco = "Yes";
                            String mood = null;
                            if (object.get("mood") != null)
                                mood = object.get("mood").toString();
                            //System.out.println(object.get("name")+"\n");
                            table.add(new TableRow((Date) object.get("age"),
                                    (String) object.get("diagnosis"),
                                    (String) object.get("hardware"),
                                    healthy,
                                    (String) object.get("height"),
                                    lung,
                                    (String) object.get("notes"),
                                    sex,
                                    (String) object.get("software"),
                                    (String) object.get("weight"),
                                    (String) object.get("objectId"),
                                    (String) object.get("name"),
                                    ((String) object.get("measurement") + (String) object.get("measurementcont")),
                                    mood,
                                    thoraco,
                                    (Date) object.get("DateMeasurement")));
                        }

//                        progressBar.setVisibility(View.GONE);
//                        scrollView.setVisibility(View.VISIBLE);

                        offset = offset + pagesize;

                        loadMoreProgress.setVisibility(View.GONE);
                        adapterAdmin.notifyDataSetChanged();

                        loadmore=true;
                    }

                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        System.out.println(fault.getMessage());
                    }
                });
    }

    private void setWindow2(){

        ScrollView.LayoutParams scrollparams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT);
        ScrollView scrollView = new ScrollView(context);
        mainlayout2.addView(scrollView, scrollparams);

        LinearLayout linmain = new LinearLayout(context);
        linmain.setOrientation(LinearLayout.VERTICAL);
        linmain.setGravity(Gravity.CENTER_HORIZONTAL);
        scrollView.addView(linmain);

        FrameLayout.LayoutParams linLayoutParam = new FrameLayout.LayoutParams(2*width/3, LinearLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams linLayoutParam2 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayoutParam2.topMargin = height/30;
        FrameLayout.LayoutParams linLayoutParam3 = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams linLayoutParam4 = new FrameLayout.LayoutParams(2*width/3, LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayoutParam4.bottomMargin = height/10;
        //linLayoutParam.gravity = Gravity.CENTER;
        //linr.setLayoutParams(linLayoutParam);

        LinearLayout.LayoutParams linLayoutParamEdit = new LinearLayout.LayoutParams(width/3, LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayoutParamEdit.gravity = Gravity.CENTER;
        RelativeLayout.LayoutParams linLayoutParamRadioButton = new RelativeLayout.LayoutParams(width/3, LinearLayout.LayoutParams.WRAP_CONTENT);

        linechart = new LineChart(context);
        linechart.setMinimumHeight(height/3);
        linmain.addView(linechart, linLayoutParam2);

        linechart2 = new LineChart(context);
        linechart2.setPadding(0,10,0,0);
        linechart2.setMinimumHeight(height/3);
        linmain.addView(linechart2, linLayoutParam3);

        dateMeasurement = new TextView(context);
        dateMeasurement.setGravity(Gravity.CENTER);
        dateMeasurement.setTypeface(dateMeasurement.getTypeface(), Typeface.BOLD);
        dateMeasurement.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        //dateMeasurement.setPadding(0,height/100,0,0);
        linmain.addView(dateMeasurement);

        TextView txt1 = new TextView(context);
        txt1.setText("Birthdate");
        txt1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        txt1.setPadding(0,height/50,0,0);
        linmain.addView(txt1, linLayoutParam);

        birthdateb = new Button(context);
//        birthdateb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Calendar cal = Calendar.getInstance();
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                SupportedDatePickerDialog.OnDateSetListener mDateSetListener = new SupportedDatePickerDialog.OnDateSetListener() {
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        ++month;
//                        birthdateb.setText(dayOfMonth + "/" + month + "/" + year);
//                        birthdate = Integer.toString(dayOfMonth) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
//                    }
//                };
//                SupportedDatePickerDialog datepicker = new SupportedDatePickerDialog(getActivity(), R.style.SpinnerDatePickerDialogTheme, mDateSetListener, year, month, day);
//                datepicker.getWindow().setBackgroundDrawable(new ColorDrawable(0));
//                datepicker.show();
//            }
//        });
        birthdateb.setBackgroundResource(R.drawable.rounded_button);
        //birthdateb.setText("dd/mm/yyyy");
        //birthdateb.setEnabled(false);
        birthdateb.setAlpha(.5f);
        linmain.addView(birthdateb, linLayoutParam);

        TextView txt2 = new TextView(context);
        txt2.setText("Weight");
        txt2.setGravity(Gravity.CENTER);
        txt2.setPadding(0,15,0,0);
        linmain.addView(txt2, linLayoutParam);

        weightTxt = new EditText(context);
        //weightTxt.setHint("kg");
        weightTxt.setEnabled(false);
        weightTxt.setGravity(Gravity.CENTER);
        weightTxt.setPadding(0,0,0,4);
        weightTxt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        linmain.addView(weightTxt, linLayoutParamEdit);

        TextView txt3 = new TextView(context);
        txt3.setText("Height");
        txt3.setGravity(Gravity.CENTER);
        txt3.setPadding(0,15,0,0);
        linmain.addView(txt3);

        heightTxt = new EditText(context);
        //heightTxt.setHint("cm");
        heightTxt.setEnabled(false);
        heightTxt.setGravity(Gravity.CENTER);
        heightTxt.setPadding(0,0,0,4);
        heightTxt.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        linmain.addView(heightTxt, linLayoutParamEdit);

        TextView txt4 = new TextView(context);
        txt4.setText("Sex");
        txt4.setGravity(Gravity.CENTER);
        txt4.setPadding(0,15,0,0);
        linmain.addView(txt4);

        radioGroupSex = new RadioGroup(context);
        radioGroupSex.setOrientation(RadioGroup.HORIZONTAL);
        radioGroupSex.setGravity(Gravity.CENTER);
        rbFemale  = new RadioButton(context);
        rbFemale.setText("Female");
        rbFemale.setGravity(Gravity.CENTER);
        rbFemale.setPadding(0,0,0,0);
        rbFemale.setEnabled(false);
        radioGroupSex.addView(rbFemale, linLayoutParamRadioButton);
        rbMale  = new RadioButton(context);
        rbMale.setText("Male");
        rbMale.setGravity(Gravity.CENTER);
        rbMale.setPadding(0,0,0,0);
        rbMale.setEnabled(false);
        radioGroupSex.addView(rbMale, linLayoutParamRadioButton);
        linmain.addView(radioGroupSex, linLayoutParam);

        TextView txt5 = new TextView(context);
        txt5.setText("Lung");
        txt5.setGravity(Gravity.CENTER);
        txt5.setPadding(0,15,0,0);
        linmain.addView(txt5, linLayoutParam);

        radioGroupLung = new RadioGroup(context);
        radioGroupLung.setOrientation(RadioGroup.HORIZONTAL);
        radioGroupLung.setGravity(Gravity.CENTER);
        rbLeft  = new RadioButton(context);
        rbLeft.setText("Left");
        rbLeft.setGravity(Gravity.CENTER);
        rbLeft.setPadding(0,0,0,0);
        rbLeft.setEnabled(false);
        radioGroupLung.addView(rbLeft, linLayoutParamRadioButton);
        rbRight  = new RadioButton(context);
        rbRight.setText("Right");
        rbRight.setGravity(Gravity.CENTER);
        rbRight.setPadding(0,0,0,0);
        rbRight.setEnabled(false);
        radioGroupLung.addView(rbRight, linLayoutParamRadioButton);
        linmain.addView(radioGroupLung, linLayoutParam);

        TextView txt6 = new TextView(context);
        txt6.setText("Pleural effusion");
        txt6.setGravity(Gravity.CENTER);
        txt6.setPadding(0,15,0,0);
        linmain.addView(txt6, linLayoutParam);

        radioGroupPleeff = new RadioGroup(context);
        radioGroupPleeff.setOrientation(RadioGroup.HORIZONTAL);
        radioGroupPleeff.setGravity(Gravity.CENTER);
        rbPEyes  = new RadioButton(context);
        rbPEyes.setText("Yes");
        rbPEyes.setTag("radioButtonPEYes");
        rbPEyes.setGravity(Gravity.CENTER);
        rbPEyes.setPadding(0,0,0,0);
        rbPEyes.setEnabled(false);
        radioGroupPleeff.addView(rbPEyes, linLayoutParamRadioButton);
        rbPEno  = new RadioButton(context);
        rbPEno.setText("No");
        rbPEno.setTag("radioButtonPENo");
        rbPEno.setGravity(Gravity.CENTER);
        rbPEno.setPadding(0,0,0,0);
        rbPEno.setEnabled(false);
        radioGroupPleeff.addView(rbPEno, linLayoutParamRadioButton);
        linmain.addView(radioGroupPleeff, linLayoutParam);

        TextView txt7 = new TextView(context);
        txt7.setText("Thoracocentesis");
        txt7.setGravity(Gravity.CENTER);
        txt7.setPadding(0,15,0,0);
        linmain.addView(txt7, linLayoutParam);

        radioGroupThoraco = new RadioGroup(context);
        radioGroupThoraco.setOrientation(RadioGroup.HORIZONTAL);
        radioGroupThoraco.setGravity(Gravity.CENTER);
        rbThoraYes  = new RadioButton(context);
        rbThoraYes.setText("Yes");
        rbThoraYes.setTag("radioButtonThoracoYes");
        rbThoraYes.setGravity(Gravity.CENTER);
        rbThoraYes.setPadding(0,0,0,0);
        rbThoraYes.setEnabled(false);
        radioGroupThoraco.addView(rbThoraYes, linLayoutParamRadioButton);
        rbThoraNo  = new RadioButton(context);
        rbThoraNo.setText("No");
        rbThoraNo.setTag("radioButtonThoracoNo");
        rbThoraNo.setGravity(Gravity.CENTER);
        rbThoraNo.setPadding(0,0,0,0);
        rbThoraNo.setEnabled(false);
        radioGroupThoraco.addView(rbThoraNo, linLayoutParamRadioButton);
        linmain.addView(radioGroupThoraco, linLayoutParam);

        TextView txt8 = new TextView(context);
        txt8.setText("Diagnosis code");
        txt8.setGravity(Gravity.CENTER);
        txt8.setPadding(0,15,0,0);
        linmain.addView(txt8, linLayoutParam);

        jspinner = new Spinner(context);
        String[] spinnerItems = new String[]{
                "J90",
                "J91",
                "J92",
                "J93",
                "J94",
                "J81",
                "Others (please note)",
                "Healthy",
                " " //gdy null
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.spinner_textview_align, spinnerItems);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        jspinner.setAdapter(spinnerArrayAdapter);
        jspinner.setEnabled(false);
        linmain.addView(jspinner, linLayoutParam);

        TextView txt9 = new TextView(context);
        txt9.setText("Mood");
        txt9.setGravity(Gravity.CENTER);
        txt9.setPadding(0,15,0,10);
        linmain.addView(txt9, linLayoutParam);

        LinearLayout linmood = new LinearLayout(context);
        linmood.setOrientation(LinearLayout.HORIZONTAL);
        //linmood.setPadding(0,40,0,40);
        linmood.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams llhmood = new LinearLayout.LayoutParams(2*width/3, height/13);
        llhmood.gravity = Gravity.CENTER_HORIZONTAL;
        linmain.addView(linmood, llhmood);

        LinearLayout.LayoutParams llhmoody = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.25f);
        mood0 = new ImageButton(context);
        mood1 = new ImageButton(context);
        mood2 = new ImageButton(context);
        mood3 = new ImageButton(context);

        mood0.setImageResource(R.drawable.smiley0);
        mood0.setBackgroundColor(Color.TRANSPARENT);
        mood0.setAdjustViewBounds(true);
        mood0.setPadding(0,0,0,0);
        mood0.setAlpha(.2f);
//        mood0.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mood = 0;
//                mood0.setAlpha(1.f);
//                mood1.setAlpha(.2f);
//                mood2.setAlpha(.2f);
//                mood3.setAlpha(.2f);
//            }
//        });
        linmood.addView(mood0, llhmoody);

        mood1.setImageResource(R.drawable.smiley1);
        mood1.setBackgroundColor(Color.TRANSPARENT);
        mood1.setAdjustViewBounds(true);
        mood1.setPadding(0,0,0,0);
        mood1.setAlpha(.2f);
//        mood1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mood = 1;
//                mood1.setAlpha(1.f);
//                mood0.setAlpha(.2f);
//                mood2.setAlpha(.2f);
//                mood3.setAlpha(.2f);
//            }
//        });
        linmood.addView(mood1, llhmoody);

        mood2.setImageResource(R.drawable.smiley2);
        mood2.setBackgroundColor(Color.TRANSPARENT);
        mood2.setAdjustViewBounds(true);
        mood2.setPadding(0,0,0,0);
        mood2.setAlpha(.2f);
//        mood2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mood = 2;
//                mood2.setAlpha(1.f);
//                mood0.setAlpha(.2f);
//                mood1.setAlpha(.2f);
//                mood3.setAlpha(.2f);
//            }
//        });
        linmood.addView(mood2, llhmoody);

        mood3.setImageResource(R.drawable.smiley3);
        mood3.setBackgroundColor(Color.TRANSPARENT);
        mood3.setAdjustViewBounds(true);
        mood3.setPadding(0,0,0,0);
        mood3.setAlpha(.2f);
//        mood3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mood = 3;
//                mood3.setAlpha(1.f);
//                mood0.setAlpha(.2f);
//                mood1.setAlpha(.2f);
//                mood2.setAlpha(.2f);
//            }
//        });
        linmood.addView(mood3, llhmoody);

        TextView txt10 = new TextView(context);
        txt10.setText("Notes");
        txt10.setGravity(Gravity.CENTER);
        txt10.setPadding(0,15,0,10);
        linmain.addView(txt10, linLayoutParam);

        notesTxt = new EditText(context);
        //notesTxt.setHint("Notes");
        notesTxt.setGravity(Gravity.CENTER);
        notesTxt.setPadding(0,10,0,0);
        notesTxt.setMinHeight(height/10);
        notesTxt.setEnabled(false);
        linmain.addView(notesTxt, linLayoutParam);

        TextView txt11 = new TextView(context);
        txt11.setText("Name");
        txt11.setGravity(Gravity.CENTER);
        txt11.setPadding(0,15,0,10);
        linmain.addView(txt11, linLayoutParam);

        filenameText = new EditText(context);
        //filenameText.setHint("Name");
        filenameText.setGravity(Gravity.CENTER);
        filenameText.setPadding(0,15,0,0);
        filenameText.setMinHeight(2*height/10/3);
        filenameText.setEnabled(false);
        linmain.addView(filenameText, linLayoutParam4);

        RelativeLayout.LayoutParams nextparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        nextparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        nextparams.addRule(RelativeLayout.CENTER_VERTICAL);
        nextparams.setMargins(16, 16, 16, 16);

        RelativeLayout.LayoutParams previousparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        previousparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        previousparams.addRule(RelativeLayout.CENTER_VERTICAL);
        previousparams.setMargins(16, 16, 16, 16);

        edit = new FloatingActionButton(context);
        save = new FloatingActionButton(context);
        cancel = new FloatingActionButton(context);
        ImageView space = new ImageView(context);
        int spaceid = ViewCompat.generateViewId();
        space.setId(spaceid);

        RelativeLayout.LayoutParams editparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        editparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        editparams.setMargins(16, 16, 16, 16);

        RelativeLayout.LayoutParams saveparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        saveparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        saveparams.addRule(RelativeLayout.ALIGN_LEFT, spaceid);
        //saveparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        saveparams.setMargins(width/25, 16, 16, 16);

        RelativeLayout.LayoutParams spaceparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        spaceparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        spaceparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //spaceparams.setMargins(16, 16, 16, 16);

        RelativeLayout.LayoutParams cancelparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cancelparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //cancelparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        cancelparams.addRule(RelativeLayout.ALIGN_RIGHT, spaceid);
        //cancelparams.addRule(RelativeLayout.ALIGN_RIGHT, save.getId());
        //cancelparams.anchorGravity = save.getId();
        //cancelparams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        cancelparams.setMargins(16, 16, width/25, 16);

        next = new FloatingActionButton(context);
        next.setContentDescription("Next");
        next.setImageResource(R.drawable.ic_chevron_right_white_24dp);
        next.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D81B60")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            next.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        next.setLayoutParams(nextparams);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentNumber++;
                fillAndDraw(currentNumber);
            }
        });
        mainlayout2.addView(next);

        previous = new FloatingActionButton(context);
        previous.setContentDescription("Previous");
        previous.setImageResource(R.drawable.ic_chevron_left_white_24dp);
        previous.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D81B60")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            previous.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        previous.setLayoutParams(previousparams);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentNumber--;
                fillAndDraw(currentNumber);
            }
        });
        mainlayout2.addView(previous);

        edit.setContentDescription("Edit");
        edit.setImageResource(R.drawable.ic_edit_white_24dp);
        edit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D81B60")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edit.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        edit.setLayoutParams(editparams);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.hide();
                enabledFields(true);
                save.show();
                cancel.show();
            }
        });
        mainlayout2.addView(edit);

        space.setLayoutParams(spaceparams);
        mainlayout2.addView(space);

        save.setContentDescription("Save");
        save.setImageResource(R.drawable.ic_done_white_24dp);
        save.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D81B60")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            save.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        save.setLayoutParams(saveparams);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (priv.equals("demo")) {
                    Toast.makeText(getActivity(), "You cannot save changes in demo mode", Toast.LENGTH_LONG).show();
                } else {
                    save.hide();
                    cancel.hide();
                    updateRecord(currentNumber);
                    enabledFields(false);
                    edit.show();
                }
            }
        });
        save.hide();
        mainlayout2.addView(save);

        cancel.setContentDescription("Cancel");
        cancel.setImageResource(R.drawable.ic_close_white_24dp);
        cancel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D81B60")));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cancel.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
        cancel.setLayoutParams(cancelparams);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.hide();
                cancel.hide();
                fillAndDraw(currentNumber);
                enabledFields(false);
                edit.show();
            }
        });
        cancel.hide();
        mainlayout2.addView(cancel);
    }

    private void fillAndDraw(int number){
        TableRow data = selectedData.get(number);

        SimpleDateFormat formatter0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateMeasurement.setText(formatter0.format(data.getDateMeasurement()));

        birthdate = "";
        if (data.getBirthdate() != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            birthdate = formatter.format(data.getBirthdate());
        }
        birthdateb.setText(birthdate);

        if (data.getWeight() != null){
            weightTxt.setText(data.getWeight());
        } else {
            weightTxt.setText("");
        }

        if (data.getHeight() != null){
            heightTxt.setText(data.getHeight());
        } else {
            heightTxt.setText("");
        }

        if(data.getSex() != null){
            switch (data.getSex()){
                case "Female":
                    rbFemale.setChecked(true);
                    rbMale.setChecked(false);
                    break;
                case "Male":
                    rbFemale.setChecked(false);
                    rbMale.setChecked(true);
                    break;
            }
        } else {
            rbFemale.setChecked(false);
            rbMale.setChecked(false);
        }

        if(data.getLung() != null){
            switch (data.getLung()){
                case "Left":
                    rbLeft.setChecked(true);
                    rbRight.setChecked(false);
                    break;
                case "Right":
                    rbLeft.setChecked(false);
                    rbRight.setChecked(true);
                    break;
            }
        } else {
            rbLeft.setChecked(false);
            rbRight.setChecked(false);
        }

        if(data.getHealthy() != null){
            switch (data.getHealthy()){
                case "Yes":
                    rbPEyes.setChecked(true);
                    rbPEno.setChecked(false);
                    break;
                case "No":
                    rbPEyes.setChecked(false);
                    rbPEno.setChecked(true);
                    break;
            }
        } else {
            rbPEyes.setChecked(false);
            rbPEno.setChecked(false);
        }

        if(data.getThoraco() != null){
            switch (data.getThoraco()){
                case "Yes":
                    rbThoraYes.setChecked(true);
                    rbThoraNo.setChecked(false);
                    break;
                case "No":
                    rbThoraYes.setChecked(false);
                    rbThoraNo.setChecked(true);
                    break;
            }
        } else {
            rbThoraYes.setChecked(false);
            rbThoraNo.setChecked(false);
        }

        if(data.getDiagnosis() != null){
            switch (data.getDiagnosis()){
                case "J90":
                    jspinner.setSelection(0);
                    break;
                case "J91":
                    jspinner.setSelection(1);
                    break;
                case "J92":
                    jspinner.setSelection(2);
                    break;
                case "J93":
                    jspinner.setSelection(3);
                    break;
                case "J94":
                    jspinner.setSelection(4);
                    break;
                case "J81":
                    jspinner.setSelection(5);
                    break;
                case "Others (please note)":
                    jspinner.setSelection(6);
                    break;
                case "Healthy":
                    jspinner.setSelection(7);
                    break;
            }
        } else {
            jspinner.setSelection(8);
        }

        if(data.getMood() != null){
            switch (data.getMood()){
                case "0":
                    mood = 0;
                    mood0.setAlpha(1.f);
                    mood1.setAlpha(.2f);
                    mood2.setAlpha(.2f);
                    mood3.setAlpha(.2f);
                    break;
                case "1":
                    mood = 1;
                    mood0.setAlpha(.2f);
                    mood1.setAlpha(1.f);
                    mood2.setAlpha(.2f);
                    mood3.setAlpha(.2f);
                    break;
                case "2":
                    mood = 2;
                    mood0.setAlpha(.2f);
                    mood1.setAlpha(.2f);
                    mood2.setAlpha(1.f);
                    mood3.setAlpha(.2f);
                    break;
                case "3":
                    mood = 3;
                    mood0.setAlpha(.2f);
                    mood1.setAlpha(.2f);
                    mood2.setAlpha(.2f);
                    mood3.setAlpha(1.f);
                    break;
            }
        }

        if(data.getNotes() != null){
            notesTxt.setText(data.getNotes());
        } else notesTxt.setText("");

        if(data.getName() != null){
            filenameText.setText(data.getName());
        } else filenameText.setText("");

        Vector<Vector> vecs = splitString(data.getMeasurement());
        double[][] xy = new double[2][1000];
        double[][] xy2 = new double[2][1000];
        xy = countData_Plot(vecs.get(0), vecs.get(1));
        xy2 = countData_Plot2(vecs.get(0), vecs.get(1));

        plotMeasurement(xy);
        plotMeasurement2(xy2);
    }

    private void enabledFields(boolean enabled){
        birthdateb.setEnabled(enabled);
        if (enabled){
            birthdateb.setAlpha(1.f);
            birthdateb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                SupportedDatePickerDialog.OnDateSetListener mDateSetListener = new SupportedDatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ++month;
                        birthdateb.setText(dayOfMonth + "/" + month + "/" + year);
                        birthdate = Integer.toString(dayOfMonth) + "/" + Integer.toString(month) + "/" + Integer.toString(year);
                    }
                };
                SupportedDatePickerDialog datepicker = new SupportedDatePickerDialog(getActivity(), R.style.SpinnerDatePickerDialogTheme, mDateSetListener, year, month, day);
                datepicker.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                datepicker.show();
            }
            });
        }
        else birthdateb.setAlpha(.5f);

        weightTxt.setEnabled(enabled);
        if (enabled) weightTxt.setHint("kg");
        else weightTxt.setHint("");

        heightTxt.setEnabled(enabled);
        if (enabled) heightTxt.setHint("cm");
        else heightTxt.setHint("");

        rbFemale.setEnabled(enabled);
        rbMale.setEnabled(enabled);
        rbLeft.setEnabled(enabled);
        rbRight.setEnabled(enabled);
        rbPEyes.setEnabled(enabled);
        rbPEno.setEnabled(enabled);
        rbThoraYes.setEnabled(enabled);
        rbThoraNo.setEnabled(enabled);

        jspinner.setEnabled(enabled);

        if (enabled){
            mood0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mood = 0;
                    mood0.setAlpha(1.f);
                    mood1.setAlpha(.2f);
                    mood2.setAlpha(.2f);
                    mood3.setAlpha(.2f);
                }
            });
            mood1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mood = 1;
                    mood0.setAlpha(.2f);
                    mood1.setAlpha(1.f);
                    mood2.setAlpha(.2f);
                    mood3.setAlpha(.2f);
                }
            });
            mood2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mood = 2;
                    mood0.setAlpha(.2f);
                    mood1.setAlpha(.2f);
                    mood2.setAlpha(1.f);
                    mood3.setAlpha(.2f);
                }
            });
            mood3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mood = 3;
                    mood0.setAlpha(.2f);
                    mood1.setAlpha(.2f);
                    mood2.setAlpha(.2f);
                    mood3.setAlpha(1.f);
                }
            });
        } else {
            mood0.setEnabled(enabled);
            mood1.setEnabled(enabled);
            mood2.setEnabled(enabled);
            mood3.setEnabled(enabled);
        }

        notesTxt.setEnabled(enabled);
        if (enabled) notesTxt.setHint("Notes");
        else notesTxt.setHint("");

        filenameText.setEnabled(enabled);
        if (enabled) filenameText.setHint("Name");
        else filenameText.setHint("");
    }

    private void updateRecord(int current){
        TableRow olddata = selectedData.get(current);
        String id = olddata.getObjectID();

        Backendless.Data.of("LABdata").save( getCandidate(id), new AsyncCallback<Map>() {
            @Override
            public void handleResponse( Map response )
            {
                System.out.println("updated?");
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                System.out.println(fault.getMessage());
            }
        } );
    }

    private Map<String, Object> getCandidate(String id) {
        Map<String, Object> oneCandidate = new HashMap();

        oneCandidate.put("objectId", id);

        Date newbirthdate = null;
        try {
            newbirthdate = new SimpleDateFormat("dd/MM/yyyy").parse(birthdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        oneCandidate.put("age", newbirthdate);
        oneCandidate.put("diagnosis", String.valueOf(jspinner.getSelectedItem()));
        RadioButton radioButtonPleeff = (RadioButton) radioGroupPleeff.findViewById(radioGroupPleeff.getCheckedRadioButtonId());
        if (radioButtonPleeff != null){
            if (radioButtonPleeff.getTag() == "radioButtonPEYes")
                oneCandidate.put("Healthy", false);
            else
                oneCandidate.put("Healthy", true);
        }
        oneCandidate.put("height", heightTxt.getEditableText().toString());
        RadioButton radioButtonLung = (RadioButton) radioGroupLung.findViewById(radioGroupLung.getCheckedRadioButtonId());
        if (radioButtonLung == null) {
            oneCandidate.put("Lung", "");
        } else {
            oneCandidate.put("Lung", String.valueOf(radioButtonLung.getText()).charAt(0));
        }
        if(mood < 4) oneCandidate.put("mood", mood);
        String notes = notesTxt.getEditableText().toString();
        oneCandidate.put("notes", notes);
        RadioButton radioButtonSex = (RadioButton) radioGroupSex.findViewById(radioGroupSex.getCheckedRadioButtonId());
        if (radioButtonSex == null) {
            oneCandidate.put("Sex", "");
        } else {
            oneCandidate.put("Sex", String.valueOf(radioButtonSex.getText()).charAt(0));
        }
        RadioButton radioButtonThoraco = (RadioButton) radioGroupThoraco.findViewById(radioGroupThoraco.getCheckedRadioButtonId());
        if (radioButtonThoraco != null){
            if (radioButtonThoraco.getTag() == "radioButtonThoracoYes")
                oneCandidate.put("Thoracocentesis", true);
            else
                oneCandidate.put("Thoracocentesis", false);
        }
        oneCandidate.put("weight", weightTxt.getEditableText().toString());
        String aFileName = filenameText.getEditableText().toString();
        oneCandidate.put("name", aFileName);
        return oneCandidate;
    }

    private Vector<Vector> splitString(String strAll) {
        Vector v0 = new Vector();
        Vector v2 = new Vector();
        ArrayList aList = new ArrayList(asList(strAll.split("\n")));
        for (int i = 0; i < aList.size(); i++) {
            ArrayList commaList = new ArrayList(asList(aList.get(i).toString().split(",")));
            if (commaList.size() > 3) {
                if (Integer.valueOf(commaList.get(2).toString()) != 0 && Integer.valueOf(commaList.get(4).toString()) != 0){
                    v0.add(commaList.get(2));
                    v2.add(commaList.get(4));
                }
            }
        }
        Vector<Vector> v0v2 = new Vector<>();
        v0v2.add(v0);
        v0v2.add(v2);
        return v0v2;
    }

    private double[][] countData_Plot(Vector v_daleki, Vector v_bliski) {

        Vector pom1 = normalize(v_daleki);
        Vector pom2 = normalize(v_bliski);
        double uplim = 0.9999;
        double lowlim = 0.00001;

        double[][] xy_temp = distpor(pom1, pom2, lowlim, uplim);
        xy_temp = leastsquares(xy_temp[0], xy_temp[1]);

        xy_temp[1] = sub_1st_el(xy_temp[1]);
        //xy_temp[1] = sub_mean_numel(xy_temp[1], 80);

        xy_temp[0] = linspaceD(1, 1000, 1000);

        return xy_temp;
    }

    private double[][] countData_Plot2(Vector v_daleki, Vector v_bliski){
        double I0daleki = 0.0029468 * Math.pow(10, -6);
        double I0bliski = 0.75009 * Math.pow(10, -6);
        double[] lb = new double[v_daleki.size()];
        for (int i = 0; i < v_daleki.size(); i++){
            double Ibliski = Double.valueOf(v_bliski.get(i).toString()) / 2932993.23671;
            double Idaleki = Double.valueOf(v_daleki.get(i).toString()) / 746580096.61836;
            double Fdaleki = Math.log10(Idaleki/I0daleki);
            double Fbliski = Math.log10(Ibliski/I0bliski);
            lb[i] = Fdaleki/Fbliski;
        }

        double[][] xyLB = new double[1000][2];
        xyLB[0] = linspaceD(0,1, 1000);
        double[] interplambeer = interpLinear(linspaceD(0,1,v_bliski.size()), lb, xyLB[0]);
        //xyLB[1] = sub_mean_numel(interplambeer, 80);
        xyLB[1] = sub_1st_el(interplambeer);

        return xyLB;
    }

    private double[][] distpor(Vector<Vector> r1, Vector<Vector> run2, double lowerl, double upperl) {
        double[][] x1y2 = new double[2][1000];

        double sum1 = 0;
        double sum2 = 0;
        int n = r1.get(1).size();
        int n2 = run2.get(1).size();

        double mx1 = Double.valueOf(Collections.max(r1.get(1)).toString());
        double mx2 = Double.valueOf(Collections.max(run2.get(1)).toString());

        Vector raw1x = new Vector();
        Vector raw1y = new Vector();
        Vector raw2x = new Vector();
        Vector raw2y = new Vector();

        int k = 0;
        for (int i = 0; i < n; i++) {
            if (Double.valueOf(r1.get(1).get(i).toString()) >= mx1 / 1000) {
                k = k + 1;
                raw1x.add(Double.valueOf(r1.get(0).get(i).toString()));
                raw1y.add(Double.valueOf(r1.get(1).get(i).toString()));
            }
        }
        int ile1 = k;
        k = 0;
        for (int i = 0; i < n2; i++) {
            if (Double.valueOf(run2.get(1).get(i).toString()) >= mx2 / 1000) {
                k = k + 1;
                raw2x.add(Double.valueOf(run2.get(0).get(i).toString()));
                raw2y.add(Double.valueOf(run2.get(1).get(i).toString()));
            }
        }
        int ile2 = k;

        if (ile1 != 0 && ile2 != 0 && mx1 != 0 && mx2 != 0) {
            for (int i = 0; i < ile1; i++) {
                sum1 = sum1 + Double.valueOf(raw1y.get(i).toString());
            }
            for (int i = 0; i < ile2; i++) {
                sum2 = sum2 + Double.valueOf(raw2y.get(i).toString());
            }

            Vector dyst1x = (Vector) raw1x.clone();
            dyst1x.add(0, 0);
            Vector dyst1y = (Vector) raw1y.clone();
            dyst1y.add(0, 0);
            Vector dyst2x = (Vector) raw2x.clone();
            dyst2x.add(0, 0);
            Vector dyst2y = (Vector) raw2y.clone();
            dyst2y.add(0, 0);

            // normowanie
            for (int i = 1; i < ile1 + 1; i++) {
                dyst1y.set(i, Double.valueOf(dyst1y.get(i - 1).toString()) + Double.valueOf(raw1y.get(i - 1).toString()));
            }
            for (int i = 1; i < ile2 + 1; i++) {
                dyst2y.set(i, Double.valueOf(dyst2y.get(i - 1).toString()) + Double.valueOf(raw2y.get(i - 1).toString()));
            }
            double[] dyst1ya = new double[dyst1y.size()];
            for (int i = 0; i < dyst1y.size(); i++) {
                //dyst1y.set(i, Double.valueOf(dyst1y.get(i).toString())/sum1);
                dyst1ya[i] = Double.valueOf(dyst1y.get(i).toString()) / sum1;
            }
            double[] dyst2ya = new double[dyst2y.size()];
            for (int i = 0; i < dyst2y.size(); i++) {
                //dyst2y.set(i, Double.valueOf(dyst2y.get(i).toString())/sum2);
                dyst2ya[i] = Double.valueOf(dyst2y.get(i).toString()) / sum2;
            }

            double[] y1 = linspaceD(lowerl, upperl, 1000);
            double[] dyst1xa = new double[dyst1x.size()];
            for (int i = 0; i < dyst1x.size(); i++) {
                dyst1xa[i] = Double.valueOf(dyst1x.get(i).toString());
            }
            double[] x1 = interpLinear(dyst1ya, dyst1xa, y1);
            x1y2[0] = x1;
            double[] dyst2xa = new double[dyst2x.size()];
            for (int i = 0; i < dyst2x.size(); i++) {
                dyst2xa[i] = Double.valueOf(dyst2x.get(i).toString());
            }
            double[] y2 = interpLinear(dyst2ya, dyst2xa, y1);
            x1y2[1] = y2;

        } else {
            double[] x1 = new double[1000];
            double[] y2 = new double[1000];
            x1y2[0] = x1;
            x1y2[1] = y2;
        }

        return x1y2;
    }

    private double[][] leastsquares(double[] xx, double[] yy) {
        double n = xx.length;
        double sumxx = 0;
        double sumyy = 0;
        double sumxy = 0;
        double sumxx2 = 0;
        for (int i = 0; i < n; i++) {
            sumxx += xx[i];
            sumyy += yy[i];
            sumxy += xx[i] * yy[i];
            sumxx2 += xx[i] * xx[i];
        }

        double wspol = sumxx / n;
        double a = (sumxy - wspol * sumyy) / (sumxx2 - wspol * sumxx);
        double b = sumyy / n - wspol * a;

        for (int i = 0; i < n; i++) {
            yy[i] = yy[i] - a * xx[i] - b;
        }

        double[][] xxyy = new double[2][1000];
        xxyy[0] = xx;
        xxyy[1] = yy;
        return xxyy;
    }

    private double[] sub_1st_el(double[] y2) {
        double y0 = y2[0];
        for (int i = 0; i < y2.length; i++) {
            y2[i] = y2[i] - y0;
        }

        return y2;
    }

    private double[] sub_mean_numel(double[] y2, int numel) {
        double sumnumel = 0;
        for (int i = 0; i < numel; i++) {
            sumnumel += y2[i];
        }
        double sub = sumnumel / numel;
        for (int i = 0; i < y2.length; i++) {
            y2[i] = y2[i] - sub;
        }

        return y2;
    }

    private Vector<Vector> normalize(Vector a) {
        int dataSize = a.size();
        Vector norm_x = linspace(0, 1, dataSize);
        Vector norm_y = new Vector();
        double integral = 0;
        for (int i = 0; i < dataSize; i++)
            integral += Integer.valueOf(a.get(i).toString());
        for (int i = 0; i < dataSize; i++) {
            double temp = Double.valueOf(a.get(i).toString()) / integral;
            norm_y.add(temp);
        }
        Vector<Vector> norm = new Vector<>();
        norm.add(norm_x);
        norm.add(norm_y);
        return norm;
    }

    private Vector linspace(double start, double stop, int n) {
        Vector result = new Vector();
        double step = (stop - start) / (n - 1);
        for (int i = 0; i <= n - 2; i++) {
            result.add(start + (i * step));
        }
        result.add(stop);
        return result;
    }

    private double[] linspaceD(double start, double stop, int n) {
        double[] result = new double[n];
        double step = (stop - start) / (n - 1);
        for (int i = 0; i < n - 1; i++) {
            result[i] = (start + (i * step));
        }
        result[n - 1] = stop;
        return result;
    }

    private static final double[] interpLinear(double[] x, double[] y, double[] xi) throws IllegalArgumentException {

        if (x.length != y.length) {
            throw new IllegalArgumentException("X and Y must be the same length");
        }
        if (x.length == 1) {
            throw new IllegalArgumentException("X must contain more than one value");
        }
        double[] dx = new double[x.length - 1];
        double[] dy = new double[x.length - 1];
        double[] slope = new double[x.length - 1];
        double[] intercept = new double[x.length - 1];

        // Calculate the line equation (i.e. slope and intercept) between each point
        for (int i = 0; i < x.length - 1; i++) {
            dx[i] = x[i + 1] - x[i];
            if (dx[i] == 0) {
                throw new IllegalArgumentException("X must be montotonic. A duplicate " + "x-value was found");
            }
            if (dx[i] < 0) {
                throw new IllegalArgumentException("X must be sorted");
            }
            dy[i] = y[i + 1] - y[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = y[i] - x[i] * slope[i];
        }

        // Perform the interpolation here
        double[] yi = new double[xi.length];
        for (int i = 0; i < xi.length; i++) {
            if ((xi[i] > x[x.length - 1]) || (xi[i] < x[0])) {
                yi[i] = Double.NaN;
            } else {
                int loc = Arrays.binarySearch(x, xi[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yi[i] = slope[loc] * xi[i] + intercept[loc];
                } else {
                    yi[i] = y[loc];
                }
            }
        }
        return yi;
    }

    private void getGraphFromCloud(String dev) {
        System.out.println("getGraphFromCloud");
        String whereClause = "device = '" + dev + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("GRAPHdata").find(queryBuilder,
                new AsyncCallback<List<Map>>() {
                    @Override
                    public void handleResponse(List<Map> foundData) {
                        if (foundData.size() == 1 && foundData.get(0).get("NsdB") != null
                                && foundData.get(0).get("NsdL") != null
                                && foundData.get(0).get("NsdR") != null
                                && foundData.get(0).get("NmeanB") != null
                                && foundData.get(0).get("NmeanL") != null
                                && foundData.get(0).get("NmeanR") != null
                                && foundData.get(0).get("LBsdB") != null
                                && foundData.get(0).get("LBsdL") != null
                                && foundData.get(0).get("LBsdR") != null
                                && foundData.get(0).get("LBmeanB") != null
                                && foundData.get(0).get("LBmeanL") != null
                                && foundData.get(0).get("LBmeanR") != null) {
                            String NsdB = foundData.get(0).get("NsdB").toString();
                            String NsdL = foundData.get(0).get("NsdL").toString();
                            String NsdR = foundData.get(0).get("NsdR").toString();
                            String NmeanB = foundData.get(0).get("NmeanB").toString();
                            String NmeanL = foundData.get(0).get("NmeanL").toString();
                            String NmeanR = foundData.get(0).get("NmeanR").toString();
                            String LBsdB = foundData.get(0).get("LBsdB").toString();
                            String LBsdL = foundData.get(0).get("LBsdL").toString();
                            String LBsdR = foundData.get(0).get("LBsdR").toString();
                            String LBmeanB = foundData.get(0).get("LBmeanB").toString();
                            String LBmeanL = foundData.get(0).get("LBmeanL").toString();
                            String LBmeanR = foundData.get(0).get("LBmeanR").toString();
                            String[] NsdB_array = NsdB.split(",");
                            String[] NsdL_array = NsdL.split(",");
                            String[] NsdR_array = NsdR.split(",");
                            String[] NmeanB_array = NmeanB.split(",");
                            String[] NmeanL_array = NmeanL.split(",");
                            String[] NmeanR_array = NmeanR.split(",");
                            String[] LBsdB_array = LBsdB.split(",");
                            String[] LBsdL_array = LBsdL.split(",");
                            String[] LBsdR_array = LBsdR.split(",");
                            String[] LBmeanB_array = LBmeanB.split(",");
                            String[] LBmeanL_array = LBmeanL.split(",");
                            String[] LBmeanR_array = LBmeanR.split(",");
                            if (NsdB_array.length == 1000
                                    && NsdL_array.length == 1000
                                    && NsdR_array.length == 1000
                                    && NmeanB_array.length == 1000
                                    && NmeanL_array.length == 1000
                                    && NmeanR_array.length == 1000
                                    && LBsdB_array.length == 1000
                                    && LBsdL_array.length == 1000
                                    && LBsdR_array.length == 1000
                                    && LBmeanB_array.length == 1000
                                    && LBmeanL_array.length == 1000
                                    && LBmeanR_array.length == 1000) {
                                h_Nmeanstd = new double[1000][6];
                                h_LBmeanstd = new double[1000][6];
                                for (int i = 0; i < 1000; i++) {
                                    h_Nmeanstd[i][0] = Double.valueOf(NmeanR_array[i]);
                                    h_Nmeanstd[i][1] = Double.valueOf(NsdR_array[i]);
                                    h_Nmeanstd[i][2] = Double.valueOf(NmeanL_array[i]);
                                    h_Nmeanstd[i][3] = Double.valueOf(NsdL_array[i]);
                                    h_Nmeanstd[i][4] = Double.valueOf(NmeanB_array[i]);
                                    h_Nmeanstd[i][5] = Double.valueOf(NsdB_array[i]);
                                    h_LBmeanstd[i][0] = Double.valueOf(LBmeanR_array[i]);
                                    h_LBmeanstd[i][1] = Double.valueOf(LBsdR_array[i]);
                                    h_LBmeanstd[i][2] = Double.valueOf(LBmeanL_array[i]);
                                    h_LBmeanstd[i][3] = Double.valueOf(LBsdL_array[i]);
                                    h_LBmeanstd[i][4] = Double.valueOf(LBmeanB_array[i]);
                                    h_LBmeanstd[i][5] = Double.valueOf(LBsdB_array[i]);
                                }
                            }
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        System.out.println(fault.getMessage());
                    }
                });

    }

    private double[] countAchtung(double[] x, double[] mean, double[]std){

        double[] x_n = new double[x.length];
        for(int i = 0; i < x.length; i++){
            x_n[i] = Math.abs(x[i] - mean[i]);
        }

        return x_n;
    }

    private void plotMeasurement(double[][] xy) {
        setArrows();

        float k = 1.5f;

        int col; //0 - right, 1 - left, 2 - both
        RadioButton radioButtonLung = (RadioButton) radioGroupLung.findViewById(radioGroupLung.getCheckedRadioButtonId());
        if (radioButtonLung == null) {
            col = 2;
        } else {
            if (String.valueOf(radioButtonLung.getText()).charAt(0) == 'R') {
                col = 0;
            } else {
                col = 1;
            }
        }

        if (h_Nmeanstd == null) h_Nmeanstd = new double[1000][6];
        //if (xy == null) xy = new double[1000][2];

        ArrayList<Entry> upperYVals = new ArrayList<>();
        //ArrayList<Entry> lowerYVals = new ArrayList<>();
        //ArrayList<Entry> meanYVals = new ArrayList<>();
        double[] mean = new double[1000];
        double[] sd = new double[1000];
        for (int i = 0; i < 1000; i++) {
            upperYVals.add(new Entry(i, (float) (k * h_Nmeanstd[i][2*col+1])));
            //lowerYVals.add(new Entry(i, (float) (h_Nmeanstd[i][2*col] - k * h_Nmeanstd[i][2*col+1])));
            //meanYVals.add(new Entry(i, (float) (h_Nmeanstd[i][2*col])));
            mean[i] = h_Nmeanstd[i][2*col];
            sd[i] = h_Nmeanstd[i][2*col+1];
        }

        // 0 - measurement, >0 - previous
        ArrayList<Entry> measurementYVals = new ArrayList<>();

        if(xy != null) {

            xy[1] = countAchtung(xy[1], mean, sd);

            for (int i = 0; i < xy[1].length; i++) {
                measurementYVals.add(new Entry(i, (float) (xy[1][i])));
            }

            LineDataSet upperDataSet, measurementDataSet;
            //, lowerDataSet, middleDataSet

            if (linechart.getData() != null && linechart.getData().getDataSetCount() > 0) {
                measurementDataSet = (LineDataSet) linechart.getData().getDataSetByIndex(1);
                measurementDataSet.setValues(measurementYVals);
                upperDataSet = (LineDataSet) linechart.getData().getDataSetByIndex(0);
                upperDataSet.setValues(upperYVals);
                //lowerDataSet = (LineDataSet) linechart.getData().getDataSetByIndex(1);
                //lowerDataSet.setValues(lowerYVals);
                //middleDataSet = (LineDataSet) linechart.getData().getDataSetByIndex(2);
                //middleDataSet.setValues(meanYVals);
                linechart.getData().notifyDataChanged();
                linechart.notifyDataSetChanged();
            } else {
                // create a dataset and give it a type
                upperDataSet = new LineDataSet(upperYVals, "");
                upperDataSet.setLineWidth(0);
                upperDataSet.setCircleSize(0);
                upperDataSet.setValueTextSize(0);
                upperDataSet.setDrawCircleHole(false);
                upperDataSet.setDrawFilled(true);
                upperDataSet.setFillAlpha(255);
                upperDataSet.setDrawValues(false);
                upperDataSet.setFillColor(Color.rgb(230, 230, 230));
                upperDataSet.setCircleColor(Color.TRANSPARENT);
                upperDataSet.setColor(Color.TRANSPARENT);
                upperDataSet.setHighLightColor(Color.TRANSPARENT);
                upperDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                upperDataSet.setFillFormatter(new IFillFormatter() {
                    @Override
                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                        return 0;
                        //return linechart.getAxisLeft().getAxisMinimum();
                    }
                });

                // create a dataset and give it a type
//                lowerDataSet = new LineDataSet(lowerYVals, "");
//                lowerDataSet.setLineWidth(0);
//                lowerDataSet.setCircleSize(0);
//                lowerDataSet.setValueTextSize(0);
//                lowerDataSet.setDrawCircleHole(false);
//                lowerDataSet.setDrawFilled(true);
//                lowerDataSet.setFillAlpha(255);
//                lowerDataSet.setDrawValues(false);
//                lowerDataSet.setFillColor(Color.rgb(230, 230, 230));
//                lowerDataSet.setCircleColor(Color.TRANSPARENT);
//                lowerDataSet.setColor(Color.TRANSPARENT);
//                lowerDataSet.setHighLightColor(Color.TRANSPARENT);
//                lowerDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//                lowerDataSet.setFillFormatter(new IFillFormatter() {
//                    @Override
//                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                        return 0;
//                        //return linechart.getAxisLeft().getAxisMaximum();
//                    }
//                });
//
//                middleDataSet = new LineDataSet(meanYVals, "");
//                middleDataSet.setLineWidth(1);
//                middleDataSet.setCircleSize(0);
//                middleDataSet.setValueTextSize(0);
//                middleDataSet.setDrawCircleHole(false);
//                middleDataSet.setDrawFilled(false);
//                middleDataSet.setDrawValues(false);
//                middleDataSet.setCircleColor(Color.TRANSPARENT);
//                middleDataSet.setColor(Color.BLUE);
//                middleDataSet.setHighLightColor(Color.TRANSPARENT);

                measurementDataSet = new LineDataSet(measurementYVals, "");
                measurementDataSet.setLineWidth(3f);
                measurementDataSet.setCircleSize(0);
                measurementDataSet.setValueTextSize(0);
                measurementDataSet.setDrawCircleHole(false);
                measurementDataSet.setDrawFilled(false);
                measurementDataSet.setDrawValues(false);
                measurementDataSet.setCircleColor(Color.TRANSPARENT);
                measurementDataSet.setColor(Color.RED);
                measurementDataSet.setHighLightColor(Color.TRANSPARENT);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(upperDataSet);
                //dataSets.add(lowerDataSet);
                //dataSets.add(middleDataSet);
                dataSets.add(measurementDataSet);
                LineData lineData = new LineData(dataSets);

                linechart.setData(lineData);

                Legend l = linechart.getLegend();
                l.setEnabled(false);
                linechart.getDescription().setEnabled(false);
            }
        }
        linechart.invalidate();
    }

    private void plotMeasurement2(double[][] xy2) {
        //setArrows();

        float k = 1;

        int col; //0 - right, 1 - left, 2 - both
        RadioButton radioButtonLung = (RadioButton) radioGroupLung.findViewById(radioGroupLung.getCheckedRadioButtonId());
        if (radioButtonLung == null) {
            col = 2;
        } else {
            if (String.valueOf(radioButtonLung.getText()).charAt(0) == 'R') {
                col = 0;
            } else {
                col = 1;
            }
        }

        if (h_LBmeanstd == null) h_LBmeanstd = new double[1000][6];
        //if (xy2 == null) xy2 = new double[1000][2];

        ArrayList<Entry> upperYVals = new ArrayList<>();
        ArrayList<Entry> lowerYVals = new ArrayList<>();
        ArrayList<Entry> meanYVals = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            upperYVals.add(new Entry(i, (float) (h_LBmeanstd[i][2*col] + k * h_LBmeanstd[i][2*col+1])));
            lowerYVals.add(new Entry(i, (float) (h_LBmeanstd[i][2*col] - k * h_LBmeanstd[i][2*col+1])));
            meanYVals.add(new Entry(i, (float) (h_LBmeanstd[i][2*col])));
        }

        // int plot: 0 - measurement, >0 - previous
        ArrayList<Entry> measurementYVals = new ArrayList<>();

        if (xy2 != null){

            for (int i = 0; i < xy2[1].length; i++) {
                measurementYVals.add(new Entry(i, (float) (xy2[1][i])));
            }

            LineDataSet upperDataSet, lowerDataSet, middleDataSet, measurementDataSet;

            if (linechart2.getData() != null && linechart2.getData().getDataSetCount() > 0) {
                measurementDataSet = (LineDataSet) linechart2.getData().getDataSetByIndex(3);
                measurementDataSet.setValues(measurementYVals);
                upperDataSet = (LineDataSet) linechart2.getData().getDataSetByIndex(0);
                upperDataSet.setValues(upperYVals);
                lowerDataSet = (LineDataSet) linechart2.getData().getDataSetByIndex(1);
                lowerDataSet.setValues(lowerYVals);
                middleDataSet = (LineDataSet) linechart2.getData().getDataSetByIndex(2);
                middleDataSet.setValues(meanYVals);
                measurementDataSet.setColor(Color.RED);
                linechart2.getData().notifyDataChanged();
                linechart2.notifyDataSetChanged();

            } else {
                // create a dataset and give it a type
                upperDataSet = new LineDataSet(upperYVals, "");
                upperDataSet.setLineWidth(0);
                upperDataSet.setCircleSize(0);
                upperDataSet.setValueTextSize(0);
                upperDataSet.setDrawCircleHole(false);
                upperDataSet.setDrawFilled(true);
                upperDataSet.setFillAlpha(255);
                upperDataSet.setDrawValues(false);
                upperDataSet.setFillColor(Color.rgb(230, 230, 230));
                upperDataSet.setCircleColor(Color.TRANSPARENT);
                upperDataSet.setColor(Color.TRANSPARENT);
                upperDataSet.setHighLightColor(Color.TRANSPARENT);
                upperDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                upperDataSet.setFillFormatter(new IFillFormatter() {
                    @Override
                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                        return 0;
                        //return linechart.getAxisLeft().getAxisMinimum();
                    }
                });

                // create a dataset and give it a type
                lowerDataSet = new LineDataSet(lowerYVals, "");
                lowerDataSet.setLineWidth(0);
                lowerDataSet.setCircleSize(0);
                lowerDataSet.setValueTextSize(0);
                lowerDataSet.setDrawCircleHole(false);
                lowerDataSet.setDrawFilled(true);
                lowerDataSet.setFillAlpha(255);
                lowerDataSet.setDrawValues(false);
                lowerDataSet.setFillColor(Color.rgb(230, 230, 230));
                lowerDataSet.setCircleColor(Color.TRANSPARENT);
                lowerDataSet.setColor(Color.TRANSPARENT);
                lowerDataSet.setHighLightColor(Color.TRANSPARENT);
                lowerDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                lowerDataSet.setFillFormatter(new IFillFormatter() {
                    @Override
                    public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                        return 0;
                        //return linechart.getAxisLeft().getAxisMaximum();
                    }
                });

                middleDataSet = new LineDataSet(meanYVals, "");
                middleDataSet.setLineWidth(1);
                middleDataSet.setCircleSize(0);
                middleDataSet.setValueTextSize(0);
                middleDataSet.setDrawCircleHole(false);
                middleDataSet.setDrawFilled(false);
                middleDataSet.setDrawValues(false);
                middleDataSet.setCircleColor(Color.TRANSPARENT);
                middleDataSet.setColor(Color.BLUE);
                middleDataSet.setHighLightColor(Color.TRANSPARENT);

                measurementDataSet = new LineDataSet(measurementYVals, "");
                measurementDataSet.setLineWidth(3f);
                measurementDataSet.setCircleSize(0);
                measurementDataSet.setValueTextSize(0);
                measurementDataSet.setDrawCircleHole(false);
                measurementDataSet.setDrawFilled(false);
                measurementDataSet.setDrawValues(false);
                measurementDataSet.setCircleColor(Color.TRANSPARENT);
                measurementDataSet.setColor(Color.RED);
                measurementDataSet.setHighLightColor(Color.TRANSPARENT);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(upperDataSet);
                dataSets.add(lowerDataSet);
                dataSets.add(middleDataSet);
                dataSets.add(measurementDataSet);
                LineData lineData = new LineData(dataSets);

                linechart2.setData(lineData);

                Legend l = linechart2.getLegend();
                l.setEnabled(false);
                linechart2.getDescription().setEnabled(false);

            }
        }

        linechart2.invalidate();
    }

    private void setArrows(){
        if(selectedData.size() > 1){
            next.setEnabled(true);
            next.setAlpha(1.f);
            previous.setEnabled(true);
            previous.setAlpha(1.f);
            if(currentNumber == 0){ //no back button
                previous.setEnabled(false);
                previous.setAlpha(.2f);
            }
            if(currentNumber == (selectedData.size()-1)){ //no next button
                next.setEnabled(false);
                next.setAlpha(.2f);
            }
        } else {
            next.setEnabled(false);
            next.setAlpha(.2f);
            previous.setEnabled(false);
            previous.setAlpha(.2f);
        }
    }

    public class ListAdapter extends BaseAdapter {

        Context context;
        private final String [] names;
        private final int [] images;
        private final int [] background;

        public ListAdapter(Context context, String [] names, int [] images, int [] background){
            super();
            this.context = context;
            this.names = names;
            this.images = images;
            this.background = background;
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;

            final View result;

            if (convertView == null) {

                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.listitem_patient, parent, false);
                viewHolder.txtName = (TextView) convertView.findViewById(R.id.patient_name);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.img_sex);
                viewHolder.background = (LinearLayout) convertView.findViewById(R.id.background);
                result = convertView;
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                result=convertView;
            }

            viewHolder.txtName.setText(names[position]);
            viewHolder.icon.setImageResource(images[position]);
            viewHolder.background.setBackgroundResource(background[position]);

            return convertView;
        }
    }

    private static class ViewHolder {
        TextView txtName;
        ImageView icon;
        LinearLayout background;
    }
}
