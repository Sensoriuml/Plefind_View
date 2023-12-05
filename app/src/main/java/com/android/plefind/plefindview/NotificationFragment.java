package com.android.plefind.plefindview;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.utils.StringUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static java.util.Arrays.asList;

public class NotificationFragment extends Fragment {

    private Context context;
    private int width, height;
    private double[][] h_Nmeanstd, h_LBmeanstd;
    private LinearLayout mainLayout;
    private TextView txt2;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        context.setTheme(android.R.style.Theme_Holo_Light);
        width = getActivity().getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        height = getActivity().getApplicationContext().getResources().getDisplayMetrics().heightPixels;

        //getGraphFromCloud("achtung");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainLayout = view.findViewById(R.id.notificationLayout);

        LinearLayout linhor = new LinearLayout(context);
        linhor.setOrientation(LinearLayout.HORIZONTAL);
        linhor.setPadding(10,20,10,0);
        mainLayout.addView(linhor);

        LinearLayout.LayoutParams linhorparams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, .5f);
        linhorparams.gravity = Gravity.CENTER;

        LinearLayout linver = new LinearLayout(context);
        linver.setOrientation(LinearLayout.VERTICAL);
        linhor.addView(linver, linhorparams);

        TextView txt1 = new TextView(context);
        txt1.setText("Number of objects");
        txt1.setGravity(Gravity.CENTER);
        linver.addView(txt1);

        txt2 = new TextView(context);
        txt2.setText("");
        txt2.setGravity(Gravity.CENTER);
        linver.addView(txt2);

        final Button synchronize = new Button(context);
        synchronize.setText("SYNCHRONIZE");
        synchronize.setGravity(Gravity.CENTER);
        synchronize.setBackgroundResource(R.drawable.rounded_button);
        linhor.addView(synchronize, linhorparams);

        progressBar = new ProgressBar(context);
        progressBar.setPadding(0, 40, 0, 0) ;
        progressBar.setVisibility(View.GONE);
        mainLayout.addView(progressBar);

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "PatientID IS NULL" );
        Backendless.Data.of( "LABdata" ).getObjectCount( queryBuilder,
                new AsyncCallback<Integer>()
                {
                    @Override
                    public void handleResponse(final Integer integer )
                    {
                        Log.i( "MYAPP", "found objects " + integer );
                        txt2.setText(Integer.toString(integer));
                        if(integer > 0){
                            synchronize.setEnabled(true);
                            synchronize.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    for (int i = 0; i < (integer + 5); i = i + 5){
                                        synchronizing(i);
                                    }
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            });
                        } else {
                            synchronize.setEnabled(false);
                        }
                    }

                    @Override
                    public void handleFault( BackendlessFault backendlessFault )
                    {
                        Log.i( "MYAPP", "error - " + backendlessFault.getMessage() );
                    }
                } );
    }

    private void synchronizing(int offset){
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(5);
        queryBuilder.setOffset(offset);
        queryBuilder.setWhereClause( "PatientID IS NULL" );
        queryBuilder.addSortBy("DateMeasurement ASC");
        Backendless.Data.of( "LABdata" ).find( queryBuilder,
                    new AsyncCallback<List<Map>>(){
                        @Override
                        public void handleResponse( List<Map> found )
                        {
                            for (final Map record : found){
                                String pesel = (String) record.get("id");
                                boolean valpesel = false;
                                if (pesel != null && pesel.length() == 11)
                                    valpesel = peselValidator(pesel);
                                System.out.println(valpesel);
                                if (valpesel) {
                                    String whereClause = "Identyfikator = '" + pesel + "'";
                                    DataQueryBuilder queryBuilder1 = DataQueryBuilder.create();
                                    queryBuilder1.setWhereClause(whereClause);
                                    Backendless.Data.of("PATIENTdata").find(queryBuilder1,
                                            new AsyncCallback<List<Map>>() {
                                                @Override
                                                public void handleResponse(List<Map> found) {
                                                    if (found.size() == 0) {
                                                        Backendless.Data.of("PATIENTdata").save(getPatient(record), new AsyncCallback<Map>() {
                                                            public void handleResponse(Map response) {
                                                                Backendless.Data.of("PATIENTdata").save(response, new AsyncCallback<Map>() {
                                                                    public void handleResponse(Map response) {

                                                                        System.out.println("wysłany pacjent");
                                                                        String patientID = response.get("objectId").toString();
                                                                        record.put("patientID", patientID);
                                                                        record.put("id", null);
                                                                        Backendless.Data.of("LABdata").save(record, new AsyncCallback<Map>() {
                                                                            @Override
                                                                            public void handleResponse(Map response) {
                                                                                System.out.println("updated");
                                                                            }

                                                                            @Override
                                                                            public void handleFault(BackendlessFault fault) {
                                                                                System.out.println(fault.getMessage());
                                                                            }
                                                                        });
                                                                    }

                                                                    public void handleFault(BackendlessFault fault) {
                                                                        System.out.println("nie wysłany pacjent");
                                                                    }
                                                                });
                                                            }

                                                            public void handleFault(BackendlessFault fault) {
                                                                System.out.println("nie wysłany pacjent");
                                                            }
                                                        });
                                                    } else {
                                                        final String patientID = found.get(0).get("objectId").toString();
                                                        Map<String, Object> tempfound = getPatient(record);
                                                        tempfound.put("objectId", patientID);
                                                        Backendless.Data.of("PATIENTdata").save(tempfound, new AsyncCallback<Map>() {
                                                            public void handleResponse(Map response) {
                                                                record.put("patientID", patientID);
                                                                record.put("id", null);
                                                                Backendless.Data.of("LABdata").save(record, new AsyncCallback<Map>() {
                                                                    @Override
                                                                    public void handleResponse(Map response) {
                                                                        System.out.println("updated");
                                                                    }

                                                                    @Override
                                                                    public void handleFault(BackendlessFault fault) {
                                                                        System.out.println(fault.getMessage());
                                                                    }

                                                                });
                                                            }

                                                            public void handleFault(BackendlessFault fault) {
                                                                System.out.println(fault.getMessage());
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault fault) {
                                                    System.out.println(fault.getMessage());
                                                }
                                            });
                                } else {
                                    //tworzy się nowy pacjent za każdym razem
                                    /*Backendless.Data.of("PATIENTdata").save(getPatient(record), new AsyncCallback<Map>() {
                                        public void handleResponse(Map response) {
                                            System.out.println("wysłany pacjent");
                                            patientID = response.get("objectId").toString();
                                        }

                                        public void handleFault(BackendlessFault fault) {
                                            System.out.println("nie wysłany pacjent");
                                        }
                                    });*/
                                    record.put("patientID", "9ED84070-CF20-F6E0-FF5C-A68FEA114A00");
                                    record.put("id", null);
                                    Backendless.Data.of("LABdata").save(record, new AsyncCallback<Map>() {
                                        @Override
                                        public void handleResponse(Map response) {
                                            System.out.println("updated");
                                        }

                                        @Override
                                        public void handleFault(BackendlessFault fault) {
                                            System.out.println(fault.getMessage());
                                        }
                                    });
                                }
                            }

                            new CountDownTimer(1000, 100) {
                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                    refreshNumberOfObject();
                                }
                            }.start();

                        }

                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            System.out.println(fault.getMessage());
                        }
                    });
    }

    private Map<String, Object> getPatient(Map record){
        Map<String, Object> patient = new HashMap();
        patient.put("Identyfikator", record.get("id"));
        patient.put("achtung", record.get("achtung"));
        if (record.get("name") != null)
            patient.put("name", record.get("name"));
        if (record.get("Sex") != null)
            patient.put("sex", record.get("Sex"));
        if (record.get("height") != null)
            patient.put("height", record.get("height"));

        return patient;
    }

    private boolean peselValidator(String pesel){
        int sum = 1 * Character.getNumericValue(pesel.charAt(0)) +
                3 * Character.getNumericValue(pesel.charAt(1)) +
                7 * Character.getNumericValue(pesel.charAt(2)) +
                9 * Character.getNumericValue(pesel.charAt(3)) +
                1 * Character.getNumericValue(pesel.charAt(4)) +
                3 * Character.getNumericValue(pesel.charAt(5)) +
                7 * Character.getNumericValue(pesel.charAt(6)) +
                9 * Character.getNumericValue(pesel.charAt(7)) +
                1 * Character.getNumericValue(pesel.charAt(8)) +
                3 * Character.getNumericValue(pesel.charAt(9));
        sum %= 10;
        sum = 10 - sum;
        sum %= 10;

        System.out.println(1 * Character.getNumericValue(pesel.charAt(0)));

        if (sum == Character.getNumericValue(pesel.charAt(10))) {
            return true;
        }
        else {
            return false;
        }
    }

    private void refreshNumberOfObject(){

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( "PatientID IS NULL" );
        Backendless.Data.of( "LABdata" ).getObjectCount( queryBuilder,
                new AsyncCallback<Integer>()
                {
                    @Override
                    public void handleResponse( Integer integer )
                    {
                        Log.i( "MYAPP", "found objects " + integer );
                        txt2.setText(Integer.toString(integer));
                        if (integer == 0){
                            progressBar.setVisibility(View.GONE);
                            BottomNavigationView bottomNav = getActivity().getWindow().findViewById(R.id.bottom_navigation);
                            bottomNav.removeBadge(bottomNav.getMenu().getItem(2).getItemId());

                        }
                    }

                    @Override
                    public void handleFault( BackendlessFault backendlessFault )
                    {
                        Log.i( "MYAPP", "error - " + backendlessFault.getMessage() );
                    }
                } );

    }

    private boolean countAchtung(double[] x, double[] mean, double[]std){

        boolean achtung;
        double[] x_n = new double[x.length];
        double max = 0;
        for(int i = 80; i < x.length-200; i++){
            x_n[i] = Math.abs(x[i] - mean[i]);
            if(x_n[i] - 1.5*std[i] > max) max = x_n[i] - 1.5*std[i];
        }

        if(max != 0) achtung = true;
        else achtung = false;

        return achtung;
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

    private double[] sub_1st_el(double[] y2) {
        double y0 = y2[0];
        for (int i = 0; i < y2.length; i++) {
            y2[i] = y2[i] - y0;
        }

        return y2;
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
                                System.out.println("getgraph");
                            }
                        }

                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                        queryBuilder.setPageSize(100);
                        queryBuilder.setOffset(500);
                        queryBuilder.setSortBy("created DESC");
                        Backendless.Data.of("LABdata").find(queryBuilder, new AsyncCallback<List<Map>>() {
                        @Override
                        public void handleResponse(List<Map> found) {

                            for (Map record : found) {
                                    String name = (String) record.get("name");
                                    String[] parts = name.split("_");
                                    String newname = "";
                                    for (int ii = 0; ii < parts.length - 2; ii++){
                                        newname += parts[ii];
                                        if (ii !=  (parts.length - 3)) newname += "_";
                                    }
                                    //String hour = parts[parts.length - 1];
                                    //String date = parts[parts.length - 2];
                                    //String dtStart = date + " " + hour;
                                    //SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
                                    //Date datedate = new Date();
                                    //try {
                                    //    datedate = format.parse(dtStart);
                                    //    System.out.println("Date -> " + datedate);
                                    //} catch (ParseException e) {
                                    //    e.printStackTrace();
                                    //}

                                    record.put("name", newname);

                                String meas = (String) record.get("measurement");
                                String lung = (String) record.get("Lung");
                                Vector v0 = new Vector();
                                Vector v2 = new Vector();

                                ArrayList aList = new ArrayList(asList(meas.split("\n")));
                                for (int i = 0; i < aList.size(); i++) {
                                    ArrayList commaList = new ArrayList(asList(aList.get(i).toString().split(",")));
                                    //System.out.println(commaList.toString());
                                    if (commaList.size() > 3) {
                                        if (Integer.valueOf(commaList.get(2).toString()) != 0 && Integer.valueOf(commaList.get(4).toString()) != 0) {
                                            v0.add(commaList.get(2));
                                            v2.add(commaList.get(4));
                                        }
                                    }
                                }
                                double[][] xy = countData_Plot(v0, v2);

                                float k = 1.5f;

                                int col; //0 - right, 1 - left, 2 - both
                                if (lung == null) {
                                    col = 2;
                                } else {
                                    if (lung.equals("R")) {
                                        col = 0;
                                    } else {
                                        col = 1;
                                    }
                                }
                                //System.out.println(col);
                                double[] mean = new double[1000];
                                double[] sd = new double[1000];
                                for (int i = 0; i < 1000; i++) {
                                    mean[i] = h_Nmeanstd[i][2 * col];
                                    sd[i] = h_Nmeanstd[i][2 * col + 1];
                                }

                                boolean achtung = countAchtung(xy[1], mean, sd);
                                System.out.println(achtung);
                                record.put("achtung", achtung);
                                Backendless.Data.of("LABdata").save(record, new AsyncCallback<Map>() {
                                    @Override
                                    public void handleResponse(Map response) {
                                        System.out.println("updated");
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        System.out.println(fault.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            System.out.println(fault.getMessage());
                        }
                    });
                }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        System.out.println(fault.getMessage());

                    }
                });
    }


}