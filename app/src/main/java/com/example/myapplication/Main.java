package com.example.myapplication;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Main#newInstance} factory method to
 * create an instance of this fragment.
 */





//class for spacing the cards withing the gridlayout
class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;
    private int headerNum;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, int headerNum) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.headerNum = headerNum;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view) - headerNum; // item position

        if (position >= 0) {
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        } else {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}


public class Main extends Fragment implements AdapterForCards.OnCardListener {
    private ArrayList<Card> cardList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mRecycleBinButton;
    private FloatingActionButton mAddTaskButton;
    private Card cardToDelete;
    private int newContactPosition = -1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_TASKS = "tasksPath";
    private static final String ARG_PRESETS = "presetsPath";
    private static final String ARG_CATEGORIES = "categories";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mTasksFilePath;
    private String mPresetsFilePath;
    private ArrayList<String> mCategories;

    public Main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tasksFilePath List of tasks.
     * @param presetsFilePath List of presets.
     * @return A new instance of fragment Main.
     */
    // TODO: Rename and change types and number of parameters
    public static Main newInstance(String tasksFilePath, String presetsFilePath, ArrayList<String> categories) {
        Main fragment = new Main();
        Bundle args = new Bundle();
        args.putString(ARG_TASKS, tasksFilePath);
        args.putString(ARG_PRESETS, presetsFilePath);
        args.putStringArrayList(ARG_CATEGORIES, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTasksFilePath = getArguments().getString(ARG_TASKS);
            mPresetsFilePath = getArguments().getString(ARG_PRESETS);
            mCategories = getArguments().getStringArrayList(ARG_CATEGORIES);

            Log.d("Instance task path", mTasksFilePath);
            Log.d("Instance preset path", mPresetsFilePath);
        }

        cardList.add(new Card("Line", "Line 2jhfhjfjgjgjgjgjgjgj"));
        cardList.add(new Card("Line 3", "Line 4"));
        cardList.add(new Card("Line 5", "Line 6"));
        cardList.add(new Card("Line 5", "Line 6"));
        cardList.add(new Card("Line 5", "Line 6"));
        cardList.add(new Card("Line 5", "Line 6"));
        cardList.add(new Card("Line 5", "Line 6"));
        cardList.add(new Card("Line 5", "Line 6"));
        cardList.add(new Card("Line 3", "Line 4"));
        cardList.add(new Card("Line 3", "Line 4"));



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mAddTaskButton=getView().findViewById(R.id.createTask);
        mAddTaskButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openAddTaskActivity();
            }
        });


        mRecycleBinButton=getView().findViewById(R.id.recycleFab);
        mRecyclerView = getView().findViewById(R.id.recyclerView);

        mLayoutManager = new GridLayoutManager(getActivity(),2);


        mAdapter = new AdapterForCards(cardList,this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 40, true, 0));

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(Callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    ItemTouchHelper.Callback Callback= new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN|ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT;
            int swipeFlags = 0; //no swipe needed
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            moveItem(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            if (isViewOverlapping(viewHolder.itemView, mRecycleBinButton)) {
                Toast.makeText(getActivity(),"card deleted",Toast.LENGTH_SHORT).show();
                cardList.remove(viewHolder.getAdapterPosition());

                mAdapter.notifyDataSetChanged();
            }
            return false;
        }






        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }





    };

    //rearrange cards when dragging cards
    private void moveItem(int oldPos, int newPos){
        Card item=(Card) cardList.get(oldPos);
        cardList.remove(oldPos);
        cardList.add(newPos,item);
        mAdapter.notifyItemMoved(oldPos,newPos);
    }

    //check if the card is on the bin icon
    private boolean isViewOverlapping(View firstView, View secondView) {

        int[] secondPosition = new int[2];

        int[] firstPosition = new int[2];
        firstView.getLocationOnScreen(firstPosition);

        secondView.getLocationOnScreen(secondPosition);

        return firstPosition[0] < secondPosition[0] + secondView.getMeasuredWidth()
                && firstPosition[0] + firstView.getMeasuredWidth() > secondPosition[0]
                && firstPosition[1] < secondPosition[1] + secondView.getMeasuredHeight()
                && firstPosition[1] + firstView.getMeasuredHeight() > secondPosition[1];
    }

    @Override
    public void onCardClick(int position) {
        //position is the item index in the list of cards
        CardDetailedFragment cardDetailedFragment=new CardDetailedFragment();
        cardDetailedFragment.show(getFragmentManager(),"TaskDetailed");

    }

    public void openAddTaskActivity() {
        Intent intent = new Intent(getActivity(), CreateActivity.class);
        intent.putExtra("tasksPath", mTasksFilePath);
        intent.putExtra("presetsPath", mPresetsFilePath);
        intent.putExtra("categories", mCategories);
        Task newTask = new Task();
        Task.ID_COUNT += 1;
        intent.putExtra("task", newTask);
        startActivityForResult(intent, 1);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mCategories = data.getStringArrayListExtra("categories");

                //For testing
                mCategories.forEach(System.out::println);
            }
        }
    }


}

