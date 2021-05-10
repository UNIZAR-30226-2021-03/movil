package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {

    public CategoryAdapter(Context context, ArrayList<Category> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        Category cat = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_category, parent, false);
        }

        // Lookup view for data population
        TextView categoria_name = (TextView) convertView.findViewById(R.id.categoria);

        // Populate the data into the template view using the data object
        categoria_name.setText(cat.getName());

        // Return the completed view to render on screen
        return convertView;
    }

    public Category getCat(int position) {
        // Get the data item for this position
        Category cat = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        return cat;
    }

}
