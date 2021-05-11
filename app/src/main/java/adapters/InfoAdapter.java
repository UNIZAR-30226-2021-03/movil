package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.R;

import java.util.ArrayList;

public class InfoAdapter extends ArrayAdapter<Info> {

    public InfoAdapter(Context context, ArrayList<Info> infos) {
        super(context, 0, infos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Info inf = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_info, parent, false);
        }

        // Lookup view for data population
        TextView info_name = (TextView) convertView.findViewById(R.id.info);

        // Populate the data into the template view using the data object
        info_name.setText(inf.getName());

        // Return the completed view to render on screen
        return convertView;
    }

    public Info getInfo(int position) {
        // Get the data item for this position
        Info inf = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        return inf;
    }

}
