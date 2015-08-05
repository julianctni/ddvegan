package com.pasta.ddvegan.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pasta.ddvegan.R;
import com.pasta.ddvegan.models.DataRepo;
import com.pasta.ddvegan.models.VeganSpot;
import com.pasta.ddvegan.sync.DatabaseManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SpotDetailFragment extends Fragment {

    VeganSpot spot;
    ImageView spotImage;
    ProgressBar imageProgress;

    public SpotDetailFragment() {
    }


    public static SpotDetailFragment create(int spotId) {
        SpotDetailFragment fragment = new SpotDetailFragment();
        Bundle args = new Bundle();
        args.putInt("spotId", spotId);
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_spot_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        spotImage = (ImageView) view.findViewById(R.id.spot_detail_image);
        imageProgress = (ProgressBar) view.findViewById(R.id.spot_detail_progress);
        ImageLoader loader = new ImageLoader();
        loader.execute();
        TextView header = (TextView) view.findViewById(R.id.spot_detail_header);
        TextView address = (TextView) view.findViewById(R.id.spot_detail_address);

        ArrayList<TextView> hours = new ArrayList<TextView>();
        hours.add((TextView) view.findViewById(R.id.spot_detail_hours_sun));
        hours.add((TextView) view.findViewById(R.id.spot_detail_hours_mon));
        hours.add((TextView) view.findViewById(R.id.spot_detail_hours_tue));
        hours.add((TextView) view.findViewById(R.id.spot_detail_hours_wed));
        hours.add((TextView) view.findViewById(R.id.spot_detail_hours_thu));
        hours.add((TextView) view.findViewById(R.id.spot_detail_hours_fri));
        hours.add((TextView) view.findViewById(R.id.spot_detail_hours_sat));

        TextView contact = (TextView) view.findViewById(R.id.spot_detail_contact);
        TextView info = (TextView) view.findViewById(R.id.spot_detail_info);

        ImageButton callButton = (ImageButton) view.findViewById(R.id.button_call);
        ImageButton mailButton = (ImageButton) view.findViewById(R.id.button_mail);
        ImageButton webButton = (ImageButton) view.findViewById(R.id.button_web);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDoCall();
            }
        });

        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSendMail();
            }
        });

        webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBrowseUrl();
            }
        });
        header.setText(spot.getName());
        address.setText(spot.getAdresse());
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);
        int day = 1;
        LinearLayout hourLayout = (LinearLayout) view.findViewById(R.id.spot_hours_layout);
        if (!spot.hasHours)
            hourLayout.setVisibility(View.GONE);
        else
            for (TextView tv : hours) {
                tv.setText(spot.getHoursForDay(day));
                if (day == today) {
                    tv.setTextColor(getResources().getColor(R.color.primary_bright));
                } else {
                    tv.setTextColor(Color.BLACK);
                }

                day++;
            }
        String phone = spot.getPhone() + "\n";
        String mail = spot.getMail() + "\n";
        String web = spot.getURL();
        if (spot.getPhone().equals(""))
            phone = "";
        if (spot.getMail().equals(""))
            mail = "";
        if (spot.getPhone().equals("") && spot.getMail().equals("") && spot.getURL().equals(""))
            contact.setText("keine Kontaktdaten vorhanden");
        else
            contact.setText(phone + mail + web);
        info.setText(spot.getInfo());

    }


    public void onClickSendMail() {
        if (!spot.getMail().equals("")) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{spot.getMail()});
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Wähle E-Mail App"));
        } else
            Toast.makeText(getActivity(), "Keine E-Mail vorhanden.", Toast.LENGTH_SHORT).show();
    }


    public void onClickDoCall() {
        try {
            if (!spot.getPhone().equals("")) {
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + spot.getPhone().replaceAll("\\s+", "")));
                startActivity(call);
            } else
                Toast.makeText(getActivity(), "Keine Telefonnummer vorhanden.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Du kannst mit diesem Gerät nicht telefonieren.", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClickBrowseUrl() {
        if (!spot.getURL().equals("")) {
            Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + spot.getURL()));
            startActivity(browse);
        } else
            Toast.makeText(getActivity(), "Keine Webseite vorhanden.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_spot_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (spot.isFavorite()) {
            menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_favorite_white_24dp);
        } else {
            menu.findItem(R.id.menu_favorite).setIcon(R.drawable.ic_favorite_outline_white_24dp);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_on_map:
                Fragment fragment = MapFragment.create(true, spot.getID());
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
                return true;

            case R.id.menu_favorite:
                DatabaseManager dbMan = new DatabaseManager(getActivity());
                if (!spot.isFavorite())
                    dbMan.setAsFavorite(spot, true);
                else
                    dbMan.setAsFavorite(spot, false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    getActivity().invalidateOptionsMenu();
                DataRepo.updateFavorites();
                SpotListFragment.spotListAdapter.notifyDataSetChanged();
                return true;
        }
        return true;
    }

    public void setFoodPicture(Bitmap foodPicture) {
        imageProgress.setVisibility(View.GONE);
        spotImage.setVisibility(View.VISIBLE);
        if (foodPicture != null)
            spotImage.setImageBitmap(foodPicture);
        else
            spotImage.setImageDrawable(getResources().getDrawable((R.drawable.nobanner)));
    }


    private class ImageLoader extends AsyncTask<Void, Integer, Integer> {
        Bitmap foodPicture;
        String pictureName = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            try {
                FileInputStream fis = getActivity().openFileInput(spot.getImgKey()+".png");
                foodPicture = BitmapFactory.decodeStream(fis);
                Log.i("LOADING IMAGE", "loading image from internal storage");
            } catch (FileNotFoundException e) {
                //e.printStackTrace();
                Log.i("LOADING IMAGE", "loading image from web");
                try {
                    Log.i("LOADING IMAGE", "loading jpg");
                    foodPicture = BitmapFactory
                            .decodeStream((InputStream) new URL("http://www.ddvegan.pastayouth.org/ddvegan/images/" + spot.getImgKey() + "_banner.jpg").getContent());

                } catch (Exception e1) {
                    //e1.printStackTrace();
                    try {
                        Log.i("LOADING IMAGE", "loading png");
                        foodPicture = BitmapFactory
                                .decodeStream((InputStream) new URL("http://www.ddvegan.pastayouth.org/ddvegan/images/" + spot.getImgKey() + "_banner.png").getContent());
                    } catch (Exception e2) {
                        //e1.printStackTrace();
                    }
                }
                if (foodPicture != null) {
                    FileOutputStream fos = null;
                    try {
                        fos = getActivity().openFileOutput(spot.getImgKey() + ".png", getActivity().MODE_PRIVATE);
                        foodPicture.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.close();
                    } catch (Exception e2) {
                        //e2.printStackTrace();
                    }
                }
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {

            setFoodPicture(foodPicture);
        }
    }

}
