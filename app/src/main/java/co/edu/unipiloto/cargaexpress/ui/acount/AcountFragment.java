package co.edu.unipiloto.cargaexpress.ui.acount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import co.edu.unipiloto.cargaexpress.Usuario;
import co.edu.unipiloto.cargaexpress.carga_express;
import co.edu.unipiloto.cargaexpress.databinding.FragmentAcountBinding;


public class AcountFragment extends Fragment {

    private FragmentAcountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AcountViewModel acountViewModel =
                new ViewModelProvider(this).get(AcountViewModel.class);

        binding = FragmentAcountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        datosUsuario(carga_express.user);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public  void datosUsuario(Usuario user){
        binding.textView3.setText(user.getNombre());
        binding.textView5.setText(user.getApellidos());
        binding.textView7.setText(user.getTipoDocumento());
        binding.textView9.setText(user.getCedula());
        binding.textView11.setText(user.getEmail());
        binding.textView13.setText(user.getRol());
    }
}