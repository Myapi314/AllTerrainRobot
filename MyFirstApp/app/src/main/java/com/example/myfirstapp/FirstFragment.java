package com.example.myfirstapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myfirstapp.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    TextView showConnStatus;



    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        showConnStatus = binding.getRoot().findViewById(R.id.textview_first);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.buttonFirst.setOnClickListener(this::connectToArduino);



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void connectToArduino(View view) {
        // Get the value of the connection status
        String oldConnStatus = showConnStatus.getText().toString();
        String buttonText = "Connect to HC-05";
        String newConnStatus = "Disconnected";
        boolean moveButtonStatus = false;
        int color = getResources().getColor(R.color.red, getActivity().getTheme());
        if (oldConnStatus.equals("Disconnected")){
            buttonText = "Disconnect from HC-05";
            newConnStatus = "Connected!";
            color = getResources().getColor(R.color.green, getActivity().getTheme());
            moveButtonStatus = true;
        }
        showConnStatus.setText(newConnStatus);
        showConnStatus.setTextColor(color);
//        binding.buttonFirst.setText(buttonText);
        binding.buttonForward.setEnabled(moveButtonStatus);
        binding.buttonRight.setEnabled(moveButtonStatus);
        binding.buttonBack.setEnabled(moveButtonStatus);
        binding.buttonLeft.setEnabled(moveButtonStatus);
        binding.buttonStop.setEnabled(moveButtonStatus);

    }

    private void moveForward(View view) {
        binding.buttonForward.setHovered(true);
        binding.buttonBack.setHovered(false);
        binding.buttonLeft.setHovered(false);
        binding.buttonRight.setHovered(false);
    }

    private void moveRight(View view) {
        binding.buttonForward.setHovered(false);
        binding.buttonBack.setHovered(false);
        binding.buttonLeft.setHovered(false);
        binding.buttonRight.setHovered(true);
    }

    private void moveBackward(View view) {
        binding.buttonForward.setHovered(false);
        binding.buttonBack.setHovered(true);
        binding.buttonLeft.setHovered(false);
        binding.buttonRight.setHovered(false);
    }

    private void moveLeft(View view) {
        binding.buttonForward.setHovered(false);
        binding.buttonBack.setHovered(false);
        binding.buttonLeft.setHovered(true);
        binding.buttonRight.setHovered(false);
    }

    private void stopMoving(View view) {
        binding.buttonForward.setHovered(false);
        binding.buttonBack.setHovered(false);
        binding.buttonLeft.setHovered(false);
        binding.buttonRight.setHovered(false);
    }

}