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
import uk.jumpingmouse.wittertainment.data.Film;

import static uk.jumpingmouse.wittertainment.R.id.txtAwardDate;

public class FilmAdapter extends ArrayAdapter<Film> {
    public FilmAdapter(Context context, int resource, List<Film> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).
                    getLayoutInflater().inflate(R.layout.list_item_film, parent, false);
        }
        Film film = getItem(position);
        if (film != null) {
            TextView txtFilmImdbId = (TextView) convertView.findViewById(R.id.txtFilmImdbId);
            TextView txtFilmTitle = (TextView) convertView.findViewById(R.id.txtFilmTitle);

            txtFilmImdbId.setText(film.getImdbId());
            txtFilmTitle.setText(film.getTitle());
        }

        return convertView;
    }
}
