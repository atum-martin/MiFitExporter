package exporter.mifit.com.mifitexporter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TrackListAdpater extends BaseAdapter implements ListAdapter {

    private final List<Track> trackList;
    private MainActivity context;

    public TrackListAdpater(MainActivity context, List<Track> trackList){
        this.context = context;
        this.trackList = trackList;
    }

    @Override
    public int getCount() {
        return trackList.size();
    }

    @Override
    public Object getItem(int i) {
        return trackList.get(i).toString();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup container) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.simplerow, null);
        }

        TextView text = view.findViewById(R.id.rowTextView);
        text.setText((String) getItem(position));
        text.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Track t = trackList.get(position);
                context.dumpTrackToStrava(t.trackId, t.type);
                Toast.makeText(context, "Dumping "+t.toString()+" to Strava.", Toast.LENGTH_LONG);
            }
        });
        return view;
    }
}
