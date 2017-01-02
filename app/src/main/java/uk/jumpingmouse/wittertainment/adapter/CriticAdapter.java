package uk.jumpingmouse.wittertainment.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import uk.jumpingmouse.wittertainment.R;
import uk.jumpingmouse.wittertainment.data.Critic;

public class CriticAdapter extends ArrayAdapter<Critic> {
    public CriticAdapter(Context context, int resource, List<Critic> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).
                    getLayoutInflater().inflate(R.layout.list_item_critic, parent, false);
        }
        Critic critic = getItem(position);
        if (critic != null) {
            TextView txtCriticId = (TextView) convertView.findViewById(R.id.txtCriticId);
            TextView txtCriticName = (TextView) convertView.findViewById(R.id.txtCriticName);
            TextView txtCriticDescription = (TextView) convertView.findViewById(R.id.txtCriticDescription);

            txtCriticId.setText(critic.getId());
            txtCriticName.setText(critic.getName());
            txtCriticDescription.setText(critic.getDescription());
        }

        return convertView;
    }
}
