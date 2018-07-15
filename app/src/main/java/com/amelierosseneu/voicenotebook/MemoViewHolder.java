package com.amelierosseneu.voicenotebook;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MemoViewHolder extends RecyclerView.ViewHolder {

    public TextView timeData;
    public TextView noteData;

    public MemoViewHolder(View itemView) {
        super(itemView);

        timeData = itemView.findViewById(R.id.memo_time);
        noteData = itemView.findViewById(R.id.memo_content);
    }

    public void bindToPost(MemoClassType record) {

        timeData.setText(record.time);
        noteData.setText(record.memo);
    }
}
