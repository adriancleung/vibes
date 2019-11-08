package com.cmput301f19t09.vibes.fragments.followingfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301f19t09.vibes.R;
import com.cmput301f19t09.vibes.models.User;
import java.util.ArrayList;

/**
 * FollowingFragmentAdapter is an ArrayAdapter that is used for both ListView's
 * in FollowingFragment
 */
public class FollowingFragmentAdapter extends ArrayAdapter<User> {
    private ArrayList<User> userList;
    private Context context;
    private int layout;

    /**
     * @param context
     * @param userList
     *
     * Contructs a FollowingFragmentAdapter, requires a passed layout (using setLayout())
     * to be functional
     */
    public FollowingFragmentAdapter(Context context, ArrayList<User> userList){
        super(context, 0, userList);
        this.userList = userList;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(this.layout, parent, false);
        }

        // For user at position in list
        User user = userList.get(position);

        // Sets the fullNameText to the user's firstName + lastName
        TextView fullNameText = view.findViewById(R.id.fullName);
        String fullName = user.getFirstName() + " " + user.getLastName();
        fullNameText.setText(fullName);

        // Sets the usernameText to the user's username
        TextView usernameText = view.findViewById(R.id.username);
        String username = user.getUserName();
        usernameText.setText(username);

        return view;
    }

    // Sets the layout to the passed layout for every item in the ArrayAdapter
    public void setLayout(int layout){
        this.layout = layout;
    }

}