package com.example.myfirstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myfirstapp.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    TextView showConnStatus;
    Button connectButton;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

//        View fragmentFirstLayout = inflater.inflate(R.layout.fragment_first, container, false);


        binding = FragmentFirstBinding.inflate(inflater, container, false);
        showConnStatus = binding.getRoot().findViewById(R.id.textview_first);
        connectButton = binding.getRoot().findViewById(R.id.button_first);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToArduino(view);
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

//        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                connectToArduino(view);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void connectToArduino(View view) {
        // Get the value of the connection status
        String oldConnStatus = showConnStatus.getText().toString();
        String buttonText = "Connect to HC-05";
        String newConnStatus = "Disconnected";
        int color = getResources().getColor(R.color.red, getActivity().getTheme());
        if (oldConnStatus.equals("Disconnected")){
            buttonText = "Disconnect from HC-05";
            newConnStatus = "Connected!";
            color = getResources().getColor(R.color.green, getActivity().getTheme());
        }
        showConnStatus.setText(newConnStatus);
        showConnStatus.setTextColor(color);
        connectButton.setText(buttonText);

    }

}