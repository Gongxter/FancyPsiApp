package com.seemoo.pis.fancypsiapp.collector;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.seemoo.pis.fancypsiapp.helper.GmailDBHelper;
import com.seemoo.pis.fancypsiapp.listener.ActivityResultCallback;
import com.seemoo.pis.fancypsiapp.listener.Listener;
import com.seemoo.pis.fancypsiapp.listener.MyPermissionCallback;
import com.seemoo.pis.fancypsiapp.ui.MainActivity;


import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by TMZ_LToP on 29.11.2016.
 */

public class GmailCollector {

    private List<String> mailList = new ArrayList<>();

    private static final String[] SCOPES = { GmailScopes.GMAIL_READONLY};
    private static final String userId = "me";
    private static final String messageFormat = "metadata";
    private static final List<String> labelIds = Arrays.asList("SENT"); // still not sure whether adding "IMPORTANT" will improve resilted list
    private static final List<String> metadataHeaders = Arrays.asList("To");
    private static final String messageFields = "payload";


    private static final String PREF_ACCOUNT_NAME = "accountName";


    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private Context c;

    private GoogleAccountCredential mCredential;
    private List<Listener> listeners = new ArrayList<>();

    private GmailDBHelper db;

    private ActivityResultCallback resultCallback = new ActivityResultCallback() {
        @Override
        public void activityResultCallback(int requestCode, int resultCode, Intent data) {
            switch(requestCode) {
                case REQUEST_GOOGLE_PLAY_SERVICES:
                    if (resultCode != Activity.RESULT_OK) {
                    } else {
                        getResultsFromApi();
                    }
                    break;
                case REQUEST_ACCOUNT_PICKER:
                    if (resultCode == Activity.RESULT_OK && data != null &&
                            data.getExtras() != null) {
                        String accountName =
                                data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        if (accountName != null) {
                            SharedPreferences settings =
                                    ((MainActivity)c).getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(PREF_ACCOUNT_NAME, accountName);
                            editor.apply();
                            mCredential.setSelectedAccountName(accountName);
                            getResultsFromApi();
                        }
                    }
                    break;
                case REQUEST_AUTHORIZATION:
                    if (resultCode == Activity.RESULT_OK) {
                        getResultsFromApi();
                    }
                    break;
            }
        }
    };

    public GmailCollector(Context c, Listener l)  {
        this.c = c;
        listeners.add(l);
        db = new GmailDBHelper(c);
        int date = DateFormat.getInstance().getCalendar().get(Calendar.WEEK_OF_YEAR);
        if(db.getDate()!= -1 && db.getDate()== date){
            mailList = db.getMails();
            l.onReady();
        }else {
            db.delete();
            mCredential = GoogleAccountCredential.usingOAuth2(
                    ((MainActivity) c).getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            ((MainActivity)c).addResultCallback(resultCallback);
            /*if (ActivityCompat.checkSelfPermission(c, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                String[] s = {Manifest.permission.GET_ACCOUNTS};
                ((MainActivity) c).addCallback(new MyPermissionCallback() {
                    @Override
                    public void permission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == RequestCode.ACCOUNTS.id()) {
                            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                                permissionGranted();
                            }
                        }
                    }
                });
                ActivityCompat.requestPermissions((MainActivity) c, s, RequestCode.ACCOUNTS.id());
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            permissionGranted();*/
            getResultsFromApi();
        }
    }
    private void permissionGranted(){
        if(ContextCompat.checkSelfPermission(c,Manifest.permission.GET_ACCOUNTS)==PackageManager.PERMISSION_GRANTED){
                chooseAccount();
        }
    }
    private void chooseAccount() {
        Log.i("GoogleApi","ChooseAccount");
        if (EasyPermissions.hasPermissions(
                c, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = ((MainActivity)c).getPreferences(Context.MODE_PRIVATE)
                    .getString("accountName", null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                Log.i("GoogleApi","has account call getResult");
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                ((MainActivity)c).startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            ((MainActivity)c).addCallback(new MyPermissionCallback() {
                @Override
                public void permission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

                }
            });
            ((MainActivity)c).addEasyPermissionCallback(new EasyPermissions.PermissionCallbacks() {
                @Override
                public void onPermissionsGranted(int requestCode, List<String> perms) {
                    for (String s : perms){
                        Log.i("Permissions",s);
                    }
                    getResultsFromApi();
                }

                @Override
                public void onPermissionsDenied(int requestCode, List<String> perms) {

                }

                @Override
                public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                    ((MainActivity)c).easyPermission(requestCode,permissions,grantResults);
                }
            });
            Log.i("EasyPermission","Requested");
            EasyPermissions.requestPermissions(
                    c,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }
    private void getResultsFromApi() {
        Log.i("GoogleApi","getResult in UpperClass");
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Log.e("GmailCollector","device is offline");
        } else {
            ((MainActivity)c).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new MakeRequest(mCredential).execute();
                }
            });

        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(c);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }
    public List<String> getMailList(){
        return mailList;
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                (MainActivity)c,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(c);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }


    private class MakeRequest extends AsyncTask<Void,Void,Void> {

        private com.google.api.services.gmail.Gmail mService = null;

        MakeRequest(GoogleAccountCredential credential)  {

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            Log.i("MakeRequest","Call new Request");
             // name and id are both the same just the email adress
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("FancyPsiApp")
                    .build();

        }


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                ListMessagesResponse listResponse = mService.users().messages().list(userId)
                        .setLabelIds(labelIds)	// query only outbox messages ("SENT")
                        .setMaxResults((long)100)	// query this number of messages
                        .execute();

                List<Message> messages = listResponse.getMessages();
                Set<String> emailList = new HashSet<String>();
                if(messages != null && messages.size() > 0) {
                    for(Message message: messages) {
                        processMessage(mService, message.getId(), emailList);
                    }
                }
                    mailList = new ArrayList<>(emailList);
                    writeToDatabase(mailList);

                    callBack();
                } catch (IOException e) {
                    if (e instanceof UserRecoverableAuthIOException) {
                        ((MainActivity)c).startActivityForResult(
                                ((UserRecoverableAuthIOException) e).getIntent(),
                                REQUEST_AUTHORIZATION);
                    } else {
                    Log.wtf("GmailCollector","Should never happen");
                    }
                }
            return null;
        }

        private void processMessage(Gmail mService, String messageId, Set<String> emailList) throws java.io.IOException {

            Message message = mService.users().messages().get(userId, messageId)
                    .setFormat(messageFormat)	// query only metadata
                    .setMetadataHeaders(metadataHeaders)	// query only "To" field (recipients)
                    .setFields(messageFields)	// query only payload of the email
                    .execute();
            if(message != null){

                // Extract payload from the message
                MessagePart messagePayload = message.getPayload();

                if(messagePayload != null) {

                    // Extract headers from payload
                    List<MessagePartHeader> messageHeaders = messagePayload.getHeaders();

                    if(messageHeaders != null && messageHeaders.size() > 0){
                        for(MessagePartHeader messageHeader: messageHeaders){
                            parseEmailAddress(messageHeader.getValue(), emailList);
                        }
                    }
                }
            }
        }


        private void callBack(){
            for (Listener l : listeners){
                l.onReady();
            }
        }

        public void parseEmailAddress(String inputAddresses, Set<String> emailList) {

            // First split several email addresses
            String[] multEmails = inputAddresses.split(", ");

            for(int i = 0; i < multEmails.length; i++) {

                // Separate a name and an address
                String[] nameEmail = multEmails[i].split(" <");

                // Adding emails to the set
                if(nameEmail.length == 1) {
                    emailList.add(nameEmail[0].replaceAll("<|>",""));
                } else {
                    String result = nameEmail[1].substring(0, nameEmail[1].indexOf(">"));
                    result = result.replaceAll("<|>","");
                    emailList.add(result);
                }
            }
        }
    }

    private void writeToDatabase(List<String> mailList) {
        if(mailList.isEmpty()){
            return;
        } else {
            for (String s:mailList){
                db.insert(s);
            }
            db.setDate();
        }
    }
}
