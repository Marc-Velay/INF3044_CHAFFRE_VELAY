package com.esiea.chaffre_velay.cvprojects;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class secondeActivity extends AppCompatActivity {

    private static final String TAG = "SECOND ACTIVITY";
    IntentFilter intentFilter ;
    BierAdapter ba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seconde);

        intentFilter = new IntentFilter(MainActivity.BIERS_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BierUpdate(), intentFilter);

        Intent intent = new Intent(this, GetBiersServices.class);
        intent.setAction(MainActivity.BIERS_UPDATE);
        this.startService(intent);

        RecyclerView rv_biere = (RecyclerView)findViewById(R.id.rv_biere);
        rv_biere.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ba = new BierAdapter(getBIERSFromFile());
        rv_biere.setAdapter(ba);

    }


    public class BierUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, intent.getAction());
            Toast.makeText(getApplicationContext(), "Downloaded", Toast.LENGTH_SHORT).show();
            ba.setNewBiere(getBIERSFromFile());
        }
    }

    private class BierAdapter extends RecyclerView.Adapter<BierAdapter.BierHolder> {

        private JSONArray biers;

        public BierAdapter(JSONArray biers) {
            this.biers = biers;
        }

        @Override
        public BierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            BierHolder bh = new BierHolder(inflater.inflate(R.layout.rv_biere_element, parent, false));
            return bh;
        }

        @Override
        public void onBindViewHolder(BierHolder holder, int position) {
            try {
                holder.name.setText(biers.getJSONObject(position).get("name").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {

            return biers.length();
        }

        public class BierHolder extends RecyclerView.ViewHolder {
            TextView name;

            public BierHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.rv_biere_element_name);
            }
        }

        public void setNewBiere(JSONArray biers) {
            this.biers = biers;
            notifyDataSetChanged();
        }
    }


    public JSONArray getBIERSFromFile() {
        try{
            InputStream in = new FileInputStream(getCacheDir() + "/" + "biers.json");
            byte[] buf = new byte[in.available()];
            in.read(buf);
            in.close();
            return new JSONArray(new String(buf, "UTF-8"));
        } catch(IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
}
