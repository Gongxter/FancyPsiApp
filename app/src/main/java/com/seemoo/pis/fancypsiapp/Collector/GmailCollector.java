package com.seemoo.pis.fancypsiapp.Collector;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.seemoo.pis.fancypsiapp.Helper.RequestCode;
import com.seemoo.pis.fancypsiapp.Listener.Listener;
import com.seemoo.pis.fancypsiapp.Listener.MyPermissionCallback;
import com.seemoo.pis.fancypsiapp.MainActivity;
import com.seemoo.pis.fancypsiapp.R;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by TMZ_LToP on 29.11.2016.
 */

public class GmailCollector {

    final String ACCOUNT_TYPE_GOOGLE = "com.google";
    final String[] FEATURES_MAIL = {"service_mail"};

    final String[] SCOPES = { GmailScopes.GMAIL_READONLY,GmailScopes.GMAIL_LABELS,GmailScopes.MAIL_GOOGLE_COM,GmailScopes.GMAIL_COMPOSE,GmailScopes.GMAIL_METADATA};

    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    HttpRequestInitializer initializer;
    Context c;

    List<Listener> listeners = new ArrayList<>();

    public GmailCollector(Context c)  {
        this.c = c;
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            String[] s = {Manifest.permission.GET_ACCOUNTS};
            ((MainActivity)c).addCallback(new MyPermissionCallback() {
                @Override
                public void permission(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                    if(requestCode == RequestCode.ACCOUNTS.id()){
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                            permissionGranted();
                        }
                    }
                }
            });
            ActivityCompat.requestPermissions((MainActivity)c,s,RequestCode.ACCOUNTS.id());
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        permissionGranted();
    }
    private void permissionGranted(){
        if(ContextCompat.checkSelfPermission(c,Manifest.permission.GET_ACCOUNTS)==PackageManager.PERMISSION_GRANTED){

            AccountManager.get(c).getAccountsByTypeAndFeatures(ACCOUNT_TYPE_GOOGLE, FEATURES_MAIL, new AccountManagerCallback<Account[]>() {
            @Override
            public void run(AccountManagerFuture<Account[]> accountManagerFuture) {
                try {
                    Account[] accs = accountManagerFuture.getResult();
                    for (Account acc : accs){
                       /** GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                                new InputStreamReader(c.getResources().openRawResource(R.raw.clientid)));
                        // set up authorization code flow
                        GoogleAuthorizationCodeFlow flow2 = new GoogleAuthorizationCodeFlow.Builder(
                                transport, jsonFactory, clientSecrets,
                                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                                .build();
                        // authorize
                         new AuthorizationCodeInstalledApp(flow2, new LocalServerReceiver()).authorize("user");
                        */


                        GoogleAccountCredential cred = GoogleAccountCredential.usingOAuth2(c,GmailScopes.all()).setBackOff(new ExponentialBackOff()).setSelectedAccount(acc);

                        MakeRequest req = new MakeRequest(cred,acc.name);
                    }
                } catch (AuthenticatorException e) {
                    Log.e("AuthenticationError",e.getMessage());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (OperationCanceledException e1) {
                    e1.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
        },null);
        }
    }

    private class MakeRequest extends AsyncTask<Void,Void,Void> {

        private Gmail mService = null;
        private String user="";

        MakeRequest(GoogleAccountCredential credential, String username) throws GeneralSecurityException, IOException {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            user=username;
            mService = new Gmail.Builder(transport,jsonFactory,credential).setApplicationName("FancyPsiApp").build();

            acquireGooglePlayServices();
            if(isGooglePlayServicesAvailable()){
                execute();
            }else{
                Log.i("GooglePlayService","not avalable");
            }

        }

        private boolean isGooglePlayServicesAvailable() {
            GoogleApiAvailability apiAvailability =
                    GoogleApiAvailability.getInstance();
            final int connectionStatusCode =
                    apiAvailability.isGooglePlayServicesAvailable(c);
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
                    apiAvailability.isGooglePlayServicesAvailable(c);
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {


            try {
                ListLabelsResponse listlableResponse =
                mService.users().labels().list("me").execute();
                ListMessagesResponse listResponse = mService.users().messages().list("me")
                        .setLabelIds(Arrays.asList("SEND"))	// query only outbox messages ("SENT")
                        .setMaxResults((long)100)	// query this number of messages
                        .execute();
                for(Message m : listResponse.getMessages()){
                    m.getId();
                    Log.i("GoogleShit",String.valueOf(m.getId()));
                }
            } catch (IOException e) {
                Log.e("GoogleShit",e.getMessage());
            }
            return null;
        }
    }
}
