package com.codepath.applicationnewyorktimessearch.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import com.codepath.applicationnewyorktimessearch.R;
import com.codepath.applicationnewyorktimessearch.enums.NewsDeskType;
import com.codepath.applicationnewyorktimessearch.utilities.NewsDeskTypeUtils;
import com.codepath.applicationnewyorktimessearch.utilities.SortOrderUtils;
import com.codepath.applicationnewyorktimessearch.models.ArticleSearchQuery;

/**
 * Created by aditikakadebansal on 10/19/16.
 */
public class SettingsDialogFragment extends DialogFragment {

    public interface IDialogSaveListener {
        public void onDialogSave();
    }
    private EditText mEditTextForBeginDate;
    private TextView mTextViewResetFilter;
    private Spinner mSpinnerForSortOrder;
    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat mDateFormatter;
    private Button mButtonForSave;
    private Button mButtonForCancel;
    private ImageView mImageClearBeginDate;
    private IDialogSaveListener mDialogSaveListener;
    private HashMap<NewsDeskType, CheckBox> mCheckboxForNewsDeskType;

    public SettingsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SettingsDialogFragment newInstance() {
        SettingsDialogFragment frag = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", "Settings");
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDialogSaveListener = (IDialogSaveListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        getDialog().setTitle("Search Filters");
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_settings, container);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViews(view);
        setUpListeners();
    }

    private void setUpViews(View view){
        mEditTextForBeginDate = (EditText) view.findViewById(R.id.edit_begin_date);
        mEditTextForBeginDate.setInputType(InputType.TYPE_NULL);
        mEditTextForBeginDate.requestFocus();
        mEditTextForBeginDate.setText(ArticleSearchQuery.getBeginDate());
        mTextViewResetFilter = (TextView)view.findViewById(R.id.text_reset);
        setDateTimeField();

        mSpinnerForSortOrder = (Spinner) view.findViewById((R.id.spinner_sort_order));
        mSpinnerForSortOrder.setSelection(ArticleSearchQuery.getSortOrderPosition());

        mButtonForSave = (Button) view.findViewById(R.id.button_save);
        mButtonForCancel = (Button) view.findViewById(R.id.button_cancel);

        mImageClearBeginDate = (ImageView) view.findViewById(R.id.image_clear_date);

        mCheckboxForNewsDeskType = new HashMap<>();
        for (int i=0;i<NewsDeskType.values().length;i++) {
            NewsDeskType newsDeskType = NewsDeskType.values()[i];
            int resourceId =  NewsDeskTypeUtils.getResourceID(newsDeskType);
            CheckBox checkBox = (CheckBox)view.findViewById(resourceId);
            if (ArticleSearchQuery.containsNewsDeskType(newsDeskType)) {
                checkBox.setChecked(true);
            }
            mCheckboxForNewsDeskType.put(newsDeskType, (CheckBox)view.findViewById(resourceId));
        }
    }

    private void setUpListeners() {
        mEditTextForBeginDate.setOnClickListener(v -> mDatePickerDialog.show());

        mButtonForSave.setOnClickListener(v -> {
            ArticleSearchQuery.setBeginDate(mEditTextForBeginDate.getText().toString());
            ArticleSearchQuery.setSortOrder(SortOrderUtils.getSortOrder(mSpinnerForSortOrder.getSelectedItem().toString()));
            for (NewsDeskType newsDeskType: NewsDeskType.values()) {
                CheckBox checkBox = mCheckboxForNewsDeskType.get(newsDeskType);
                if (checkBox.isChecked()) {
                    ArticleSearchQuery.addToNewsDeskTypes(newsDeskType);
                } else {
                    ArticleSearchQuery.removeFromNewsDeskTypes(newsDeskType);
                }
            }
            mDialogSaveListener.onDialogSave();
            dismiss();
        });

        mButtonForCancel.setOnClickListener(v -> dismiss());

        mImageClearBeginDate.setOnClickListener(v -> mEditTextForBeginDate.setText(""));

        mTextViewResetFilter.setOnClickListener(v -> {
            mEditTextForBeginDate.setText("");
            mSpinnerForSortOrder.setSelection(0);
            for (NewsDeskType newsDeskType: NewsDeskType.values()) {
                mCheckboxForNewsDeskType.get(newsDeskType).setChecked(false);
            }
        });
    }

    private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                mEditTextForBeginDate.setText(mDateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }
}
