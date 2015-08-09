package com.pasta.ddvegan.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.models.VeganSpot;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpotReportFragment extends DialogFragment {

    VeganSpot spot;
    EditText message;
    EditText from;
    TextView charCount;
    CheckBox wrongOffer;
    CheckBox wrongHours;
    CheckBox wrongAddress;
    CheckBox wrongContact;
    String errors = "Fehlerkategorien:\n";

    public SpotReportFragment() {
    }

    public static SpotReportFragment createSpotReportFragment(VeganSpot spot) {
        SpotReportFragment fragment = new SpotReportFragment();
        Bundle args = new Bundle();
        args.putInt("spotId", spot.getID());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            spot = DataRepo.veganSpots.get(getArguments().getInt("spotId"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_spot_report, container);
        wrongAddress = (CheckBox) view.findViewById(R.id.checkbox_address);
        wrongHours = (CheckBox) view.findViewById(R.id.checkbox_hours);
        wrongContact = (CheckBox) view.findViewById(R.id.checkbox_contact);
        wrongOffer = (CheckBox) view.findViewById(R.id.checkbox_offer);

        Button b = (Button) view.findViewById(R.id.sendMessageButton);
        message = (EditText) view.findViewById(R.id.messageInput);
        from = (EditText) view.findViewById(R.id.mailAddress);
        charCount = (TextView) view.findViewById(R.id.charCount);
        message.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                charCount.setText(message.getText().toString().length()
                        + " / 750");
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (message.getText().toString().length() < 5
                        || message.getText().toString().trim().length() == 0)
                    Toast.makeText(getActivity(),
                            "Deine Nachricht sollte mehr als ein paar Buchstaben enthalten.",
                            Toast.LENGTH_SHORT).show();
                else if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(from.getText().toString()).matches()))
                    Toast.makeText(getActivity(),
                            "Gebe bitte eine gültige E-Mail Adresse an.",
                            Toast.LENGTH_SHORT).show();
                else if (!(wrongAddress.isChecked() || wrongHours.isChecked() || wrongOffer.isChecked() || wrongContact.isChecked())) {
                    Toast.makeText(getActivity(),
                            "Wähle mindestens eine Fehlerkategorie aus.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (wrongAddress.isChecked())
                        errors += "Adressdaten\n";
                    if (wrongOffer.isChecked())
                        errors += "Angebot\n";
                    if (wrongHours.isChecked())
                        errors += "Öffnungszeiten\n";
                    if (wrongContact.isChecked())
                        errors += "Kontaktdaten\n";
                    String content = "Fehlerreport " + spot.getName() + " (" + spot.getID() + ")\n\n" + errors + "\n\nPersönliche Nachricht:\n\n" + message.getText().toString();
                    MailSender ms = new MailSender(content, from.getText().toString(), DataRepo.appVersion, spot.getName());
                    ms.execute();
                }
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
    }

    private class MailSender extends AsyncTask<Void, Integer, Integer> {
        String msg = "";
        String version = "";
        String from = "";
        String spotName = "";

        public MailSender(String msg, String from, String version, String spotName) {
            this.msg = msg;
            this.version = version;
            this.from = from;
            this.spotName = spotName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(DataRepo.apiReport);
            httpPost.setHeader(HTTP.CONTENT_TYPE,
                    "application/x-www-form-urlencoded;charset=UTF-8");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

                nameValuePairs.add(new BasicNameValuePair("msg", msg));
                nameValuePairs.add(new BasicNameValuePair("version", version));
                nameValuePairs.add(new BasicNameValuePair("from", from));
                nameValuePairs.add(new BasicNameValuePair("spot", spotName));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

                httpClient.execute(httpPost);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return 1;
            } catch (IOException e1) {
                e1.printStackTrace();
                return 1;
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {
            if (result < 1) {
                getDialog().dismiss();
                Toast.makeText(getActivity(), "Nachricht wurde übermittelt!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Aus technischen Gründen kann gerade keine Nachricht versandt werden.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

