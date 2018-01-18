package com.physphil.android.remindme.room;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.persistence.room.TypeConverter;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.physphil.android.remindme.R;
import com.physphil.android.remindme.models.Recurrence;
import com.physphil.android.remindme.room.entities.Reminder;

/**
 * Copyright (c) 2017 Phil Shadlyn
 */
public class Converters2 {

    @TypeConverter
    public static int fromRecurrence(final Recurrence recurrence) {
        return recurrence.getId();
    }

    @TypeConverter
    public static Recurrence fromId(final int id) {

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).run();
        Object context = null;

        LiveData<Reminder> reminder = new MutableLiveData<Reminder>();
        LiveData<String> time = Transformations.map(reminder, new Function<Reminder, String>() {
            @Override
            public String apply(Reminder input) {
                return "";
            }
        });






        new AlertDialog.Builder((Context) context)
                .setSingleChoiceItems(R.array.recurrence_options, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return Recurrence.Companion.fromId(id);
    }
}
