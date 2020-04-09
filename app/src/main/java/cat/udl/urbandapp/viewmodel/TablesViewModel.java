package cat.udl.urbandapp.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

import cat.udl.urbandapp.models.Instrument;
import cat.udl.urbandapp.models.User;
import cat.udl.urbandapp.preferences.PreferencesProvider;
import cat.udl.urbandapp.services.TablesServiceI;
import cat.udl.urbandapp.services.TablesServiceImpl;
import cat.udl.urbandapp.services.UserServiceI;
import cat.udl.urbandapp.services.UserServiceImpl;

public class TablesViewModel extends AndroidViewModel {
    private UserServiceI repository;
    private TablesServiceI tablesRepository;
    private MutableLiveData<String> responseLiveDataToken = new MutableLiveData<>();
    private UserViewModel userViewModel;
    private MutableLiveData<User> responseLiveUser;
    private LiveData<List<Instrument>> instrumentsList;
    private SharedPreferences mPreferences = PreferencesProvider.providePreferences();

    public TablesViewModel(@NonNull Application application) {
        super(application);
        repository = new UserServiceImpl();
        userViewModel = new UserViewModel(getApplication());
        tablesRepository = new TablesServiceImpl();
        responseLiveUser = repository.getLiveDataUser();

        /*instrumentsList = Transformations.switchMap(responseLiveDataToken, new Function<String, LiveData<List<Instrument>>>() {
            @Override
            public LiveData<List<Instrument>> apply(String input) {
                instrumentsList = tablesRepository.getInstrumentsList();
                return instrumentsList;
            }

        });*/
        //loadData();
    }

    //USED FOR RECYCLER VIEW

    public LiveData<List<Instrument>> getInstruments(){
        return instrumentsList;
    }

    public void addInstrument(String nameInstrument, int exp){
        Toast.makeText(getApplication(), nameInstrument + "   " + exp, Toast.LENGTH_SHORT).show();
        Instrument ins = new Instrument(nameInstrument, exp);
        tablesRepository.setTableUserInstrument(ins);
    }
    public LiveData<User> getResponseLiveDataUser() {
        return this.responseLiveUser;
    }





}
