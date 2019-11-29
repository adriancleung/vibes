package com.cmput301f19t09.vibes.fragments.followingfragment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.cmput301f19t09.vibes.MainActivity;
import com.cmput301f19t09.vibes.R;
import com.cmput301f19t09.vibes.models.User;
import com.cmput301f19t09.vibes.models.UserManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * FollowingFragmentAdapter is an ArrayAdapter that is used for both ListView's
 * in FollowingFragment
 */
public class FollowingFragmentAdapter extends ArrayAdapter<String> {
    private List<String> userList;
    private Context context;
    private int viewMode;

    /**
     * @param context : Context
     * @param mode : String
     *
     * Constructs a FollowingFragmentAdapter, requires a passed layout (using setLayout())
     * and a passed FragmentActivity (using setActivity()) to be functional
     */
    FollowingFragmentAdapter(Context context, String mode){
        super(context, 0);
        this.context = context;
        this.userList = new ArrayList<>();

        if (mode.equals("following")) {
            viewMode = R.layout.following_list;
        } else {
            viewMode = R.layout.requested_list;
        }
    }

    /**
     * @param position : int
     * @param convertView : View
     * @param parent : ViewGroup
     * @return view
     *
     * For every item in the userList passed in the constructor, the username,
     * first name, last name, and user picture of the layout are set to the values
     * corresponding to the user. A user's profile can be opened by clicking on the
     * profile image. The list is sorted before the view is returned.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //View view = convertView;

        //if (view == null){
            View view = LayoutInflater.from(context).inflate(viewMode, parent, false);
        //}

        final TextView fullNameText = view.findViewById(R.id.fullName);
        final TextView usernameText = view.findViewById(R.id.username);
        final ImageView userImage = view.findViewById(R.id.profileImage);

        String userUID = userList.get(position);

        if (userUID == null) {
            return view;
        }

        final User user = UserManager.getUser(userUID);

        if (user.isLoaded())
        {
            Log.d("TEST", "Creating view for loaded user " + user.getFirstName());
            Glide.with(getContext()).load(user.getProfileURL()).into(userImage);
            userImage.setClipToOutline(true);

            fullNameText.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
            usernameText.setText(user.getUserName());
        }
        else
        {
            user.addObserver(new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    Glide.with(getContext()).load(user.getProfileURL()).into(userImage);
                    userImage.setClipToOutline(true);

                    fullNameText.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                    usernameText.setText(user.getUserName());

                    user.deleteObserver(this);
                }
            });
        }

        userImage.setOnClickListener(v -> goToProfile(userUID));

        if (viewMode == R.layout.requested_list) {
            Button confirmButton = view.findViewById(R.id.btn_confirm);
            confirmButton.setOnClickListener(v -> {
                User currentUser = UserManager.getCurrentUser();
                if (currentUser != null) {
                    currentUser.acceptRequest(user.getUid());
                }
            });

            Button deleteButton = view.findViewById(R.id.btn_delete);
            deleteButton.setOnClickListener(v -> {
                User currentUser = UserManager.getCurrentUser();
                if (currentUser != null) {
                    currentUser.removeRequest(user.getUid());
                }
            });

            view.findViewById(R.id.req_cont).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(userUID);
                }
            });

        }
        else
        {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToProfile(userUID);
                }
            });
        }

        return view;
    }

    void refreshData(List<String> uidList) {

        userList.clear();
        clear();

        for (String id : userList)
        {
            UserManager.removeUserObservers(id);
        }

        if (uidList == null) {
            return;
        }

        for (String id : uidList)
        {
            UserManager.addUserObserver(id, new Observer()
            {
                @Override
                public void update(Observable o, Object arg)
                {
                    notifyDataSetChanged();
                }
            });
        }

        //userList.addAll(uidList);
        userList.addAll(uidList);
        addAll(uidList);
    }

    private void goToProfile(String uid)
    {
        User user = UserManager.getUser(uid);
        if (user.isLoaded())
        {
            ((MainActivity) getContext()).setProfileFragment(user.getUid());
        }
    }

    public int getViewMode() {
        return viewMode;
    }
}