package cat.udl.urbandapp.services;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cat.udl.urbandapp.dao.IUserDAO;
import cat.udl.urbandapp.dao.UserDAOImpl;
import cat.udl.urbandapp.models.User;
import cat.udl.urbandapp.network.RetrofitClientInstance;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserServiceImpl implements UserServiceI {

    private IUserDAO userDAO = new UserDAOImpl();
    Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
    public final MutableLiveData<String> mResponseToken;
    public final MutableLiveData<User> mUser;
    public final MutableLiveData<List<User>> mAllUsers;
    public UserServiceImpl() {
        userDAO = new UserDAOImpl();
        mResponseToken = new MutableLiveData<>();
        mUser = new MutableLiveData<>();
        mAllUsers = new MutableLiveData<>();
    }
    public MutableLiveData<String> getLiveDataToken(){
        return mResponseToken;
    }
    public MutableLiveData<User> getLiveDataUser(){
        return mUser;
    }

    public MutableLiveData<List<User>> getLiveDataAllUsers(){
        return mAllUsers;
    }


    @Override
    public void getProfileUser(final String Auth){

        userDAO.getProfileUser(Auth).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200 ){
                    try {

                        String respuestaBody = response.body().string();
                        Log.d("getUser", "Ok getUser");
                        Log.d("getUser", respuestaBody);
                        JSONObject mUserjson = new JSONObject(respuestaBody);
                        Log.d("getUser", "El JSONObject es: " + mUserjson.toString());
                        User u = new User();

                        u.setUsername(mUserjson.getString("username"));
                        u.setCreated_at(mUserjson.getString("created_at"));

                        Log.d("getUser", u.getUsername());
                        Log.d("getUser", u.getCreated_at());

                        mUser.setValue(u);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    mUser.setValue(new User());
                    Log.d("getUser", "Error en la call a la API llamada retornada con codigo" + response.code() + " message:" + response.message() );
                    Log.d("getUser", "header es: " + Auth);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("getUser", t.getMessage().toString());
                mUser.setValue(new User());
            }
        });
    }


    @Override
    public void getAllUsers(){

        userDAO.getAllUsers().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200 ){
                    try {

                        String respuestaBody = response.body().string();
                        Log.d("getAllUsers", "Ok getAllUser");
                        Log.d("getAllUsers", respuestaBody);
                        JSONArray mUsers = new JSONArray(respuestaBody);
                        List<User> mList = new ArrayList<>();
                        for (int i = 0; i < mUsers.length(); i++) {
                            JSONObject mUserjson =  mUsers.getJSONObject(i);
                            User u = new User();

                            u.setUsername(mUserjson.getString("username"));
                            u.setCreated_at(mUserjson.getString("created_at"));
                            String latlong = mUserjson.getString("gps");
                            String[] parts = latlong.split(",");
                            float latitude = Float.parseFloat(parts[0]);
                            float longitude = Float.parseFloat(parts[1]);
                            u.setLatitude(latitude);
                            u.setLongitude(longitude);
                            mList.add(u);
                        }
                        mAllUsers.setValue(mList);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    mUser.setValue(new User());
                    Log.d("getAllUsers", "Error en la call a la API llamada retornada con codigo" + response.code() + " message:" + response.message() );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("getAllUser", t.getMessage().toString());
            }
        });
    }

   // String mResponse = RetrofitClientInstance.getRetrofitInstance().create(UserServiceI.class).createTokenUser();
   @Override
   public void registerUser(JsonObject userJson) {
       userDAO.registerUser(userJson);
   }

    @Override
    public void createTokenUser(String Auth){

        userDAO.createTokenUser(Auth).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200 ){
                    try {
                        String authToken = response.body().string().split(":")[1];
                        authToken=authToken.substring(2,authToken.length()-2);

                        Log.d("UserService", authToken);
                        mResponseToken.setValue(authToken);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    String aux = null;
                    try {
                        String r = response.errorBody().string();
                        Log.d("UserService", r);
                        mResponseToken.setValue(r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("UserService", t.getMessage().toString());
                mResponseToken.setValue(t.getMessage().toString());
            }
        });
    }


}
