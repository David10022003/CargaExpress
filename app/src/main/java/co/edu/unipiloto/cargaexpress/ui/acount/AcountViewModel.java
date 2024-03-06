package co.edu.unipiloto.cargaexpress.ui.acount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AcountViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AcountViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}