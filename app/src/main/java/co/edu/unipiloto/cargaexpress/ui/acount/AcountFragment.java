package co.edu.unipiloto.cargaexpress.ui.acount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import co.edu.unipiloto.cargaexpress.databinding.FragmentAcountBinding;


public class AcountFragment extends Fragment {

    private FragmentAcountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AcountViewModel acountViewModel =
                new ViewModelProvider(this).get(AcountViewModel.class);

        binding = FragmentAcountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAcount;
        acountViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}