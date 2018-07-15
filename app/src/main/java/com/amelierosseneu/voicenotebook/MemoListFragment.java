package com.amelierosseneu.voicenotebook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MemoListFragment extends Fragment {
    private FirebaseRecyclerAdapter<MemoClassType, MemoViewHolder> mAdapter;
    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private View rootView;
    private Query memosQuery;
    private FirebaseRecyclerOptions options;
    private String userid = "";
    private String databsePath = "server/saving-data/memos";
    private String TAG = "MemoListFragment";
    private int MAX_NUMBER_OF_MEMOS = 50;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.memos_list, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = rootView.findViewById(R.id.memos_listview);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mManager = new LinearLayoutManager(getContext());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        updateList(userid);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    /* This is teh function that populates the list with memos */

    public void updateList (final String uid){
        Log.d(TAG,"updating list");

        //Set the path to the data
        memosQuery = mDatabase.child(databsePath).child(userid).limitToLast(MAX_NUMBER_OF_MEMOS);;
        options = new FirebaseRecyclerOptions.Builder<MemoClassType>().setQuery(memosQuery, MemoClassType.class).build();

      /*
        //For debug: checking if we got the right data
        memosQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG,"Content of query:");
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Log.d(TAG, snapshot.getKey() + ": " + snapshot.getValue());
                            mRecycler.setAdapter(mAdapter);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        */

        //Ser the list adapter
        mAdapter = new FirebaseRecyclerAdapter<MemoClassType, MemoViewHolder>(options) {
            @Override
            public MemoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MemoViewHolder(inflater.inflate(R.layout.memo_item, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MemoViewHolder viewHolder, int position, MemoClassType model) {
                viewHolder.bindToPost(model);
            }
        };

        mAdapter.stopListening();
        mRecycler.setAdapter(mAdapter);
        mAdapter.startListening();
    }

}
