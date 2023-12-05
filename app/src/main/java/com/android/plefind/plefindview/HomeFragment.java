package com.android.plefind.plefindview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class HomeFragment extends Fragment {

    private Context context;
    private int width;
    private int height;

    private LinearLayout mainlayout;
    public static String priv = "";
    public static String objectid = "";

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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Backendless.setUrl(getResources().getString(R.string.SERVER_URL));
        Backendless.initApp(getContext(), getResources().getString(R.string.APPLICATION_ID), getResources().getString(R.string.API_KEY));

        mainlayout = view.findViewById(R.id.layout_home);
        mainlayout.setGravity(Gravity.CENTER_VERTICAL);

        int hb = (int) (0.093 * height);
        int hb2 = 105 * height / 1280;
        float scale = getResources().getDisplayMetrics().density;
        //float font12 = 10.64f * height * 0.72f / 480 / scale;
        //float font10 = 8.f * height * 0.72f / 480 / scale;
        final boolean[] emailisnull = {true};
        final boolean[] passwordisnull = {true};
        final Button register = new Button(context);
        final Button login = new Button(context);

        LinearLayout.LayoutParams llparamsaux = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparamsaux.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        llparamsaux.topMargin = 0;

        final LinearLayout.LayoutParams layep = new LinearLayout.LayoutParams(672*width/805, hb);
        layep.gravity = Gravity.CENTER;

        ContextThemeWrapper themedContext = new ContextThemeWrapper(context, R.style.AppTextTheme);
        TextInputLayout emaillay = new TextInputLayout(themedContext);
        mainlayout.addView(emaillay, layep);
        final TextInputEditText emailtext = new TextInputEditText(emaillay.getContext());
        emailtext.setHint("E-mail");
        emailtext.setSingleLine(true);
        //emailtext.setPadding(10,0,0,0);
        //emailtext.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.64f);
        //emailtext.setTextSize(TypedValue.COMPLEX_UNIT_PT, font12);
        emailtext.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        TextWatcher textWatcheremail = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (emailtext.getText().length() != 0){
                    emailisnull[0] = false;
                    if (!passwordisnull[0]) {
                        register.setEnabled(true);
                        register.setAlpha(1.f);
                        login.setEnabled(true);
                        login.setAlpha(1.f);
                    }
                } else {
                    register.setEnabled(false);
                    register.setAlpha(.5f);
                    login.setEnabled(false);
                    login.setAlpha(.5f);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        emailtext.addTextChangedListener(textWatcheremail);
        emaillay.addView(emailtext);

        TextInputLayout passwordlay = new TextInputLayout(themedContext);
        passwordlay.setPasswordVisibilityToggleEnabled(true);
        mainlayout.addView(passwordlay, layep);
        final TextInputEditText passwordtext = new TextInputEditText(passwordlay.getContext());
        passwordtext.setHint("Password");
        passwordtext.setSingleLine(true);
        //passwordtext.setTextSize(TypedValue.COMPLEX_UNIT_PT, font12);
        passwordtext.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        TextWatcher textWatcherpassword = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (passwordtext.getText().length() != 0){
                    passwordisnull[0] = false;
                    if (!emailisnull[0]) {
                        register.setEnabled(true);
                        register.setAlpha(1.f);
                        login.setEnabled(true);
                        login.setAlpha(1.f);
                    }
                } else {
                    register.setEnabled(false);
                    register.setAlpha(.5f);
                    login.setEnabled(false);
                    login.setAlpha(.5f);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        passwordtext.addTextChangedListener(textWatcherpassword);
        passwordlay.addView(passwordtext);

        Button forgotButton = new Button(context);
        forgotButton.setBackgroundColor(Color.TRANSPARENT);
        forgotButton.setText("Forgot your password?");
        forgotButton.setTextColor(Color.BLACK);
        forgotButton.setPadding(0, 0, 0, hb/7);
        forgotButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(forgotButton, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog resetdialog = new Dialog(context);

                float radius = 20*height/800;
                //float font20 = (float) (0.02 * h + 4.2 + 8);
                float scale = getResources().getDisplayMetrics().density;
                float font17 = 15.f * width * 0.72f / 480 / scale;
                float font18 = 16.f * width * 0.72f / 480 / scale;
                float font15 = 13.f * width * 0.72f / 480 / scale;

                resetdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                GradientDrawable shapereset =  new GradientDrawable();
                shapereset.setCornerRadius( radius );
                shapereset.setColor(Color.rgb(254,251,240));
                int w = 2*width/3;
                int h = 2*width/3;
                shapereset.setSize(w, h);
                resetdialog.getWindow().setBackgroundDrawable(shapereset);

                RelativeLayout rlres = new RelativeLayout(context);
                resetdialog.setContentView(rlres);
                resetdialog.getWindow().setLayout(w, h);

                LinearLayout linres = new LinearLayout(context);
                linres.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                rlres.addView(linres, linLayoutParam);

                LinearLayout.LayoutParams llpar1 = new LinearLayout.LayoutParams(w, h/4);
                LinearLayout.LayoutParams llpar2 = new LinearLayout.LayoutParams(w, h/4);
                LinearLayout.LayoutParams llpar3 = new LinearLayout.LayoutParams(9*w/10, h/4);
                llpar3.topMargin = w/5/5;
                llpar3.gravity = Gravity.CENTER;
                LinearLayout linhres = new LinearLayout(context);
                linhres.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams llpar4 = new LinearLayout.LayoutParams(w, ViewGroup.LayoutParams.FILL_PARENT);
                llpar4.bottomMargin = h/5/20;
                LinearLayout.LayoutParams llpar4b = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT, 0.5f);

                TextView title = new TextView(context);
                title.setText("Reset password");
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.DKGRAY);
                //TextViewCompat.setAutoSizeTextTypeWithDefaults(title, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                title.setTextSize(TypedValue.COMPLEX_UNIT_PT, font17);
                title.setPadding( w/20, h/5/20, w/20, 0);
                linres.addView(title, llpar1);

                TextView request = new TextView(context);
                request.setText("Please enter the e-mail address associated with your account");
                request.setGravity(Gravity.CENTER);
                request.setTextColor(Color.DKGRAY);
                //TextViewCompat.setAutoSizeTextTypeWithDefaults(request, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                request.setTextSize(TypedValue.COMPLEX_UNIT_PT, font15);
                request.setPadding( w/20, 0, w/20, h/5/10);
                linres.addView(request, llpar2);

                ContextThemeWrapper themedContextres = new ContextThemeWrapper(context, R.style.AppTheme);
                TextInputLayout emailresetlay = new TextInputLayout(themedContextres);
                //emailresetlay.setBoxStrokeColor(Color.BLACK);
                linres.addView(emailresetlay, llpar3);
                final TextInputEditText emailrestext = new TextInputEditText(emailresetlay.getContext());
                emailrestext.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
                emailrestext.setTextSize(TypedValue.COMPLEX_UNIT_PT, font18);
                emailrestext.setHint("E-mail");
                emailrestext.setSingleLine(true);
                //emailrestext.setTextColor(Color.BLACK);
                //emailrestext.setHintTextColor(Color.BLACK);
                //emailrestext.setLinkTextColor(Color.BLACK);
                emailresetlay.addView(emailrestext);

                linres.addView(linhres, llpar4);

                Button resetbuttoncancel = new Button(context);
                resetbuttoncancel.setText("CANCEL");
                //resetbuttoncancel.setGravity(Gravity.FILL_HORIZONTAL);
                resetbuttoncancel.setBackgroundColor(Color.TRANSPARENT);
                resetbuttoncancel.setTextColor(Color.RED);
                resetbuttoncancel.setGravity(Gravity.CENTER);
                //resetbuttoncancel.setPadding(0, h/20, 0, h/20);
                //TextViewCompat.setAutoSizeTextTypeWithDefaults(resetbuttoncancel, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                resetbuttoncancel.setTextSize(TypedValue.COMPLEX_UNIT_PT, font17);
                resetbuttoncancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetdialog.dismiss();
                    }
                });
                linhres.addView(resetbuttoncancel, llpar4b);

                Button resetbuttonok = new Button(context);
                resetbuttonok.setText("OK");
                //resetbuttonok.setGravity(Gravity.FILL_HORIZONTAL);
                resetbuttonok.setBackgroundColor(Color.TRANSPARENT);
                resetbuttonok.setTextColor(Color.BLUE);
                resetbuttonok.setGravity(Gravity.CENTER);
                //resetbuttonok.setPadding(0, h/20, 0, h/20);
                //TextViewCompat.setAutoSizeTextTypeWithDefaults(resetbuttonok, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                resetbuttonok.setTextSize(TypedValue.COMPLEX_UNIT_PT, font17);
                resetbuttonok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Backendless.UserService.restorePassword( emailrestext.getText().toString(), new AsyncCallback<Void>()
                        {
                            public void handleResponse( Void response )
                            {
                                // Backendless has completed the operation - an email has been sent to the user
                                openErrorDialog("An e-mail has been sent");
                                resetdialog.dismiss();
                            }

                            public void handleFault( BackendlessFault fault )
                            {
                                // password revovery failed, to get the error code call fault.getCode()
                                openErrorDialog(fault.getMessage());
                            }
                        });
                    }
                });
                linhres.addView(resetbuttonok, llpar4b);

                resetdialog.show();

            }
        });
        //mainlayout.addView(forgotButton, llparamsaux);

        LinearLayout linh = new LinearLayout(context);
        linh.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linhParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.49f);
        LinearLayout.LayoutParams linhParam2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.02f);
        LinearLayout.LayoutParams llh = new LinearLayout.LayoutParams(672*width/805, LinearLayout.LayoutParams.WRAP_CONTENT);
        llh.gravity = Gravity.CENTER_HORIZONTAL;
        mainlayout.addView(linh, llh);

        register.setText("REGISTER");
        //register.setTextColor(Color.WHITE);
        register.setEnabled(false);
        register.setAlpha(.5f);
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(register, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        //register.setTextSize(TypedValue.COMPLEX_UNIT_PT, font12);
        register.setGravity(Gravity.CENTER);
        register.setBackgroundResource(R.drawable.rounded_button_login);
        register.setPadding(0,0,0,0);
        //register.setHeight(2*w/4*160/988);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailtext.getText().toString();
                String password = passwordtext.getText().toString();
                registration(email, password);
            }
        });
        linh.addView(register, linhParam);

        Button space = new Button(context);
        space.setVisibility(View.INVISIBLE);
        linh.addView(space, linhParam2);

        login.setText("LOGIN");
        //login.setTextColor(Color.WHITE);
        login.setEnabled(false);
        login.setAlpha(.5f);
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(login, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        //login.setTextSize(TypedValue.COMPLEX_UNIT_PT, font12);
        login.setGravity(Gravity.CENTER);
        login.setBackgroundResource(R.drawable.rounded_button_login);
        login.setPadding(0,0,0,0);
        //login.setHeight(2*w/4*160/988);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailtext.getText().toString();
                String password = passwordtext.getText().toString();
                loginBackendless(email, password);
            }
        });
        linh.addView(login, linhParam);

    }

    private void openErrorDialog(String errortext) {

        final Dialog alertDialog = new Dialog(context);

        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        GradientDrawable shapealert =  new GradientDrawable();
        shapealert.setColor(Color.rgb(254,251,240));
        int w = (int) (width*0.618034f);
        int h = (int) (w*0.75f);//0.618034);//3*w/4;

        float radius = 20*height/800;
        //float font20 = (float) (0.02 * h + 4.2 + 8);
        float scale = getResources().getDisplayMetrics().density;
        float font15 = 14.5f * width * 0.72f / 480 / scale;
        //float font17 = 17.f * width * 0.72f / 480;

        shapealert.setSize(w, h);
        shapealert.setCornerRadius( radius );
        alertDialog.getWindow().setBackgroundDrawable(shapealert);

        RelativeLayout rl = new RelativeLayout(context);
        alertDialog.setContentView(rl);
        alertDialog.getWindow().setLayout(w, h);

        LinearLayout linr = new LinearLayout(context);
        linr.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rl.addView(linr, linLayoutParam);

        LinearLayout.LayoutParams lay1 = new LinearLayout.LayoutParams(w, LinearLayout.LayoutParams.FILL_PARENT);
        lay1.bottomMargin = 2*h/3/10;
        LinearLayout.LayoutParams lay2 = new LinearLayout.LayoutParams(w, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView msg = new TextView(context);
        msg.setText(errortext);
        msg.setGravity(Gravity.CENTER);
        msg.setTextColor(Color.DKGRAY);
        msg.setTextSize(TypedValue.COMPLEX_UNIT_PT, font15);
        msg.setPadding(w/10, 2*h/3/10, w/10, 2*h/3/10);
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(msg, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        linr.addView(msg, lay2);

        Button okbutton = new Button(context);
        okbutton.setText("OK");
        okbutton.setTextColor(Color.BLUE);
        okbutton.setTextSize(TypedValue.COMPLEX_UNIT_PT, font15);
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(okbutton, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        okbutton.setBackgroundColor(Color.TRANSPARENT);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        linr.addView(okbutton, lay1);

        alertDialog.show();
    }

    private void registration(final String email, final String password){

        //initBackendless();
        BackendlessUser user = new BackendlessUser();
        user.setEmail( email );
        user.setPassword( password );
        //user.setProperty("birthdate", birthdate.getTime());
        //user.setProperty("sex", sex);

        Backendless.UserService.register( user, new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse(BackendlessUser registeredUser)
            {
                Backendless.UserService.login( email, password, new AsyncCallback<BackendlessUser>()
                {
                    public void handleResponse( BackendlessUser user )
                    {
                        priv = (String) user.getProperty("privileges");
                        objectid = user.getObjectId();

                        MainActivity.bottomNav.setSelectedItemId(R.id.nav_cloud);
                        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CloudFragment()).commit();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        openErrorDialog(fault.getMessage());
                    }
                });

                //login();
                //Log.d("reg", "successful reg");
            }

            public void handleFault(BackendlessFault fault)
            {
                openErrorDialog(fault.getMessage());
                //login();
                //Log.d("reg", "fault");
            }

        } );
    }

    private void loginBackendless(String email, String password){

        Backendless.UserService.login( email, password, new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse( BackendlessUser user )
            {
                priv = (String) user.getProperty("privileges");
                objectid = user.getObjectId();

                MainActivity.bottomNav.setSelectedItemId(R.id.nav_cloud);
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CloudFragment()).commit();
            }

            public void handleFault( BackendlessFault fault )
            {
                Log.d("log", fault.getMessage());
                openErrorDialog(fault.getMessage());
                //allToCloud();
                // login failed, to get the error code call fault.getCode()
            }
        });
    }

}
