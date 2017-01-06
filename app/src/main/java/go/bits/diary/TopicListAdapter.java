package go.bits.diary;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rushikesh on 4/1/17
 */

class TopicListAdapter extends BaseAdapter {
    private  List<String> topics;
    private Context context;
    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public Object getItem(int i) {
        return topics.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView == null){
            v = View.inflate(this.context, R.layout.textview_topic,null);
        }
        else {
            v = convertView;
        }
        TextView topicView = (TextView) v.findViewById(R.id.topic_view);
        if(topics.get(position) != null) {
            topicView.setText(topics.get(position));
            final Intent intent = new Intent(this.context, TopicNotes.class);
            intent.putExtra("TopicName", topics.get(position));
            View.OnClickListener topicClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(intent);
                }
            };
            topicView.setOnClickListener(topicClickListener);
        }
        return v;
    }

    TopicListAdapter(Context context, List<String> topics){
        this.context = context;
        this.topics = topics;
    }
}
