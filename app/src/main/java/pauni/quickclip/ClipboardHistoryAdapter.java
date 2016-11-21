package pauni.quickclip;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.v7.appcompat.R.attr.colorBackgroundFloating;


class ClipboardHistoryAdapter extends BaseAdapter {

    Context context;
    String[] clips;
    private static LayoutInflater inflater = null;

    ClipboardHistoryAdapter(Context context, String[] clips) {
        this.context = context;
        this.clips = clips;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return clips.length;
    }

    @Override
    public Object getItem(int position) {
        return clips[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeItem(int position) {
        //converting array to list, to remove certain item, and converting it back
        ArrayList<String> list = new ArrayList<>(Arrays.asList(clips));
        list.remove(position);
        clips = list.toArray(new String[0]);

        ClipboardHistoryActivity.removeTime(position);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View vi = convertView;

        if (vi == null)
            vi = inflater.inflate(R.layout.custom_row, parent, false);

        TextView tv_clip = (TextView) vi.findViewById(R.id.tv_clipboard);//display clip
        TextView tv_time = (TextView) vi.findViewById(R.id.tv_timestamp);//display timestamp of it
        ImageButton delete = (ImageButton) vi.findViewById(R.id.imageButton);//delete an entry
        final CardView cardView = (CardView) vi.findViewById(R.id.card_clipboard);//for coloring it

        tv_clip.setText(clips[position]);
        tv_time.setText(ClipboardHistoryActivity.getTimestamps(position));

        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setCardviewColor(cardView, R.color.red_bright);
                        break;
                    case MotionEvent.ACTION_UP:
                        setCardviewColor(cardView, R.color.white);
                        removeItem(position);
                        notifyDataSetChanged();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        setCardviewColor(cardView, R.color.white);
                        break;
                }
                return true;
            }
        });

        tv_clip.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                SetClipboard.setClipboard(clips[position], context);
                SystemClock.sleep(200);
                Intent intent = new Intent("finish_activity");
                context.sendBroadcast(intent);

                return true;
            }

        });

        return  vi;
    }

    private void setCardviewColor(CardView c, int color) {
        c.setBackgroundColor(context.getResources().getColor(color));
    }

}