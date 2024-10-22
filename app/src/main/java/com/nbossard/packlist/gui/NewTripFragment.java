/*
 * PackList is an open-source packing-list for Android
 *
 * Copyright (c) 2017 Nicolas Bossard and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.nbossard.packlist.gui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.nbossard.packlist.PackListApp;
import com.nbossard.packlist.R;
import com.nbossard.packlist.databinding.FragmentNewTripBinding;
import com.nbossard.packlist.model.Trip;
import com.nbossard.packlist.model.TripFormatter;
import com.nbossard.packlist.process.saving.ISaving;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Allow user to input new trip characteristics or edit.
 *
 * @author Created by nbossard on 30/12/15.
 */
public class NewTripFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    // ********************** CONSTANTS *********************************************************************

    /** Bundle mandatory parameter when instantiating this fragment. */
    private static final String BUNDLE_PAR_TRIP_ID = "bundleParTripId";

    /** Frag to identify fragment for start date picker. */
    private static final String DATE_PICKER_START_TAG = "datePickerStart";

    /** Frag to identify fragment for end date picker. */
    private static final String DATE_PICKER_END_TAG = "datePickerEnd";

    /** End of trip date as a GregorianCalendar. */
    private GregorianCalendar mEndDate;

    /** Start of trip date as a GregorianCalendar. */
    private GregorianCalendar mStartDate;


    // *********************** FIELDS ***********************************************************************

    /** For communicating with hosting activity. */
    private INewTripFragmentActivity mHostingActivity;

    /** Root view for easy findViewById use.*/
    private View mRootView;

    /** Hosting activity interface. */
    private INewTripFragmentActivity mIHostingActivity;

    /** Calendar to retrieve current date. */
    private Calendar mCalendar = Calendar.getInstance();

    /** Start date dialog picker. */
    private DatePickerDialog dateStartPickerDialog;

    /** End date dialog picker. */
    private DatePickerDialog dateEndPickerDialog;

    /** Text view for input of "trip start date". */
    private TextView mStartDateTV;

    /** Text view for input of "trip end date". */
    private TextView mEndDateTV;

    /** EditText for input of "free notes on trip". */
    private EditText mNoteTV;

    /** EditText for input of "trip name". */
    private EditText mNameTV;

    /** Button to open dialog to pick a start date. */
    private AppCompatImageButton mStartDateButton;

    /** Button to open dialog to pick a end date. */
    private AppCompatImageButton mEndDateButton;

    /** Value provided when instantiating this fragment, unique identifier of trip. */
    @SuppressWarnings("FieldCanBeLocal")
    private UUID mTripId;

    /** Trip object to be displayed and added item. */
    private Trip mTrip;

    // *********************** METHODS **********************************************************************
    /**
     * Create a new instance of MyFragment that will be initialized
     * with the given arguments.
     * @param parTripId identifier of trip to be displayed
     * @return a NewTripFragment called with accurate arguments
     */
    public static NewTripFragment newInstance(final UUID parTripId) {
        NewTripFragment f = new NewTripFragment();
        if (parTripId != null) {
            Bundle b = new Bundle();
            b.putCharSequence(BUNDLE_PAR_TRIP_ID, parTripId.toString());
            f.setArguments(b);
        }
        return f;
    }

    /**
     * Empty parameters constructor.
     */

    public NewTripFragment() {
        mCalendar = Calendar.getInstance();
    }

    @Override
    public final void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mIHostingActivity = (INewTripFragmentActivity) getActivity();

        Bundle args = getArguments();
        mTripId = null;
        if (args != null) {
            mTripId = UUID.fromString(args.getString(BUNDLE_PAR_TRIP_ID, ""));
            if (mTripId != null) {
                mTrip = mIHostingActivity.loadSavedTrip(mTripId);
            } else {
                mTrip = new Trip();
            }
        } else {
            mTrip = new Trip();
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_new_trip, container, false);

        // Magic of binding
        // Do not use this syntax, it will overwrite activity (we are in a fragment)
        //mBinding = DataBindingUtil.setContentView(getActivity(), R.layout.fragment_trip_detail);
        FragmentNewTripBinding mBinding = DataBindingUtil.bind(mRootView);
        mBinding.setTrip(mTrip);
        mBinding.setTripFormatter(new TripFormatter(getContext()));
        mBinding.executePendingBindings();

        mStartDate = mTrip.getStartDate();
        mEndDate = mTrip.getEndDate();

        return mRootView;
    }

    @Override
    public final void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trip_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public final void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHostingActivity = (INewTripFragmentActivity) getActivity();

        // Getting views
        mNameTV = mRootView.findViewById(R.id.new_trip__name__edit);
        mStartDateTV = mRootView.findViewById(R.id.new_trip__start_date__edit);
        mStartDateButton = mRootView.findViewById(R.id.new_trip__start_date__button);
        mEndDateButton = mRootView.findViewById(R.id.new_trip__end_date__button);
        mEndDateTV = mRootView.findViewById(R.id.new_trip__end_date__edit);
        mNoteTV = mRootView.findViewById(R.id.new_trip__note__edit);


        // Adding listeners
        addListenerOnStartDateTextView();
        addListenerOnStartDateButton();
        addListenerOnEndDateTextView();
        addListenerOnEndDateButton();
    }

    @Override
    public final void onResume() {
        super.onResume();
        mIHostingActivity.showFABIfAccurate(false);
    }

    @Override
    public final void onDetach() {
        super.onDetach();
        mIHostingActivity.showFABIfAccurate(true);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem parItem) {
        int id = parItem.getItemId();
        if (id == R.id.save) {
            saveTrip();
            return true;
        }

        return super.onOptionsItemSelected(parItem);
    }


    /**
     * actions to be done when user clicks on "submit" button.
     */
    private void saveTrip() {

        // update trip
        mTrip.setName(mNameTV.getText().toString());
        mTrip.setStartDate(mStartDate);
        mTrip.setEndDate(mEndDate);
        mTrip.setNote(mNoteTV.getText().toString());

        // asking supporting activity to launch creation of new trip
        mHostingActivity.saveTrip(mTrip);

        // navigating back
        FragmentManager fragMgr = getActivity().getSupportFragmentManager();
        fragMgr.beginTransaction().remove(NewTripFragment.this).commit();
        fragMgr.popBackStack();
    }

    ;

    /**
     * Add a listener on "trip start date" button.
     */
    private void addListenerOnStartDateButton() {
        mStartDateButton.setOnClickListener(
                v -> showStartDatePicker());
    }

    /**
     * Add a listener on "trip end date" button.
     */
    private void addListenerOnEndDateButton() {
        mEndDateButton.setOnClickListener(
                v -> showEndDatePicker());
    }

    /**
     * Add a listener on "trip start date" text field.
     */
    private void addListenerOnStartDateTextView() {
        mStartDateTV.setOnClickListener(
                v -> showStartDatePicker());
    }

    /**
     * Add a listener on "trip end date" text field.
     */
    private void addListenerOnEndDateTextView() {
        mEndDateTV.setOnClickListener(
                v -> showEndDatePicker());
    }

    private void showStartDatePicker() {
        dateStartPickerDialog = new DatePickerDialog(getContext(), this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        dateStartPickerDialog.show();
    }

    private void showEndDatePicker() {
        dateEndPickerDialog = new DatePickerDialog(getContext(), this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH));
        dateEndPickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (view.getTag() != null && view.getTag().equals(DATE_PICKER_START_TAG)) {
            mStartDate = new GregorianCalendar(year, month, dayOfMonth);
            mStartDateTV.setText(DateFormat.getDateInstance().format(mStartDate.getTime()));
        } else if (view.getTag() != null && view.getTag().equals(DATE_PICKER_END_TAG)) {
            mEndDate = new GregorianCalendar(year, month, dayOfMonth);
            mEndDateTV.setText(DateFormat.getDateInstance().format(mEndDate.getTime()));
        }
    }
}