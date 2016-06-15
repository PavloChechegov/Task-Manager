package com.pavlochechegov.taskmanager.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.pavlochechegov.taskmanager.R;
import com.pavlochechegov.taskmanager.utils.SaveTask;

public class DeleteDialogFragment extends DialogFragment {

    public interface DeleteAllItem {
        void delete();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof DeleteAllItem)) {
            throw new ClassCastException(activity.toString() + " must implement DeleteAllItem");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialogWrapper.Builder(getActivity())
                .setTitle("Delete tasks")
                .setMessage("Do you want to delete all tasks?")
                .setIcon(R.drawable.ic_delete_forever_purple_600_24dp)
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DeleteAllItem) getActivity()).delete();
                    }
                }).setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();


//        return new AlertDialog.Builder(getActivity())
//                .setTitle("Delete tasks")
//                .setMessage("Do you want to delete all tasks?")
//                .setIcon(R.drawable.ic_delete_forever_purple_600_24dp)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ((DeleteAllItem) getActivity()).delete();
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                }).create();

    }
}
