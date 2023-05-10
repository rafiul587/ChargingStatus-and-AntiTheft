package com.chargingstatusmonitor.souhadev.ui.fragments.antitheft;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chargingstatusmonitor.souhadev.AppDataStore;
import com.chargingstatusmonitor.souhadev.MyApplication;
import com.chargingstatusmonitor.souhadev.databinding.FragmentSetPinBinding;

public class SetPinFragment extends Fragment {

    private FragmentSetPinBinding binding;
    AppDataStore dataStore;

    public SetPinFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSetPinBinding.inflate(inflater, container, false);
        dataStore = ((MyApplication) requireContext().getApplicationContext()).getDataStore();
        binding.btnConfirm.setOnClickListener(v ->{
            String code = binding.codeEditText.getText() == null ? "" : binding.codeEditText.getText().toString().trim();
            String confirmCode = binding.confirmEditText.getText() == null ? "" : binding.confirmEditText.getText().toString();
            if(code.isEmpty()){
                binding.codeEditText.setError("Can't be empty!");
                return;
            }
            if(confirmCode.isEmpty()){
                binding.confirmEditText.setError("Can't be empty!");
                return;
            }
            if(code.equals(confirmCode)){
                dataStore.saveStringValue(AppDataStore.ALARM_CLOSING_PIN, code);
                Toast.makeText(requireContext(), "Pin has been set successfully!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            }else {
                binding.error.setVisibility(View.VISIBLE);
                binding.error.setText("Confirmation code didn't match!");
            }

        });
        binding.codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.codeEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.confirmEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.confirmEditText.setError(null);
                binding.error.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}