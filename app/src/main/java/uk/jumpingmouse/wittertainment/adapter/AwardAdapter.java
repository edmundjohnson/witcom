package uk.jumpingmouse.wittertainment.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import uk.jumpingmouse.wittertainment.R;
import uk.jumpingmouse.wittertainment.data.Award;

import java.util.List;

public class AwardAdapter extends ArrayAdapter<Award> {
    public AwardAdapter(Context context, int resource, List<Award> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).
                    getLayoutInflater().inflate(R.layout.list_item_award, parent, false);
        }
        Award award = getItem(position);
        if (award != null) {
            TextView txtAwardDate = (TextView) convertView.findViewById(R.id.txtAwardDate);
            TextView txtCategoryId = (TextView) convertView.findViewById(R.id.txtAwardCategoryId);
            TextView txtFilmId = (TextView) convertView.findViewById(R.id.txtAwardFilmId);
            TextView txtCriticId = (TextView) convertView.findViewById(R.id.txtAwardCriticId);
            TextView txtCriticReview = (TextView) convertView.findViewById(R.id.txtAwardCriticReview);

            txtAwardDate.setText(award.getAwardDate());
            txtCategoryId.setText(award.getCategoryId());
            txtFilmId.setText(award.getFilmId());
            txtCriticId.setText(award.getCriticId());
            txtCriticReview.setText(award.getCriticReview());
        }

        return convertView;
    }
}
