package com.pavlochechegov.taskmanager.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.pavlochechegov.taskmanager.R;


public class ChooseAvatarDialog extends DialogFragment {

    public interface ChooseAvatar {
        void cameraIntent();

        void galleryIntent();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ChooseAvatarDialog.ChooseAvatar)) {
            throw new ClassCastException(activity.toString() + " must implement ChooseAvatar");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final CharSequence[] items = {"Take photo", "Choose from library",
                "Cancel"};
        return new AlertDialogWrapper.Builder(getActivity())
                .setTitle("Please choose avatar")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take photo")) {
                            ((ChooseAvatar)getActivity()).cameraIntent();
                        } else if (items[item].equals("Choose from library")) {
                            ((ChooseAvatar)getActivity()).galleryIntent();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                }).show();
    }
}



