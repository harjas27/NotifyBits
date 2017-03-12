package in.wolfpack.notifybits;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.Toast;

//import com.pilanify.databaseapp.adapter.ListProductAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;

//import com.google.api.services.calendar.model.Calendar;
//import com.pilanify.databaseapp.adapter.ListProductAdapter;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Reminders;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.provider.CalendarContract.Calendars;

import java.io.IOException;
import java.util.*;

import android.database.Cursor;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import java.util.Calendar;


/**
 * Created by harjas on 24-07-2016.
 */
public class first extends Activity implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public int lid;
    public int tid;
    public int pid;
    public int cid;
    private static final String BUTTON_TEXT = "Add Event";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    EditText lecsectionnotext;
    EditText tutsectionnonotext;
    EditText praccticalsectionnonotext;
    EditText coursenotext;
    Button add;
    Button show;
    Button details;
    private ListView lvProduct;
    //public  ListProductAdapter adapter;
    public List<Product> mProductList;
    private DatabaseHelper mDBHelper;
    public String temp1 = "";
    public static String courseName = "";
    public String instructorName = "";
    public String temp2 = "";
    public String roomNo = "";
    public static String dayNo = "";
    public static String hourNo = "";
    public String temp3 = "";
    public String sectionNo = "";
    public String accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //database
        //lvProduct = (ListView)findViewById(R.id.listview_product);
        mDBHelper = new DatabaseHelper(this);

        //Check exists database
        File database = getApplicationContext().getDatabasePath(DatabaseHelper.DBNAME);
        if (false == database.exists()) {
            mDBHelper.getReadableDatabase();
            //Copy db
            if (copyDatabase(this)) {
                Toast.makeText(this, "Copy database succes", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Copy data error", Toast.LENGTH_SHORT).show();
                //return;
            }
        }
        //Get product list in db when db exists
        mProductList = mDBHelper.getListProduct();
        //Init adapter
        //adapter = new ListProductAdapter(this, mProductList);
        //Set adapter for listview
        //lvProduct.setAdapter(adapter);
        //end of database
        add = (Button) findViewById(R.id.add);
        show=(Button)findViewById(R.id.timetable);
        details=(Button)findViewById(R.id.details);
        lecsectionnotext = (EditText) findViewById(R.id.lecsectionnotext);
        tutsectionnonotext = (EditText) findViewById(R.id.tutsectionnonotext);
        praccticalsectionnonotext = (EditText) findViewById(R.id.praccticalsectionnonotext);
        coursenotext = (EditText) findViewById(R.id.coursenotext);
        // lecsectionnotext.setText(mProductList.get(25).getPrice());
        // tutsectionnonotext.setText(mProductList.get(25).getTitle());

        //tutsectionnonotext.setEnabled(false);
        //praccticalsectionnonotext.setEnabled(false);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lec=0,prac=0,tut =0;
                if(checkCourse())
                {
                    if (lecsectionnotext.getText().toString().trim().isEmpty() && tutsectionnonotext.getText().toString().trim().isEmpty() && praccticalsectionnonotext.getText().toString().trim().isEmpty()) {
                        Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                        toast.setText("Please fill at least one from Lecture, Tutorial and/or Practical");
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        //other setters
                        toast.show();
                    }
                    else
                    {
                        if(!lecsectionnotext.getText().toString().trim().isEmpty())
                        {
                            if(checkLec())
                            {
                                lec=2;
                            }
                            else
                            {
                                Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                                toast.setText("No such lecture section");
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                //other setters
                                toast.show();
                                lec=1;
                            }
                            //lec=true;
                        }
                        if(!tutsectionnonotext.getText().toString().trim().isEmpty())
                        {
                            if(checkTut())
                            {
                                tut=2;
                            }
                            else
                            {
                                Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                                toast.setText("No such tutorial section");
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                //other setters
                                toast.show();
                                tut=1;
                            }
                        }
                        if(!praccticalsectionnonotext.getText().toString().trim().isEmpty())
                        {
                            if(checkPrac())
                            {
                                prac=2;
                            }
                            else
                            {
                                Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                                toast.setText("No such practical section");
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                //other setters
                                toast.show();
                                prac=1;
                            }
                        }
                    }
                    if( (lec!=1  && prac!=1 && tut!=1) && !(lec==0  && prac==0 && tut==0)  )
                    {
                        Details s = new Details();
                        if(lec==2)
                        {
                            StudentdbHelper st = new StudentdbHelper(first.this);
                            st.addContact(mProductList.get(lid));
                        }
                        if(prac==2)
                        {
                            StudentdbHelper st = new StudentdbHelper(first.this);
                            st.addContact(mProductList.get(pid));
                        }
                        if(tut==2)
                        {
                            StudentdbHelper st = new StudentdbHelper(first.this);
                            st.addContact(mProductList.get(tid));
                        }
                        getResultsFromApi();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                    toast.setText("Invalid Course Number");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //other setters
                    toast.show();
                }
            }
        });

        show.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(first.this,TimeTable.class);
                startActivity(i);
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(first.this, Details.class);
                startActivity(i);
            }
        });
        /*add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add.setEnabled(false);
                //mOutputText.setText("");
                boolean l = true, t = true, p = true;
                if (searchcourse()) {
                    if (lecsectionnotext.getText().toString().trim().isEmpty() && tutsectionnonotext.getText().toString().trim().isEmpty() && praccticalsectionnonotext.getText().toString().trim().isEmpty()) {
                        Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                        toast.setText("Please fill at least one from Lecture, Tutorial and/or Practical");
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        //other setters
                        toast.show();

                    } else {
                        if (checkproject()) {
                            Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                            toast.setText("THIS COURSE CANNOT BE ADDED TO CALENDER");
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            //other setters
                            toast.show();
                        } else {
                            if (checkcourse()) {
                                if (!lecsectionnotext.getText().toString().trim().isEmpty())
                                    l = false;
                                if (!tutsectionnonotext.getText().toString().trim().isEmpty())
                                    t = false;
                                if (!praccticalsectionnonotext.getText().toString().trim().isEmpty()) {
                                    if (searchspecial())
                                        p = true;
                                    else
                                        p = false;
                                }
                            } else {
                                if (!lecsectionnotext.getText().toString().trim().isEmpty()) {
                                    if (searchlec())
                                        l = true;
                                    else
                                        l = false;
                                }
                                if (!tutsectionnonotext.getText().toString().trim().isEmpty()) {
                                    if (searchtut())
                                        t = true;
                                    else
                                        t = false;
                                }
                                if (!praccticalsectionnonotext.getText().toString().trim().isEmpty()) {
                                    if (searchprac())
                                        p = true;
                                    else
                                        p = false;
                                }
                            }

                            if (l && p && t) {
                                getResultsFromApi();
                            } else {
                                Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                                toast.setText("YOUR ENTRIES ARE NOT VALID");
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                //other setters
                                toast.show();
                            }
                        }
                    }
                } else {
                    Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                    toast.setText("INVALID COURSE NUMBER");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //other setters
                    toast.show();
                }
                add.setEnabled(true);
            }
        });*/
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Updating your Calendar");
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

    }

    //validations begin
    public boolean checkCourse()
    {
        int f=1;
        for (int i = 0; i < mProductList.size(); i++)
        {
            if (mProductList.get(i).getNumber().equals(coursenotext.getText().toString().trim())) {
                f = 0;
                cid = i;
                break;
            }
        }
        if(f==1)
            return false;
        else
            return true;
    }

    public boolean checkLec()
    {
        int f=1;
        for (int i = cid; i < mProductList.size(); i++)
        {
            if (mProductList.get(i).getNumber().equals(coursenotext.getText().toString().trim()))
            {
                if (mProductList.get(i).getLecture() == 1)
                {
                    if (mProductList.get(i).getSec().equals(lecsectionnotext.getText().toString().trim()))
                    {
                        lid=i;
                        f=0;
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
        if(f==1)
            return false;
        else
            return true;
    }

    public boolean checkPrac()
    {
        int f=1;
        int j=0;
        for (int i = cid; i < mProductList.size(); i++)
        {
            if (mProductList.get(i).getPractical() == 1)
            {
                j=i;
                break;
            }
        }
        for (int i = j; i < mProductList.size(); i++)
        {
            if (mProductList.get(i).getNumber().equals(coursenotext.getText().toString().trim()))
            {
                if (mProductList.get(i).getPractical() == 1)
                {
                    if (mProductList.get(i).getSec().equals(praccticalsectionnonotext.getText().toString().trim()))
                    {
                        f=0;
                        pid=i;
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
        if(f==1)
            return false;
        else
            return true;
    }

    public boolean checkTut()
    {
        int f=1;
        int j=0;
        for (int i = cid; i < mProductList.size(); i++)
        {
            if (mProductList.get(i).getTutorial() == 1)
            {
                j=i;
                break;
            }
        }
        for (int i = j; i < mProductList.size(); i++)
        {
            if (mProductList.get(i).getNumber().equals(coursenotext.getText().toString().trim()))
            {
                if (mProductList.get(i).getTutorial() == 1)
                {
                    if (mProductList.get(i).getSec().equals(tutsectionnonotext.getText().toString().trim()))
                    {
                        f=0;
                        tid=i;
                        break;
                    }
                }
                else
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
        if(f==1)
            return false;
        else
            return true;
    }
    /**public boolean searchcourse() {
        // View x;
        int f = 1;
        for (int i = 0; i < mProductList.size(); i++) {

            if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim())) {
                f = 0;
                cid = i;
                break;
            }
        }
        if (f == 0)
            return true;
        else
            return false;
    }

    public boolean checkproject() {
        temp1 = mProductList.get(cid).getDescription();
        temp2 = mProductList.get(cid).getDescription2();
        int k = temp1.indexOf(';');
        courseName = temp1.substring(0, k);
        instructorName = temp1.substring(k + 1, temp1.length());
        k = temp2.indexOf(';');
        int k2 = temp2.lastIndexOf(';');
        roomNo = temp2.substring(0, k);
        dayNo = temp2.substring(k + 1, k2);
        if (dayNo.equals("N.A."))
            return true;
        else
            return false;
    }

    public boolean checkcourse() {
        String course = coursenotext.getText().toString().trim();
        if (course.equals("BIO F110") || course.equals("CHEM F110") || course.equals("CHE F312") || course.equals("EEE F245") || course.equals("EEE F246") || course.equals("GS F222") || course.equals("ME F110") || course.equals("ME F215") || course.equals("PHY F110") || course.equals("PHY F214")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean searchlec() {
        int f = 1;
        int count = 0;
        for (int i = cid; i < mProductList.size(); i++) {
            String tempp1 = mProductList.get(i).getDescription();
            int k = tempp1.indexOf(';');
            String courseName1 = tempp1.substring(0, k);
            if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && !courseName1.equals("Tutorial") && !courseName1.equals("Practical")) {
                count += 1;
            } else if (count != 0) {
                break;
            }
        }
        if (count == 1) {
            if (lecsectionnotext.getText().toString().replace(" ", "").equals("1")) {
                f = 0;
                lid = cid;
            } else {
                f = 1;
            }
        } else {
            for (int i = cid; i < mProductList.size(); i++) {
                String tempp1 = mProductList.get(i).getDescription();
                int k = tempp1.indexOf(';');
                String courseName1 = tempp1.substring(0, k);
                if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && mProductList.get(i).getPrice().equals(lecsectionnotext.getText().toString().trim()) && !courseName1.equals("Tutorial") && !courseName1.equals("Practical")) {
                    f = 0;
                    lid = i;
                    break;
                }
            }
        }
        if (f == 0)
            return true;
        else
            return false;
    }

    public boolean searchtut() {
        int f = 1;
        int count = 0;
        int i;
        for (i = cid; i < mProductList.size(); i++) {
            String tempp1 = mProductList.get(i).getDescription();
            int k = tempp1.indexOf(';');
            String courseName1 = tempp1.substring(0, k);
            if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && courseName1.equals("Tutorial")) {
                count += 1;
            } else if (count != 0) {
                break;
            }
        }
        if (count == 1) {
            if (tutsectionnonotext.getText().toString().replace(" ", "").equals("1")) {
                f = 0;
                tid = i - 1;
            } else {
                f = 1;
            }
        } else {
            for (i = cid; i < mProductList.size(); i++) {
                String tempp1 = mProductList.get(i).getDescription();
                int k = tempp1.indexOf(';');
                String courseName1 = tempp1.substring(0, k);
                if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && courseName1.equals("Tutorial") && mProductList.get(i).getPrice().equals(tutsectionnonotext.getText().toString().trim())) {
                    f = 0;
                    tid = i;
                    break;
                }

            }
        }
        if (f == 0)
            return true;
        else
            return false;
    }

    public boolean searchprac() {
        int f = 1;
        int count = 0;
        int i;
        for (i = cid; i < mProductList.size(); i++) {
            String tempp1 = mProductList.get(i).getDescription();
            int k = tempp1.indexOf(';');
            String courseName1 = tempp1.substring(0, k);
            if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && courseName1.equals("Practical")) {
                count += 1;
            } else if (count != 0) {
                break;
            }
        }
        if (count == 1) {
            if (praccticalsectionnonotext.getText().toString().replace(" ", "").equals("1")) {
                f = 0;
                pid = i - 1;
            } else {
                f = 1;
            }
        } else {
            for (i = cid; i < mProductList.size(); i++) {
                String tempp1 = mProductList.get(i).getDescription();
                int k = tempp1.indexOf(';');
                String courseName1 = tempp1.substring(0, k);
                if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && courseName1.equals("Practical") && mProductList.get(i).getPrice().equals(praccticalsectionnonotext.getText().toString().trim())) {
                    f = 0;
                    pid = i;
                    break;
                }

            }
        }
        if (f == 0)
            return true;
        else
            return false;
    }

    public boolean searchspecial() {
        int f = 1;
        int count = 0;
        for (int i = cid; i < mProductList.size(); i++) {
            String tempp1 = mProductList.get(i).getDescription();
            int k = tempp1.indexOf(';');
            String courseName1 = tempp1.substring(0, k);
            if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && !courseName1.equals("Practical") && !courseName1.equals("Tutorial")) {
                count += 1;
            } else if (count != 0) {
                break;
            }
        }
        if (count == 1) {
            if (praccticalsectionnonotext.getText().toString().replace(" ", "").equals("1")) {
                f = 0;
                pid = cid;
            } else {
                f = 1;
            }
        } else {
            for (int i = cid; i < mProductList.size(); i++) {
                String tempp1 = mProductList.get(i).getDescription();
                int k = tempp1.indexOf(';');
                String courseName1 = tempp1.substring(0, k);
                if (mProductList.get(i).getTitle().equals(coursenotext.getText().toString().trim()) && mProductList.get(i).getPrice().equals(praccticalsectionnonotext.getText().toString().trim()) && !courseName1.equals("Tutorial") && !courseName1.equals("Practical")) {
                    f = 0;
                    pid = i;
                    break;
                }

            }
        }
        if (f == 0)
            return true;
        else
            return false;
    }**/
    //validations end

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
            toast.setText("NO NETWORK CONNECTION");
            toast.setGravity(Gravity.CENTER, 0, 0);
            //other setters
            toast.show();
        } else {
            Activity mActivity = first.this;

            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    new MakeRequestTask(mCredential).execute();
                }
            });
            ;
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                    toast.setText("This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and retry");
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    //other setters
                    toast.show();

                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, Objects.requireNonNull(grantResults));
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                first.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("NotifyBITS")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            //private String getDataFromApi()throws IOException{
            // List the next 10 events from the primary calendar.
            /*DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            //Events event2=mService.events().insert("primary",)
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }*/

            //calender entry begins
            if (!lecsectionnotext.getText().toString().trim().isEmpty()) {
                //temp1 = mProductList.get(lid).getDescription();
                //temp2 = mProductList.get(lid).getDescription2();
                //int k = temp1.indexOf(';')
                //k = temp2.indexOf(';');
                //int k2 = temp2.lastIndexOf(';');
                courseName = mProductList.get(lid).getNumber();
                instructorName = mProductList.get(lid).getInst();
                roomNo = mProductList.get(lid).getRoom();
                dayNo = mProductList.get(lid).getDays();
                hourNo = mProductList.get(lid).getHour();
                sectionNo = mProductList.get(lid).getSec();
                /**if (courseName.compareTo("Tutorial") != 0 && courseName.compareTo("Practical") != 0) {
                    temp3 = "Course Name -";
                } else {
                    temp3 = "";
                }**/
                /**if (mProductList.get(lid).getPrice().compareTo("0") != 0) {
                    sectionNo = (mProductList.get(lid).getPrice());
                } else {
                    sectionNo = "";
                }*/
                sectionNo = mProductList.get(lid).getSec();
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
                final String[] EVENT_PROJECTION = new String[]{
                        Calendars._ID,                           // 0
                        //Calendars.ACCOUNT_NAME,                  // 1
                        //Calendars.CALENDAR_DISPLAY_NAME,         // 2
                        //Calendars.OWNER_ACCOUNT                  // 3
                };

                // The indices for the projection array above.
                final int PROJECTION_ID_INDEX = 0;
                final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
                final int PROJECTION_DISPLAY_NAME_INDEX = 2;
                final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
                // Run query
                Cursor cur = null;
                ContentResolver cr = getContentResolver();
                Uri uri = Calendars.CONTENT_URI;
                String selection = "(" + Calendars.ACCOUNT_NAME + " = ?)";
                String s = accountName;
                String[] selectionArgs = new String[]{s};
                // Submit the query and get a Cursor object back.
                cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);


                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
                long calID = 0;
                cur.moveToNext();
                calID = cur.getLong(PROJECTION_ID_INDEX);
                //tutsectionnonotext.setText(hourNo);
                for (int i = 0; i < dayNo.length(); i++) {
                    char ch = dayNo.charAt(i);
                    char ch2;
                    if (ch != ' ') {
                        if (ch == 'M') {
                            if (hourNo.trim().length() == 1) {
                                int h = getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                //java.util.Calendar.getInstance();
                                Calendar beginTime = java.util.Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                 Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName + " " + mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION, instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION, roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;
                        }
                        if(ch=='W')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 18, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 18, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 18, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 18, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 18, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 18, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='F')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 13, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 13, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 13, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 13, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 13, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 13, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }

                            continue;
                        }
                        if(ch=='S')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 14, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 14, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 14, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 14, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 14, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 14, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }

                            continue;
                        }
                        if(ch=='T')
                        {
                            if(dayNo.charAt(dayNo.length()-1)=='T' || dayNo.charAt(i+1)!='h') {
                                if(hourNo.trim().length()==1)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 17, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 17, h, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                                }
                                else if(hourNo.trim().length()==3)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 17, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 17, h+1, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                else if(hourNo.trim().length()==5)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 17, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 17, h+2, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }

                            }
                                continue;
                            }
                            else
                            {

                                if(hourNo.trim().length()==1)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 19, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 19, h, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                                }
                                else if(hourNo.trim().length()==3)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 19, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 19, h+1, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                else if(hourNo.trim().length()==5)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 19, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 19, h+2, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }

                            }
                                continue;



                            }

                        }



                    }


            if(!tutsectionnonotext.getText().toString().trim().isEmpty())
            {
                //temp1 =mProductList.get(tid).getDescription();
                //temp2=mProductList.get(tid).getDescription2();
                /*int k = temp1.indexOf(';');
                courseName=temp1.substring(0, k);
                instructorName=temp1.substring(k + 1, temp1.length());
                k=temp2.indexOf(';');
                int k2=temp2.lastIndexOf(';');
                roomNo=temp2.substring(0,k);
                dayNo=temp2.substring(k + 1, k2);
                hourNo=temp2.substring(k2+1,temp2.length());*/
                courseName = mProductList.get(tid).getNumber();
                instructorName = mProductList.get(tid).getInst();
                roomNo = mProductList.get(tid).getRoom();
                dayNo = mProductList.get(tid).getDays();
                hourNo = mProductList.get(tid).getHour();
                sectionNo = mProductList.get(tid).getSec();
                /*if(courseName.compareTo("Tutorial")!=0  && courseName.compareTo("Practical")!=0)
                {temp3="Course Name -";}else{temp3="";}*/
                /*if(mProductList.get(tid).getPrice().compareTo("0")!=0)
                {sectionNo=(mProductList.get(tid).getPrice());}
                else{sectionNo="";}*/

                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
                final String[] EVENT_PROJECTION = new String[] {
                        Calendars._ID,                           // 0
                        //Calendars.ACCOUNT_NAME,                  // 1
                        //Calendars.CALENDAR_DISPLAY_NAME,         // 2
                        //Calendars.OWNER_ACCOUNT                  // 3
                };

                // The indices for the projection array above.
                final int PROJECTION_ID_INDEX = 0;
                final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
                final int PROJECTION_DISPLAY_NAME_INDEX = 2;
                final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
                // Run query
                Cursor cur = null;
                ContentResolver cr = getContentResolver();
                Uri uri = Calendars.CONTENT_URI;
                String selection = "(" + Calendars.ACCOUNT_NAME + " = ?)";
                String s=accountName;
                String[] selectionArgs = new String[] {s};
                // Submit the query and get a Cursor object back.
                cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);


                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
                long calID=0;
                cur.moveToNext();
                calID = cur.getLong(PROJECTION_ID_INDEX);
                //tutsectionnonotext.setText(hourNo);
                for(int i=0;i<dayNo.length();i++)
                {
                    char ch=dayNo.charAt(i);
                    char ch2;
                    if(ch!=' ')
                    {
                        if(ch=='M')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 1, 13, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 1, 13, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }

                            continue;
                        }
                        if(ch=='W')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 18, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 18, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 3, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 3, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 3, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 3, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='F')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 13, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 13, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 5, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 5, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 5, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 5, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='S')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 14, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 14, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 6, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 6, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 6, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 6, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 990);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='T')
                        {
                            if(dayNo.charAt(dayNo.length()-1)=='T' || dayNo.charAt(i+1)!='h') {
                                if(hourNo.trim().length()==1)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 17, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 17, h, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 990);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                                }
                                else if(hourNo.trim().length()==3)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2016, 7, 2, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2016, 7, 2, h+1, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 990);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                else if(hourNo.trim().length()==5)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2016, 7, 2, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2016, 7, 2, h+2, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 990);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                            continue;
                            }

                            else
                            {

                                if(hourNo.trim().length()==1)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 19, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 19, h, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 990);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                                }
                                else if(hourNo.trim().length()==3)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2016, 7, 4, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2016, 7, 4, h+1, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 990);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                else if(hourNo.trim().length()==5)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2016, 7, 4, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2016, 7, 4, h+2, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(tid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 990);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }                                continue;


                            }

                        }



                    }
                }
            }
            if(!praccticalsectionnonotext.getText().toString().trim().isEmpty())
            {
                //temp1 =mProductList.get(pid).getDescription();
                //temp2=mProductList.get(pid).getDescription2();
                /*int k = temp1.indexOf(';');
                courseName=temp1.substring(0, k);
                instructorName=temp1.substring(k + 1, temp1.length());
                k=temp2.indexOf(';');
                int k2=temp2.lastIndexOf(';');
                roomNo=temp2.substring(0,k);
                dayNo=temp2.substring(k + 1, k2);
                hourNo=temp2.substring(k2+1,temp2.length());
                /*if(courseName.compareTo("Tutorial")!=0  && courseName.compareTo("Practical")!=0)
                {temp3="Course Name -";}else{temp3="";}*/
                /*if(mProductList.get(pid).getPrice().compareTo("0")!=0)
                {sectionNo=(mProductList.get(pid).getPrice());}
                else{sectionNo="";}*/
                courseName = mProductList.get(pid).getNumber();
                instructorName = mProductList.get(pid).getInst();
                roomNo = mProductList.get(pid).getRoom();
                dayNo = mProductList.get(pid).getDays();
                hourNo = mProductList.get(pid).getHour();
                sectionNo = mProductList.get(pid).getSec();
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
                final String[] EVENT_PROJECTION = new String[] {
                        Calendars._ID,                           // 0
                        //Calendars.ACCOUNT_NAME,                  // 1
                        //Calendars.CALENDAR_DISPLAY_NAME,         // 2
                        //Calendars.OWNER_ACCOUNT                  // 3
                };

                // The indices for the projection array above.
                final int PROJECTION_ID_INDEX = 0;
                final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
                final int PROJECTION_DISPLAY_NAME_INDEX = 2;
                final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
                // Run query
                Cursor cur = null;
                ContentResolver cr = getContentResolver();
                Uri uri = Calendars.CONTENT_URI;
                String selection = "(" + Calendars.ACCOUNT_NAME + " = ?)";
                String s=accountName;
                String[] selectionArgs = new String[] {s};
                // Submit the query and get a Cursor object back.
                cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);


                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@//
                long calID=0;
                cur.moveToNext();
                calID = cur.getLong(PROJECTION_ID_INDEX);
                //tutsectionnonotext.setText(hourNo);
                for(int i=0;i<dayNo.length();i++)
                {
                    char ch=dayNo.charAt(i);
                    char ch2;
                    if(ch!=' ')
                    {
                        if(ch=='M')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 16, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 16, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='W')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 3, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 3, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 18, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 18, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 18, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 18, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='F')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 5, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 5, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 13, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 13, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 13, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 13, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='S')
                        {
                            if(hourNo.trim().length()==1)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2016, 7, 6, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2016, 7, 6, h, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                            }
                            else if(hourNo.trim().length()==3)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 14, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 14, h+1, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            else if(hourNo.trim().length()==5)
                            {
                                int h=getHour(hourNo.charAt(0));
                                long startMillis = 0;
                                long endMillis = 0;
                                Calendar beginTime = Calendar.getInstance();
                                beginTime.set(2017, 0, 14, h, 00);
                                startMillis = beginTime.getTimeInMillis();
                                Calendar endTime = Calendar.getInstance();
                                endTime.set(2017, 0, 14, h+2, 50);
                                endMillis = endTime.getTimeInMillis();
                                ContentResolver cr1 = getContentResolver();
                                ContentValues values = new ContentValues();
                                values.put(CalendarContract.Events.DTSTART, startMillis);
                                values.put(CalendarContract.Events.DTEND, endMillis);
                                values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                // get the event ID that is the last element in the Uri
                                Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                long eventID = Long.parseLong(uri1.getLastPathSegment());
                                ContentValues reminders = new ContentValues();
                                reminders.put(Reminders.EVENT_ID, eventID);
                                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                reminders.put(Reminders.MINUTES, 10);
                                uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                            }
                            continue;

                        }
                        if(ch=='T')
                        {
                            if(dayNo.charAt(dayNo.length()-1)=='T' || dayNo.charAt(i+1)!='h') {
                                if(hourNo.trim().length()==1)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2016, 7, 2, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2016, 7, 2, h, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                                }
                                else if(hourNo.trim().length()==3)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 17, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 17, h+1, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                else if(hourNo.trim().length()==5)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 17, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 17, h+2, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                continue;
                            }
                            else
                            {

                                if(hourNo.trim().length()==1)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 19, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 19, h, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);
                                }
                                else if(hourNo.trim().length()==3)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 19, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 19, h+1, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                else if(hourNo.trim().length()==5)
                                {
                                    int h=getHour(hourNo.charAt(0));
                                    long startMillis = 0;
                                    long endMillis = 0;
                                    Calendar beginTime = Calendar.getInstance();
                                    beginTime.set(2017, 0, 19, h, 00);
                                    startMillis = beginTime.getTimeInMillis();
                                    Calendar endTime = Calendar.getInstance();
                                    endTime.set(2017, 0, 19, h+2, 50);
                                    endMillis = endTime.getTimeInMillis();
                                    ContentResolver cr1 = getContentResolver();
                                    ContentValues values = new ContentValues();
                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                    values.put(CalendarContract.Events.DTEND, endMillis);
                                    values.put(CalendarContract.Events.RRULE, "FREQ=WEEKLY;UNTIL=20170501T000000Z");
                                    values.put(CalendarContract.Events.TITLE, courseName +" "+ mProductList.get(pid).getTitle());
                                    values.put(CalendarContract.Events.DESCRIPTION,instructorName);
                                    //values.put(CalendarContract.Events.DESCRIPTION, mProductList.get(lid).getTitle());
                                    values.put(CalendarContract.Events.CALENDAR_ID, calID);
                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Kolkata");
                                    values.put(CalendarContract.Events.EVENT_LOCATION,roomNo);
                                    //values.put(CalendarContract.Events.CALENDAR_ID,calID);
                                    // get the event ID that is the last element in the Uri
                                    Uri uri1 = cr1.insert(CalendarContract.Events.CONTENT_URI, values);
                                    long eventID = Long.parseLong(uri1.getLastPathSegment());
                                    ContentValues reminders = new ContentValues();
                                    reminders.put(Reminders.EVENT_ID, eventID);
                                    reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                                    reminders.put(Reminders.MINUTES, 10);
                                    uri1 = cr.insert(Reminders.CONTENT_URI, reminders);

                                }
                                continue;



                            }

                        }



                    }
                }
            }
            result();

            return null;
            //return "event added";
        }


        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            //mProgress.show();
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
            toast.setText("CALENDER UPDATED");
            toast.setGravity(Gravity.CENTER, 0, 0);
            //other setters
            toast.show();
            if (output == null || output.size() == 0) {

                //mOutputText.setText("No results returned.");
            } else {

                output.add(0, "Data retrieved using the Google Calendar API:");
                //mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            first.REQUEST_AUTHORIZATION);
                } else {
                    Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                    toast.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    /*mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());*/

                }
            } else {
                Toast toast = Toast.makeText(first.this, "message", Toast.LENGTH_SHORT);
                toast.setText("Request cancelled.");
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                //mOutputText.setText("Request cancelled.");
            }

        }
        protected int getHour(char c)
        {
            int h=8;
            switch(c)
            {
                case '1':
                    h=8;
                    break;
                case '2':
                    h=9;
                    break;
                case '3':
                    h=10;
                    break;
                case '4':
                    h=11;
                    break;
                case '5':
                    h=12;
                    break;
                case '6':
                    h=13;
                    break;
                case '7':
                    h=14;
                    break;
                case '8':
                    h=15;
                    break;
                case '9':
                    h=16;
                    break;
            }
            return h;
        }
        protected String result(){
            return "event added";
        }
    }


        private boolean copyDatabase(Context context) {
            try {

                InputStream inputStream = context.getAssets().open(DatabaseHelper.DBNAME);
                String outFileName = DatabaseHelper.DBLOCATION + DatabaseHelper.DBNAME;
                OutputStream outputStream = new FileOutputStream(outFileName);
                byte[]buff = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(buff)) > 0) {
                    outputStream.write(buff, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                Log.w("MainActivity","DB copied");
                return true;
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }


}
